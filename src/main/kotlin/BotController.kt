package sidim.doma

import dev.inmo.tgbotapi.extensions.api.answers.answerCallbackQuery
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitContentMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onContentMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onDataCallbackQuery
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onText
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.from
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.message
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.text
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.RawChatId
import dev.inmo.tgbotapi.types.chat.CommonUser
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.message.content.TextedContent
import dev.inmo.tgbotapi.utils.RiskFeature
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import sidim.doma.ui.Localization.getText
import sidim.doma.ui.UiUtil
import java.time.format.DateTimeFormatter

class BotController(feedbackChatId: String, private val messageService: MessageService) {
    private val feedbackChatId = ChatId(RawChatId(feedbackChatId.toLong()))

    companion object {
        private const val ANSWER_TIMEOUT = 120_000L // 2 minutes
        private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy")
    }

    @OptIn(RiskFeature::class)
    suspend fun registerHandlers(context: BehaviourContext) {
        with(context) {
            onCommand("start") { message ->
                val chatId = message.chat.id
                val locale = getUserLocale(message)
                messageService.sendTextMessage(chatId, getText("message.start", locale))
            }

            onCommand("help") { message ->
                val chatId = message.chat.id
                val locale = getUserLocale(message)
                messageService.sendTextMessage(chatId, getText("message.help", locale))
            }

            onDataCallbackQuery(initialFilter = { it.data.startsWith("/reply_to_") }) { callback ->
                val (userChatId, userLocal) = parseReplyCallbackData(callback.data)
                val replyingUserId = callback.user.id
                val replyingLocale = callback.user.languageCode ?: "en"

                bot.answerCallbackQuery(callbackQuery = callback)

                messageService.sendTextMessage(
                    feedbackChatId, "${
                        getText("message.input_answer_for_user", replyingLocale)
                    } (ID: $userChatId):"
                )

                val replyingUserMessage = withTimeoutOrNull(ANSWER_TIMEOUT) {
                    waitContentMessage()
                        .filter { it.from?.id == replyingUserId }
                        .first()
                }

                if (replyingUserMessage != null) {
                    val header = getText("message.answer_from_dev", userLocal)
                    val content = replyingUserMessage.content
                    val adminPart = (content as? TextedContent)?.text ?: ""

                    val fullAnswer = if (adminPart.isNotBlank()) {
                        "<b>$header</b>\n$adminPart"
                    } else {
                        "<b>$header</b>"
                    }

                    val targetChatId = ChatId(RawChatId(userChatId))

                    if (content is TextContent) {
                        messageService.sendTextMessage(
                            targetChatId,
                            fullAnswer
                        )
                    } else {
                        messageService.copyMessage(
                            fromChatId = feedbackChatId,
                            messageId = replyingUserMessage.messageId,
                            toChatId = targetChatId,
                            text = fullAnswer
                        )
                    }

                    messageService.sendTextMessage(
                        feedbackChatId,
                        getText("message.message_delivered", replyingLocale, userChatId)
                    )

                    val replyingUserName =
                        callback.user.username ?: getText("message.anonymous", replyingLocale)

                    val currentTime = java.time.LocalDateTime.now().format(TIME_FORMATTER)

                    callback.message?.messageId?.let {
                        val originalText = callback.message?.text ?: ""
                        val updatedText = getText(
                            "message.answered_by",
                            replyingLocale,
                            originalText,
                            replyingUserName,
                            currentTime
                        )

                        messageService.editTextMessage(
                            feedbackChatId,
                            it,
                            updatedText,
                            replyMarkup = UiUtil.replyKeyboard(userChatId, userLocal)
                        )
                    }
                } else {
                    messageService.sendTextMessage(
                        feedbackChatId,
                        getText("message.timeout_reset", replyingLocale, userChatId)
                    )
                }
            }

            onText(initialFilter = { it.text?.startsWith("/") != true }) { message ->
                if (message.chat.id == feedbackChatId) return@onText

                val userMessage = message.content.text.trim()
                val userChatId = message.chat.id.chatId.long
                val locale = getUserLocale(message)
                val userName = getUserUsername(message)
                val text = "📩 ${
                    getText(
                        "message.feedback_from",
                        locale
                    )
                } $userName (ID: ${userChatId}/${locale}):\n${userMessage}"

                messageService.sendTextMessage(
                    feedbackChatId,
                    text,
                    UiUtil.replyKeyboard(userChatId, locale)
                )

                messageService.sendTextMessage(
                    message.chat.id,
                    getText("message.feedback_delivered", locale)
                )
            }

            onContentMessage(initialFilter = {
                val isCommand = it.text?.startsWith("/") == true
                val isText = it.content is TextContent?

                !isCommand && !isText
            }) { message ->
                if (message.chat.id == feedbackChatId) return@onContentMessage

                val userChatId = message.chat.id.chatId.long
                val locale = getUserLocale(message)
                val userName = getUserUsername(message)

                val headerText = "📩 ${
                    getText("message.feedback_from", locale)
                } $userName (ID: ${userChatId}/${locale}):"

                messageService.copyMessage(
                    fromChatId = message.chat.id,
                    messageId = message.messageId,
                    toChatId = feedbackChatId,
                )

                messageService.sendTextMessage(
                    feedbackChatId,
                    headerText,
                    UiUtil.replyKeyboard(userChatId, locale)
                )

                messageService.sendTextMessage(
                    message.chat.id,
                    getText("message.feedback_delivered", locale)
                )
            }
        }
    }

    @OptIn(RiskFeature::class)
    private fun getUserLocale(message: CommonMessage<*>): String =
        (message.from as CommonUser).languageCode ?: "en"

    @OptIn(RiskFeature::class)
    private fun getUserUsername(message: CommonMessage<*>): String =
        message.from?.username?.toString() ?: getText(
            "message.anonymous",
            getUserLocale(message)
        )

    private fun parseReplyCallbackData(data: String): Pair<Long, String> {
        val (id, locale) = data.removePrefix("/reply_to_").split("_")
        return id.toLong() to locale
    }
}
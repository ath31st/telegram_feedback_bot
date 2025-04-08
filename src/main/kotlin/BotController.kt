package sidim.doma

import dev.inmo.tgbotapi.extensions.api.answers.answerCallbackQuery
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitContentMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onDataCallbackQuery
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onText
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.from
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.message
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.text
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.RawChatId
import dev.inmo.tgbotapi.types.chat.CommonUser
import dev.inmo.tgbotapi.utils.RiskFeature
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import sidim.doma.ui.Localization
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
                val locale = (message.from as CommonUser).languageCode ?: "en"
                messageService.sendTextMessage(
                    chatId,
                    Localization.getText("message.start", locale)
                )
            }

            onCommand("help") { message ->
                val chatId = message.chat.id
                val locale = (message.from as CommonUser).languageCode ?: "en"
                messageService.sendTextMessage(
                    chatId,
                    Localization.getText("message.help", locale)
                )
            }

            onDataCallbackQuery(initialFilter = { it.data.startsWith("/reply_to_") }) { callback ->
                val idAndLocal = callback.data.removePrefix("/reply_to_").split("_")
                val userChatId = idAndLocal[0].toLong()
                val userLocal = idAndLocal[1]
                val replyingUserId = callback.user.id
                val replyingLocal = callback.user.languageCode ?: "en"

                bot.answerCallbackQuery(callbackQuery = callback)

                messageService.sendTextMessage(
                    feedbackChatId,
                    "${
                        Localization.getText(
                            "message.input_answer_for_user",
                            replyingLocal
                        )
                    } (ID: $userChatId):"
                )

                val replyingUserMessage = withTimeoutOrNull(ANSWER_TIMEOUT) {
                    waitContentMessage()
                        .filter { it.from?.id == replyingUserId }
                        .first()
                }

                if (replyingUserMessage != null) {
                    messageService.sendTextMessage(
                        ChatId(RawChatId(userChatId)),
                        "${
                            Localization.getText(
                                "message.answer_from_dev",
                                userLocal
                            )
                        }\n${replyingUserMessage.text}"
                    )
                    messageService.sendTextMessage(
                        feedbackChatId,
                        Localization.getText("message.message_delivered", replyingLocal, userChatId)
                    )

                    val replyingUserName = callback.user.username
                        ?: Localization.getText("message.anonymous", replyingLocal)

                    val currentTime = java.time.LocalDateTime.now().format(TIME_FORMATTER)

                    callback.message?.messageId?.let {
                        val originalText = callback.message?.text ?: ""
                        val updatedText = Localization.getText(
                            "message.answered_by",
                            replyingLocal,
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
                        Localization.getText("message.timeout_reset", replyingLocal, userChatId)
                    )
                }
            }

            onText(initialFilter = { it.text?.startsWith("/") != true }) { message ->
                if (message.chat.id == feedbackChatId) return@onText

                val userMessage = message.content.text.trim()
                val userChatId = message.chat.id.chatId.long
                val user = (message.from as CommonUser)
                val local = user.languageCode ?: "en"
                val userName = user.username ?: Localization.getText("message.anonymous", local)
                val text = "📩 ${
                    Localization.getText(
                        "message.feedback_from",
                        local
                    )
                } $userName (ID: ${userChatId}/${local}):\n${userMessage}"

                messageService.sendTextMessage(
                    feedbackChatId,
                    text,
                    UiUtil.replyKeyboard(userChatId, local)
                )

                messageService.sendTextMessage(
                    message.chat.id,
                    Localization.getText("message.feedback_delivered", local)
                )
            }
        }
    }
}
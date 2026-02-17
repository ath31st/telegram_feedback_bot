package sidim.doma

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.edit.text.editMessageText
import dev.inmo.tgbotapi.extensions.api.send.copyMessage
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.MessageId
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.message.HTMLParseMode
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MessageService(
    private val bot: TelegramBot,
    private val logger: Logger = LoggerFactory.getLogger("MessageService")
) {
    suspend fun sendTextMessage(
        chatId: IdChatIdentifier,
        text: String,
        replyMarkup: InlineKeyboardMarkup? = null
    ) {
        try {
            bot.sendTextMessage(chatId, text, parseMode = HTMLParseMode, replyMarkup = replyMarkup)
        } catch (e: Exception) {
            if (e.message?.contains("403") == true) {
                logger.error("Failed to send message to $chatId: ${e.message}")
            }
        }
    }

    suspend fun editTextMessage(
        chatId: IdChatIdentifier,
        messageId: MessageId,
        text: String,
        replyMarkup: InlineKeyboardMarkup?
    ) {
        try {
            bot.editMessageText(
                chatId,
                messageId,
                text,
                parseMode = HTMLParseMode,
                replyMarkup = replyMarkup
            )
        } catch (e: Exception) {
            if (e.message?.contains("403") == true) {
                logger.error("Failed to edit message $messageId in chat $chatId: ${e.message}")
            }
        }
    }

    suspend fun copyMessage(
        fromChatId: IdChatIdentifier,
        messageId: MessageId,
        toChatId: IdChatIdentifier,
        replyMarkup: InlineKeyboardMarkup? = null
    ) {
        try {
            bot.copyMessage(
                toChatId = toChatId,
                fromChatId = fromChatId,
                messageId = messageId,
                replyMarkup = replyMarkup
            )
        } catch (e: Exception) {
            if (e.message?.contains("403") == true) {
                logger.error(
                    "Failed to copy message $messageId from $fromChatId to $toChatId: ${e.message}"
                )
            }
        }
    }
}
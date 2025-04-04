package sidim.doma

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.IdChatIdentifier
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
}
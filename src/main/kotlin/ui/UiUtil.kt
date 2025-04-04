package sidim.doma.ui

import dev.inmo.tgbotapi.extensions.utils.types.buttons.dataButton
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.utils.row

object UiUtil {
    fun replyKeyboard(chatId: Long, locale: String): InlineKeyboardMarkup =
        inlineKeyboard {
            row {
                dataButton(
                    text = "\uD83D\uDCE8 ${Localization.getButton("button.reply_to", locale)}",
                    data = "/reply_to_${chatId}_$locale"
                )
            }
        }
}
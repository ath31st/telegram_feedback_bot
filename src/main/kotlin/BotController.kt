package sidim.doma

import dev.inmo.tgbotapi.extensions.api.answers.answerCallbackQuery
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onDataCallbackQuery
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onText
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.text
import dev.inmo.tgbotapi.utils.RiskFeature

class BotController {
    @OptIn(RiskFeature::class)
    suspend fun registerHandlers(context: BehaviourContext) {
        with(context) {
            onCommand("start") { message ->
                val chatId = message.chat.id
                bot.sendTextMessage(chatId, "startovo")
            }

            onCommand("help") { message ->
                val chatId = message.chat.id
                bot.sendTextMessage(chatId, "helpovo")
            }

            onDataCallbackQuery { callback ->
                val chatId = callback.from.id
                when {
                    callback.data.startsWith("/test_") -> {
                        println("test")
                    }

                    else -> {
                        println("stub")
                    }
                }
                bot.answerCallbackQuery(callback)
            }

            onText(initialFilter = { it.text?.startsWith("/") != true }) { message ->
                val chatId = message.chat.id
                bot.sendTextMessage(chatId, message.content.text)
            }
        }
    }
}
package sidim.doma

import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import sidim.doma.config.configureLogger

fun main(args: Array<String>): Unit = runBlocking {
    val logger = LoggerFactory.getLogger("Main")

    configureLogger()

    val botToken = args.getOrNull(0)
        ?: System.getenv("TELEGRAM_BOT_TOKEN")
        ?: throw IllegalStateException("Telegram bot token not provided in config or environment")

    val bot = telegramBot(botToken)
    val controller = BotController()

    launch {
        try {
            bot.buildBehaviourWithLongPolling {
                controller.registerHandlers(this)
                logger.info("Bot successfully started")
            }.join()
        } catch (e: Exception) {
            logger.error("Bot failed with error: ${e.message}", e)
        }
    }.invokeOnCompletion { throwable ->
        if (throwable != null) logger.error("Bot stopped with error: ${throwable.message}")
        else logger.info("Bot stopped")
    }
}
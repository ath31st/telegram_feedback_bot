package sidim.doma

import dev.inmo.tgbotapi.bot.ktor.telegramBot
import org.slf4j.LoggerFactory
import sidim.doma.config.configureLogger

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("Main")

    configureLogger()

    val botToken = args.getOrNull(0)
        ?: System.getenv("TELEGRAM_BOT_TOKEN")
        ?: throw IllegalStateException("Telegram bot token not provided in config or environment")

    val bot = telegramBot(botToken)
}
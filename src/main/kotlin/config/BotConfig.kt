package sidim.doma.config

import com.typesafe.config.ConfigFactory

data class BotConfig(
    val token: String,
    val feedbackChatId: String
)

fun loadConfig(): BotConfig {
    val config = ConfigFactory.load()
    return BotConfig(
        token = config.getString("bot.token"),
        feedbackChatId = config.getString("bot.feedbackChatId")
    )
}
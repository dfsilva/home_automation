import com.elbekD.bot.Bot

fun main(args: Array<String>){
    val token = System.getenv("TOKEN")
    val username = System.getenv("USERNAME")

    val bot = Bot.createPolling(username, token)


    bot.onCommand("/start") { msg, _ ->
        bot.sendMessage(msg.chat.id, "Hello World!")
    }

    bot.start()
}
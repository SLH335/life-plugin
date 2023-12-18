package xyz.hafemann.life.commands

import dev.jorel.commandapi.kotlindsl.*
import xyz.hafemann.life.Life
import xyz.hafemann.life.utils.GameManager
import kotlin.math.max

object SessionCommand {
    fun register() {
        val sessionStart = subcommand("start") {
            integerArgument("session_duration", optional = true)
            integerArgument("break_duration", optional = true)
            booleanArgument("shutdown_after_session", optional = true)
            anyExecutor { sender, args ->
                val sessionDuration = args["session_duration"] as Int?
                    ?: max(0, Life.instance.config.getInt("game.session_duration"))
                val breakDuration = args["break_duration"] as Int?
                    ?: max(0, Life.instance.config.getInt("game.break_duration"))
                val shutdownAfterSession = args["shutdown_after_session"] as Boolean?
                    ?: Life.instance.config.getBoolean("game.shutdown_after_session")

                if (sessionDuration <= 0) {
                    sender.sendMessage("Specify a session duration above 0 minutes")
                    return@anyExecutor
                }

                GameManager.startSession(sessionDuration, breakDuration, shutdownAfterSession)
            }
        }

        commandAPICommand("session") {
            withPermission("life.admin")
            subcommand(sessionStart)
        }
    }
}
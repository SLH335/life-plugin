package xyz.hafemann.life.commands

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import xyz.hafemann.life.utils.GameManager

object SessionCommand {
    fun register() {
        val sessionStart = subcommand("start") {
            anyExecutor { _, _ ->
                GameManager.startSession()
            }
        }

        commandAPICommand("session") {
            withPermission("life.admin")
            subcommand(sessionStart)
        }
    }
}
package xyz.hafemann.life.commands

import dev.jorel.commandapi.kotlindsl.*
import org.bukkit.entity.Player
import xyz.hafemann.life.utils.BoogeyManager
import xyz.hafemann.life.utils.BoogeyManager.succeedBoogeyman

object BoogeyCommand {
    fun register() {
        val boogeyChoose = subcommand("choose") {
            anyExecutor { _, _ ->
                BoogeyManager.chooseBoogeyman(0)
            }
        }

        val boogeySucceed = subcommand("succeed") {
            playerArgument("player")
            anyExecutor { _, args ->
                val player = args["player"] as Player

                player.succeedBoogeyman()
            }
        }

        commandAPICommand("boogey") {
            withPermission("life.admin")
            subcommand(boogeyChoose)
            subcommand(boogeySucceed)
        }
    }
}
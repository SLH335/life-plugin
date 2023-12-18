package xyz.hafemann.life.commands

import dev.jorel.commandapi.kotlindsl.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import xyz.hafemann.life.Life
import xyz.hafemann.life.utils.BoogeyManager
import xyz.hafemann.life.utils.GameManager

object GameCommand {
    fun register() {
        val gameStart = subcommand("start") {
            anyExecutor { sender, _ ->
                val spawn = Life.instance.config.getLocation("game.spawn")

                if (spawn == null) {
                    if (sender is Player) {
                        Life.instance.config.set("game.spawn", sender.location)
                    } else {
                        sender.sendMessage(Component
                            .text("A spawn point has to be set for the game to be started")
                            .color(NamedTextColor.RED))
                        return@anyExecutor
                    }
                }

                GameManager.startGame(5)
            }
        }

        val gameSetSpawn = subcommand("setspawn") {
            playerExecutor { player, _ ->
                Life.instance.config.set("game.spawn", player.location)
                player.world.spawnLocation = player.location
                player.world.worldBorder.center = player.location

                player.sendMessage("Set world spawn to " + player.x.toInt() + " " + player.y.toInt() + " "
                        + player.z.toInt())
            }
        }

        commandAPICommand("game") {
            withPermission("life.admin")
            subcommand(gameStart)
            subcommand(gameSetSpawn)
        }
    }
}
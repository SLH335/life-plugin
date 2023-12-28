package xyz.hafemann.life.commands

import dev.jorel.commandapi.kotlindsl.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import xyz.hafemann.life.Life
import xyz.hafemann.life.utils.BoogeyManager
import xyz.hafemann.life.utils.GameManager
import kotlin.math.max

object GameCommand {
    fun register() {
        val gameStart = subcommand("start") {
            integerArgument("start_delay", optional = true)
            integerArgument("session_duration", optional = true)
            integerArgument("break_duration", optional = true)
            booleanArgument("shutdown_after_session", optional = true)
            anyExecutor { sender, args ->
                val startDelay = max(0, args["start_delay"] as Int? ?: 5)
                val sessionDuration = args["session_duration"] as Int?
                    ?: max(0, Life.instance.config.getInt("game.session_duration"))
                val breakDuration = args["break_duration"] as Int?
                    ?: max(0, Life.instance.config.getInt("game.break_duration"))
                val shutdownAfterSession = args["shutdown_after_session"] as Boolean?
                    ?: Life.instance.config.getBoolean("game.shutdown_after_session")

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

                if (sessionDuration <= 0) {
                    sender.sendMessage("Specify a session duration above 0 minutes")
                    return@anyExecutor
                }

                GameManager.startGame(startDelay, sessionDuration, breakDuration, shutdownAfterSession)
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

        val gameDeathmatch = subcommand("deathmatch") {
            integerArgument("remaining_time")
            playerExecutor { _, args ->
                val remainingTime = args["remaining_time"] as Int
                val mapSize = Life.instance.config.getInt("game.map_size")

                GameManager.startDeathmatch(remainingTime, mapSize)
            }
        }

        commandAPICommand("game") {
            withPermission("life.admin")
            subcommand(gameStart)
            subcommand(gameSetSpawn)
            subcommand(gameDeathmatch)
        }
    }
}
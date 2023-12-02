package xyz.hafemann.life.commands

import dev.jorel.commandapi.kotlindsl.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.hafemann.life.utils.LifeManager.addLife
import xyz.hafemann.life.utils.LifeManager.life
import xyz.hafemann.life.utils.LifeManager.lives
import xyz.hafemann.life.utils.LifeManager.removeLife
import xyz.hafemann.life.utils.Lives

object LivesCommand {
    private fun sendLivesUpdateMessage(player: Player, sender: CommandSender, lives: Int) {
        val senderName = if (sender is Player)
            sender.displayName().color(sender.life().color)
        else sender.name()
            .color(NamedTextColor.BLUE)

        if (player == sender) {
            player.sendMessage(
                Component.translatable("lives.set.self", Component.text(lives).color(Lives.byValue(lives).color))
            )
        } else {
            player.sendMessage(
                Component.translatable("lives.set.by_other",
                    Component.text(lives).color(Lives.byValue(lives).color), senderName)
            )

            sender.sendMessage(
                Component.translatable("lives.set.set_other", player.displayName(),
                    Component.text(lives).color(Lives.byValue(lives).color))
            )
        }
    }

    fun register() {
        val livesList = subcommand("list") {
            anyExecutor { sender, _ ->
                sender.sendMessage(
                    Component.text("----------").decorate(TextDecoration.BOLD)
                    .append(Component.translatable("lives"))
                    .append(Component.text("----------")))

                for (current in sender.server.onlinePlayers) {
                    sender.sendMessage(
                        Component.translatable("lives.get.other", current.displayName(),
                        Component.text(current.lives()).color(current.life().color)))
                }

                sender.sendMessage(Component.text("-------------------------").decorate(TextDecoration.BOLD))
            }
        }

        val livesGet = subcommand("get") {
            playerArgument("player")
            anyExecutor { sender, args ->
                val player = args["player"] as Player
                val lives = player.lives()
                val one = if (lives == 1) ".one" else ""

                if (sender == player) {
                    sender.sendMessage(
                        Component.translatable("lives.get.self$one",
                            Component.text(lives).color(Lives.byValue(lives).color))
                    )
                } else {
                    sender.sendMessage(
                        Component.translatable(
                            "lives.get.other$one", player.displayName(),
                            Component.text(lives).color(Lives.byValue(lives).color)
                        )
                    )
                }
            }
        }

        val livesSet = subcommand("set") {
            playerArgument("player")
            integerArgument("amount")
            anyExecutor { sender, args ->
                val player = args["player"] as Player
                val lives = args["amount"] as Int

                sendLivesUpdateMessage(player, sender, lives)

                player.lives(lives)
            }
        }

        val livesAdd = subcommand("add") {
            playerArgument("player")
            anyExecutor { sender, args ->
                val player = args["player"] as Player
                val lives = player.lives() + 1

                sendLivesUpdateMessage(player, sender, lives)

                player.addLife()
            }
        }

        val livesRemove = subcommand("remove") {
            playerArgument("player")
            anyExecutor { sender, args ->
                val player = args["player"] as Player
                val lives = player.lives() - 1

                sendLivesUpdateMessage(player, sender, lives)

                player.removeLife()
            }
        }

        commandAPICommand("lives") {
            withPermission("life.admin")
            subcommand(livesList)
            subcommand(livesGet)
            subcommand(livesSet)
            subcommand(livesAdd)
            subcommand(livesRemove)
        }
    }
}
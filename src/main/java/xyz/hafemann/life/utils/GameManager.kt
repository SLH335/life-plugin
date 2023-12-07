package xyz.hafemann.life.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.GameMode
import xyz.hafemann.life.Life
import xyz.hafemann.life.utils.LifeManager.lives

object GameManager {
    private val scheduler = Life.instance.server.scheduler

    fun startGame() {
        val spawn = Life.instance.config.getLocation("game.spawn") ?: return
        val mapSize = Life.instance.config.getDouble("game.map_size")
        val lives = Life.instance.config.getInt("game.lives")

        val worldBorder = Life.instance.server.getWorld("world")!!.worldBorder

        worldBorder.center = spawn
        worldBorder.size = 20.0

        for (player in Life.instance.server.onlinePlayers) {
            player.lives(lives)
            player.gameMode = GameMode.SURVIVAL
        }

        var gameTimer = 5*60
        scheduler.runTaskTimer(Life.instance, { task ->
            when (gameTimer) {
                5*60 -> {
                    Life.instance.server.broadcast(
                        Component.translatable("game.start.soon", Component.text(5))
                            .color(NamedTextColor.YELLOW))

                }
                in 61..<5*60 -> {
                    if (gameTimer % 60 == 0) {
                        val minutes = gameTimer / 60
                        Life.instance.server.broadcast(
                            Component.translatable("game.start.soon", Component.text(minutes))
                                .color(NamedTextColor.YELLOW)
                        )
                    }
                }
                60 -> {
                    Life.instance.server.broadcast(
                        Component.translatable("game.start.one")
                            .color(NamedTextColor.YELLOW))
                }
                in 1..5 -> {
                    val color = when (gameTimer) {
                        1 -> NamedTextColor.RED
                        2 -> NamedTextColor.YELLOW
                        else -> NamedTextColor.GREEN
                    }
                    for (player in Life.instance.server.onlinePlayers) {
                        player.showTitle(Title.title(Component.text(gameTimer)
                            .color(color), Component.empty()))
                    }
                }
                0 -> {
                    task.cancel()
                    Life.instance.server.getWorld("world")!!.worldBorder.size = mapSize
                    startSession()
                }
            }
            gameTimer--
        }, 0, 20) // run once per second
    }

    fun startSession() {
        val sessionDuration = Life.instance.config.getInt("game.session_duration")
        val breakDuration = Life.instance.config.getInt("game.break_duration")
        val shutdownAfterSession = Life.instance.config.getBoolean("game.shutdown_after_session")

        var sessionTimer = sessionDuration + breakDuration
        scheduler.runTaskTimer(Life.instance, { task ->
            when (sessionTimer) {
                sessionDuration + breakDuration -> {
                    for (player in Life.instance.server.onlinePlayers) {
                        player.showTitle(
                            Title.title(
                                Component.translatable("session.start")
                            .color(NamedTextColor.GREEN), Component.empty()))
                    }

                    Life.instance.server.broadcast(
                        Component.translatable("session.start")
                        .color(NamedTextColor.GREEN))
                }
                sessionDuration / 2 + 5 -> {
                    if (breakDuration > 0) {
                        Life.instance.server.broadcast(
                            Component.translatable("session.break.start.soon", Component.text(5))
                                .color(NamedTextColor.YELLOW))
                    }
                }
                sessionDuration / 2 + 1 -> {
                    if (breakDuration > 0) {
                        Life.instance.server.broadcast(
                            Component.translatable("session.break.start.one")
                            .color(NamedTextColor.YELLOW))
                    }
                }
                sessionDuration / 2 -> {
                    if (breakDuration > 0) {
                        for (player in Life.instance.server.onlinePlayers) {
                            player.showTitle(
                                Title.title(
                                Component.translatable("session.break.start").color(NamedTextColor.GREEN),
                                Component.translatable("session.break.end.soon", Component.text(breakDuration))))
                        }

                        Life.instance.server.broadcast(
                            Component.translatable("session.break.start")
                            .color(NamedTextColor.GREEN).appendSpace().append(
                                    Component
                                .translatable("session.break.end.soon", Component.text(breakDuration))))
                    }
                }
                sessionDuration / 2 - breakDuration + 5 -> {
                    if (breakDuration > 5) {
                        Life.instance.server.broadcast(
                            Component.translatable("session.break.end.soon",
                            Component.text(5)).color(NamedTextColor.YELLOW))
                    }
                }
                sessionDuration / 2 - breakDuration + 1 -> {
                    if (breakDuration > 1) {
                        Life.instance.server.broadcast(
                            Component.translatable("session.break.end.one")
                            .color(NamedTextColor.YELLOW))
                    }
                }
                sessionDuration / 2 - breakDuration -> {
                    if (breakDuration > 0) {
                        for (player in Life.instance.server.onlinePlayers) {
                            player.showTitle(
                                Title.title(
                                Component.translatable("session.break.end").color(NamedTextColor.GREEN),
                                Component.translatable("session.remaining",
                                    Component.text(sessionDuration/2))))
                        }

                        Life.instance.server.broadcast(
                            Component.translatable("session.break.end")
                            .color(NamedTextColor.GREEN).appendSpace().append(
                                    Component
                                .translatable("session.remaining", Component.text(sessionDuration/2))))
                    }
                }
                5 -> {
                    Life.instance.server.broadcast(
                        Component.translatable("session.end.soon",
                        Component.text(5)).color(NamedTextColor.YELLOW))
                }
                1 -> {
                    Life.instance.server.broadcast(
                        Component.translatable("session.end.one")
                        .color(NamedTextColor.YELLOW))
                }
                0 -> {
                    Life.instance.server.broadcast(
                        Component.translatable("session.end").color(NamedTextColor.RED)
                        .append(Component.translatable("session.close.soon", Component.text(5))))
                }
                -4 -> {
                    Life.instance.server.broadcast(
                        Component.translatable("session.close.one")
                        .color(NamedTextColor.RED))
                }
                -5 -> {
                    task.cancel()
                    for (player in Life.instance.server.onlinePlayers) {
                        player.kick(Component.translatable("session.end"))
                    }
                    if (shutdownAfterSession) {
                        Bukkit.shutdown()
                    }
                }
            }
            sessionTimer--
        }, 0, 20*60) // run once per minute
    }
}
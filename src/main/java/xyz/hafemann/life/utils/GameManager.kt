package xyz.hafemann.life.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.GameMode
import xyz.hafemann.life.Life
import xyz.hafemann.life.utils.BoogeyManager.failBoogeyman
import xyz.hafemann.life.utils.BoogeyManager.isBoogeyman
import xyz.hafemann.life.utils.LifeManager.lives

object GameManager {
    private val scheduler = Life.instance.server.scheduler

    fun startGame(delay: Int, sessionDuration: Int, breakDuration: Int, shutdownAfterSession: Boolean) {
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
        Utility.sendTablist(sessionDuration*60)

        var gameTimer = delay * 60
        scheduler.runTaskTimer(Life.instance, { task ->
            when (gameTimer) {
                !in -10..60 -> {
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
                0, -2, -4, -6, -8 -> {
                    val countdown = gameTimer / 2 + 5
                    val color = when (countdown) {
                        1 -> NamedTextColor.RED
                        2 -> NamedTextColor.YELLOW
                        else -> NamedTextColor.GREEN
                    }
                    for (player in Life.instance.server.onlinePlayers) {
                        player.showTitle(Title.title(Component.text(countdown)
                            .color(color), Component.empty()))
                    }
                }
                -10 -> {
                    task.cancel()
                    Life.instance.server.getWorld("world")!!.worldBorder.size = mapSize
                    startSession(sessionDuration, breakDuration, shutdownAfterSession)
                }
            }
            gameTimer--
        }, 0, 20) // run once per second
    }

    fun startSession(sessionDuration: Int, breakDuration: Int, shutdownAfterSession: Boolean) {
        BoogeyManager.chooseBoogeyman(5*60)

        var sessionTimer = (sessionDuration + breakDuration) * 60
        scheduler.runTaskTimer(Life.instance, { task ->
            if (sessionTimer > 0) {
                Utility.sendTablist(sessionTimer)
            } else {
                for (player in Life.instance.server.onlinePlayers) {
                    player.sendPlayerListHeaderAndFooter(Component.empty(), Component.empty())
                }
            }
            if (sessionTimer % 60 == 0) {
                when (val sessionMinutes = sessionTimer / 60) {
                    sessionDuration + breakDuration -> {
                        for (player in Life.instance.server.onlinePlayers) {
                            player.showTitle(
                                Title.title(
                                    Component.translatable("session.start")
                                        .color(NamedTextColor.GREEN), Component.empty()
                                )
                            )
                        }

                        Life.instance.server.broadcast(
                            Component.translatable("session.start")
                                .color(NamedTextColor.GREEN)
                        )
                    }

                    sessionDuration / 2 + 5 -> {
                        if (breakDuration > 0) {
                            Life.instance.server.broadcast(
                                Component.translatable("session.break.start.soon", Component.text(5))
                                    .color(NamedTextColor.YELLOW)
                            )
                        }
                    }

                    sessionDuration / 2 + 1 -> {
                        if (breakDuration > 0) {
                            Life.instance.server.broadcast(
                                Component.translatable("session.break.start.one")
                                    .color(NamedTextColor.YELLOW)
                            )
                        }
                    }

                    sessionDuration / 2 -> {
                        if (breakDuration > 0) {
                            for (player in Life.instance.server.onlinePlayers) {
                                player.showTitle(
                                    Title.title(
                                        Component.translatable("session.break.start").color(NamedTextColor.GREEN),
                                        Component.translatable(
                                            "session.break.end.soon",
                                            Component.text(breakDuration)
                                        )
                                    )
                                )
                            }

                            Life.instance.server.broadcast(
                                Component.translatable("session.break.start")
                                    .color(NamedTextColor.GREEN).appendSpace().append(
                                        Component
                                            .translatable("session.break.end.soon", Component.text(breakDuration))
                                    )
                            )
                        }
                    }

                    sessionDuration / 2 - breakDuration + 5 -> {
                        if (breakDuration > 5) {
                            Life.instance.server.broadcast(
                                Component.translatable(
                                    "session.break.end.soon",
                                    Component.text(5)
                                ).color(NamedTextColor.YELLOW)
                            )
                        }
                    }

                    sessionDuration / 2 - breakDuration + 1 -> {
                        if (breakDuration > 1) {
                            Life.instance.server.broadcast(
                                Component.translatable("session.break.end.one")
                                    .color(NamedTextColor.YELLOW)
                            )
                        }
                    }

                    sessionDuration / 2 - breakDuration -> {
                        if (breakDuration > 0) {
                            for (player in Life.instance.server.onlinePlayers) {
                                player.showTitle(
                                    Title.title(
                                        Component.translatable("session.break.end").color(NamedTextColor.GREEN),
                                        Component.translatable(
                                            "session.remaining",
                                            Component.text(sessionDuration / 2)
                                        )
                                    )
                                )
                            }

                            Life.instance.server.broadcast(
                                Component.translatable("session.break.end")
                                    .color(NamedTextColor.GREEN).appendSpace().append(
                                        Component
                                            .translatable("session.remaining", Component.text(sessionDuration / 2))
                                    )
                            )
                        }
                    }

                    in intArrayOf(10, 20, 30, 60, 90, 120, 180) -> {
                        Life.instance.server.broadcast(
                            Component.translatable(
                                "session.remaining",
                                Component.text(sessionMinutes)
                            ).color(NamedTextColor.YELLOW)
                        )
                    }

                    5 -> {
                        Life.instance.server.broadcast(
                            Component.translatable(
                                "session.end.soon",
                                Component.text(5)
                            ).color(NamedTextColor.YELLOW)
                        )
                    }

                    1 -> {
                        Life.instance.server.broadcast(
                            Component.translatable("session.end.one")
                                .color(NamedTextColor.YELLOW)
                        )
                    }

                    0 -> {
                        Life.instance.server.broadcast(
                            Component.translatable("session.end").color(NamedTextColor.RED)
                                .append(
                                    Component.space().append(
                                        Component.translatable(
                                            "session.close.soon",
                                            Component.text(5)
                                        )
                                    )
                                )
                        )

                        for (player in Life.instance.server.onlinePlayers) {
                            player.showTitle(
                                Title.title(
                                    Component.translatable("session.end").color(NamedTextColor.RED),
                                    Component.translatable("session.close.soon", Component.text(5))
                                )
                            )

                            if (player.isBoogeyman()) {
                                player.failBoogeyman()
                            }
                        }
                    }

                    -4 -> {
                        Life.instance.server.broadcast(
                            Component.translatable("session.close.one")
                                .color(NamedTextColor.RED)
                        )
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
            }
            sessionTimer--
        }, 0, 20) // run once per second
    }

    fun startDeathmatch(remainingTime: Int, mapSize: Int) {
        val sizeReduction = (mapSize-20)/remainingTime // reduction each interval; leave 20 block diameter
        var currentSize = mapSize

        Life.instance.server.broadcast(Component.text("The deathmatch has started!").color(NamedTextColor.RED))

        var currentTime = remainingTime*60 + 15 // shrink border in 15 seconds
        scheduler.runTaskTimer(Life.instance, { task ->
            when (currentTime % 60) {
                10 -> {
                    Life.instance.server.broadcast(
                        Component.text("The border will shrink in 10 seconds").color(NamedTextColor.YELLOW))
                }
                0 -> {
                    currentSize -= sizeReduction
                    Life.world.worldBorder.setSize(currentSize.toDouble(), 10)
                    Life.instance.server.broadcast(Component.text("The border is shrinking")
                        .color(NamedTextColor.RED))
                }
            }
            currentTime--
            if (currentTime == 0) task.cancel()
        }, 0, 20) // run once per second
    }
}
package xyz.hafemann.life.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import xyz.hafemann.life.Life
import xyz.hafemann.life.utils.LifeManager.lives
import kotlin.random.Random

object BoogeyManager {
    private val scheduler = Life.instance.server.scheduler

    fun OfflinePlayer.isBoogeyman(): Boolean {
        val boogeyScore = Life.scoreboard.getObjective("boogey")!!.getScore(this)
        return boogeyScore.score == 1
    }

    private fun OfflinePlayer.setBoogeyman() {
        val boogeyScore = Life.scoreboard.getObjective("boogey")!!.getScore(this)
        boogeyScore.score = 1
    }

    fun OfflinePlayer.clearBoogeyman() {
        val boogeyScore = Life.scoreboard.getObjective("boogey")!!.getScore(this)
        boogeyScore.score = 0
    }

    fun Player.failBoogeyman() {
        if (!isBoogeyman()) return

        clearBoogeyman()
        lives(1)

        for (current in Life.instance.server.onlinePlayers) {
            if (current == this) {
                current.sendMessage(Component.translatable("boogey.task.failure.self"))
            } else {
                current.sendMessage(Component.translatable("boogey.task.failure.other",
                    displayName()))
            }
        }
    }

    fun Player.succeedBoogeyman() {
        if (!isBoogeyman()) return

        clearBoogeyman()
        addPotionEffect(PotionEffect(PotionEffectType.ABSORPTION, 15, 1))
        addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 30, 1))
        addPotionEffect(PotionEffect(PotionEffectType.FIRE_RESISTANCE, 30, 1))

        showTitle(Title.title(
            Component.translatable("boogey.task.success.self.title").color(NamedTextColor.GREEN),
            Component.translatable("boogey.task.success.self.subtitle")))

        Life.instance.server.broadcast(Component.translatable("boogey.task.success.other", displayName())
            .color(NamedTextColor.WHITE))
    }

    fun chooseBoogeyman(timer: Int) {
        var boogeyTimer = timer
        scheduler.runTaskTimer(Life.instance, { task ->
            when (boogeyTimer) {
                5 * 60 -> {
                    Life.instance.server.broadcast(
                        Component.translatable("boogey.choice.soon",
                        Component.text(5)).color(NamedTextColor.RED))
                }
                60 -> {
                    Life.instance.server.broadcast(
                        Component.translatable("boogey.choice.one")
                        .color(NamedTextColor.RED))
                }
                0 -> {
                    Life.instance.server.broadcast(
                        Component.translatable("boogey.choice.now")
                        .color(NamedTextColor.RED))
                }
                -5 -> {
                    for (player in Life.instance.server.onlinePlayers) {
                        player.showTitle(
                            Title.title(
                                Component.text("3")
                            .color(NamedTextColor.GREEN), Component.empty()))
                    }
                }
                -7 -> {
                    for (player in Life.instance.server.onlinePlayers) {
                        player.showTitle(
                            Title.title(
                                Component.text("2")
                            .color(NamedTextColor.YELLOW), Component.empty()))
                    }
                }
                -9 -> {
                    for (player in Life.instance.server.onlinePlayers) {
                        player.showTitle(
                            Title.title(
                                Component.text("1")
                            .color(NamedTextColor.RED), Component.empty()))
                    }
                }
                -11 -> {
                    for (player in Life.instance.server.onlinePlayers) {
                        player.showTitle(
                            Title.title(
                                Component.translatable("boogey.choice.you_are")
                            .color(NamedTextColor.YELLOW), Component.empty()))
                    }
                }
                -14 -> {
                    val playerCount = Life.instance.server.onlinePlayers.size
                    val boogeyIndex = Random.nextInt(playerCount)

                    for (i in 0..<playerCount) {
                        val player = Life.instance.server.onlinePlayers.toList()[i]
                        if (i == boogeyIndex) {
                            player.showTitle(
                                Title.title(
                                    Component.translatable("boogey.role.boogey")
                                .color(NamedTextColor.RED), Component.empty()))
                            player.sendMessage(Component.translatable("boogey.description"))
                            player.setBoogeyman()
                        } else {
                            player.showTitle(
                                Title.title(
                                    Component.translatable("boogey.role.not_boogey")
                                .color(NamedTextColor.GREEN), Component.empty()))
                        }
                    }
                    task.cancel()
                }
            }
            boogeyTimer--
        }, 5*60*20 - 5*60*20, 20)
    }
}
package xyz.hafemann.life.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import xyz.hafemann.life.Life
import kotlin.math.max

enum class Lives(val value: Int, val title: String, val color: NamedTextColor) {
    DEAD(0, "gray", NamedTextColor.GRAY),
    RED(1, "red", NamedTextColor.RED),
    YELLOW(2, "yellow", NamedTextColor.YELLOW),
    GREEN(3, "green", NamedTextColor.GREEN),
    DARK_GREEN(4, "dark_green", NamedTextColor.DARK_GREEN);

    companion object {
        fun byValue(value: Int): Lives {
            for (life in Lives.entries) {
                if (life.value == value) {
                    return life
                }
            }
            return DARK_GREEN
        }
    }
}

object LifeManager {
    fun OfflinePlayer.refreshLives() {
        val lives = lives()

        for (life in Lives.entries) {
            if (life.value > lives) continue
            if (this !is Player) continue
            displayName(Component.text(name).color(life.color))
            Life.scoreboard.getTeam(life.title)!!.addPlayer(this)
        }
    }

    fun OfflinePlayer.lives(): Int {
        val objective = Life.scoreboard.getObjective("lives")
        val score = objective!!.getScore(this)
        return score.score
    }

    fun OfflinePlayer.life(): Lives {
        return Lives.byValue(lives())
    }

    fun OfflinePlayer.lives(lives: Int) {
        val objective = Life.scoreboard.getObjective("lives")
        val score = objective!!.getScore(this)
        score.score = max(0, lives)
        refreshLives()
    }

    fun OfflinePlayer.addLife() {
        lives(lives() + 1)
    }

    fun OfflinePlayer.removeLife() {
        lives(lives() - 1)
    }
}
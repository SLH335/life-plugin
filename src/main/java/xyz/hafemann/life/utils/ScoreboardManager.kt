package xyz.hafemann.life.utils

import net.kyori.adventure.text.Component
import org.bukkit.scoreboard.Criteria
import xyz.hafemann.life.Life
import xyz.hafemann.life.utils.LifeManager.refreshLives

object ScoreboardManager {
    fun setupLives() {
        if (Life.scoreboard.getObjective("lives") == null) {
            Life.scoreboard.registerNewObjective("lives", Criteria.DUMMY, Component.translatable("lives"))
        }

        for (life in Lives.entries) {
            if (Life.scoreboard.getTeam(life.title) == null) {
                Life.scoreboard.registerNewTeam(life.title).color(life.color)
            }
        }

        for (player in Life.instance.server.onlinePlayers) {
            player.refreshLives()
        }
    }

    fun setupBoogey() {
        if (Life.scoreboard.getObjective("boogey") == null) {
            Life.scoreboard.registerNewObjective("boogey", Criteria.DUMMY, Component.translatable("boogey"))
        }
    }
}
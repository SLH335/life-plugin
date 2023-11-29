package xyz.hafemann.life

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.Scoreboard
import xyz.hafemann.life.utils.ScoreboardManager

class Life : JavaPlugin() {
    override fun onEnable() {
        // Plugin startup logic
        instance = this
        scoreboard = server.scoreboardManager.mainScoreboard

        setupScoreboard()
    }

    private fun setupScoreboard() {
        ScoreboardManager.setupLives()
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    companion object {
        lateinit var instance: JavaPlugin
        lateinit var scoreboard: Scoreboard
    }
}

package xyz.hafemann.life

import net.kyori.adventure.key.Key
import net.kyori.adventure.translation.GlobalTranslator
import net.kyori.adventure.translation.TranslationRegistry
import net.kyori.adventure.util.UTF8ResourceBundleControl
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.Scoreboard
import xyz.hafemann.life.utils.ScoreboardManager
import java.util.*

class Life : JavaPlugin() {
    override fun onEnable() {
        // Plugin startup logic
        instance = this
        scoreboard = server.scoreboardManager.mainScoreboard

        registerTranslations()
        setupScoreboard()
    }

    private fun registerTranslations() {
        val registry: TranslationRegistry = TranslationRegistry.create(Key.key("life"))

        val bundleEn = ResourceBundle.getBundle("xyz.hafemann.life.Bundle", Locale.US,
            UTF8ResourceBundleControl.get())
        val bundleDe = ResourceBundle.getBundle("xyz.hafemann.life.Bundle", Locale.GERMANY,
            UTF8ResourceBundleControl.get())
        registry.registerAll(Locale.GERMANY, bundleDe, true)
        registry.registerAll(Locale.US, bundleEn, true)
        GlobalTranslator.translator().addSource(registry)
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

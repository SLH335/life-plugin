package xyz.hafemann.life

import net.kyori.adventure.key.Key
import net.kyori.adventure.translation.GlobalTranslator
import net.kyori.adventure.translation.TranslationRegistry
import net.kyori.adventure.util.UTF8ResourceBundleControl
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.Scoreboard
import xyz.hafemann.life.commands.GameCommand
import xyz.hafemann.life.commands.LivesCommand
import xyz.hafemann.life.commands.SessionCommand
import xyz.hafemann.life.listeners.PlayerDeathListener
import xyz.hafemann.life.utils.ScoreboardManager
import java.util.*

class Life : JavaPlugin() {
    override fun onEnable() {
        // Plugin startup logic
        instance = this
        scoreboard = server.scoreboardManager.mainScoreboard

        setupConfig()
        registerTranslations()
        setupScoreboard()
        registerCommands()
        registerListeners()
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

    private fun setupConfig() {
        saveResource("config.yml", false);
        saveDefaultConfig();
        config.set("spawn", Location(server.getWorld("world"), 0.0, 0.0, 0.0))
        saveConfig()
    }

    private fun setupScoreboard() {
        ScoreboardManager.setupLives()
    }

    private fun registerCommands() {
        LivesCommand.register()
        GameCommand.register()
        SessionCommand.register()
    }

    private fun registerListeners() {
        server.pluginManager.registerEvents(PlayerDeathListener(), this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    companion object {
        lateinit var instance: JavaPlugin
        lateinit var scoreboard: Scoreboard
    }
}

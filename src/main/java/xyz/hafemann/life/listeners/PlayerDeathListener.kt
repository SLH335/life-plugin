package xyz.hafemann.life.listeners

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import xyz.hafemann.life.Life
import xyz.hafemann.life.utils.LifeManager.lives
import xyz.hafemann.life.utils.LifeManager.removeLife
import xyz.hafemann.life.utils.Utility
import xyz.hafemann.life.utils.Utility.head

class PlayerDeathListener: Listener {
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.player

        // handle player head drop
        if (player.killer != null) {
            val playerHead = player.head(player.killer)
            player.world.dropItem(player.location, playerHead)
        }

        player.removeLife()

        // handle elimination
        if (player.lives() == 0) {
            player.world.spawnEntity(Location(player.world, player.x, player.y - 10, player.z), EntityType.LIGHTNING)
            player.gameMode = GameMode.SPECTATOR
            event.isCancelled = true
            event.deathMessage()?.let { Life.instance.server.broadcast(it) }
            Life.instance.server.broadcast(Component.translatable("lives.eliminated.other", player.displayName())
                .color(NamedTextColor.WHITE))

            for (current in player.server.onlinePlayers) {
                if (current == player) {
                    current.showTitle(Title.title(
                        Component.translatable("lives.eliminated.self").color(NamedTextColor.RED),
                        Component.empty()))
                } else {
                    current.showTitle(Title.title(player.displayName().color(NamedTextColor.RED),
                        Component.translatable("lives.eliminated.subtitle")))
                }
            }
        }
    }
}
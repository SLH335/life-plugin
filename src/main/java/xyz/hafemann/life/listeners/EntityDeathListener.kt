package xyz.hafemann.life.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import xyz.hafemann.life.Life
import kotlin.random.Random

class EntityDeathListener : Listener {
    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        event.entity.killer ?: return // only works when killed by player

        if (!event.entityType.isSpawnable) return
        if (Random.nextInt(20) != 0) return  // 5% drop chance

        // drop spawn egg of killed mob
        val eggMaterial = Life.instance.server.itemFactory.getSpawnEgg(event.entityType) ?: return
        val spawnEgg = ItemStack(eggMaterial)
        event.drops.add(spawnEgg)
    }
}
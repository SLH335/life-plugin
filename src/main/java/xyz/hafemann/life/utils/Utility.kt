package xyz.hafemann.life.utils

import com.destroystokyo.paper.profile.PlayerProfile
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import xyz.hafemann.life.utils.LifeManager.life
import java.net.MalformedURLException
import java.net.URL
import java.util.*

object Utility {
    private fun skinProfile(skinUrl: String): PlayerProfile? {
        val profile = Bukkit.createProfile(UUID.randomUUID())
        val textures = profile.textures

        val url: URL
        try {
            url = URL(skinUrl)
        } catch (_: MalformedURLException) {
            return null
        }

        textures.skin = url
        profile.setTextures(textures)

        return profile
    }

    // get player head item with optional killer message
    fun Player.head(killer: Player? = null): ItemStack {
        val skinUrl = playerProfile.textures.skin.toString()
        val profile = skinProfile(skinUrl)
        val head = ItemStack(Material.PLAYER_HEAD)

        if (profile == null) {
            return head
        }

        val meta = head.itemMeta as SkullMeta
        meta.playerProfile = profile
        meta.displayName(Component.text("$name's Head").color(life().color)
            .decoration(TextDecoration.ITALIC, false))

        if (killer != null) {
            meta.lore(
                listOf(
                    Component.text("Killed by ").color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false).append(killer.displayName())
                )
            )
        }

        head.itemMeta = meta

        return head
    }

    // get timestamp from seconds
    fun timestamp(secondsPlayed: Int): String {
        val hours = secondsPlayed / 60 / 60
        val minutes = secondsPlayed / 60 - hours * 60
        val seconds = secondsPlayed % 60

        return "$hours:${minutes.toString().padStart(2, '0')}:" +
                seconds.toString().padStart(2, '0')
    }
}
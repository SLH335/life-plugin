package xyz.hafemann.life.utils

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.inventory.StonecuttingRecipe
import xyz.hafemann.life.Life

object RecipeManager {
    fun leadRecipe(): Recipe {
        val leadRecipe = ShapedRecipe(NamespacedKey(Life.instance, "honey_lead"), ItemStack(Material.LEAD))

        leadRecipe.shape("SS ", "SH ", "  S")
        leadRecipe.setIngredient('S', Material.STRING)
        leadRecipe.setIngredient('H', Material.HONEY_BOTTLE)

        return leadRecipe
    }

    fun mossBlockRecipe(): Recipe {
        val mossBlockRecipe = ShapelessRecipe(NamespacedKey(Life.instance, "moss_block"),
            ItemStack(Material.MOSS_BLOCK))

        mossBlockRecipe.addIngredient(Material.WHEAT_SEEDS)
        mossBlockRecipe.addIngredient(Material.BONE_MEAL)

        return mossBlockRecipe
    }

    fun nameTagRecipe(): Recipe {
        val nameTagRecipe = ShapedRecipe(NamespacedKey(Life.instance, "name_tag"), ItemStack(Material.NAME_TAG))

        nameTagRecipe.shape("  S", " P ", "P  ")
        nameTagRecipe.setIngredient('S', Material.STRING)
        nameTagRecipe.setIngredient('P', Material.PAPER)

        return nameTagRecipe
    }

    fun saddleRecipe(): Recipe {
        val saddleRecipe = ShapedRecipe(NamespacedKey(Life.instance, "saddle"), ItemStack(Material.SADDLE))

        saddleRecipe.shape("L L", " L ")
        saddleRecipe.setIngredient('L', Material.LEATHER)

        return saddleRecipe
    }

    fun slimeballRecipe(): Recipe {
        val slimeballRecipe = ShapelessRecipe(NamespacedKey(Life.instance, "slimeball"),
            ItemStack(Material.SLIME_BALL))

        slimeballRecipe.addIngredient(Material.MAGMA_CREAM)

        return slimeballRecipe
    }

    fun spawnerRecipe(): Recipe {
        val spawnerRecipe = ShapedRecipe(NamespacedKey(Life.instance, "spawner"), ItemStack(Material.SPAWNER))

        spawnerRecipe.shape("IBI", "B B", "IBI")
        spawnerRecipe.setIngredient('B', Material.IRON_BARS)
        spawnerRecipe.setIngredient('I', Material.IRON_BLOCK)

        return spawnerRecipe
    }

    fun sporeBlossomRecipe(): Recipe {
        val sporeBlossomRecipe = ShapelessRecipe(NamespacedKey(Life.instance, "spore_blossom"),
            ItemStack(Material.SPORE_BLOSSOM))

        sporeBlossomRecipe.addIngredient(Material.MOSS_BLOCK)
        sporeBlossomRecipe.addIngredient(Material.LILAC)

        return sporeBlossomRecipe
    }

    fun stickyPistonRecipe(): Recipe {
        val stickyPistonRecipe = ShapedRecipe(NamespacedKey(Life.instance, "honey_sticky_piston"),
            ItemStack(Material.STICKY_PISTON))

        stickyPistonRecipe.shape("H", "P")
        stickyPistonRecipe.setIngredient('H', Material.HONEY_BOTTLE)
        stickyPistonRecipe.setIngredient('P', Material.PISTON)

        return stickyPistonRecipe
    }

    fun tntRecipe(): Recipe {
        val tntRecipe = ShapedRecipe(NamespacedKey(Life.instance, "tnt"), ItemStack(Material.TNT))

        tntRecipe.shape("PSP", "SGS", "PSP")
        tntRecipe.setIngredient('P', Material.PAPER)
        tntRecipe.setIngredient('S', RecipeChoice.MaterialChoice(Material.SAND, Material.RED_SAND))
        tntRecipe.setIngredient('G', Material.GUNPOWDER)

        return tntRecipe
    }

    fun dripstoneBlockStonecuttingRecipe(): StonecuttingRecipe {
        return StonecuttingRecipe(
            NamespacedKey(Life.instance, "stonecutting_dripstone_block"),
            ItemStack(Material.DRIPSTONE_BLOCK),
            Material.GRANITE
        )
    }

    fun pointedDripstoneStonecuttingRecipe(): StonecuttingRecipe {
        return StonecuttingRecipe(
            NamespacedKey(Life.instance, "stonecutting_pointed_dripstone"),
            ItemStack(Material.POINTED_DRIPSTONE),
            Material.GRANITE
        )
    }
}
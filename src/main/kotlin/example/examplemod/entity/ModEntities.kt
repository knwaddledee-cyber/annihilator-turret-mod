package example.examplemod.entity

import example.examplemod.ExampleMod
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.registerObject

object ModEntities {
    val REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ExampleMod.ID)

    val ANNIHILATOR_TURRET by REGISTRY.registerObject("annihilator_turret") {
        EntityType.Builder.of(::AnnihilatorTurretEntity, MobCategory.MISC)
            .sized(1.0f, 1.5f)
            .build("annihilator_turret")
    }
}

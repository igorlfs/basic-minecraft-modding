package biden.tutorialmod.block.entity;

import biden.tutorialmod.TutorialMod;
import biden.tutorialmod.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * ModBlockEntities
 */
public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> B_DEFERRED_REGISTER = DeferredRegister
            .create(ForgeRegistries.BLOCK_ENTITY_TYPES, TutorialMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<GemInfusingStationBlockEntity>> GEM_INFUSING_STATION = B_DEFERRED_REGISTER
            .register("gem_infusing_station", () -> BlockEntityType.Builder
                    .of(GemInfusingStationBlockEntity::new, ModBlocks.GEM_INFUSING_STATION.get()).build(null));

    public static void register(IEventBus eventBus) {
        B_DEFERRED_REGISTER.register(eventBus);
    }
}

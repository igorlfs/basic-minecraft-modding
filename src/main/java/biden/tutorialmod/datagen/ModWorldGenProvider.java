package biden.tutorialmod.datagen;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import biden.tutorialmod.TutorialMod;
import biden.tutorialmod.world.feature.ModConfiguredFeatures;
import biden.tutorialmod.world.feature.ModPlacedFeatures;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

/**
 * ModWorldGenProvider
 */
public class ModWorldGenProvider extends DatapackBuiltinEntriesProvider {

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap);

    public ModWorldGenProvider(PackOutput output, CompletableFuture<Provider> registries) {
        super(output, registries, BUILDER, Collections.singleton(TutorialMod.MOD_ID));
    }

}

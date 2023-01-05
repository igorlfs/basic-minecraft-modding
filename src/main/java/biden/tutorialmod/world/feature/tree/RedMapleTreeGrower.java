package biden.tutorialmod.world.feature.tree;

import org.jetbrains.annotations.Nullable;

import biden.tutorialmod.world.feature.ModConfiguredFeatures;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

/**
 * RedMapleTreeGrower
 */
public class RedMapleTreeGrower extends AbstractTreeGrower {

    @Nullable
    @Override
    protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource pRandom, boolean pLargeHive) {
        return ModConfiguredFeatures.RED_MAPLE.getHolder().get();
    }
}

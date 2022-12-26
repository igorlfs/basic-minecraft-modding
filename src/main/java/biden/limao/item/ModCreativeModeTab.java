package biden.limao.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

/**
 * ModCreativeModeTab
 */
public class ModCreativeModeTab {

    public static final CreativeModeTab TUTORIAL_TAB = new CreativeModeTab("limaotab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.ZIRCON.get());
        }
    };
}

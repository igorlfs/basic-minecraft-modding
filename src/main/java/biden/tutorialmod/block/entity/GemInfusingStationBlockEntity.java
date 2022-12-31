package biden.tutorialmod.block.entity;

import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import biden.tutorialmod.block.custom.GemInfusingStationBlock;
import biden.tutorialmod.item.ModItems;
import biden.tutorialmod.recipe.GemInfusingStationRecipe;
import biden.tutorialmod.screen.GemInfusingStationMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

/**
 * GemInfusingStationBlockEntity
 */
public class GemInfusingStationBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemStackHandler = new ItemStackHandler(3) {
        protected void onContentsChanged(int slot) {
            setChanged();
        };

        public boolean isItemValid(int slot, ItemStack stack) {
            return switch (slot) {
                case 0 -> stack.getItem() == ModItems.ZIRCON.get();
                case 1 -> stack.getItem() == ModItems.RAW_ZIRCON.get();
                case 2 -> false;
                default -> super.isItemValid(slot, stack);
            };
        };
    };

    private LazyOptional<IItemHandler> lazyOptional = LazyOptional.empty();

    private final Map<Direction, LazyOptional<WrappedHandler>> directionWrappedHandlerMap = Map.of(Direction.DOWN,
            LazyOptional.of(() -> new WrappedHandler(itemStackHandler, (i) -> i == 2, (i, s) -> false)),
            Direction.NORTH, LazyOptional.of(() -> new WrappedHandler(itemStackHandler, (index) -> index == 1,
                    (index, stack) -> itemStackHandler.isItemValid(1, stack))),
            Direction.SOUTH,
            LazyOptional.of(() -> new WrappedHandler(itemStackHandler, (i) -> i == 2, (i, s) -> false)),
            Direction.EAST, LazyOptional.of(() -> new WrappedHandler(itemStackHandler, (i) -> i == 1,
                    (index, stack) -> itemStackHandler.isItemValid(1, stack))),
            Direction.WEST,
            LazyOptional.of(() -> new WrappedHandler(itemStackHandler, (index) -> index == 0 || index == 1,
                    (index, stack) -> itemStackHandler.isItemValid(0, stack)
                            || itemStackHandler.isItemValid(1, stack))));

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 78;

    public GemInfusingStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GEM_INFUSING_STATION.get(), pos, state);
        this.data = new ContainerData() {

            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> GemInfusingStationBlockEntity.this.progress;
                    case 1 -> GemInfusingStationBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> GemInfusingStationBlockEntity.this.progress = value;
                    case 1 -> GemInfusingStationBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }

        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new GemInfusingStationMenu(id, inventory, this, this.data);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Gem Infusing Station");
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == null) {
                return lazyOptional.cast();
            }

            if (directionWrappedHandlerMap.containsKey(side)) {
                Direction localDir = this.getBlockState().getValue(GemInfusingStationBlock.FACING);

                if (side == Direction.UP || side == Direction.DOWN) {
                    return directionWrappedHandlerMap.get(side).cast();
                }

                return switch (localDir) {
                    default -> directionWrappedHandlerMap.get(side.getOpposite()).cast();
                    case EAST -> directionWrappedHandlerMap.get(side.getClockWise()).cast();
                    case SOUTH -> directionWrappedHandlerMap.get(side).cast();
                    case WEST -> directionWrappedHandlerMap.get(side.getCounterClockWise()).cast();
                };
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyOptional = LazyOptional.of(() -> itemStackHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyOptional.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", itemStackHandler.serializeNBT());
        nbt.putInt("gem_infusing_station.progress", this.progress);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemStackHandler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("gem_infusing_station.progress");
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemStackHandler.getSlots());
        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            inventory.setItem(i, itemStackHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GemInfusingStationBlockEntity pEntity) {
        if (level.isClientSide()) {
            return;
        }

        if (hasRecipe(pEntity)) {
            pEntity.progress++;
            setChanged(level, pos, state);

            if (pEntity.progress >= pEntity.maxProgress) {
                craftItem(pEntity);
            }
        } else {
            pEntity.resetProgress();
            setChanged(level, pos, state);
        }
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private static void craftItem(GemInfusingStationBlockEntity pEntity) {
        Level level = pEntity.level;
        SimpleContainer inventory = new SimpleContainer(pEntity.itemStackHandler.getSlots());
        for (int i = 0; i < pEntity.itemStackHandler.getSlots(); i++) {
            inventory.setItem(i, pEntity.itemStackHandler.getStackInSlot(i));
        }

        Optional<GemInfusingStationRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(GemInfusingStationRecipe.Type.INSTANCE, inventory, level);
        if (hasRecipe(pEntity)) {
            pEntity.itemStackHandler.extractItem(1, 1, false);
            pEntity.itemStackHandler.setStackInSlot(2,
                    new ItemStack(recipe.get().getResultItem().getItem(),
                            pEntity.itemStackHandler.getStackInSlot(2).getCount() + 1));
            pEntity.resetProgress();
        }
    }

    private static boolean hasRecipe(GemInfusingStationBlockEntity entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemStackHandler.getSlots());
        for (int i = 0; i < entity.itemStackHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemStackHandler.getStackInSlot(i));
        }

        Optional<GemInfusingStationRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(GemInfusingStationRecipe.Type.INSTANCE, inventory, level);

        return recipe.isPresent() && canInsertAmountIntoOutputSlot(inventory)
                && canInsetItemIntoOutputSlot(inventory, recipe.get().getResultItem());
    }

    private static boolean canInsetItemIntoOutputSlot(SimpleContainer inventory, ItemStack itemStack) {
        return inventory.getItem(2).getItem() == itemStack.getItem() || inventory.getItem(2).isEmpty();
    }

    private static boolean canInsertAmountIntoOutputSlot(SimpleContainer inventory) {
        return inventory.getItem(2).getMaxStackSize() > inventory.getItem(2).getCount();
    }

}

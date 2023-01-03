package biden.tutorialmod.networking.packet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import biden.tutorialmod.block.entity.GemInfusingStationBlockEntity;
import biden.tutorialmod.screen.GemInfusingStationMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkEvent;

/**
 * ExampleC2S
 */
public class ItemStackSyncS2CPacket {

    private final ItemStackHandler itemStackHandler;
    private final BlockPos pos;

    public ItemStackSyncS2CPacket(ItemStackHandler itemStackHandler, BlockPos pos) {
        this.itemStackHandler = itemStackHandler;
        this.pos = pos;
    }

    public ItemStackSyncS2CPacket(FriendlyByteBuf buf) {
        List<ItemStack> collection = buf.readCollection(ArrayList::new, FriendlyByteBuf::readItem);
        this.itemStackHandler = new ItemStackHandler(collection.size());
        for (int i = 0; i < collection.size(); i++) {
            this.itemStackHandler.insertItem(i, collection.get(i), false);
        }

        this.pos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf) {
        Collection<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            list.add(itemStackHandler.getStackInSlot(i));
        }

        buf.writeCollection(list, FriendlyByteBuf::writeItem);
        buf.writeBlockPos(this.pos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.level
                    .getBlockEntity(pos) instanceof GemInfusingStationBlockEntity blockEntity) {
                blockEntity.setHandler(this.itemStackHandler);
            }
        });
        return true;
    }

}

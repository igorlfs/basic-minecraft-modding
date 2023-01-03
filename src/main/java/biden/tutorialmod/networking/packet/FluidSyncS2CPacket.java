package biden.tutorialmod.networking.packet;

import java.util.function.Supplier;

import biden.tutorialmod.block.entity.GemInfusingStationBlockEntity;
import biden.tutorialmod.screen.GemInfusingStationMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;

/**
 * ExampleC2S
 */
public class FluidSyncS2CPacket {

    private final FluidStack fluidStack;
    private final BlockPos pos;

    public FluidSyncS2CPacket(FluidStack energy, BlockPos pos) {
        this.fluidStack = energy;
        this.pos = pos;
    }

    public FluidSyncS2CPacket(FriendlyByteBuf buf) {
        this.fluidStack = buf.readFluidStack();
        this.pos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeFluidStack(this.fluidStack);
        buf.writeBlockPos(this.pos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.level
                    .getBlockEntity(pos) instanceof GemInfusingStationBlockEntity blockEntity) {
                blockEntity.setFluid(this.fluidStack);
                if (minecraft.player.containerMenu instanceof GemInfusingStationMenu menu
                        && menu.getBlockEntity().getBlockPos().equals(pos)) {
                    menu.setFluidStack(this.fluidStack);
                }
            }
        });
        return true;
    }

}

package biden.tutorialmod.networking.packet;

import java.util.function.Supplier;

import biden.tutorialmod.client.ClientThirstData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/**
 * ExampleC2S
 */
public class ThirstDataSyncS2CPacket {

    private final int thirst;

    public ThirstDataSyncS2CPacket(int thirst) {
        this.thirst = thirst;
    }

    public ThirstDataSyncS2CPacket(FriendlyByteBuf buf) {
        this.thirst = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.thirst);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // HERE WE ARE ON THE SERVER
            ClientThirstData.setPlayerThirst(thirst);
        });
        return true;
    }

}

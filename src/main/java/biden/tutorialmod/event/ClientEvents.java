package biden.tutorialmod.event;

import biden.tutorialmod.TutorialMod;
import biden.tutorialmod.block.entity.ModBlockEntities;
import biden.tutorialmod.block.entity.renderer.GemInfusingStationBlockEntityRenderer;
import biden.tutorialmod.client.ThirstHudOverlay;
import biden.tutorialmod.networking.ModMessages;
import biden.tutorialmod.networking.packet.DrinkWaterC2SPacket;
import biden.tutorialmod.util.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * ClientEvents
 */
public class ClientEvents {

    @Mod.EventBusSubscriber(modid = TutorialMod.MOD_ID, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key eKey) {
            if (KeyBinding.DRINKING_KEY.consumeClick()) {
                ModMessages.sendToServer(new DrinkWaterC2SPacket());
            }
        }
    }

    @Mod.EventBusSubscriber(modid = TutorialMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(KeyBinding.DRINKING_KEY);
        }

        @SubscribeEvent
        public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("thirst", ThirstHudOverlay.HUD_THIRST);
        }

        @SubscribeEvent
        public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ModBlockEntities.GEM_INFUSING_STATION.get(),
                    GemInfusingStationBlockEntityRenderer::new);
        }
    }
}

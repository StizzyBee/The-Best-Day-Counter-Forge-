package com.example.daycounter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/** Client-only Forge 1.21.1 entrypoint. */
@Mod(DayCounterClient.MOD_ID)
public class DayCounter {

    @SuppressWarnings("unused")
    public DayCounter(FMLJavaModLoadingContext context) {
        DayCounterClient.CONFIG = DayCounterConfig.load();
        context.getModEventBus().addListener(this::onAddOverlayLayers);
        MinecraftForge.EVENT_BUS.addListener(this::onScreenInitPost);
    }

    private void onAddOverlayLayers(AddGuiOverlayLayersEvent event) {
        event.getLayeredDraw().add(
            ResourceLocation.fromNamespaceAndPath(DayCounterClient.MOD_ID, "day_counter"),
            DayCounterClient::renderDayCounter);
    }

    private void onScreenInitPost(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof PauseScreen screen)) return;

        Button quitButton = findBottomMostButton(event);
        if (quitButton == null) return;

        int btnW = quitButton.getWidth();
        int btnX = quitButton.getX();
        int btnY = quitButton.getY();

        quitButton.setY(btnY + 24);

        event.addListener(Button.builder(
            Component.literal("Day Counter Settings"),
            b -> Minecraft.getInstance().setScreen(
                new DayCounterSettingsScreen(screen, DayCounterClient.CONFIG))
        ).bounds(btnX, btnY, btnW, 20).build());
    }

    /**
     * The pause menu's bottom-most button is "Save and Quit to Title". Anchoring
     * to it by on-screen position is locale-independent and robust against
     * translation-key changes, unlike matching the button's label text.
     */
    private static Button findBottomMostButton(ScreenEvent.Init.Post event) {
        Button bottom = null;
        for (GuiEventListener listener : event.getListenersList()) {
            if (listener instanceof Button button
                    && (bottom == null || button.getY() > bottom.getY())) {
                bottom = button;
            }
        }
        return bottom;
    }
}

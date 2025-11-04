package com.kelvin124124.afkautoclicker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AFKAutoClickerMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ModEventHandler {
    private static boolean afkModeActive = false;
    private static long lastClickTime = 0;
    private static final long CLICK_COOLDOWN_MS = 800;
    
    private static float savedYaw = 0;
    private static float savedPitch = 0;
    
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        try {
            if (KeyBindings.AFK_TOGGLE_KEY != null && KeyBindings.AFK_TOGGLE_KEY.consumeClick()) {
                toggleAFKMode();
            }
        } catch (Exception e) {
            // Basic error handling - silently fail
        }
    }
    
    private static void toggleAFKMode() {
        afkModeActive = !afkModeActive;
        if (afkModeActive) {
            lockCamera();
        }
    }
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !afkModeActive) return;
        
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.level == null || mc.gameMode == null) return;
            
            enforceCamera();
            
            long currentTime = System.currentTimeMillis();
            if (shouldPerformClick(mc, currentTime)) {
                performLeftClick(mc);
                lastClickTime = currentTime;
            }
        } catch (Exception e) {
            // Basic error handling - silently fail
        }
    }
    
    private static void performLeftClick(Minecraft mc) {
        // Swing arm for visual feedback
        mc.player.swing(InteractionHand.MAIN_HAND);
        
        // Attack entity if targeting one
        if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) mc.hitResult;
            mc.gameMode.attack(mc.player, entityHit.getEntity());
        }
        // Mine block if targeting one
        else if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) mc.hitResult;
            mc.gameMode.continueDestroyBlock(blockHit.getBlockPos(), blockHit.getDirection());
        }
    }
    
    private static boolean shouldPerformClick(Minecraft mc, long currentTime) {
        if (currentTime - lastClickTime < CLICK_COOLDOWN_MS) {
            return false;
        }
        
        ItemStack heldItem = mc.player.getMainHandItem();
        if (!heldItem.isEmpty()) {
            float itemCooldown = mc.player.getCooldowns().getCooldownPercent(heldItem.getItem(), 0);
            if (itemCooldown > 0) {
                return false;
            }
        }
        
        return true;
    }
    
    private static void lockCamera() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            savedYaw = mc.player.getYRot();
            savedPitch = mc.player.getXRot();
        }
    }
    
    private static void enforceCamera() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.setYRot(savedYaw);
            mc.player.setXRot(savedPitch);
            mc.player.setYHeadRot(savedYaw);
            mc.player.yRotO = savedYaw;
            mc.player.xRotO = savedPitch;
        }
    }
    
    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (!afkModeActive) return;
        if (!event.getOverlay().id().toString().equals("minecraft:hotbar")) return;
        
        try {
            Minecraft mc = Minecraft.getInstance();
            GuiGraphics graphics = event.getGuiGraphics();
            
            String message = "AFK mode active. Press " + 
                KeyBindings.AFK_TOGGLE_KEY.getTranslatedKeyMessage().getString() + 
                " to deactivate.";
            
            int screenWidth = mc.getWindow().getGuiScaledWidth();
            int screenHeight = mc.getWindow().getGuiScaledHeight();
            int x = (screenWidth - mc.font.width(message)) / 2;
            int y = screenHeight - 59;
            
            graphics.drawString(mc.font, message, x, y, 0xFF5555, true);
        } catch (Exception e) {
            // Basic error handling - silently fail
        }
    }
}
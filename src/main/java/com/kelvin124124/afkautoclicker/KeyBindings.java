package com.kelvin124124.afkautoclicker;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static KeyMapping AFK_TOGGLE_KEY;
    
    public static void register() {
        AFK_TOGGLE_KEY = new KeyMapping(
            "key.afkautoclicker.toggle",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.categories.afkautoclicker"
        );
    }
    
    public static void onRegisterKeyMappings(net.minecraftforge.client.event.RegisterKeyMappingsEvent event) {
        event.register(AFK_TOGGLE_KEY);
    }
}
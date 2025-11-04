package com.kelvin124124.afkautoclicker;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("afkautoclicker")
public class afkautoclickerMod {
    public static final String MOD_ID = "afkautoclicker";
    
    public afkautoclickerMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }
    
    private void clientSetup(FMLClientSetupEvent event) {
        KeyBindings.register();
        MinecraftForge.EVENT_BUS.register(new ModEventHandler());
    }
}

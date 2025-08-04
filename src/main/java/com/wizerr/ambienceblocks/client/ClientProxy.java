package com.wizerr.ambienceblocks.client;

import com.wizerr.ambienceblocks.client.gui.ambience.CompendiumGUI;
import net.minecraft.client.Minecraft;

public class ClientProxy {
    public static void openCompendium() {
        Minecraft.getInstance().setScreen(new CompendiumGUI());
    }
}

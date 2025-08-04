package com.wizerr.ambienceblocks;

import com.wizerr.ambienceblocks.ambience.compendium.ServerCompendium;
import com.wizerr.ambienceblocks.ambience.sync.target.TargetSyncServer;
import com.wizerr.ambienceblocks.client.ambience.AmbienceController;
import com.wizerr.ambienceblocks.client.rendering.RenderTypeHelper;
import com.wizerr.ambienceblocks.config.AmbienceConfig;
import com.wizerr.ambienceblocks.registry.CreativeTab;
import com.wizerr.ambienceblocks.util.PacketHandler;
import com.wizerr.ambienceblocks.registry.RegistryHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Main.MODID)
public class Main {
    public static final String MODID = "ambienceblocks";
    private static final Logger LOGGER = LogManager.getLogger();

    public Main() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register registry handlers
        RegistryHandler.init(modEventBus);
        CreativeTab.CREATIVE_MODE_TABS.register(modEventBus);

        // Register configs
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AmbienceConfig.COMMON_SPEC);

        // Register event listeners
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::doClientStuff);

        // Register for server events
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Initializing Ambience Blocks common setup");
        PacketHandler.register();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        LOGGER.info("Initializing Ambience Blocks client setup");

        // Initialize and register AmbienceController
        AmbienceController controller = new AmbienceController();
        MinecraftForge.EVENT_BUS.register(controller);

        // Register client compendium
        if (controller.compendium != null) {
            MinecraftForge.EVENT_BUS.register(controller.compendium);
        }

        // Register shaders
        FMLJavaModLoadingContext.get().getModEventBus().addListener(RenderTypeHelper::registerShadersEvent);
    }

    @SubscribeEvent
    public void onServerStart(ServerStartingEvent event) {
        LOGGER.info("Server starting - initializing Ambience Blocks server components");

        // Initialize and register target sync system
        TargetSyncServer targetSync = new TargetSyncServer();
        MinecraftForge.EVENT_BUS.register(targetSync);

        // Initialize and register server compendium
        ServerCompendium compendium = new ServerCompendium(LOGGER);
        MinecraftForge.EVENT_BUS.register(compendium);
    }

    @SubscribeEvent
    public void onServerStop(ServerStoppingEvent event) {
        LOGGER.info("Server stopping - cleaning up Ambience Blocks resources");
        // Any cleanup needed when server stops
    }
}
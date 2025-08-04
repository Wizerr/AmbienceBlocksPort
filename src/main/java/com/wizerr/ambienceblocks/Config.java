package com.wizerr.ambienceblocks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // Config values
    public static final ForgeConfigSpec.BooleanValue LOG_DIRT_BLOCK;
    public static final ForgeConfigSpec.IntValue MAGIC_NUMBER;
    public static final ForgeConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS;

    public static final ForgeConfigSpec SPEC;

    // Runtime cached values
    public static boolean logDirtBlock;
    public static int magicNumber;
    public static String magicNumberIntroduction;
    public static Set<Item> items;

    static {
        LOG_DIRT_BLOCK = BUILDER
                .comment("Whether to log the dirt block on common setup")
                .define("logDirtBlock", true);

        MAGIC_NUMBER = BUILDER
                .comment("A magic number")
                .defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);

        MAGIC_NUMBER_INTRODUCTION = BUILDER
                .comment("What you want the introduction message to be for the magic number")
                .define("magicNumberIntroduction", "The magic number is... ");

        ITEM_STRINGS = BUILDER
                .comment("A list of items to log on common setup.")
                .defineList("items", List.of("minecraft:iron_ingot"), Config::validateItemName);

        SPEC = BUILDER.build();
    }

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && ResourceLocation.isValidResourceLocation(itemName);
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == SPEC) {
            refreshCache();
        }
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == SPEC) {
            refreshCache();
        }
    }

    private static void refreshCache() {
        logDirtBlock = LOG_DIRT_BLOCK.get();
        magicNumber = MAGIC_NUMBER.get();
        magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get();

        items = ITEM_STRINGS.get().stream()
                .map(ResourceLocation::new)
                .map(ForgeRegistries.ITEMS::getValue)
                .filter(item -> item != null)
                .collect(Collectors.toSet());
    }
}
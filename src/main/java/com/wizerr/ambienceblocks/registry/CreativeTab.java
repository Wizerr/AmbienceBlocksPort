package com.wizerr.ambienceblocks.registry;

import com.wizerr.ambienceblocks.Main;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Main.MODID);

    public static final RegistryObject<CreativeModeTab> AMBIENCE_TAB = CREATIVE_MODE_TABS.register("ambiencetab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(RegistryHandler.AMBIENCE_BLOCK.get()))
                    .title(Component.translatable("itemGroup.ambiencetab"))
                    .displayItems((parameters, output) -> {
                        output.accept(RegistryHandler.AMBIENCE_BLOCK.get());
                        output.accept(RegistryHandler.INVISIBLE_AMBIENCE_BLOCK.get());
                        output.accept(RegistryHandler.WOODEN_AMBIENCE_BLOCK.get());
                        output.accept(RegistryHandler.AMBIENCE_BLOCK_FINDER.get());
                        output.accept(RegistryHandler.AMBIENCE_COMPENDIUM.get());
                    })
                    .build()
    );
}

package com.wizerr.ambienceblocks.registry;

import com.wizerr.ambienceblocks.Main;
import com.wizerr.ambienceblocks.blocks.AmbienceBlock;
import com.wizerr.ambienceblocks.blocks.BlockItemBase;
import com.wizerr.ambienceblocks.blocks.InvisibleAmbienceBlock;
import com.wizerr.ambienceblocks.blocks.WoodenAmbienceBlock;
import com.wizerr.ambienceblocks.items.ItemAmbienceBlockFinder;
import com.wizerr.ambienceblocks.items.ItemCompendium;
import com.wizerr.ambienceblocks.tileentity.AmbienceTileEntity;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RegistryHandler {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Main.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Main.MODID);
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Main.MODID);

    public static void init(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        PARTICLE_TYPES.register(modEventBus);
    }

    //Blocks
    public static final RegistryObject<Block> AMBIENCE_BLOCK =
            BLOCKS.register("ambience_block", AmbienceBlock::new);
    public static final RegistryObject<Block> INVISIBLE_AMBIENCE_BLOCK =
            BLOCKS.register("invisible_ambience_block", InvisibleAmbienceBlock::new);
    public static final RegistryObject<Block> WOODEN_AMBIENCE_BLOCK =
            BLOCKS.register("wooden_ambience_block", WoodenAmbienceBlock::new);

    //Block Items
    public static final RegistryObject<Item> AMBIENCE_BLOCK_ITEM =
            ITEMS.register("ambience_block", () -> new BlockItemBase(AMBIENCE_BLOCK.get()));
    public static final RegistryObject<Item> INVISIBLE_AMBIENCE_BLOCK_ITEM =
            ITEMS.register("invisible_ambience_block", () -> new BlockItemBase(INVISIBLE_AMBIENCE_BLOCK.get()));
    public static final RegistryObject<Item> WOODEN_AMBIENCE_BLOCK_ITEM =
            ITEMS.register("wooden_ambience_block", () -> new BlockItemBase(WOODEN_AMBIENCE_BLOCK.get()));

    //Items
    public static final RegistryObject<Item> AMBIENCE_BLOCK_FINDER =
            ITEMS.register("ambience_block_finder", ItemAmbienceBlockFinder::new);
    public static final RegistryObject<Item> AMBIENCE_COMPENDIUM =
            ITEMS.register("ambience_compendium", ItemCompendium::new);
    //TODO override item code to add custom right click behavior for the ambience compendium

    //Tile Entities
    public static final RegistryObject<BlockEntityType<AmbienceTileEntity>> AMBIENCE_TILE_ENTITY =
            BLOCK_ENTITY_TYPES.register("ambience_block",
                    () -> BlockEntityType.Builder.of(AmbienceTileEntity::new, AMBIENCE_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<AmbienceTileEntity>> INVISIBLE_AMBIENCE_TILE_ENTITY =
            BLOCK_ENTITY_TYPES.register("invisible_ambience_block",
                    () -> BlockEntityType.Builder.of(AmbienceTileEntity::new, INVISIBLE_AMBIENCE_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<AmbienceTileEntity>> WOODEN_AMBIENCE_TILE_ENTITY =
            BLOCK_ENTITY_TYPES.register("wooden_ambience_block",
                    () -> BlockEntityType.Builder.of(AmbienceTileEntity::new, WOODEN_AMBIENCE_BLOCK.get()).build(null));
}

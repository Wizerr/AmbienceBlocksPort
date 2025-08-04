package com.wizerr.ambienceblocks.blocks;

import com.wizerr.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.wizerr.ambienceblocks.tileentity.AmbienceTileEntity;
import com.wizerr.ambienceblocks.registry.RegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class AmbienceBlock extends Block implements EntityBlock {

    public AmbienceBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(-1.0F, 3600000.0F)
                .sound(SoundType.METAL)
                .noLootTable()
        );
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return RegistryHandler.AMBIENCE_TILE_ENTITY.get().create(pos, state);
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!worldIn.isClientSide)
            return InteractionResult.SUCCESS;

        if (Minecraft.getInstance().level.getBlockEntity(pos) == null)
            return InteractionResult.PASS;

        if (!(Minecraft.getInstance().level.getBlockEntity(pos) instanceof AmbienceTileEntity))
            return InteractionResult.PASS;

        if (!player.isCreative())
            return InteractionResult.PASS;

        Minecraft.getInstance().setScreen(new AmbienceGUI((AmbienceTileEntity) Minecraft.getInstance().level.getBlockEntity(pos)));

        return InteractionResult.SUCCESS;
    }
}

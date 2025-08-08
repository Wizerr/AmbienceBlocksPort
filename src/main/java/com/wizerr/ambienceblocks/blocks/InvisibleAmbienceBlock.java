package com.wizerr.ambienceblocks.blocks;

import com.wizerr.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.wizerr.ambienceblocks.tileentity.AmbienceTileEntity;
import com.wizerr.ambienceblocks.registry.RegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class InvisibleAmbienceBlock extends Block implements EntityBlock {

    public InvisibleAmbienceBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.NONE)
                .strength(-1.0F, 3600000.0F)
                .sound(SoundType.METAL)
                .noCollission()
                .noLootTable());
    }

    @Override
    public boolean addRunningEffects(BlockState state, Level world, BlockPos pos, Entity entity) {
        return true;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return RegistryHandler.INVISIBLE_AMBIENCE_TILE_ENTITY.get().create(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext ecc && ecc.getEntity() instanceof Player player) {
            if (!player.isCreative()) {
                return Shapes.empty();
            }
        }
        return Shapes.block();
    }
/*
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }
    */

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

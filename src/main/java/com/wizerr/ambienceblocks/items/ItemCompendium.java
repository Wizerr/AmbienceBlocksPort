package com.wizerr.ambienceblocks.items;

import com.wizerr.ambienceblocks.packets.PacketOpenCompendiumGui;
import com.wizerr.ambienceblocks.util.PacketHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;

public class ItemCompendium extends Item {
    public ItemCompendium() {
        super(new Properties().stacksTo(1));
    }
/*
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if(!worldIn.isClientSide())
            return super.use(worldIn, playerIn, handIn);

        //Minecraft.getInstance().setScreen(new CompendiumGUI());
        //PacketHandler.NET.sendToServer(new PacketAskCompendiumGui());

        return super.use(worldIn, playerIn, handIn);
    }*/
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if (!world.isClientSide) {
            PacketHandler.NET.sendTo(new PacketOpenCompendiumGui(), ((ServerPlayer) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }

        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), world.isClientSide());
    }

}

package com.wizerr.ambienceblocks.packets;

import com.wizerr.ambienceblocks.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketOpenCompendiumGui {

    public PacketOpenCompendiumGui() {}

    public static void encode(PacketOpenCompendiumGui msg, FriendlyByteBuf buf) {
    }

    public static PacketOpenCompendiumGui decode(FriendlyByteBuf buf) {
        return new PacketOpenCompendiumGui();
    }

    public static void handle(PacketOpenCompendiumGui msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientProxy::openCompendium)
        );
        ctx.get().setPacketHandled(true);
    }
}

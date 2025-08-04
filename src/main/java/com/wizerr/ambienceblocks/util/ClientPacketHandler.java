package com.wizerr.ambienceblocks.util;

import com.wizerr.ambienceblocks.client.ambience.AmbienceController;
import com.wizerr.ambienceblocks.packets.*;
import com.wizerr.ambienceblocks.tileentity.AmbienceTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

@OnlyIn(Dist.CLIENT)
public class ClientPacketHandler {
    public static DistExecutor.SafeRunnable handlePacketTargeting(final PacketTargeting pkt) {
        return new DistExecutor.SafeRunnable() {
            @Override
            public void run() {
                Minecraft mc = Minecraft.getInstance();

                if (mc.level == null)
                    return;

                if (!mc.level.isClientSide())
                    return;

                Entity source = mc.level.getEntity(pkt.source);

                if (source == null)
                    return;

                if(source instanceof Mob) {
                    Mob mob = (Mob) source;

                    AmbienceController.instance.target.updateTargeting(mob);
                }
            }
        };
    }

    public static DistExecutor.SafeRunnable handlePacketNotTargeting(final PacketNotTargeting pkt) {
        return new DistExecutor.SafeRunnable() {
            @Override
            public void run() {
                Minecraft mc = Minecraft.getInstance();

                if (mc.level == null)
                    return;

                //I know this is overkill but I'm scared
                if (!mc.level.isClientSide())
                    return;

                Entity source = mc.level.getEntity(pkt.source);

                if (source == null)
                    return;

                if(source instanceof Mob) {
                    Mob mob = (Mob) source;

                    AmbienceController.instance.target.updateStopTargeting(mob);
                }
            }
        };
    }

    public static DistExecutor.SafeRunnable handlePacketAmbienceTE(final PacketUpdateAmbienceTE pkt) {
        return new DistExecutor.SafeRunnable() {
            @Override
            public void run() {
                Minecraft mc = Minecraft.getInstance();

                if (mc.level == null)
                    return;

                if (!mc.level.isClientSide())
                    return;

                if (!mc.level.isLoaded(pkt.pos))
                    return;

                BlockEntity tile = mc.level.getBlockEntity(pkt.pos);

                if (tile == null)
                    return;

                if (!(tile instanceof AmbienceTileEntity))
                    return;

                AmbienceTileEntity finalTile = (AmbienceTileEntity) tile;
                finalTile.data = pkt.data;
                AmbienceController.instance.stopFromTile(finalTile);
            }
        };
    }

    public static DistExecutor.SafeRunnable handlePacketCompendium(PacketCompendium pkt) {
        return new DistExecutor.SafeRunnable() {
            @Override
            public void run() {
                Minecraft mc = Minecraft.getInstance();

                if (mc.level == null)
                    return;

                if (!mc.level.isClientSide())
                    return;

                AmbienceController.instance.compendium.clear();

                AmbienceController.instance.compendium.addAllEntries(pkt.entries);
            }
        };
    }
/*
    public static DistExecutor.SafeRunnable handlePacketAskForCompendium() {
        return new DistExecutor.SafeRunnable() {
            @Override
            public void run() {
                Minecraft mc = Minecraft.getInstance();

                if (mc.level == null)
                    return;

                if (!mc.level.isClientSide())
                    return;

                //mc.setScreen(new CompendiumGUI());
            }
        };
    }

    public static DistExecutor.SafeRunnable handlePacketItIsInStructure(PacketItIsInStructure pkt) {
        return new DistExecutor.SafeRunnable() {
            @Override
            public void run() {
                //Minecraft mc = Minecraft.getInstance();
                if(pkt.result)
                    AmbienceController.instance.structure.playerIsInStructure(pkt.structure, pkt.range, pkt.full);
                else
                    AmbienceController.instance.structure.playerIsntInStructure(pkt.structure, pkt.range, pkt.full);
            }
        };
    }*/
}

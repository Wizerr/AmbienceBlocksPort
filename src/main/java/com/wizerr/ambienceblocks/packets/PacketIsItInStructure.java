package com.wizerr.ambienceblocks.packets;

import com.mojang.datafixers.util.Pair;
import com.wizerr.ambienceblocks.util.PacketHandler;
import com.wizerr.ambienceblocks.util.StaticUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.function.Supplier;

public class PacketIsItInStructure {
    public String structure;
    public double range;
    public boolean full;

    private static final Logger LOGGER = LogManager.getLogger();

    public PacketIsItInStructure(String structure, double range, boolean full) {
        this.structure = structure;
        this.range = range;
        this.full = full;
    }

    public static PacketIsItInStructure decode(FriendlyByteBuf buf) {
        String structure = buf.readUtf(30);
        double range = buf.readDouble();
        boolean full = buf.readBoolean();
        return new PacketIsItInStructure(structure, range, full);
    }

    public static void encode(PacketIsItInStructure msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.structure);
        buf.writeDouble(msg.range);
        buf.writeBoolean(msg.full);
    }

    public static void handle(final PacketIsItInStructure pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_SERVER)) {
                ServerPlayer player = ctx.get().getSender();
                ServerLevel world = player.serverLevel();
                LOGGER.trace("Looking for structure: " + pkt.structure);

                Holder<Structure> structureHolder = getStructureHolder(world, pkt.structure);
                if (structureHolder == null) {
                    LOGGER.trace("Structure not found: " + pkt.structure);
                    return;
                }

                HolderSet<Structure> holderSet = HolderSet.direct(structureHolder);

                Pair<BlockPos, Holder<Structure>> pair = world.getChunkSource().getGenerator()
                        .findNearestMapStructure(world, holderSet, player.blockPosition(), 100, false);

                boolean isIn = false;

                BoundingBox playerBB = new BoundingBox(player.blockPosition());
                if (pkt.range != 0) {
                    playerBB = StaticUtil.growBoundingBox(playerBB, pkt.range);
                }

                if (pair != null && pair.getFirst() != null) {
                    LOGGER.trace("Nearest structure found at " + pair.getFirst());
                    BlockPos structurePos = pair.getFirst();
                    Holder<Structure> foundStructure = pair.getSecond();

                    LevelChunk chunk = world.getChunkAt(structurePos);
                    StructureStart structureStart = chunk.getStartForStructure(foundStructure.value());

                    if (structureStart != null) {
                        LOGGER.trace("Structure start found in chunk at " + structurePos);
                        if (pkt.full) {
                            if (structureStart.getBoundingBox().intersects(playerBB)) {
                                isIn = true;
                                LOGGER.trace("Player is fully inside the structure bounding box");
                            }
                        } else {
                            for (StructurePiece piece : structureStart.getPieces()) {
                                if (piece.getBoundingBox().intersects(playerBB)) {
                                    isIn = true;
                                    LOGGER.trace("Player is inside a structure piece bounding box");
                                    break;
                                }
                            }
                            if (!isIn) {
                                LOGGER.trace("Player is NOT inside any structure piece bounding box");
                            }
                        }
                    } else {
                        LOGGER.trace("No structure start found for this structure in the chunk");
                    }
                } else {
                    LOGGER.trace("No nearest structure found");
                }

                PacketHandler.NET.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                        new PacketItIsInStructure(pkt.structure, pkt.range, pkt.full, isIn));
            }
        });
        ctx.get().setPacketHandled(true);

    }

    private static TagKey<Structure> getStructureTag(String structure) {
        return TagKey.create(Registries.STRUCTURE, new ResourceLocation(structure));
    }

    private static Holder<Structure> getStructureHolder(ServerLevel world, String structureName) {
        ResourceLocation structureRL = new ResourceLocation(structureName);
        Registry<Structure> structureRegistry = world.registryAccess().registryOrThrow(Registries.STRUCTURE);
        Optional<Holder.Reference<Structure>> optionalHolder = structureRegistry.getHolder(ResourceKey.create(Registries.STRUCTURE, structureRL));
        return optionalHolder.orElse(null);
    }

}
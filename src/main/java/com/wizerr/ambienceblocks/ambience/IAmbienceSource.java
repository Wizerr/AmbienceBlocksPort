package com.wizerr.ambienceblocks.ambience;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public interface IAmbienceSource {
    AmbienceData getData();
    Vec3 getOrigin();

    default boolean isWithinBounds(Player player) {
        return getData().isWithinBounds(player, getOrigin());
    }
    default double distanceTo(Player player) {
        return getData().distanceFromCenter(player, getOrigin());
    }
    default double getPercentageHowCloseIsPlayer(Player player) {
        return getData().getPercentageHowCloseIsPlayer(player, getOrigin());
    }
}

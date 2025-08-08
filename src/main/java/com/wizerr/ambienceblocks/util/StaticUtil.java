package com.wizerr.ambienceblocks.util;

import com.wizerr.ambienceblocks.ambience.util.AmbienceEnumName;
import com.wizerr.ambienceblocks.ambience.util.AmbienceType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.ArrayList;

public class StaticUtil {
    public static final int LENGTH_SOUND = 100;
    public static final int LENGTH_COND_INPUT = 200;

    public static <T extends Enum<T>> T getEnumValue(int index, T[] values) {
        //index %= values.length;
        //return values[index >= 0 ? index : -index];
        return values[index >= 0 && index < values.length ? index : 0];
    }

    public static <T extends Enum<T>> T getEnumValueIncludingCustom(String name, T[] values) {
        for(T value : values) {
            if(value instanceof AmbienceEnumName) {
                if(((AmbienceEnumName) value).getName().equals(name))
                    return value;
            } else {
                if(value.name().equals(name))
                    return value;
            }
        }
        return values[0];
    }

    public static <T extends Enum<T>> T getEnumValue(String name, T[] values) {
        for(T value : values) {
            if(value.name().equals(name))
                return value;
        }
        return values[0];
    }

    public static ArrayList<String> getListOfAmbienceType() {
        ArrayList<String> list = new ArrayList<>();
        for(AmbienceType value : AmbienceType.values())
            list.add(value.getName());
        return list;
    }

    public static ArrayList<String> getListOfSoundCategories() {
        ArrayList<String> list = new ArrayList<>();
        for(SoundSource value : SoundSource.values())
            list.add(value.getName());
        return list;
    }
/*
    public static ArrayList<String> getListOfStructures() {
        ArrayList<String> list = new ArrayList<>();
        for (Map.Entry<ResourceKey<StructureType<?>>, StructureType<?>> entry : BuiltInRegistries.STRUCTURE_TYPE.entrySet()) {
            list.add(entry.getKey().location().toString());
        }
        return list;
    }
*/
    public static BoundingBox growBoundingBox(BoundingBox playerBB, double range) {
        return playerBB.inflatedBy((int) range);
    }
}

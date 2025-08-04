package com.wizerr.ambienceblocks.util;

import com.wizerr.ambienceblocks.ambience.util.AmbienceEnumName;
import com.wizerr.ambienceblocks.ambience.util.AmbienceType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.phys.AABB;
//import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.core.registries.BuiltInRegistries;
//import net.minecraft.core.registries.Registries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        ArrayList<String> list = new ArrayList<String>();
        for(AmbienceType value : AmbienceType.values())
            list.add(value.getName());
        return list;
    }

    public static ArrayList<String> getListOfSoundCategories() {
        ArrayList<String> list = new ArrayList<String>();
        for(SoundSource value : SoundSource.values())
            list.add(value.getName());
        return list;
    }

    public static ArrayList<String> getListOfStructureTypes() {
        ArrayList<String> list = new ArrayList<>();
        for (Map.Entry<ResourceKey<StructureType<?>>, StructureType<?>> entry : BuiltInRegistries.STRUCTURE_TYPE.entrySet()) {
            list.add(entry.getKey().location().toString());
        }
        return list;
    }

    public static AABB growBoundingBox(AABB playerBB, double range) {
        return playerBB.inflate(range/2);
        //AABB bb = new AABB(playerBB);
        //bb.setMaxX(bb.maxX + range/2);
        /*bb.maxX += range/2;
        bb.maxY += range/2;
        bb.maxZ += range/2;
        bb.minX -= range/2;
        bb.minY -= range/2;
        bb.minZ -= range/2;*/
        //return bb;
    }

    public static BoundingBox growBoundingBox(BoundingBox playerBB, double range) {
        return playerBB.inflatedBy((int) range);
    }
}

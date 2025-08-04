package com.wizerr.ambienceblocks.ambience.bounds;

import com.google.gson.JsonObject;
import com.wizerr.ambienceblocks.ambience.util.AmbienceAxis;
import com.wizerr.ambienceblocks.util.StaticUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class CylinderBounds extends AbstractBounds {
    public static final int id = 1;

    private double radius;
    private double length;
    private AmbienceAxis axis;

    public CylinderBounds(double radius, double length, AmbienceAxis axis) {
        this.radius = radius;
        this.length = length;
        this.axis = axis;
    }

    public CylinderBounds() {
        this.radius = 0;
        this.length = 0;
        this.axis = AmbienceAxis.Y;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public AmbienceAxis getAxis() {
        return axis;
    }

    public void setAxis(AmbienceAxis axis) {
        this.axis = axis;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String getName() {
        return "Cylinder";
    }

    @Override
    public boolean isWithinBounds(Player player, Vec3 origin) {
        double dist = distanceFromCenter(player, origin);

        double playerPos = getPlayerPosByAxis(player, axis);
        double originPos = getVec3dPosByAxis(origin, axis);
        return dist < radius && playerPos < originPos + length / 2D && playerPos > originPos - length / 2D;
    }

    //We can ignore y, we're only interested in the other two coordinates
    @Override
    public double distanceFromCenter(Player player, Vec3 origin) {
        Vec3 axisMask = axis.getIgnoreAxisMask();

        Vec3 vecPlayer = new Vec3(player.getX() * axisMask.x, player.getY() * axisMask.y, player.getZ() * axisMask.z);
        Vec3 vecTile = new Vec3(origin.x() * axisMask.x, origin.y() * axisMask.y - blockOffset.y, origin.z() * axisMask.z);

        return vecPlayer.distanceTo(vecTile);
    }

    @Override
    public double getPercentageHowCloseIsPlayer(Player player, Vec3 origin) {
        return (radius - distanceFromCenter(player, origin)) / radius;//distanceFromCenter(player, origin) / radius;
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag compound = new CompoundTag();

        compound.putDouble("radius", radius);
        compound.putDouble("length", length);
        compound.putInt("axis", axis.getId());

        return compound;
    }

    @Override
    public void fromNBT(CompoundTag compound) {
        this.radius = compound.getDouble("radius");
        this.length = compound.getDouble("length");
        this.axis = AmbienceAxis.getAxisFromInt(compound.getInt("axis"));
    }

    @Override
    public void toBuff(FriendlyByteBuf buf) {
        buf.writeDouble(this.radius);
        buf.writeDouble(this.length);
        buf.writeInt(this.axis.getId());
    }

    @Override
    public void fromBuff(FriendlyByteBuf buf) {
        this.radius = buf.readDouble();
        this.length = buf.readDouble();
        this.axis = AmbienceAxis.getAxisFromInt(buf.readInt());
    }

    @Override
    public void toJson(JsonObject json) {
        json.addProperty("radius", radius);
        json.addProperty("length", length);
        json.addProperty("axis", axis.name());
    }

    @Override
    public void fromJson(JsonObject json) {
        radius = json.get("radius").getAsDouble();
        length = json.get("length").getAsDouble();
        axis = StaticUtil.getEnumValue(json.get("axis").getAsString(), AmbienceAxis.values());
    }

    @Override
    public String toString() {
        return getName() + " : " +
                "radius=" + radius +
                ", length=" + length +
                '}';
    }
}

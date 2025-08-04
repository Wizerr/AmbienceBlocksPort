package com.wizerr.ambienceblocks.ambience.conds;

import com.google.gson.JsonObject;
import com.wizerr.ambienceblocks.ambience.IAmbienceSource;
import com.wizerr.ambienceblocks.ambience.util.AmbienceEquality;
import com.wizerr.ambienceblocks.ambience.util.messenger.AbstractAmbienceWidgetMessenger;
import com.wizerr.ambienceblocks.ambience.util.messenger.AmbienceWidgetEnum;
import com.wizerr.ambienceblocks.util.StaticUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class PlayerUnderwaterCond extends AbstractCond {
    private AmbienceEquality equal;

    private static final String EQUAL = "equal";

    public PlayerUnderwaterCond(AmbienceEquality equal) {
        this.equal = equal;
    }

    @Override
    public AbstractCond clone() {
        return new PlayerUnderwaterCond(equal);
    }

    @Override
    public String getName() {
        return "player.underwater";
    }

    @Override
    public String getListDescription() {
        return "[" + getName() + "] " + equal.getName();
    }

    @Override
    public boolean isTrue(Player player, Level worldIn, IAmbienceSource sourceIn) {
        return equal.testFor(player.isUnderWater());
    }

    @Override
    public List<AbstractAmbienceWidgetMessenger> getWidgets() {
        List<AbstractAmbienceWidgetMessenger> list = new ArrayList<>();
        list.add(new AmbienceWidgetEnum<>(EQUAL, "", 20, equal));
        return list;
    }

    @Override
    public void getDataFromWidgets(List<AbstractAmbienceWidgetMessenger> allWidgets) {
        for(AbstractAmbienceWidgetMessenger widget : allWidgets) {
            if(EQUAL.equals(widget.getKey()) && widget instanceof AmbienceWidgetEnum)
                equal = (AmbienceEquality) ((AmbienceWidgetEnum) widget).getValue();
        }
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(EQUAL, equal.ordinal());
        return nbt;
    }

    @Override
    public void fromNBT(CompoundTag nbt) {
        equal = StaticUtil.getEnumValue(nbt.getInt(EQUAL), AmbienceEquality.values());
    }

    @Override
    public void toBuff(FriendlyByteBuf buf) {
        buf.writeInt(equal.ordinal());
    }

    @Override
    public void fromBuff(FriendlyByteBuf buf) {
        this.equal = StaticUtil.getEnumValue(buf.readInt(), AmbienceEquality.values());
    }

    @Override
    public void toJson(JsonObject json) {
        json.addProperty(EQUAL, equal.name());
    }

    @Override
    public void fromJson(JsonObject json) {
        equal = StaticUtil.getEnumValue(json.get(EQUAL).getAsString(), AmbienceEquality.values());
    }
}

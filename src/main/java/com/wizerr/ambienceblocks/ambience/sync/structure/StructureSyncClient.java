package com.wizerr.ambienceblocks.ambience.sync.structure;

import com.wizerr.ambienceblocks.ambience.sync.Countdown;
import com.wizerr.ambienceblocks.config.AmbienceConfig;
import com.wizerr.ambienceblocks.packets.PacketIsItInStructure;
import com.wizerr.ambienceblocks.util.PacketHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class StructureSyncClient {
    public static StructureSyncClient instance;

    private final HashMap<StructureRequestType, Countdown> sentRequest = new HashMap<>(); // To not spam the server
    private final HashMap<StructureRequestType, Countdown> isInStructureTimer = new HashMap<>();

    //private static final Logger LOGGER = LogManager.getLogger();

    public StructureSyncClient() {
        instance = this;
    }

    public void tick() {
        //Only stops if the countdown reaches 0 or the entity is unloaded from the world (or killed)
        isInStructureTimer.entrySet().removeIf(entry -> entry.getValue().tick());
        sentRequest.entrySet().removeIf(entry -> entry.getValue().tick());
    }

    public boolean isPlayerInStructure(String structure, double range, boolean full) {
        StructureRequestType request = new StructureRequestType(structure, range, full);
        sendRequestForStructureInfo(request);
        return isInStructureTimer.containsKey(request);
    }

    private void sendRequestForStructureInfo(StructureRequestType request) {
        if(!sentRequest.containsKey(request)) {
            sentRequest.put(request, new Countdown(getRequestSentTickTime()));
            PacketHandler.NET.sendToServer(new PacketIsItInStructure(request.getStructure(), request.getRange(), request.isFull()));
        }
    }

    public void playerIsInStructure(String structure, double range, boolean full) {
        //LOGGER.debug("Player is in structure: {} (range: {}, full: {})", structure, range, full);
        StructureRequestType request = new StructureRequestType(structure, range, full);
        if(isInStructureTimer.containsKey(request))
            isInStructureTimer.get(request).setTime(getRequestApprovedTickTime());
        else
            isInStructureTimer.put(request, new Countdown(getRequestApprovedTickTime()));
    }

    public void playerIsntInStructure(String structure, double range, boolean full) {
        //LOGGER.debug("Player is NOT in structure: {} (range: {}, full: {})", structure, range, full);
        StructureRequestType request = new StructureRequestType(structure, range, full);
        isInStructureTimer.remove(request);
    }

    private int getRequestSentTickTime() {
        return AmbienceConfig.structureCountdownAmount;
    }

    private int getRequestApprovedTickTime() {
        return AmbienceConfig.structureCountdownAmount * 2;
    }

    public static class StructureRequestType {
        String structure;
        double range;
        boolean full;

        public StructureRequestType(String structure, double range, boolean full) {
            this.structure = structure;
            this.range = range;
            this.full = full;
        }

        public String getStructure() {
            return structure;
        }

        public double getRange() {
            return range;
        }

        public void setRange(double range) {
            this.range = range;
        }

        public boolean isFull() {
            return full;
        }

        public void setFull(boolean full) {
            this.full = full;
        }

        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof StructureRequestType))
                return false;

            StructureRequestType that = (StructureRequestType) obj;
            return this.getStructure().equals(that.getStructure()) && this.getRange() == that.getRange() && this.isFull() == that.isFull();
        }

        @Override
        public int hashCode() {
            int hashCode = 1;
            hashCode = 31 * hashCode + getStructure().hashCode();
            hashCode = 31 * hashCode + Double.hashCode(getRange());
            hashCode = 31 * hashCode + Boolean.hashCode(isFull());
            return hashCode;
        }
    }

}

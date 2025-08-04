package com.wizerr.ambienceblocks.client.ambience;

import com.wizerr.ambienceblocks.util.Unused;

//holds reference to a sound which can be grabbed later
@Unused(type=Unused.Type.REMOVE)
public class AmbienceReservation {
    public AmbienceSlot owner;
    public AmbienceInstance reserved;
    public String playingResource;

    public AmbienceReservation(String playingResource) {
        this.playingResource = playingResource;
    }

    public void play() {

        AmbienceController.instance.handler.play(reserved);
    }
}
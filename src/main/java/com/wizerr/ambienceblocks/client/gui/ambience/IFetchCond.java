package com.wizerr.ambienceblocks.client.gui.ambience;

import com.wizerr.ambienceblocks.ambience.conds.AbstractCond;

public interface IFetchCond {
    void fetch(AbstractCond newCond, AbstractCond oldCond);
}

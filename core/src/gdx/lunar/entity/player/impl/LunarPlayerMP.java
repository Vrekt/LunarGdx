package gdx.lunar.entity.player.impl;

import com.badlogic.ashley.core.Entity;
import gdx.lunar.entity.player.mp.LunarNetworkEntityPlayer;

/**
 * A default network player.
 */
public class LunarPlayerMP extends LunarNetworkEntityPlayer {

    public LunarPlayerMP(Entity entity, boolean initializeComponents) {
        super(entity, initializeComponents);
    }

    public LunarPlayerMP(boolean initializeComponents) {
        super(initializeComponents);
    }
}

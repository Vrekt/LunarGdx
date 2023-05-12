package lunar.shared.entity.player.impl;

import com.badlogic.ashley.core.Entity;
import lunar.shared.entity.player.mp.LunarNetworkEntityPlayer;

/**
 * Represents a default multiplayer (player) state
 */
public class NetworkPlayer extends LunarNetworkEntityPlayer {

    public NetworkPlayer(Entity entity, boolean initializeComponents) {
        super(entity, initializeComponents);
    }

    public NetworkPlayer(boolean initializeComponents) {
        super(initializeComponents);
    }
}

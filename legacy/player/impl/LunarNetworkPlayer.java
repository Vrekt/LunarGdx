package gdx.lunar.entity.player.impl;

import gdx.lunar.entity.drawing.Rotation;
import gdx.lunar.entity.player.LunarNetworkEntityPlayer;

/**
 * Represents a player over the network.
 */
public class LunarNetworkPlayer extends LunarNetworkEntityPlayer {

    public LunarNetworkPlayer(int entityId, float playerScale, float playerWidth, float playerHeight, Rotation rotation) {
        super(entityId, playerScale, playerWidth, playerHeight, rotation);
    }

    public LunarNetworkPlayer(float playerScale, float playerWidth, float playerHeight, Rotation rotation) {
        super(playerScale, playerWidth, playerHeight, rotation);
    }
}

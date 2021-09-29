package gdx.examples.advanced.entity;

import gdx.lunar.entity.drawing.Rotation;
import gdx.lunar.entity.player.impl.LunarPlayer;

/**
 * Here I could define some custom behaviour or just simply expand.
 */
public class Player extends LunarPlayer {

    public Player() {
        super((1 / 16.0f), 16f, 18f, Rotation.FACING_UP);
    }

}

package gdx.examples.advanced.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import gdx.lunar.entity.drawing.Rotation;
import gdx.lunar.entity.player.impl.LunarPlayer;

/**
 * Here I could define some custom behaviour or just simply expand.
 */
public class Player extends LunarPlayer {

    public Player() {
        super((1 / 16.0f), 16f, 18f, Rotation.FACING_UP);
    }

    @Override
    protected void pollInput() {
        super.pollInput();

        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            System.err.println("APPLYING FORCE");
            // test apply forces over the network.
            final Vector2 point = body.getWorldPoint(new Vector2(5.0f, -5));
            final float fx = body.getMass() * (getX() * 150);
            final float fy = body.getMass() * (getY() * 150);
            this.worldIn.applyForceToPlayerNetwork(this.connection, fx, fy, point.x, point.y, true);
        }
    }
}

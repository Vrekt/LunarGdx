package gdx.lunar.entity.player.impl;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import gdx.lunar.entity.player.LunarEntityPlayer;
import gdx.lunar.network.AbstractConnection;

/**
 * A default player with basic input and movement handling.
 */
public class LunarPlayer extends LunarEntityPlayer {

    public LunarPlayer(boolean initializeComponents) {
        super(initializeComponents);
    }

    public LunarPlayer(boolean initializeComponents, String name, AbstractConnection connection) {
        super(initializeComponents);
        setEntityName(name);
        setConnection(connection);
        setMoveSpeed(6.0f);
        setSize(1.0f, 1.0f);
    }

    public LunarPlayer(boolean initializeComponents, String name, AbstractConnection connection, float width, float height) {
        super(initializeComponents);
        setEntityName(name);
        setConnection(connection);
        setMoveSpeed(1.0f);
        setSize(width, height);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        pollInput();

        // update body vel
        if (body != null) body.setLinearVelocity(getVelocity());
    }

    public void render(SpriteBatch batch, float delta) {

    }

    /**
     * Poll the input
     */
    public void pollInput() {

    }
}

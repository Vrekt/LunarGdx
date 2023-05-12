package lunar.shared.entity.player.impl;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import gdx.lunar.network.AbstractConnection;
import lunar.shared.entity.player.LunarEntityPlayer;

/**
 * Represents a default player state
 */
public class Player extends LunarEntityPlayer {

    public Player(boolean initializeComponents) {
        super(initializeComponents);
    }

    public Player(boolean initializeComponents, String name, AbstractConnection connection) {
        super(initializeComponents);
        setEntityName(name);
        setConnection(connection);
    }

    public Player(boolean initializeComponents, String name, AbstractConnection connection, float width, float height) {
        super(initializeComponents);
        setEntityName(name);
        setConnection(connection);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        pollInput();

        // update body vel
        if (body != null)
            body.setLinearVelocity(getVelocity());
    }

    public void render(SpriteBatch batch, float delta) {

    }

    /**
     * Poll the input
     */
    public void pollInput() {

    }
}

package lunar.shared.entity.player.impl;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lunar.shared.entity.player.AbstractLunarEntityPlayer;

/**
 * Represents a default player state
 */
public class Player extends AbstractLunarEntityPlayer {

    public Player(boolean initializeComponents) {
        super(initializeComponents);
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

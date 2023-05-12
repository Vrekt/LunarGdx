package gdx.examples.basic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import gdx.lunar.world.LunarWorld;
import lunar.shared.entity.player.impl.Player;

/**
 * Represents a basic player.
 */
public final class DemoPlayer extends Player {

    public DemoPlayer(boolean initializeComponents, TextureRegion playerTexture) {
        super(initializeComponents);

        setMoveSpeed(6.0f);
        setHasMoved(true);
        setNetworkSendRatesInMs(10, 10);
        setIgnoreOtherPlayerCollision(true);

        // default player texture
        putRegion("player", playerTexture);

        // default player configuration
        setSize(16, 16, (1 / 16.0f));
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    @Override
    public void pollInput() {
        setVelocity(0.0f, 0.0f, false);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            rotation = 0f;
            setVelocity(0.0f, moveSpeed, false);
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            rotation = 1f;
            setVelocity(0.0f, -moveSpeed, false);
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            rotation = 2f;
            setVelocity(-moveSpeed, 0.0f, false);
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            rotation = 3f;
            setVelocity(moveSpeed, 0.0f, false);
        }
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        batch.draw(getRegion("player"), getInterpolated().x, getInterpolated().y, getWidthScaled(), getHeightScaled());
    }

    @Override
    public void spawnInWorld(LunarWorld world, Vector2 position) {
        super.spawnInWorld(world, position);
        body.setFixedRotation(true);
    }
}

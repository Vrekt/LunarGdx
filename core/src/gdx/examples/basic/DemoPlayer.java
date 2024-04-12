package gdx.examples.basic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import gdx.lunar.world.LunarWorld;
import lunar.shared.entity.player.impl.LunarPlayer;

/**
 * Represents a basic player.
 */
public final class DemoPlayer extends LunarPlayer {

    public DemoPlayer(boolean initializeComponents, TextureRegion playerTexture) {
        super(initializeComponents);

        setMoveSpeed(6.0f);
        disablePlayerCollision(true);
        setMoving(true);
        setNetworkSendRateInMs(10, 10);
        disablePlayerCollision(true);

        // default player texture
        addRegion("player", playerTexture);

        // default player configuration
        setSize(16, 16, (1 / 16.0f));
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    @Override
    public void pollInput() {
        setVelocity(0.0f, 0.0f);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            setAngle(0f);
            setVelocity(0.0f, -getMoveSpeed());
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            setAngle(1f);
            setVelocity(0.0f, getMoveSpeed());
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            setAngle(2f);
            setVelocity(getMoveSpeed(), 0.0f);
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            setAngle(3f);
            setVelocity(-getMoveSpeed(), 0.0f);
        }
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        batch.draw(getRegion("player"), getInterpolatedPosition().x, getInterpolatedPosition().y, getScaledWidth(), getScaledHeight());
    }

    @Override
    public void spawnInWorld(LunarWorld world, Vector2 position) {
        super.spawnInWorld(world, position);
        body.setFixedRotation(true);
    }
}

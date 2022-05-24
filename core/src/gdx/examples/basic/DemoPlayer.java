package gdx.examples.basic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lunar.shared.entity.player.impl.LunarPlayer;

/**
 * Represents a basic player.
 */
public final class DemoPlayer extends LunarPlayer {

    public DemoPlayer(boolean initializeComponents, TextureRegion playerTexture) {
        super(initializeComponents);

        setMoveSpeed(6.0f);
        setFixedRotation(false);
        setHasMoved(true);
        setNetworkSendRatesInMs(10, 10);

        // default player texture
        putRegion("player", playerTexture);

        // default player configuration
        setConfig(16, 16, (1 / 16.0f));
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
            setVelocity(0.0f, -moveSpeed, false);
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            rotation = 1f;
            setVelocity(0.0f, moveSpeed, false);
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            rotation = 2f;
            setVelocity(moveSpeed, 0.0f, false);
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            rotation = 3f;
            setVelocity(-moveSpeed, 0.0f, false);
        }

    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        batch.draw(getRegion("player"), getInterpolated().x, getInterpolated().y, getWidthScaled(), getHeightScaled());
    }
}

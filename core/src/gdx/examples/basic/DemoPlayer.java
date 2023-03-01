package gdx.examples.basic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import gdx.lunar.protocol.packet.client.CPacketEnterInstance;
import gdx.lunar.world.LunarWorld;
import lunar.shared.entity.LunarEntity;
import lunar.shared.player.LunarEntityPlayer;
import lunar.shared.player.impl.LunarPlayer;
import lunar.shared.player.mp.LunarNetworkEntityPlayer;

/**
 * Represents a basic player.
 */
public final class DemoPlayer extends LunarPlayer {

    private boolean instance = false;

    public DemoPlayer(boolean initializeComponents, TextureRegion playerTexture) {
        super(initializeComponents);

        setMoveSpeed(6.0f);
        setHasMoved(true);
        setNetworkSendRatesInMs(10, 10);
        setIgnorePlayerCollision(true);

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
        } else if (Gdx.input.isKeyPressed(Input.Keys.I) && !instance) {
            instance = true;
            getConnection().sendImmediately(new CPacketEnterInstance(22));
        }

    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        batch.draw(getRegion("player"), getInterpolated().x, getInterpolated().y, getWidthScaled(), getHeightScaled());
    }

    @Override
    public <P extends LunarEntityPlayer, N extends LunarNetworkEntityPlayer, E extends LunarEntity> void spawnEntityInWorld(LunarWorld<P, N, E> world, float x, float y) {
        super.spawnEntityInWorld(world, x, y);
        body.setFixedRotation(true);
    }
}

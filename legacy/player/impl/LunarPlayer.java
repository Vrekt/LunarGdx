package gdx.lunar.entity.player.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import gdx.lunar.entity.drawing.Rotation;
import gdx.lunar.entity.player.LunarEntityPlayer;
import gdx.lunar.network.AbstractConnection;
import gdx.lunar.protocol.packet.client.CPacketSetProperties;

/**
 * Represents a player within the lunar system.
 */
public class LunarPlayer extends LunarEntityPlayer {

    /**
     * The sending rates of position and velocity packets in milliseconds.
     */
    protected long positionSendRateMs = 150, velocitySendRateMs = 100;
    protected long lastVelocitySend, lastPositionSend;

    /**
     * The move speed
     */
    protected float moveSpeed = 6.5f;
    protected AbstractConnection connection;

    public LunarPlayer(int entityId, float playerScale, float playerWidth, float playerHeight, Rotation rotation) {
        super(entityId, playerScale, playerWidth, playerHeight, rotation);
    }

    public LunarPlayer(float playerScale, float playerWidth, float playerHeight, Rotation rotation) {
        super(playerScale, playerWidth, playerHeight, rotation);
    }

    public void setConnection(AbstractConnection connection) {
        this.connection = connection;
    }

    public AbstractConnection getConnection() {
        return connection;
    }

    public long getPositionSendRate() {
        return positionSendRateMs;
    }

    public void setPositionSendRate(long positionSendRateMs) {
        this.positionSendRateMs = positionSendRateMs;
    }

    public long getVelocitySendRate() {
        return velocitySendRateMs;
    }

    public void setVelocitySendRate(long velocitySendRateMs) {
        this.velocitySendRateMs = velocitySendRateMs;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    /**
     * override here to ensure we tell the server our username.
     *
     * @param name username
     */
    @Override
    public void setName(String name) {
        super.setName(name);

        if (connection != null) connection.send(new CPacketSetProperties(connection.alloc(), name));
    }

    @Override
    public void update(float delta) {
        pollInput();

        // update pos + vel
        body.setLinearVelocity(velocity.x, velocity.y);
        setPosition(body.getPosition().x, body.getPosition().y);

        // update rendering and then send their position and vel over the network.
        renderer.update(delta, rotation, !velocity.isZero());
        sendVelocityAndPosition();

        // reset velocity after updating to stop player from moving after input stopped.
        // TODO: You may want to override this behaviour.
        velocity.set(0f, 0f);
    }

    /**
     * Poll the input
     */
    protected void pollInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            velocity.set(-moveSpeed, 0f);
            rotation = Rotation.FACING_LEFT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velocity.set(moveSpeed, 0f);
            rotation = Rotation.FACING_RIGHT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            velocity.set(0f, moveSpeed);
            rotation = Rotation.FACING_UP;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            velocity.set(0f, -moveSpeed);
            rotation = Rotation.FACING_DOWN;
        }
    }

    /**
     * Move this player.
     */
    public void move(float x, float y, Rotation rotation) {
        velocity.set(x, y);
        this.rotation = rotation;
    }

    /**
     * Send this players position and velocity to others.
     */
    public void sendVelocityAndPosition() {
        if (connection == null) return;

        final long now = System.currentTimeMillis();
        if (now - lastVelocitySend >= velocitySendRateMs) {
            connection.sendPlayerVelocity(rotation, velocity.x, velocity.y);
            lastVelocitySend = now;
        }

        if (now - lastPositionSend >= positionSendRateMs) {
            connection.sendPlayerPosition(rotation, position.x, position.y);
            lastPositionSend = now;
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.connection != null) {
            connection.disconnect();
            connection.close();
        }
        this.connection = null;
    }
}

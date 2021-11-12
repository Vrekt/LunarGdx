package gdx.lunar.protocol.packet.client;

import gdx.lunar.protocol.handler.ClientPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

/**
 * Sent by clients to update their velocity
 */
public class CPacketVelocity extends Packet {

    public static final int PID = 884;

    private float velocityX, velocityY, rotation;

    public static void handle(ClientPacketHandler handler, ByteBuf buf) {
        handler.handlePlayerVelocity(new CPacketVelocity(buf));
    }

    public CPacketVelocity(float rotation, float velocityX, float velocityY) {
        this.rotation = rotation;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    private CPacketVelocity(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return x vel
     */
    public float getVelocityX() {
        return velocityX;
    }

    /**
     * @return y vel
     */
    public float getVelocityY() {
        return velocityY;
    }

    /**
     * @return the rotation value
     */
    public float getRotation() {
        return rotation;
    }

    @Override
    public int getId() {
        return PID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeFloat(rotation);
        buffer.writeFloat(velocityX);
        buffer.writeFloat(velocityY);
    }

    @Override
    public void decode() {
        rotation = buffer.readFloat();
        velocityX = buffer.readFloat();
        velocityY = buffer.readFloat();
    }
}

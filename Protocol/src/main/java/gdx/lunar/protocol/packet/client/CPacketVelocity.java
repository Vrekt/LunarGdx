package gdx.lunar.protocol.packet.client;

import gdx.lunar.protocol.handler.ClientPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * Sent by clients to update their velocity
 */
public class CPacketVelocity extends Packet {

    public static final int PID = 10;

    /**
     * Velocity
     */
    private float velocityX, velocityY;

    /**
     * Rotation
     */
    private int rotation;

    public static void handle(ClientPacketHandler handler, ByteBuf buf) {
        handler.handlePlayerVelocity(new CPacketVelocity(buf));
    }

    public CPacketVelocity(ByteBufAllocator allocator, float velocityX, float velocityY, int rotation) {
        super(allocator);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.rotation = rotation;
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
    public int getRotation() {
        return rotation;
    }

    @Override
    public int getId() {
        return PID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeFloat(velocityX);
        buffer.writeFloat(velocityY);
        buffer.writeInt(rotation);
    }

    @Override
    public void decode() {
        velocityX = buffer.readFloat();
        velocityY = buffer.readFloat();
        rotation = buffer.readInt();
    }
}

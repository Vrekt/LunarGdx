package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * Sent by the server to update a client velocity
 */
public class SPacketPlayerVelocity extends Packet {

    public static final int PID = 8;

    /**
     * The entity ID
     */
    private int entityId;

    /**
     * Velocity
     */
    private float velocityX, velocityY;

    /**
     * Rotation index
     */
    private int rotation;

    public static void handle(ServerPacketHandler handler, ByteBuf buf) {
        handler.handlePlayerVelocity(new SPacketPlayerVelocity(buf));
    }

    /**
     * Initialize
     *
     * @param entityId  the entity ID
     * @param velocityX velocity X
     * @param velocityY velocity Y
     * @param rotation  rotation index
     */
    public SPacketPlayerVelocity(ByteBufAllocator allocator, int entityId, float velocityX, float velocityY, int rotation) {
        super(allocator);
        this.entityId = entityId;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.rotation = rotation;
    }

    private SPacketPlayerVelocity(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return EID
     */
    public int getEntityId() {
        return entityId;
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
        buffer.writeInt(entityId);
        buffer.writeFloat(velocityX);
        buffer.writeFloat(velocityY);
        buffer.writeInt(rotation);
    }

    @Override
    public void decode() {
        entityId = buffer.readInt();
        velocityX = buffer.readFloat();
        velocityY = buffer.readFloat();
        rotation = buffer.readInt();
    }

}

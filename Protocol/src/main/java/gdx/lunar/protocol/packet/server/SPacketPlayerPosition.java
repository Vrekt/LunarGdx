package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * Sent by the server to update a players position
 */
public class SPacketPlayerPosition extends Packet {

    public static final int PID = 7;

    /**
     * EID
     * Rotation
     */
    private int entityId, rotation;

    /**
     * Position
     */
    private float x, y;

    public static void handle(ServerPacketHandler handler, ByteBuf buf) {
        handler.handlePlayerPosition(new SPacketPlayerPosition(buf));
    }

    /**
     * Initialize
     *
     * @param entityId the entity
     * @param rotation rotation
     * @param x        x
     * @param y        y
     */
    public SPacketPlayerPosition(ByteBufAllocator allocator, int entityId, int rotation, float x, float y) {
        super(allocator);
        this.entityId = entityId;
        this.rotation = rotation;
        this.x = x;
        this.y = y;
    }

    public SPacketPlayerPosition(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return EID
     */
    public int getEntityId() {
        return entityId;
    }

    /**
     * @return rotation index
     */
    public int getRotation() {
        return rotation;
    }

    /**
     * @return x
     */
    public float getX() {
        return x;
    }

    /**
     * @return y
     */
    public float getY() {
        return y;
    }

    @Override
    public int getId() {
        return PID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(entityId);
        buffer.writeInt(rotation);
        buffer.writeFloat(x);
        buffer.writeFloat(y);
    }

    @Override
    public void decode() {
        entityId = buffer.readInt();
        rotation = buffer.readInt();
        x = buffer.readFloat();
        y = buffer.readFloat();
    }
}

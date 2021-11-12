package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

/**
 * Sent by the server to update a players position
 */
public class SPacketPlayerPosition extends Packet {

    public static final int PID = 995;

    private int entityId;
    private float x, y, rotation;

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
    public SPacketPlayerPosition(int entityId, float rotation, float x, float y) {
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
    public float getRotation() {
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
        buffer.writeFloat(rotation);
        buffer.writeFloat(x);
        buffer.writeFloat(y);
    }

    @Override
    public void decode() {
        entityId = buffer.readInt();
        rotation = buffer.readFloat();
        x = buffer.readFloat();
        y = buffer.readFloat();
    }
}

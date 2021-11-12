package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

/**
 * Sent to indicate the client should spawn an entity.
 */
public class SPacketSpawnEntity extends Packet {
    public static final int PID = 999;

    private String entityName;
    private float x, y;
    private int temporaryEntityId, entityId;

    public static void handle(ServerPacketHandler handler, ByteBuf buf) {
        handler.handleSpawnEntity(new SPacketSpawnEntity(buf));
    }

    public SPacketSpawnEntity(String entityName,
                              float x,
                              float y,
                              int temporaryEntityId,
                              int entityId) {
        this.entityName = entityName;
        this.x = x;
        this.y = y;
        this.temporaryEntityId = temporaryEntityId;
        this.entityId = entityId;
    }

    public SPacketSpawnEntity(ByteBuf buffer) {
        super(buffer);
    }

    public String getEntityName() {
        return entityName;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getTemporaryEntityId() {
        return temporaryEntityId;
    }

    public int getEntityId() {
        return entityId;
    }

    @Override
    public int getId() {
        return PID;
    }

    @Override
    public void encode() {
        writeId();
        writeString(entityName);
        buffer.writeFloat(x);
        buffer.writeFloat(y);
        buffer.writeInt(temporaryEntityId);
        buffer.writeInt(entityId);
    }

    @Override
    public void decode() {
        this.entityName = readString();
        this.x = buffer.readFloat();
        this.y = buffer.readFloat();
        this.temporaryEntityId = buffer.readInt();
        this.entityId = buffer.readInt();
    }

}

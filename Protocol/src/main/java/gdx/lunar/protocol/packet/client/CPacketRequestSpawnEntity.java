package gdx.lunar.protocol.packet.client;

import gdx.lunar.protocol.handler.ClientPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

/**
 * Request to spawn an entity in the world
 */
public class CPacketRequestSpawnEntity extends Packet {

    public static final int PID = 16;

    private String entityName;
    private float x, y;
    private int temporaryEntityId;

    public static void handle(ClientPacketHandler handler, ByteBuf buf) {
        handler.handleRequestSpawnEntity(new CPacketRequestSpawnEntity(buf));
    }

    public CPacketRequestSpawnEntity(String entityName, float x, float y, int temporaryEntityId) {
        this.entityName = entityName;
        this.x = x;
        this.y = y;
        this.temporaryEntityId = temporaryEntityId;
    }

    public CPacketRequestSpawnEntity(ByteBuf buffer) {
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
    }

    @Override
    public void decode() {
        this.entityName = readString();
        this.x = buffer.readFloat();
        this.y = buffer.readFloat();
        this.temporaryEntityId = buffer.readInt();
    }
}

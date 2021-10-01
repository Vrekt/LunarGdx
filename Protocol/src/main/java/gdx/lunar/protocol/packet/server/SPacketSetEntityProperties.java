package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * Set properties of an entity.
 */
public class SPacketSetEntityProperties extends Packet {

    public static final int PID = 22;

    private int entityId;
    private String entityName;

    public static void handle(ServerPacketHandler handler, ByteBuf buf) {
        handler.handleSetEntityProperties(new SPacketSetEntityProperties(buf));
    }

    public SPacketSetEntityProperties(ByteBufAllocator allocator, int entityId, String entityName) {
        super(allocator);
        this.entityId = entityId;
        this.entityName = entityName;
    }

    public SPacketSetEntityProperties(ByteBuf buffer) {
        super(buffer);
    }

    public int getEntityId() {
        return entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    @Override
    public int getId() {
        return PID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(entityId);
        writeString(entityName);
    }

    @Override
    public void decode() {
        entityId = buffer.readInt();
        entityName = readString();
    }
}

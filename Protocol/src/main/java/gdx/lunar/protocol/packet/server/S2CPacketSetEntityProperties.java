package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handlers.ServerPacketHandler;
import gdx.lunar.protocol.packet.GamePacket;
import io.netty.buffer.ByteBuf;

/**
 * Indicates a change to an entity with in the world, either name change or entity ID change
 */
public class S2CPacketSetEntityProperties extends GamePacket {

    public static final int PACKET_ID = 1116;

    protected int entityId;
    protected String entityName;
    // indicates which was changed (could be both)
    protected boolean wasNameChanged, wasEntityIdChanged;

    public static void handle(ServerPacketHandler handler, ByteBuf buffer) {
        handler.handleSetEntityProperties(new S2CPacketSetEntityProperties(buffer));
    }

    public S2CPacketSetEntityProperties(int entityId, String entityName) {
        this.entityId = entityId;
        this.entityName = entityName;
    }

    public S2CPacketSetEntityProperties(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return this entity new (or the same) entity ID
     */
    public int getEntityId() {
        return entityId;
    }

    /**
     * @return this entity new (or the same) entity name
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * @return {@code true} if this entity had its ID changed
     */
    public boolean wasEntityIdChanged() {
        return wasEntityIdChanged;
    }

    /**
     * @return {@code true} if this entity had its name changed
     */
    public boolean wasNameChanged() {
        return wasNameChanged;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(entityId);
        writeString(entityName);
        buffer.writeBoolean(wasEntityIdChanged);
        buffer.writeBoolean(wasNameChanged);
    }

    @Override
    public void decode() {
        entityId = buffer.readInt();
        entityName = readString();
        wasEntityIdChanged = buffer.readBoolean();
        wasNameChanged = buffer.readBoolean();
    }
}

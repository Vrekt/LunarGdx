package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

/**
 * Sent by the server to remove a player client side
 */
public class SPacketRemovePlayer extends Packet {

    public static final int PID = 994;

    /**
     * entity ID
     */
    private int entityId;

    public static void handle(ServerPacketHandler handler, ByteBuf buf) {
        handler.handleRemovePlayer(new SPacketRemovePlayer(buf));
    }

    /**
     * Initialize
     *
     * @param entityId ID to remove
     */
    public SPacketRemovePlayer(int entityId) {
        this.entityId = entityId;
    }

    private SPacketRemovePlayer(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return the ID
     */
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
        buffer.writeInt(entityId);
    }

    @Override
    public void decode() {
        entityId = buffer.readInt();
    }
}

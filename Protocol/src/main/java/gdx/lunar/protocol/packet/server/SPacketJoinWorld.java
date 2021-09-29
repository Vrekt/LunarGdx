package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * A response to {@link gdx.lunar.protocol.packet.client.CPacketJoinWorld}
 */
public class SPacketJoinWorld extends Packet {

    /**
     * PID
     */
    public static final int PID = 12;

    private boolean isAllowed;
    private String notAllowedReason;
    private int entityId;

    public static void handle(ServerPacketHandler handler, ByteBuf buf) {
        handler.handleJoinWorld(new SPacketJoinWorld(buf));
    }

    public SPacketJoinWorld(ByteBufAllocator allocator, boolean isAllowed, String notAllowedReason, int entityId) {
        super(allocator);
        this.isAllowed = isAllowed;
        this.notAllowedReason = notAllowedReason;
        this.entityId = entityId;
    }

    private SPacketJoinWorld(ByteBuf buffer) {
        super(buffer);
    }

    public boolean isAllowed() {
        return isAllowed;
    }

    public String getNotAllowedReason() {
        return notAllowedReason;
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
        buffer.writeBoolean(isAllowed);
        if (notAllowedReason != null) {
            writeString(notAllowedReason);
        } else {
            buffer.writeInt(entityId);
        }
    }

    @Override
    public void decode() {
        isAllowed = buffer.readBoolean();
        if (!isAllowed) {
            notAllowedReason = readString();
        } else {
            entityId = buffer.readInt();
        }
    }
}

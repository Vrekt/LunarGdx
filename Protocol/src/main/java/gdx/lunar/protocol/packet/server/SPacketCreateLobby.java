package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * Sent in response to {@link gdx.lunar.protocol.packet.client.CPacketCreateLobby}
 */
public class SPacketCreateLobby extends Packet {
    public static final int PID = 20;

    private boolean isAllowed;
    private String notAllowedReason;
    private int entityId, lobbyId;

    public static void handle(ServerPacketHandler handler, ByteBuf buf) {
        handler.handleCreateLobby(new SPacketCreateLobby(buf));
    }

    public SPacketCreateLobby(ByteBufAllocator allocator, String notAllowedReason) {
        super(allocator);
        this.isAllowed = false;
        this.notAllowedReason = notAllowedReason;
    }

    public SPacketCreateLobby(ByteBufAllocator allocator, int entityId, int lobbyId) {
        super(allocator);
        this.isAllowed = true;
        this.entityId = entityId;
        this.lobbyId = lobbyId;
    }

    public SPacketCreateLobby(ByteBuf buffer) {
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

    public int getLobbyId() {
        return lobbyId;
    }

    @Override
    public int getId() {
        return 20;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeBoolean(isAllowed);
        if (!isAllowed) {
            writeString(notAllowedReason);
        } else {
            buffer.writeInt(lobbyId);
            buffer.writeInt(entityId);
        }
    }

    @Override
    public void decode() {
        isAllowed = buffer.readBoolean();
        if (!isAllowed) {
            notAllowedReason = readString();
        } else {
            lobbyId = buffer.readInt();
            entityId = buffer.readInt();
        }
    }
}

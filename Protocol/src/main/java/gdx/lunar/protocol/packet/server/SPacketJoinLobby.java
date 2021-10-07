package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * Indicate to join a lobby.
 */
public class SPacketJoinLobby extends Packet {

    public static final int PID = 24;

    private String lobbyName;
    private int lobbyId, entityId;

    public static void handle(ServerPacketHandler handler, ByteBuf buf) {
        handler.handleJoinLobby(new SPacketJoinLobby(buf));
    }

    public SPacketJoinLobby(ByteBufAllocator allocator, String lobbyName, int lobbyId, int entityId) {
        super(allocator);
        this.lobbyName = lobbyName;
        this.lobbyId = lobbyId;
        this.entityId = entityId;
    }

    public SPacketJoinLobby(ByteBufAllocator allocator, String lobbyName, int entityId) {
        super(allocator);
        this.lobbyName = lobbyName;
        this.entityId = entityId;
    }

    public SPacketJoinLobby(ByteBufAllocator allocator, int lobbyId, int entityId) {
        super(allocator);
        this.lobbyId = lobbyId;
        this.entityId = entityId;
    }

    public SPacketJoinLobby(ByteBuf buffer) {
        super(buffer);
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public int getLobbyId() {
        return lobbyId;
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
        buffer.writeInt(entityId);
        buffer.writeBoolean(lobbyName != null);
        if (lobbyName != null) writeString(lobbyName);
        buffer.writeInt(lobbyId);
    }

    @Override
    public void decode() {
        this.entityId = buffer.readInt();
        final boolean hasName = buffer.readBoolean();
        if (hasName) {
            this.lobbyName = readString();
        }
        this.lobbyId = buffer.readInt();
    }
}

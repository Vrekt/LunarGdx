package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

/**
 * Indicate to join a lobby.
 */
public class SPacketJoinLobby extends Packet {

    public static final int PID = 9914;

    protected String lobbyName;
    protected int lobbyId, entityId;

    public static void handle(ServerPacketHandler handler, ByteBuf buf) {
        handler.handleJoinLobby(new SPacketJoinLobby(buf));
    }

    public SPacketJoinLobby(String lobbyName, int lobbyId, int entityId) {
        this.lobbyName = lobbyName;
        this.lobbyId = lobbyId;
        this.entityId = entityId;
    }

    public SPacketJoinLobby(String lobbyName, int entityId) {
        this.lobbyName = lobbyName;
        this.entityId = entityId;
    }

    public SPacketJoinLobby(int lobbyId, int entityId) {
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

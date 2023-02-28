package gdx.lunar.protocol.packet.client;

import gdx.lunar.protocol.handler.ClientPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

/**
 * Clients request to join a lobby.
 */
public class CPacketJoinLobby extends Packet {

    public static final int PID = 8811;

    protected String lobbyName;
    protected int lobbyId;

    public static void handle(ClientPacketHandler handler, ByteBuf buf) {
        handler.handleJoinLobby(new CPacketJoinLobby(buf));
    }

    public CPacketJoinLobby(String lobbyName, int lobbyId) {
        this.lobbyName = lobbyName;
        this.lobbyId = lobbyId;
    }

    public CPacketJoinLobby(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public CPacketJoinLobby(int lobbyId) {
        this.lobbyId = lobbyId;
    }

    public CPacketJoinLobby(ByteBuf buffer) {
        super(buffer);
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public int getLobbyId() {
        return lobbyId;
    }

    @Override
    public int getId() {
        return PID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeBoolean(lobbyName != null);
        if (lobbyName != null) writeString(lobbyName);
        buffer.writeInt(lobbyId);
    }

    @Override
    public void decode() {
        final boolean hasName = buffer.readBoolean();
        if (hasName) {
            this.lobbyName = readString();
        }
        this.lobbyId = buffer.readInt();
    }
}

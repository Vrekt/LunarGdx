package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

/**
 * Server denies request to join a lobby.
 */
public class SPacketJoinLobbyDenied extends Packet {

    public static final int PID = 9913;

    private String reason;

    public static void handle(ServerPacketHandler handler, ByteBuf buf) {
        handler.handleJoinLobbyDenied(new SPacketJoinLobbyDenied(buf));
    }

    public SPacketJoinLobbyDenied(String reason) {
        this.reason = reason;
    }

    public SPacketJoinLobbyDenied(ByteBuf buffer) {
        super(buffer);
    }

    public String getReason() {
        return reason;
    }

    @Override
    public int getId() {
        return PID;
    }

    @Override
    public void encode() {
        writeId();
        writeString(reason);
    }

    @Override
    public void decode() {
        this.reason = readString();
    }
}

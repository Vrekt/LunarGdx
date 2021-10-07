package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * Server denies request to join a lobby.
 */
public class SPacketJoinLobbyDenied extends Packet {

    public static final int PID = 23;

    private String reason;

    public static void handle(ServerPacketHandler handler, ByteBuf buf) {
        handler.handleJoinLobbyDenied(new SPacketJoinLobbyDenied(buf));
    }

    public SPacketJoinLobbyDenied(ByteBufAllocator allocator, String reason) {
        super(allocator);
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

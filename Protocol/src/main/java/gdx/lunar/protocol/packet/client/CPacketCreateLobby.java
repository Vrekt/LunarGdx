package gdx.lunar.protocol.packet.client;

import gdx.lunar.protocol.handler.ClientPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

/**
 * A request to create a lobby.
 */
public class CPacketCreateLobby extends Packet {
    public static final int PID = 19;

    public static void handle(ClientPacketHandler handler, ByteBuf buf) {
        handler.handleCreateLobby(new CPacketCreateLobby(buf));
    }

    public CPacketCreateLobby() {
    }

    public CPacketCreateLobby(ByteBuf buffer) {
        super(buffer);
    }

    @Override
    public int getId() {
        return PID;
    }

    @Override
    public void encode() {
        writeId();
    }
}

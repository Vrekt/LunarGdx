package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handlers.ServerPacketHandler;
import gdx.lunar.protocol.packet.GamePacket;
import io.netty.buffer.ByteBuf;

/**
 * Sent from the server to indicate a disconnect for whatever reason.
 */
public class S2CPacketDisconnected extends GamePacket {

    public static final int PACKET_ID = 1112;

    // the reason the server disconnected the client
    protected String disconnectReason;

    public static void handle(ServerPacketHandler handler, ByteBuf buffer) {
        handler.handleDisconnect(new S2CPacketDisconnected(buffer));
    }

    public S2CPacketDisconnected(String disconnectReason) {
        this.disconnectReason = disconnectReason;
    }

    public S2CPacketDisconnected(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return the disconnect reason as a string
     */
    public String getDisconnectReason() {
        return disconnectReason;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();
        writeString(disconnectReason);
    }

    @Override
    public void decode() {
        disconnectReason = readString();
    }
}

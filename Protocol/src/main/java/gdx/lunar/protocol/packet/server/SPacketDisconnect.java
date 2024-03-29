package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

/**
 * Sent by the server to disconnect a player.
 */
public class SPacketDisconnect extends Packet {

    public static final int PID = 991;

    /**
     * The reason for the disconnect
     */
    protected String reason;

    public static void handle(ServerPacketHandler handler, ByteBuf buf) {
        handler.handleDisconnect(new SPacketDisconnect(buf));
    }

    public SPacketDisconnect(String reason) {
        this.reason = reason;
    }

    private SPacketDisconnect(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return the reason
     */
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
        reason = readString();
    }

}

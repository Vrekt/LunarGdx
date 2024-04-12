package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handlers.ServerPacketHandler;
import gdx.lunar.protocol.packet.GamePacket;
import io.netty.buffer.ByteBuf;

/**
 * Ping packet sent from the server to the client in response to a ping
 */
public class S2CPacketPing extends GamePacket {

    public static final int PACKET_ID = 1113;

    // current client time in ms, current server time in ms.
    protected long clientTime, serverTime;

    public static void handle(ServerPacketHandler handler, ByteBuf buffer) {
        handler.handlePing(new S2CPacketPing(buffer));
    }

    public S2CPacketPing(long clientTime, long serverTime) {
        this.clientTime = clientTime;
        this.serverTime = serverTime;
    }

    public S2CPacketPing(ByteBuf buffer) {
        super(buffer);
    }

    public long getClientTime() {
        return clientTime;
    }

    public long getServerTime() {
        return serverTime;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeLong(clientTime);
        buffer.writeLong(serverTime);
    }

    @Override
    public void decode() {
        clientTime = buffer.readLong();
        serverTime = buffer.readLong();
    }
}

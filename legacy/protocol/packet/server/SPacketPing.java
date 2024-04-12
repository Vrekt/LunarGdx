package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

public class SPacketPing extends Packet {

    public static final int PID = 9918;

    protected long clientTime, serverTime;

    public static void handle(ServerPacketHandler handler, ByteBuf buf) {
        handler.handlePing(new SPacketPing(buf));
    }

    public SPacketPing(long clientTime, long serverTime) {
        this.clientTime = clientTime;
        this.serverTime = serverTime;
    }

    private SPacketPing(ByteBuf buffer) {
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
        return PID;
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

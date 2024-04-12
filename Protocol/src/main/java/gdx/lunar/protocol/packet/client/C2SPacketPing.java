package gdx.lunar.protocol.packet.client;

import gdx.lunar.protocol.handlers.ClientPacketHandler;
import gdx.lunar.protocol.packet.GamePacket;
import io.netty.buffer.ByteBuf;

/**
 * Ping request from the client to -> server
 */
public class C2SPacketPing extends GamePacket {

    public static final int PACKET_ID = 2223;
    // the current time of the client
    protected long currentTime;

    public static void handle(ClientPacketHandler handler, ByteBuf buffer) {
        handler.handlePing(new C2SPacketPing(buffer));
    }

    public C2SPacketPing(ByteBuf buffer) {
        super(buffer);
    }

    public C2SPacketPing(long currentTime) {
        this.currentTime = currentTime;
    }

    public long getTime() {
        return currentTime;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeLong(currentTime);
    }

    @Override
    public void decode() {
        currentTime = buffer.readLong();
    }
}

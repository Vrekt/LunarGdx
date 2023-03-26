package gdx.lunar.protocol.packet.client;

import gdx.lunar.protocol.handler.ClientPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

/**
 * Basic ping time packet
 */
public class CPacketPing extends Packet {

    public static final int PID = 8813;
    protected long time;

    public static void handle(ClientPacketHandler handler, ByteBuf buf) {
        handler.handlePing(new CPacketPing(buf));
    }

    public CPacketPing(ByteBuf buffer) {
        super(buffer);
    }

    public CPacketPing(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    @Override
    public int getId() {
        return PID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeLong(time);
    }

    @Override
    public void decode() {
        time = buffer.readLong();
    }
}

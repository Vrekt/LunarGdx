package gdx.lunar.protocol.packet.client;

import gdx.lunar.protocol.handler.ClientPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

/**
 * Set player properties like their username or anything else.
 */
public class CPacketSetProperties extends Packet {

    public static final int PID = 889;

    protected String username;

    public static void handle(ClientPacketHandler handler, ByteBuf buf) {
        handler.handleSetProperties(new CPacketSetProperties(buf));
    }

    public CPacketSetProperties(String username) {
        this.username = username;
    }

    public CPacketSetProperties(ByteBuf buffer) {
        super(buffer);
    }

    public String getUsername() {
        return username;
    }

    @Override
    public int getId() {
        return PID;
    }

    @Override
    public void encode() {
        writeId();
        writeString(username);
    }

    @Override
    public void decode() {
        this.username = readString();
    }
}

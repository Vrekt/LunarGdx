package gdx.lunar.protocol.packet.client;

import gdx.lunar.protocol.handlers.ClientPacketHandler;
import gdx.lunar.protocol.packet.GamePacket;
import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;

/**
 * Inform the server the client has disconnected.
 */
public class C2SPacketDisconnected extends GamePacket {

    public static final int PACKET_ID = 2222;

    // given reason, if any.
    protected String givenReason;

    public static void handle(ClientPacketHandler handler, ByteBuf buffer) {
        handler.handleDisconnected(new C2SPacketDisconnected(buffer));
    }

    /**
     * @param givenReason the reason or {@code null} if not specified.
     */
    public C2SPacketDisconnected(String givenReason) {
        this.givenReason = givenReason == null ? StringUtil.EMPTY_STRING : givenReason;
    }

    public C2SPacketDisconnected(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return the given reason, or {@code ""} if none.
     */
    public String getGivenReason() {
        return givenReason;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();
        writeString(givenReason);
    }

    @Override
    public void decode() {
        givenReason = readString();
    }
}

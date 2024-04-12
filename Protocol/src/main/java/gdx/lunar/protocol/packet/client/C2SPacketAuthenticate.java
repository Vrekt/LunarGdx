package gdx.lunar.protocol.packet.client;

import gdx.lunar.protocol.handlers.ClientPacketHandler;
import gdx.lunar.protocol.packet.GamePacket;
import io.netty.buffer.ByteBuf;

/**
 * Attempt to authenticate with a server
 */
public class C2SPacketAuthenticate extends GamePacket {

    public static final int PACKET_ID = 2221;

    protected String gameVersion;
    protected int protocolVersion;

    public static void handle(ClientPacketHandler handler, ByteBuf buffer) {
        handler.handleAuthentication(new C2SPacketAuthenticate(buffer));
    }

    public C2SPacketAuthenticate(String gameVersion, int protocolVersion) {
        this.gameVersion = gameVersion;
        this.protocolVersion = protocolVersion;
    }

    private C2SPacketAuthenticate(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return the client version
     */
    public String getGameVersion() {
        return gameVersion;
    }

    /**
     * @return the protocol version
     */
    public int getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();
        writeString(gameVersion);
        buffer.writeInt(protocolVersion);
    }

    @Override
    public void decode() {
        gameVersion = readString();
        protocolVersion = buffer.readInt();
    }
}

package gdx.lunar.protocol.packet.client;

import gdx.lunar.protocol.handler.ClientPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

/**
 * A basic authentication packet.
 */
public class CPacketAuthentication extends Packet {

    public static final int PID = 881;

    protected String gameVersion;
    protected int protocolVersion;

    public static void handle(ClientPacketHandler handler, ByteBuf buf) {
        handler.handleAuthentication(new CPacketAuthentication(buf));
    }

    /**
     * Initialize
     *
     * @param gameVersion     the game version
     * @param protocolVersion the protocol version
     */
    public CPacketAuthentication(String gameVersion, int protocolVersion) {
        this.gameVersion = gameVersion;
        this.protocolVersion = protocolVersion;
    }

    private CPacketAuthentication(ByteBuf buffer) {
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
        return PID;
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

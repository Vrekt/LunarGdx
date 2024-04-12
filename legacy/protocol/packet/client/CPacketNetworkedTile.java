package gdx.lunar.protocol.packet.client;

import gdx.lunar.protocol.handler.ClientPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

/**
 * A packet wrapper around a networked tile
 */
public class CPacketNetworkedTile extends Packet {

    public static final int PID = 26;

    protected float x, y;
    protected boolean isNetworked;
    protected String tileTexture;

    public static void handle(ClientPacketHandler handler, ByteBuf buf) {
        handler.handleNetworkTile(new CPacketNetworkedTile(buf));
    }

    public CPacketNetworkedTile(float x, float y, boolean isNetworked, String tileTexture) {
        this.x = x;
        this.y = y;
        this.isNetworked = isNetworked;
        this.tileTexture = tileTexture;
    }

    public CPacketNetworkedTile(ByteBuf buffer) {
        super(buffer);
    }

    @Override
    public int getId() {
        return PID;
    }
}

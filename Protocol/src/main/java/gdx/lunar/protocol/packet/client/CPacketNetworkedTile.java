package gdx.lunar.protocol.packet.client;

import gdx.lunar.protocol.handler.ClientPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * A packet wrapper around a networked tile
 */
public class CPacketNetworkedTile extends Packet {

    public static final int PID = 26;

    private float x, y;
    private boolean isNetworked;
    private String tileTexture;

    public static void handle(ClientPacketHandler handler, ByteBuf buf) {
        handler.handleNetworkTile(new CPacketNetworkedTile(buf));
    }

    public CPacketNetworkedTile(ByteBufAllocator allocator, float x, float y, boolean isNetworked, String tileTexture) {
        super(allocator);
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

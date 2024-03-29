package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

/**
 * Sent by the server to create a player client side
 */
public class SPacketCreatePlayer extends Packet {

    public static final int PID = 993;

    protected String username;
    protected int entityId;
    protected float x, y;

    public static void handle(ServerPacketHandler handler, ByteBuf buf) {
        handler.handleCreatePlayer(new SPacketCreatePlayer(buf));
    }

    /**
     * Initialize
     *
     * @param username username
     * @param entityId ID
     * @param x        starting X
     * @param y        starting Y
     */
    public SPacketCreatePlayer(String username, int entityId, float x, float y) {
        if (username == null) {
            System.err.println("WARNING: username in SPacketCreatePlayer is NULL, this will cause decoding issues.");
        }
        this.username = username;
        this.entityId = entityId;
        this.x = x;
        this.y = y;
    }

    private SPacketCreatePlayer(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return ID
     */
    public int getEntityId() {
        return entityId;
    }

    /**
     * @return X
     */
    public float getX() {
        return x;
    }

    /**
     * @return Y
     */
    public float getY() {
        return y;
    }

    @Override
    public int getId() {
        return PID;
    }

    @Override
    public void encode() {
        writeId();
        writeString(username);
        buffer.writeInt(entityId);
        buffer.writeFloat(x);
        buffer.writeFloat(y);
    }

    @Override
    public void decode() {
        username = readString();
        entityId = buffer.readInt();
        x = buffer.readFloat();
        y = buffer.readFloat();
    }
}

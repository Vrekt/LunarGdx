package gdx.lunar.protocol.packet.server;

import com.badlogic.gdx.math.Vector2;
import gdx.lunar.protocol.handlers.ServerPacketHandler;
import gdx.lunar.protocol.packet.GamePacket;
import io.netty.buffer.ByteBuf;

/**
 * Update clients on a players position
 */
public class S2CPacketPlayerPosition extends GamePacket {

    public static final int PACKET_ID = 1119;

    protected int entityId;
    protected float x, y, rotation;

    public static void handle(ServerPacketHandler handler, ByteBuf buffer) {
        handler.handlePlayerPosition(new S2CPacketPlayerPosition(buffer));
    }

    public S2CPacketPlayerPosition(int entityId, float rotation, float x, float y) {
        this.entityId = entityId;
        this.rotation = rotation;
        this.x = x;
        this.y = y;
    }

    public S2CPacketPlayerPosition(int entityId, float rotation, Vector2 position) {
        this(entityId, rotation, position.x, position.y);
    }

    public S2CPacketPlayerPosition(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return EID
     */
    public int getEntityId() {
        return entityId;
    }

    /**
     * @return rotation index
     */
    public float getRotation() {
        return rotation;
    }

    /**
     * @return x
     */
    public float getX() {
        return x;
    }

    /**
     * @return y
     */
    public float getY() {
        return y;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(entityId);
        buffer.writeFloat(rotation);
        buffer.writeFloat(x);
        buffer.writeFloat(y);
    }

    @Override
    public void decode() {
        entityId = buffer.readInt();
        rotation = buffer.readFloat();
        x = buffer.readFloat();
        y = buffer.readFloat();
    }
}

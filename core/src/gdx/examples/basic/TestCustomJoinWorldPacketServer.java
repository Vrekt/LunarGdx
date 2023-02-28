package gdx.examples.basic;

import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.server.SPacketJoinWorld;
import io.netty.buffer.ByteBuf;

public class TestCustomJoinWorldPacketServer extends SPacketJoinWorld {

    protected boolean testField;

    public static void handle(ServerPacketHandler handler, ByteBuf buf) {
        handler.handleJoinWorld(new TestCustomJoinWorldPacketServer(buf));
    }

    public TestCustomJoinWorldPacketServer(String worldName, int entityId) {
        super(worldName, entityId);
    }

    public TestCustomJoinWorldPacketServer(ByteBuf buffer) {
        super(buffer);
    }

    public void setTestField(boolean testField) {
        this.testField = testField;
    }

    public boolean isTestField() {
        return testField;
    }

    @Override
    public void encode() {
        super.encode();
        buffer.writeBoolean(testField);
    }

    @Override
    public void decode() {
        worldName = readString();
        entityId = buffer.readInt();
        testField = buffer.readBoolean();
    }

}

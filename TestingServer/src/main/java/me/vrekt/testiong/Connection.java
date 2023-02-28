package me.vrekt.testiong;

import gdx.examples.basic.TestCustomJoinWorldPacket;
import gdx.examples.basic.TestCustomJoinWorldPacketServer;
import gdx.lunar.protocol.packet.client.CPacketJoinWorld;
import gdx.lunar.protocol.packet.server.SPacketWorldInvalid;
import gdx.lunar.server.entity.LunarServerPlayerEntity;
import gdx.lunar.server.game.LunarServer;
import gdx.lunar.server.network.connection.ServerPlayerConnection;
import gdx.lunar.server.world.ServerWorld;
import io.netty.channel.Channel;

public class Connection extends ServerPlayerConnection {

    public Connection(Channel channel, LunarServer server) {
        super(channel, server);
    }

    @Override
    public void handleJoinWorld(CPacketJoinWorld packet) {
        handleJoinWorld((TestCustomJoinWorldPacket) packet);
    }

    public void handleJoinWorld(TestCustomJoinWorldPacket packet) {
        System.err.println("Test field is " + packet.isTestField());
        if (packet.getUsername() == null) {
            this.sendImmediately(new SPacketWorldInvalid(packet.getWorldName(), "Invalid username."));
            return;
        } else if (!server.getWorldManager().worldExists(packet.getWorldName())) {
            this.sendImmediately(new SPacketWorldInvalid(packet.getWorldName(), "World does not exist."));
            return;
        }

        final ServerWorld world = server.getWorldManager().getWorld(packet.getWorldName());
        if (world.isFull()) {
            return;
        }

        this.player = new LunarServerPlayerEntity(true, server, this);
        this.player.setEntityName(packet.getUsername());
        this.player.setWorldIn(world);
        this.player.setEntityId(world.assignEntityId());

        final TestCustomJoinWorldPacketServer server1 = new TestCustomJoinWorldPacketServer(packet.getWorldName(), player.getEntityId());
        server1.setTestField(packet.isTestField());
        sendImmediately(server1);
    }

}

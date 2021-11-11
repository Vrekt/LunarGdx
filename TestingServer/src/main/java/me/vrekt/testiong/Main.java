package me.vrekt.testiong;

import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.server.GameServer;
import gdx.lunar.server.NettyServer;
import gdx.lunar.server.world.impl.LunarWorldAdapter;

public class Main {

    public static void main(String[] args) {

        final LunarProtocol protocol = new LunarProtocol(true);
        final GameServer gameServer = new GameServer(protocol, "1.0");

        final NettyServer server = new NettyServer("localhost", 6969, protocol, gameServer);
        server.bind();

        gameServer.getWorldManager().addWorld("Athena", new LunarWorldAdapter("Athena", 1000, 1000, 1000, 100));
        gameServer.getWorldManager().getWorld("Athena").setSpawnLocation(64.25f, 8.887496f);
        gameServer.getWorldManager().addWorld("I", new LunarWorldAdapter("I", 1000, 1000, 1000, 100));
        gameServer.getWorldManager().getWorld("I").setSpawnLocation(8, 2.3f);
        gameServer.start();

    }

}

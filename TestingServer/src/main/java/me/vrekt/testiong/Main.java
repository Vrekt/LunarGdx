package me.vrekt.testiong;

import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.server.LunarGameServer;
import gdx.lunar.server.LunarNettyServer;
import gdx.lunar.server.world.impl.LunarWorldAdapter;

public class Main {

    public static void main(String[] args) {

        final LunarProtocol protocol = new LunarProtocol(true);
        final LunarNettyServer server = new LunarNettyServer("localhost", 6969, protocol);
        server.bind();

        final LunarGameServer gameServer = new LunarGameServer();
        gameServer.getWorldManager().addWorld("Athena", new LunarWorldAdapter("Athena", 1000, 1000, 1000, 100));
        gameServer.getWorldManager().getWorld("Athena").setSpawnLocation(64.25f, 8.887496f);
        gameServer.getWorldManager().addWorld("I", new LunarWorldAdapter("I", 1000, 1000, 1000, 100));
        gameServer.getWorldManager().getWorld("I").setSpawnLocation(8, 2.3f);
        gameServer.start();

    }

}

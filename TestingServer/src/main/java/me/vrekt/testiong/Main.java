package me.vrekt.testiong;

import gdx.lunar.server.LunarGameServer;
import gdx.lunar.server.LunarNettyServer;
import gdx.lunar.server.configuration.LunarServerConfiguration;
import gdx.lunar.server.world.impl.LunarWorldAdapter;

public class Main {

    public static void main(String[] args) {
        final LunarNettyServer server = new LunarNettyServer("localhost", 6969);
        server.bind().join();


        final LunarGameServer s = new LunarGameServer();
        s.getWorldManager().addWorld("Athena", new LunarWorldAdapter());
        s.start();

        final LunarServerConfiguration configuration = s.getConfiguration();
        System.err.println(configuration.maxWorldsPerThread);

    }

}

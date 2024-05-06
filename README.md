# Lunar

## What is Lunar?
Lunar is a library for developing multiplayer games with LibGDX. In it's current state its more appropriate for small scale multiplayer games or prototypes.

## Headline features
- Ashley and Box2d support
- Fully customizable entity system
- Networked players and entities with interpolation
- A-lot of boilerplate written for you
- Expandable protocol

### Beyond the headline features
- Support for texture/sprite entities
- Expandable networked worlds
- Multiple entity types for players, network players, etc
- A comprehensive server library
- + more


### A quick peek inside
*provided without any context of course, but you get the idea.*

```java
final MyPlayer player = ...;
world.spawnPlayerInWorld(player, 0.0f, 0.0f);
```

```java
final PlayerConnectionHandler connection = clientServer.getConnection();
connection.registerPacket(MyCustomPacket.ID, MyCustomPacket::new, packet -> handleMyPacket(packet));
```

```java
// override default behaviour
connection.registerHandlerSync(S2CPacketJoinWorld.PACKET_ID, packet -> world.handleWorldJoin((S2CPacketJoinWorld) packet));
```

```java
// an example of a basic player with many customizable configuration options
public final class DemoPlayer extends LunarPlayer {

    public DemoPlayer(boolean initializeComponents, TextureRegion playerTexture) {
        super(initializeComponents);

        setMoveSpeed(6.0f);
        disablePlayerCollision(true);
        setNetworkSendRateInMs(10, 10);
        // default player texture
        addRegion("player", playerTexture);
        // default player configuration
        setSize(16, 16, (1 / 16.0f));
    }
}
```
```java
// Create a networked world for others to join us.
// By default the world will handle physics, player updates and network updates!
world = new MultiplayerGameWorld(player, new World(Vector2.Zero, true), myGameInstance);
// add default world systems
world.addWorldSystems();
// ignore player collisions
world.addDefaultPlayerCollisionListener();
// Spawn our player in the world.
player.spawnEntityInWorld(world, mySpawnX, mySpawnY);
```

```java
// connect to remote server.
final LunarClientServer server = new LunarClientServer(myProtocol, "localhost", 6969);
server.connect();

// get our connection
final PlayerConnection connection = (PlayerConnection) server.getConnection();
// join a remote server world
connection.joinWorld("MyWorld", player.getName());
```

```java
// provide our own implementation for player connections
server.setConnectionProvider(channel -> new MyPlayerConnectionHandler(channel, protocol));
```

## Documentation and Examples
The wiki includes most things you will need to get started. Not all methods are described, I encourage to explore the source of a few key components like `AbstractLunarEntity` and `AbstractGameWorld` as this is where most fundamentals reside.

## Future Plans
*things that should have been implemented forever ago*
- Instances: basically just interiors and other things within a world you enter, like a dungeon for example.
- Encryption and better authentication
- And in general, expanding upon this library

# Using Lunar
**Lunar uses Java 21.**

You can find releases in the releases section. Both client and server rely on the Protocol dependency.

A netty dependency is also required:
```java
// or whatever version
implementation group: 'io.netty', name: 'netty-all', version: '4.1.100.Final'
```

# Lunar

#### What is Lunar?

Lunar is a networking library for LibGDX. With Lunar you can quickly create multiplayer game prototypes or full multiplayer games, quickly and efficiently! Lunar provides many common utilities such as entities, worlds, protocol, and box2d support!

**Lunar is still in very early development, expect many, many changes.**

#### Features
- Networked box2d worlds..
- Networked player moving and velocity.
- Networked player creation/removing.
- Ashley entity system
- A expandable protocol with SSL support.
- Very customizable and extendable.

### Get A Taste
```java
// apply a knock-back force to ourselves or another network player
player.applyForce(x, y, 1.0f, 1.0f, true);
```

```java
// spawn a new entity in the world
world.spawnEntityInWorld(new MyEntity(), x, y);
```

```java
// register a unique custom packet.
connection.registerPacket(99, MyCustomPacket::new, packet -> handleEntityPropertiesPacket(packet));
// Override default handlers
connection.registerHandlerSync(ConnectionOption.HANDLE_JOIN_WORLD, packet -> handle(packet));
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
server.setConnectionProvider(channel -> new PlayerConnectionHandler(channel, protocol));

// override default server packet handlers
protocol.changeDefaultServerPacketHandlerFor(SPacketJoinWorld.PID, (buf, handler) -> doSomething(buf, handler));
```

Want to jump in? Check out the [Quick Start Guide](https://github.com/Vrekt/LunarGdx/wiki/Quick-Start-Guide)

## Documentation and Examples

[Examples](https://github.com/Vrekt/LunarGdx/tree/main/core/src/gdx/examples)

[Wiki](https://github.com/Vrekt/LunarGdx/wiki)

# Planned Features
- Networked collision
- Networked textures, tiles, maps
- Better protocol security [#9](https://github.com/Vrekt/LunarGdx/issues/9)
- 'Instances' within worlds for interiors, rooms, dungeons, etc. Allows all the features as a normal world.
- ...

# Using Lunar
You can find releases in the releases section. Both client and server rely on the Protocol dependency.

You must also add a dependency for netty-all.
##### Gradle 6.7.2
```java
implementation group: 'io.netty', name: 'netty-all', version: '4.1.48.Final'
```

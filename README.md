# Tank Trouble clone

![](https://cdn.discordapp.com/attachments/365201384015921152/752924734806818956/unknown.png)

Java implemented, socket based multiplayer, topdown game made as a side project.

The game runs on port `1331`. <br/> Possible running client & server application with both flags.

#### Current features

-[x] Player Connecting | Moving | Disconnecting
-[x] Multithreaded Server (Each client has its own thread)
-[x] Command line arguments
-[x] Client-side raycasting

```bash
    --name <name>       : Set player name
    --ip <server_ip>    : Set server ip
    -c | --client       : Run client?
    -s | --server       : Run server?
```

#### Features TODO

-[ ] SpriteSheet for tanks
-[ ] Bullet shooting
-[ ] Client multi-threading
-[ ] Synchronized death
-[ ] Synchronized powerup spawning
-[ ] Packet security??
-[ ] Terrain generation options:
    - Generic maze generator
    - Procedural generation?
    - Cellular automata => Marching squares
 
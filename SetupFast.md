# Fast Setup

> Okay, I've downloaded the plugin. Now what?

## Step 1: Get the map

There are two ways to do this:

### Option 1: World file (easier, but this deletes your current world)

1. [Click here to download the world file.](https://github.com/greatericontop/GreatImpostor/raw/refs/heads/main/greatimpostor-world.zip)
2. Delete your current world folder if you have one. (If there are builds there you want to keep, back it up first!)
3. Put it in your server's root folder (the same folder as your `server.jar`) and unzip it. It should create the folder `world`.

### Option 2: WorldEdit schematic (keeps your current world but doesn't have maps preinstalled)

1. [Click here to download the schematic file.](https://github.com/greatericontop/GreatImpostor/raw/refs/heads/main/greatimpostor-schematic.schem)
2. Put it in `plugins/FastAsyncWorldEdit/schematics/` or `plugins/WorldEdit/schematics/`
3. Use the command `/schem load greatimpostor-schematic` to load the schematic.
4. Teleport to these **exact** coordinates (activate fly first so you don't fall): `/tp @s 9712.5 200.0 9231.5`
5. Use the command `//paste` to paste the schematic.

*Note: both pre-built maps were made in 1.21.4, but you can probably load them in earlier versions.*

## Step 2: Install the config file

1. Make sure you've started the server at least once with the plugin so that the folder `plugins/GreatImpostor` exists.
2. Stop the server if it's running.
3. [Click here to download the prewritten config file.](https://github.com/greatericontop/GreatImpostor/raw/refs/heads/main/prewritten-config-yml-here/config.yml)
4. Delete the default config file (`plugins/GreatImpostor/config.yml`) and replace it with the one you just downloaded.

---

That's it! You can now start the server and start a game.

# Custom Setup

This guide is aimed at those who want to make their own custom map.
This guide assumes that you've already built the map itself and are now trying to make it work with GreatImpostor.

> *If any of this is confusing or unclear, please feel free to open an issue and ask for help!*

## Signs

Signs (specifically oak signs) need to be tagged; otherwise clicking on them does nothing.
To do this, run the command `/impostor debug setSign <TAG>` while looking at a sign.
Once tagged, you can put any text/color/glow ink on a sign.
The plugin only looks for the tag.

`TAG` can be:
- `@emergency` - Clicking this sign will start an emergency meeting.
- `@sabotage=<NAME>` - Clicking this sign opens the menu for the task to fix a certain sabotage.
  - `@sabotage=REACTOR` - Reactor meltdown fix (same tag for both signs)
  - `@sabotage=OXYGEN_IN_OXYGEN` - Oxygen sabotage fix in Oxygen
  - `@sabotage=OXYGEN_IN_ADMIN` - Oxygen sabotage fix in Admin (these two are different!)
  - `@sabotage=LIGHTS` - Fix lights sabotage fix
  - `@sabotage=COMMUNICATIONS` - Comms sabotage fix
- `@securitycameras` - Clicking this sign will put the player into viewing security cameras.
- A task: see [`SubTask.java`](https://github.com/greatericontop/GreatImpostor/blob/main/src/main/java/io/github/greatericontop/greatimpostor/task/Subtask.java) (lines 29-81) for the list. Examples: `ACCEPT_POWER_OXYGEN`, `START_REACTOR`, `UNLOCK_MANIFOLDS`

## Starting Location

The starting location is in `config.yml` under `starting-location`.
It is where players are teleported to at the beginning of the game and after a meeting.
It should look something like this:

```yml
starting-location:
  world-name: world
  x: 9482.5
  y: 139.0
  z: 9451.5
```

Change the `world-name` to the name of your world (it's probably one of `world`, `world_nether`, or `world_the_end`, and set the coordinates.

## Vents

In the default map, vents are marked with trapdoors.
This isn't actually necessary, although you do want to mark the vent locations in some way so impostors know where they are.
Vents are actually fully defined in `config.yml` under `vents`.

It looks like this:

```yml
vents:
# First vent system
- - - 9453.5  # First vent in first vent system
    - 139.2
    - 9477.5
  - - 9451.5  # Second vent in first vent system
    - 139.2
    - 9464.5
  - - 9446.5  # Third vent in first vent system
    - 139.2
    - 9478.5
# Second vent system
- - - 9498.5
    - 139.2
    - 9451.5
  - - 9489.5
    - 139.2
    - 9481.5
  - - 9510.5
    - 139.2
    - 9472.5
```

This looks quite scary but isn't actually that complicated.
The `vents` section is a list of vent systems.
Each vent system has a list of vents, and each vent is its XYZ coordinates.
In this case, the first vent system has three vents: `9453.5, 139.2, 9477.5`, `9451.5, 139.2, 9464.5`, and `9446.5, 139.2, 9478.5`.

Whenever an impostor is standing near any of these coordinates and they press shift, they will be put into the vent.
When they press space to change vents, they'll go to the next vent in the same system.
When they exit the vent, they will exit the vent at those coordinates.

*Also note the decimals. The `.5` on the XZ is important because that makes you spawn in the middle of the block, and the `.2` on the Y teleports you on top of the trapdoor instead of inside it.*

## Security Cameras

Security cameras are likewise mostly managed by `config.yml` under `cameras`.
The only other part is the sign that you place in the world and tag `@securitycameras`.
It looks like this:

```yml
cameras:
- - 9430.5
  - 139.5
  - 9470.5
- - 9456.5
  - 139.5
  - 9447.5
- - 9482.5
  - 139.5
  - 9473.5
- - 9516.5
  - 139.5
  - 9472.5
```

Think of this as a single vent system but for cameras.
When a player clicks the sign, they'll be put into spectator and teleported to the first location, then the second, and so on.
Again, note the decimals.

## Sabotage Fix Coordinates

The sabotage fix coordinates are in `config.yml` under `sabotage-fix-coordinates`.
They're used for drawing the particles that point players toward the fix location.
It looks like this:

```yml
sabotage-fix-coordinates:
  reactor:
  - 9418.5
  - 9461.5
  oxygen-in-oxygen:
  - 9504.5
  - 9461.5
  oxygen-in-admin:
  - 9503.5
  - 9472.5
```

There are 3 of them: `reactor`, `oxygen-in-oxygen`, and `oxygen-in-admin`.
(There's only one location for reactor even though there are two reactor signs; just point it to reactor.
Make sure to have different coordinates for the two oxygen locations though, and don't mix them up.
This lets players figure out which panel they still need to fix if 1/2 are done.)

---

> That's it! That's how to build a custom map!

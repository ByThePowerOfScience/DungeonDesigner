# Overall Design

## 

## Actors

Trigger Block:
- When a player is inside a trigger, its connected Trigger Blocks emit redstone power.
- When it receives redstone power, it stops checking the trigger.
  - This also includes soft redstone power so a flag block on top of it or beside it will stop it from checking, since this is a common idiom.
  - So this block needs output and input faces

Flags:
- Flag Reader: While its flag is True, it emits redstone power.
- Flag Setter: When it receives redstone power, its flag is set to True.
- Flag Resetter: When it receives redstone power, its flag is set back to False.
  - This is its own block because un-setting flags is not a good idea.
  - However, it'll be tremendously useful in dev.

Fight Block:
- When it receives redstone power, it spawns the mobs it has configured at the positions configured.
- When its mobs are defeated, it emits redstone power.
- If the player dies, all fight blocks stop emitting redstone power.
  - Should there be a "fight start" block and a "fight end" block so we can do adaptive music and also restart the whole fight?


# Phase 1: Builder

In this "phase", done in a singleplayer world, the blocks are all mutable and configurable.

Flags
- Each flag's status is stored in the world data, since we can probably expect them not to make more than one dungeon in a world.
- Configuration: 
  - The individual flags are _items_ (Flag Variable), with its name being the name of the flag.
  - Insert a FVar into the block to set it to track that flag
- Action:
  - Flag Readers check the world data every tick for the flag's status
  - Flag Setter sets the flag in the world data to True
  - Flag Resetter sets the flag in the world data to False

Trigger Block
- Configuration:
  - Triggers are items to define bounding boxes
  - Trigger items are inserted into Trigger Blocks to bind them to that trigger
- Action:
  - Every tick, the block itself checks its trigger for players.

Fight Block
- Configuration:
  - Pipette item: load a spawn egg into it, bind to a position, and put it in a chest next to the fight block

Dungeon Nexus
- I can't think of anything for this to do in the build phase
- Chest next to it will hold Rooms
- To build the dungeon, use a Dungeon Planner or something to define the bounding box of the dungeon, then shift-rclick on the Nexus to build it




# Phase 2: Compiled

This is after the whole thing has been built in a singleplayer world and exported to a file.

None of the blocks are configurable, and only a handful even have tile entities.

Trigger Block: No internal state or ticking
- The Nexus stores its trigger and checks it.
  - When a player is in the trigger, the Nexus sets its BlockState to POWERED

Flags:
- Flag Reader: No internal state or ticking
  - Nexus sets its blockstate on flag change
- Flag Setter/Resetter:
  - Tile Entity:
    - Saves the position of the Nexus
  - Behavior:
    - When it receives redstone power, updates the flag on the Nexus
    - The Nexus will then notify the listening Flag Readers


Fight Block:
- State:
  - Set of { Mob NBT : SpawnPos }
    - Saved to NBT 
  - List of Mob UUIDs, the UUID of the mobs that have already been spawned
    - NOT saved to NBT
- Behavior:
  - When it receives redstone power, it spawns the entities
  - Every tick, check if its mobs are still alive
    - If any are dead, subtract them from the list
    - If list is empty, set BlockState to POWERED=true
- Nexus-Triggered Behavior:
  - On reset: 
    - Remove its spawned entities from the world so they don't double-spawn
    - Clear the list of spawned entities
    - Set BlockState to POWERED=false

## Dungeon Nexus

This pretty much handles the entire logic for the dungeon.




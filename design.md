# Mechanics

## Triggers
- Just AABBs that check if a player is in them every tick
- Also need to keep track of if a player has already activated a trigger

## Design

- What if, instead of having these complex blocks that hold items and everything, we just made each trigger be its own block?
- When the trigger is activated, the block emits a redstone signal.
  - Same with the fight controller: when the fight is completed, have the block _itself_ emit a redstone signal

Everything was going to be redstone enabled anyway so people could use command blocks and activate doors.
So what if instead of having a high-level "complicated block stores state and activates a linked redstone torch", we
just stick to the "in-world" thing and have everything be a block that itself emits redstone?

One thing though: each trigger should absolutely be an _item_, and then the item is put into a block that handles the trigger.
This is so redstone wire and the trigger blocks don't have to be completely ripped out if someone decides to change the bounds of a trigger.
- Also should include some kind of rudimentary wireless redstone for ease of use

When a player dies inside a dungeon, they keep their inventory and respawn at the dungeon entrance.
Same with when they log out: when they log back in, they appear at the dungeon entrance, with any fights they were in reset to their start.
- Oof, speaking of, I'll need to make an "ongoing transaction"-type thing so I can reset the trigger(s) that started the fight too
- Either that or have the "has completed fight" thing be a FLAG that they could then make disable the trigger

## Redstone Utilities

### Wireless Redstone
Receiver and transmitter. Bind them to each other, set them to an ID, and have them go through the world data to find and update their counterpart

### Simple Gates

- NOT
- AND
- OR is kinda implicit with how redstone works, but it might be easier for people to think about if it has its own block
- Diode that when it gets a side input no longer permits output (like a locked repeater, but doesn't persist "ON" states)
- Basic wire that doesn't lose signal strength over distance?



# Blocks

## Dungeon Nexus
1. Makes the dungeon able to be moved
   - Positions are made relative to the dungeon nexus when the dungeon is built
   - Allows the dungeon to be serialized to some kind of NBT
2. Tracks if the dungeon has been completed and if we should stop checking triggers
3. The only thing that checks the triggers, for performance reasons 
   - Trigger tracking algorithm that ends early if the condition is satisfied
     1. Massive dungeon trigger, to see if any player is inside the dungeon at all
     2. Then check which room of the dungeon the player is in
     3. Then check if the player is in any triggers in the room
4. Holds the spawn point of the dungeon
   - Players respawn at the start of the dungeon instead of at home, and keep inventory as well

Should keep itself chunkloaded as long as a player is within the bounds of the dungeon.  
Builders should always place them at the dungeon entrance so it's always one of the first things rendered when someone logs back in.

World data will have to hold the AABB for the dungeon to make sure the player is sent to the right place when respawning or logging in.

## Room Controller
- Holds:
  - Bounds of the room
  - Triggers in the room
- Item inventory:
  - Should it be a GUI or just a chest next to it? Probably the latter for simplicity?

## Fight controller
- Holds:
  - List of spawn waves
- Spawn Waves
    - Each wave has enemies
        - Position, nbt, type
            - Spawn the enemy of that type at the position with the given NBT
            - Load a spawn egg into an item, and rclick it on a position
            - Use a command to cycle waves
    - When the wave is defeated, the next wave starts
        - if there are no more waves remaining, activate Result

## Trigger Block
- Holds:
  - A trigger item
- Behavior:
  - Emits a redstone signal if a player is inside its trigger
  - If it receives a redstone signal, stops it from checking the trigger

## Flag Block
- Holds:
  - A Flag item
- Behavior:
  - Emits a redstone signal if its flag is true

## Flag Setter
- Holds:
  - A Flag item
- Behavior:
  - When it receives a redstone signal on the ON side, sets the flag to True
  - When it receives a redstone signal on the OFF side, sets the flag to False



# Items

## Variables

### Trigger Brush
- Draws a trigger
- Holds:
  - First corner pos
  - Second corner pos
- Saves that trigger to an item?
  - Just a way to hold the NBT in a tangible form
- Maybe the item that stores it should be the brush
  - Like a "variable" from Integrated Dynamics combined with a WE wand
- Behavior:
  - Right-click on block: save first pos
  - Shift-rclick on block: save second pos

### Flag
- Placeholder item that can be named



## Wrenches

### Room Binder
Behavior:
- Shift-rclick on a Room Block to bind it to the room
- Rclick on corners to draw a giant trigger
    - Flag Blocks inside the trigger are bound to the flag state of the Room Block
    - Trigger blocks' triggers are registered to the Room

### Dungeon Binder
Behavior:
- Shift-rclick on a dungeon nexus to bind it to the "dungeon"
- Rclick on corners to draw a giant trigger
  - Flag Blocks inside the trigger are bound to the flag state of the Nexus
  - Gives the Nexus knowledge of all of the Room Blocks and their associated triggers

# Recipes

Named Flag + unnamed Flag = 2 Named Flag
Configured Trigger + unconfigured Trigger = 2 Configured Trigger
Configured Trigger = unconfigured Trigger
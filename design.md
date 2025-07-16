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


Something that'll be _very_ common when building is using a flag to disable a trigger permanently (such as "trigger to start a fight, then the 'completed fight' flag is set, then after completing it the trigger is disabled permanently"), so we might want to build that in.
Plus it'll keep us from checking the triggers constantly


either that or honestly like the fight block could emit a reds


# Story
As in "Agile" stories.  Here's how a player uses the mod:

Start:
1. They place a Dungeon Nexus block somewhere.
2. They run `/dungeondesigner start` while facing the Nexus to bind it to their player. Now everything from DDL they place will be bound to that Nexus.

Triggers:
1. Player holds a TriggerVar item, and marks out an area.
2. Player places a TriggerBlock and puts that TriggerVar inside.
3. Player wires redstone coming from the TriggerBlock to an iron door.
4. They step inside the trigger area and the TriggerBlock powers, opening the iron door.
5. They set up some more triggers and more redstone.
6. Player holds a RoomVar and marks out the bounding box of a "room".
7. Player inserts the RoomVar into a chest next to the Nexus.
8. Player runs the `/dungeondesigner build <filename>` command.

Flags:
1. Player names a FlagVar item and gets a stack of them. // (Should there be a special item to automate that part?)
2. Player places a FlagBlock.
3. Player puts one of the stack of FlagVars into the FlagBlock.
4. Player places redstone coming from an output face of the FlagBlock.
5. Player places another FlagBlock somewhere else.
6. Player puts one from the same stack of FlagVars into the FlagBlock.
7. Player places redstone leading into the input face of that FlagBlock.
8. Player powers the redstone leading into the FlagBlock.
9. That FlagBlock emits redstone power, and immediately the redstone coming from the other FlagBlock becomes powered as well.
10. Player unpowers the input FlagBlock. Both FlagBlocks stay powered.
11. Player powers the RESET face of one of the connected FlagBlocks.
12. Both FlagBlocks unpower.

Building and Serialization:
1. The Nexus checks every TriggerBlock that had been placed.
2. Each held TriggerVar's AABB is converted to use a relative offset from the Nexus' position.
3. Each TrigerBlock's world position is converted to a relative offset.
4. Triggers are saved to a map of `{ AABB: [...triggerblocks it will power] }`.
5. Rooms' AABBs are saved to a list.
6. FlagBlocks' flags are deduplicated and saved to a map of: `{ FlagName : [...flagblocks] }`
6. The whole structure is saved to `<filename>.nbt`. (hm TODO figure out how to save and load structures like this)
    - Save the dungeon logic blocks as a non-interactive "production" blockstate:
      - Dungeon Nexus has a special tile entity that unpacks the NBT

Deserialization and prod usage:
1. Dimension reserves a place for the structure to be placed, and the structure's blocks generate like vanilla.
2. Dungeon Nexus is placed as the 
3. 
2. For each Trigger AABB:
    - If the TriggerBB is at all inside any Room, 

While holding a TriggerVar, the bounding box should be visible in-world.


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
        - if there are no more waves remaining, emit redstone signal from Result face
- Has a comparator output while a fight is in progress, so you can disable other triggers in the room while it's happening


## Trigger Block
- Holds:
  - A trigger item
- Behavior:
  - Emits a redstone signal if a player is inside its trigger
  - If it receives a redstone signal, stops it from checking the trigger


Possibility: comparator output for "number of players currently inside the trigger"


## Flag Block
- Holds:
  - A Flag item
- Behavior:
  - Emits a constant redstone signal from output side if its flag is true
  - Has an input side to set the flag to "true"
  - Has a reset side to set the flag to "false"




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

Question: since we need to know the bounds of the room already for trigger performance, should we just
1. Keep a list of all triggers
2. When a room bounding box is defined, check every trigger bounding box to see if it's inside that room
3. If it is, make it so the room will invoke it


I'm genuinely questioning if there should even _be_ room controllers,
or if everything should be bound to the dungeon nexus,
with all rooms' bounding boxes being stored with the nexus 
instead of arbitrary room blocks that don't actually do anything.

Then you have a _player_ set a Nexus as their active nexus, and all of their tools automatically bind with the nexus?
Or even just have a Room variable item that draws the room bounding box, then stick it in a chest next to the dungeon nexus or something

The rooms really don't do anything except make sure the triggers inside it aren't queried until the room is entered.
They don't need to be blocks.

Also, there's nothing dynamic in any blocks.
This is purely an in-world builder: once it's turned into NBT, 
everything will be invoked with relative block positions from the Nexus.

So really, the only thing that needs to have any state _is_ the Nexus.  
The Nexus will:
- Catalogue trigger AABBs by room zone for performance
- Map flag changes to the blocks that need to be set to "powered"
- Map trigger AABBs to the trigger holders that need to be set to "powered"

Also the Fight Controller block needs to have state.  It'll be activated by redstone, after all.

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
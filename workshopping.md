Credit each version and addon in the research entry

Composition with effect objects even in the single-item wands instead of hardcoding the effects. Will easily allow transposing wands to wand foci.
(Actually, can't even do that since items don't have entities attached... unless we inject a field into ItemStack but like that's dumb)

# Mechanics
- Chunk Vis and Flux
  - The more aura nodes in a chunk, the faster vis regenerates in the chunk
- Aura Nodes
  - Generate throughout the world
  - Are generated with COMPOUND ASPECTS of a certain strength
    - If not widened with a Node Stabilizer, will only provide primal aspects. Think of it as expanding a pinprick of a black hole: instead of mulching everything that goes through into energy, now it can let atoms (compound aspects) through.
  - If flux gets too high in the chunk and surpasses an aura node's strength, it consumes all flux and turns into a tiny flux rift
    - Pried-open nodes have a 0.25x modifier applied to their strength.
  - Can be moved with crystal clusters like TC2 in early-game

# Roleplay Mechanics

## Quest System

A short quest log.

Really only reserved for "not the research minigame" progression, like when you need to scan something specific to unlock a research or go somewhere or do a dungeon quest

## Dialogue
Conversations with important NPCs, specifically while on quests.

Mechanics:
* Dialogue boxes for their speech.
* Oblivion/FNV-style response options. Maybe just one or two that don't really affect the dialogue, but help the player engage with the conversation more.
* Summary of the conversation should be recorded in the quest log

Implementation Notes:
- Exiting the game or leaving the server while in a conversation should have the conversation able to be restarted when they join back in.
  - Essentially, don't save any "had conversation" flags persistently until the conversation has been completed.
- Conversation should be activated by interacting with an entity, but it should be governed by a wholly separate system.
  - If the entity dies or despawns while the conversation is active, it _should_ keep playing the conversation as though nothing had happened.
  - Likewise, if it's a quest where you need to "talk to an entity, get something from them, come back, and talk to them again", it should activate on _any type_ of that entity.
- Make the player invincible while conversation is active.
  - I'm imagining a player talking to a villager and getting slowly pushed away by a zombie lmao

## Cutscenes
- Dark Souls-style boss entry cutscene on first entering arena
- Final boss defeat cutscene
- Need to check how Replay Mod or that animation program does their "moving camera while your character plays actions in the world" thing


## Dungeons

_Not_ procedurally-generated: single structures like a zelda map.

Located in their own dimension. 

Everything is warded so players can't break or interact with anything.
- Possibility: make some debris and like cracked walls breakable so people can still explore and find secrets in there?



# Blocks
- Screw it we're bringing Infused Crops back, you can't stop me :D
- Celestial Gateway (TTKami)
  - Pearls to connect locations
- Ranged Item Collector
- Arcane Lamp
- Warded Arcana

# Constructs

- Mob spawner multiblock?


# Tools

- I WANT A HANDY BAG FROM ENDER UTILS! IT'S SO USEFUL FOR MODPACKS!!!
  - kind of OP for normal play though... maybe not.
  - or maybe lock it to postgame, with satchels only for earlier-game
- Dolly
- Satchel(s)



# Automation

- Dynamism Tablet (Thaumic Tinkerer)
- Actually Additions: Phantomfaces of all kinds
- Redcrystal (with channels? Will _this_ be YARM??? (hint: no))
- Vis Reader
  - Emits redstone signal based on level of chunk aura

## Item Transport
- Item Mirror for sending and receiving items
- Item Grate



# Wand Foci

## Utility

- Ender Chest
- Crafting Focus (Automagy)
  - Upgrades: 
    - Call it with a keybind
    - Add internal inventory (or even link to an inventory like Evilcraft?)
- Portable Hole
  - Also has a consumable form with a longer distance
- Feather Fall
  - Gives the slow fall potion effect for a few seconds
- Blink
  - Should be postgame
- Dodge
  - Shoots you off to one side
  - Should be midgame, unlocked after first dungeon. Balance combat around it.

## Combat

OOH actually yeah taking notes from COTL spells is a great idea

Spell Inspo:
- COTL
- Skyrim (and its spell mods)

All projectiles should be programmed like this:
1. Client sends server an "I want to make this projectile with this position and direction" packet.
2. Server creates a "proxy" entity representing that projectile.
3. Server tells all clients in the same world a projectile has been created. 
4. When client receives packet, they each render and simulate that projectile on their end starting at when they receive it.
5. Hit detection is done on the HIT PLAYER'S END.
6. Target's client sends the server an "I've been hit" packet when the locally-simulated projectile hits them.
7. Server tells the "proxy" entity to deal the damage to the player, so that the server understands.

So instead of making a server-only entity passed to the client, projectiles are their own thing done client-side.  
This is to give dodger's advantage in PVP, since vanilla projectile netcode is _awful_.

Only the slow high-damage projectiles should be like that; everything else should be as close to hitscan as possible.



The "small projectile" attacks (e.g. firebolt) should use the wand-waving "slash slash slash slash" style of casting (like Carian Bolt), around 120 BPM
- They should home in on the target you're closest to looking at.
  - This means we don't even have to deal with projectile netcode, it's just hitscan with delayed effect.
- Able to be blocked by a shield spell

Damage and defense values based on wand focus tier and upgrades

### Fire:
- Flamethrower
  - Basic spell: low damage but very low vis cost
  - Pierces enemies, has an active hitbox essentially
  - Short range, ~7-10 blocks
  - Countered by just being out of range
- Firebolt
  - Fast-ish long-range attack that homes in on enemies
    - 120 bpm, "slash slash slash" waving casting
  - Countered by shield spell
- Lob bouncing fireballs
  - High-ish vis cost, ~2 second cooldown
  - Upgrade possibility: Leave lava trail where it bounces
  - Mainly for locking down opponents and dealing massive damage, but can't be spammed without running out of mana
  - Countered by dodging
- AoE: Lava pool (not actual lava blocks, but like that SMO tomato pool)


### Frost
- Stream
  - Slows down target (not vanilla, no FOV change)
  - Longer effect the longer they're hit like pyro
- Spike wave like COTL
- Large flying ice spear
  - Like a big icicle flying horizontally across the ground
  - Upgrade: leaves ground below slippery
- Snowfall AoE


### Shock
- Lightning stream
- Chain lightning
- AoE: Lightning storm


### Mass Effect
Because the biotics in ME3 are too cool to ignore
- Throw
- Pull
- Vortex
- Gravity


### Defense
- Dodge
  - Flings you off to one side based on which way you're strafing
  - Should this even be a wand focus? Wand foci take a bit of time to switch. If we're balancing combat around it, it needs to be immediately accessible.
    - Maybe we only balance combat around it post-gauntlets
- Shield
  - Puts up a temporary shield that sticks around and blocks a certain amount of damage.
  - Perfect for countering those rapid-fire bolts in PVP
- Heal
  - Same as TC6: heals you or a small area around you depending on 


# Dungeons


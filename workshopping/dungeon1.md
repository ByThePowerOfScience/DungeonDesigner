# Second Age Factory

<hr/>

## Summary
You've hit a wall with your research into alchemy.  
The few scattered fragments of knowledge you've gathered require that you infuse items with "earth" and "fire", but your attempts to replicate this with raw materials fall short.

Despite being a universal technology by the time of the notes you've gathered, it all seems dependent on an item called a "Philosopher's Stone", able to break down matter into some kind of magically-attuned slurry.  
Despite your best efforts, you cannot find a method to craft such a stone _without_ that slurry; presumably due to the recipe being made obsolete by later developments.  
Without that foundation to fall back on, you're stuck.  Your only way to proceed is to find an intact stone to jump-start your progression.

You've heard some villagers mention a newly-discovered ruin from the second age, yet untouched by human hands.  
By your calculations, that should be just barely past the time of the stone's discovery.  If you're lucky, one may still be there.

<hr/>

### Quest: Built to Last

Unlocks Essentia and essentia-based alchemy.

#### 1. Investigate rumors of an ancient ruin.
- Description: Talk to a cartographer about a map.

Just purchasing a structure map like vanilla, but mixin so the villager _always_ has this trade if this quest is active.

After you purchase it, the cartographer stops you and locks you in a conversation.

**Conversation: Cartographer tells you about heretic**

- Him: "Thank you for your business, friend. Actually, you're the second person to ask about that place."
- You:
    1. "Someone else came here before me?"
    2. "So I'm too late???"
- Him:
    1. "Yes, I sent him down there a few days ago. He just got back last night."
    2. "I don't think so; he came back not too long ago empty-handed."
- Him: "He's somewhere around here if you want to talk to him.  You'll know him when you see him. He's got a very... _distinct_ fashion sense."
    - [Spawn a TC4 Heretic villager on one of the village beds]
        - ImplNote:
            - Heretic should have a deterministic UUID based on the player initiating the quest. This way he'll get autodeleted if people try to load multiple of him at once.
            - Heretic should be a reskinned Nitwit.


#### 2. Talk to the heretic.
- Description: TODO

**Conversation: Cartographer summons heretic [fallback]**

If you come back and talk to a cartographer again before talking to the heretic, you enter dialogue. This conversation no longer appears if the player has talked to the heretic already.
- You:
    1. "I'd like to trade." [Open regular trade menu]
    2. "I can't find the guy you mentioned."
- Him:
    2. "I'll see if I can grab him for you. One second."
    - [Screen goes black]
    - [When it comes back up, the heretic is standing next to him]
        - ImplNote: Save the UUID for the heretic in the player's data. If he exists in the world, teleport him here. Otherwise spawn him in with the same UUID as the previous one to prevent cheese, and since the other one is unloaded rn it'll be deleted when it's loaded again.


**Conversation: Heretic**

Personality: snarky edgy loser lol

Summary:
- The Heretic wanted to raid the place, but he couldn't get inside. He says he was so hype and cool and could have done it easily if he could just get in.
- He found a key in the rubble, but it didn't work on the keyhole, "stupid frickin broken loser key trying to keep me out"
- "Here take the key, not that it'll do you much good. If I couldn't make it work, there's no way a loser like you could."
- "Even if you did, there's no way a chump like you would make it out alive. The machines in there would rip a silly little baby man like you to pieces."
- "So go ahead, try your best. Good luck, poser, you'll need it. edge edge edge edge edge"

_Gives item: Broken Factory Key_
- If you use it on the keyhole before restoring it, it gives a chat message like "_The wall hums for a second, then the key falls to the ground. Maybe you can research how to fix it._"

#### _Unlocks research: Factory Key_

> While it looks like a mundane key at first glance, closer inspection reveals an incredible example of arcane engineering.
>
> Complex patterns woven into the enchantment act as a sort of 'magical signature', uniquely identifying the key as genuine.
> Without knowing the exact spell that created the key, there's no way anyone could ever forge a fake.
>
> Amazingly, despite having faded enough to be unrecognizable, it seems the original enchantment is _entirely intact_.  You theorize that all you'd need to do is infuse a little bit of Aura to make it strong enough to function.
>
> You wonder how the heretic could have missed this.

(this research unlocks access to the Warded Arcana research)

#### 3. Repair the Factory Key
Description: Research how to repair the factory key.

Just do the research and craft the Restored Factory Key

#### 4. Explore the Dungeon

<hr/>

### Design
#### Theme: Ancient Factory
- This factory bulk-manufactures Philosopher's Stones. Lucky you!
- Not quite high-magic, but not low-magic either. Modern-looking factory with Thaumcraft steampunk/fantasy elements.
- Dungeon is structured like a real factory: assembly floor, management office, docking, warehouse, hallways connecting them
- Enemy theming is "manufactury golems"
- Security alarm is going off from the moment you enter, which is why the golems are hostile

#### Palette
- Lots of stone brick
- Essentia pipes go through the walls and line the ceilings connecting different parts of the factory

#### Architecture Inspiration:
- FNV Sunset Sarsaparilla
- Eidolon's catacombs
- Quark's Stronghold

### Rooms

#### Entry room: Assembly Floor
Inspiration: FNV Sunset Sarsaparilla

- Assembly lines for the material components, with conveyor belts etc.
- Automated essentia processing
- Essentia pipes running up and down the walls
- Broken philosopher's stones (models, not pickupable items) litter the ground near the belts
- Piles of boxes for shipping

#### Miniboss Arena: Shipping/Loading
TODO

#### Boss Arena: Warehouse

Cutscene:
- Starts out with a lot of shelves like an actual warehouse, and a giant golem walks in from a side garage door carrying a large crate.
- He sees you and immediately drops the crate. He stances up, and with a single swing of his massive arm, he shatters all of the lined-up shelves into debris, then roars.



### Combat

#### Player's expected combat capabilities:
- Dodging: turning around and sprinting away
- Damage:
    - Bows
    - Swords
    - Beam and Bolt spells
- Armor:
    - Iron, maybe diamond
- Defense: Shield _maybe_, but probably not since wand rclick no likey offhand
- No enchantments

#### Enemies:
- Basic:
    - Worker Golems
        - Design
            - Should look a _lot_ like the ones the player has access to, and be the same size. (or maybe slightly larger so the player can hit them easier)
            - Have a few different variants that look like they're optimized for hauling, lifting, assembly
        - Attacks
            - Melee
            - For arena fights, mainly intend to swarm the player
        - Variants
            - Forklift golem: throws the player into the air
            - Laser golem (similar to Embers): skeleton AI, shoots a telegraphed laser attack
                - have a line-up beam with a humming noise that intensifies for a few seconds so you can get out of there
            - Hammer golem: regular melee
- Miniboss: Larger nimble golems
    - attack you one at a time then jump away, like OoT Lizalfos
    - TODO rework, no reason to just copy the OoT Lizalfos miniboss fight one-for-one

### Boss: Ancient Administrator

Question: should it be an administrator? or should it be something you wouldn't expect to be threatening, like a dump truck golem? I feel like it's better to have it just be unassuming but massive

Design:
- _Massive_ golem, about the size of the Last Giant or Velstadt from DS2
    - Big thing is arms that can easily drag the floor
- If it's an administrator, maybe take some design hints from the golem from that Telltale "everyone taken over by an AI" episode?
- Tanky, but slow-moving

Arena:
- About the size of the Smelter Demon arena
- Changes for phase 2


Attacks:
- Slow "drag the floor in front of him" attack like the Last Giant
- Slam the ground, causing a quick shockwave that goes in a straight line
    - Earth spikes rising from the ground
    - Deals a good chunk of damage, launches player into the ceiling
    - Telegraphed by highlighting the blocks it'll affect as he raises his arm like OSRS

#### Phase 2 Transition
- Slap the player through the wall to the opposite side of a long room
- Attack with slow-moving shockwaves on different lanes of the room that the player has to dodge as they get back to the boss
    - Like the giant guy in front of the OoT Forest Temple, but shoots them faster
    - Visuals: Dust arc-slash-looking in front, with the wynncraft crystal snake "blocks rising" effect behind it
- Immune to ranged damage until you get back to the fight
    - Actually, with the fast firebolt spells being so satisfying to use, it might be better to allow ranged magic damage to work.


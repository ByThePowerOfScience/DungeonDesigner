/**
 * All classes in this package are for the dynamic in-world dungeon builder.
 * The logic for the dungeon is controlled by each individual block,
 * and each has its own state and probably ticks on its own too.
 *
 * After being exported, all "actor" blocks from this package are turned into behaviorless placeholders, and
 * all of their logic is brought into the Nexus for optimization.  Whether they output redstone power depends on
 * if their blockstate is powered, which is controlled by the Nexus.
 */
package btpos.mcmods.dungeondesigner.builder;
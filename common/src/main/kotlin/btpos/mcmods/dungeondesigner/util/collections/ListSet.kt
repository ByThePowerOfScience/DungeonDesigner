package btpos.mcmods.dungeondesigner.util.collections

import java.util.HashSet
import java.util.function.IntFunction

/**
 * A list that doesn't allow duplicate values.
 */
class ListSet<T>(private val internal: MutableList<T> = mutableListOf()) : MutableList<T> by internal {
    // FIXME this is stupid but also I don't care I'm literally using this once and never again
    init {
        val set = HashSet<T>(internal.size)
        internal.forEach(set::add)
        internal.clear()
        internal.addAll(set)
    }
    
    @Suppress("OVERRIDE_DEPRECATION", "DEPRECATION", "RedundantOverride")
    override fun <T : Any?> toArray(generator: IntFunction<Array<out T?>?>) = super.toArray(generator)
    
    override fun add(element: T): Boolean {
        if (internal.contains(element))
            return false
        
        return internal.add(element)
    }
    
    override fun add(index: Int, element: T) {
        if (internal.contains(element))
            return
        
        internal.add(index, element)
    }
    
    /**
     * Remove all instances of it in the thing if they exist
     */
    override fun remove(element: T): Boolean {
        var did = false
        while (internal.remove(element)) { did = true }
        return did
    }
}
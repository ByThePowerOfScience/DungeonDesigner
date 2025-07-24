package btpos.mcmods.devutil.common.structure

interface IOnChange {
    /**
     * Callback to be invoked whenever this object's state is changed. (Usually to updated the NBT serialization).
     *
     * Implementers need to make sure the setter also sets onChange for any member IChangeListeners.
     */
    var onChange: () -> Unit
}
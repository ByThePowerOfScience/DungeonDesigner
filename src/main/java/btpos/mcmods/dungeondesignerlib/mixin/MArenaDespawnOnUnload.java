package btpos.mcmods.dungeondesignerlib.mixin;

import btpos.mcmods.dungeondesignerlib.builder.blocks.actors.FightControllerKt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Makes it so entities spawned by the {@link btpos.mcmods.dungeondesignerlib.builder.blocks.actors.TileFightController FightController} aren't saved to the Chunk when they unload.
 * @see btpos.mcmods.dungeondesignerlib.builder.nbt.IEntitySpawnDataKt#trySpawnEntity
 */
@Mixin(Entity.class)
public abstract class MArenaDespawnOnUnload {
    
    @Shadow public abstract CompoundTag getPersistentData();
    
    @Shadow public abstract boolean isRemoved();
    
    @Inject(
            method="getEncodeId",
            at=@At("HEAD"),
            cancellable = true
    )
    private void checkForNoSaveNbt(CallbackInfoReturnable<String> cir) {
        if (this.isRemoved() && this.getPersistentData().contains(FightControllerKt.TAGKEY_SPAWNED_BY_FIGHT_CONTROLLER)) {
            cir.setReturnValue(null);
        }
    }
}

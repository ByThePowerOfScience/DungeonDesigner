package btpos.mcmods.dungeondesigner.mixin;

import btpos.mcmods.dungeondesigner.builder.nbt.IEntitySpawnDataKt;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Makes it so entities spawned by the {@link btpos.mcmods.dungeondesigner.builder.blocks.actors.TileFightController FightController} aren't saved to the Chunk when they unload.
 * @see btpos.mcmods.dungeondesigner.builder.nbt.IEntitySpawnDataKt#trySpawnEntity
 */
@Mixin(Entity.class)
public abstract class MArenaDespawnOnUnload {
    @Inject(
            method="getEncodeId",
            at=@At("HEAD"),
            cancellable = true
    )
    private void checkForNoSaveNbt(CallbackInfoReturnable<String> cir) {
        if (IEntitySpawnDataKt.isMobTemporary(((Entity)(Object)this))) {
            cir.setReturnValue(null);
        }
    }
}

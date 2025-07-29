package btpos.test.mixin;

import btpos.unittest.JUnitBootstrapKt;
import net.minecraftforge.gametest.ForgeGameTestHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * I'm just assuming that by the time we get to Forge's gametestrunner, we're loaded enough that our unit tests won't crash.
 */
@Mixin(value = ForgeGameTestHooks.class, remap = false)
abstract class MJUnitBootstrap {
    @Inject(
            method="registerGametests",
            at=@At("HEAD")
    )
    private static void foo(CallbackInfo ci) {
        System.out.println("Started Bootstrapping JUNIT");
        JUnitBootstrapKt.bootstrapJUnit();
    }
}

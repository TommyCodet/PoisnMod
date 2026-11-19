package net.poisn.poisnmod.mixin;

import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.poisn.poisnmod.PoisnModClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin {
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void poisnmod$rememberAccountName(Avatar avatar, AvatarRenderState state, float partialTick, CallbackInfo ci) {
        PoisnModClient.rememberPlayerName(state, avatar.getScoreboardName());
    }
}

package net.poisn.poisnmod.mixin;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.poisn.poisnmod.PoisnModClient;
import net.poisn.poisnmod.PoisnModClient.MemberEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    @Inject(method = "submitNameDisplay", at = @At("TAIL"), require = 1)
    private void poisnmod$submitExtraNameplate(EntityRenderState state, PoseStack poseStack,
                                                SubmitNodeCollector collector, CameraRenderState camera,
                                                CallbackInfo ci) {
        if (!PoisnModClient.isHugoServer() || state.nameTag == null) return;

        MemberEntry entry = PoisnModClient.find(state.nameTag.getString());
        if (entry == null) return;

        String above = entry.above();
        String below = entry.below();
        if (!above.isBlank()) {
            collector.submitNameTag(poseStack, state.nameTagAttachment, -10,
                    colored(above, entry.aboveColor()), false, state.lightCoords, camera);
        }
        if (!below.isBlank()) {
            collector.submitNameTag(poseStack, state.nameTagAttachment, 10,
                    colored(below, entry.belowColor()), false, state.lightCoords, camera);
        }
    }

    private static Component colored(String text, String rawColor) {
        return Component.literal(text).withStyle(style -> style.withColor(TextColor.fromRgb(parseColor(rawColor))));
    }

    private static int parseColor(String raw) {
        if (raw == null || raw.isBlank()) return 0xFFFFFF;
        String value = raw.trim();
        if (value.matches("(?i)#[0-9a-f]{6}")) {
            return Integer.parseInt(value.substring(1), 16);
        }
        if (value.startsWith("&") && value.length() >= 2) {
            return switch (Character.toLowerCase(value.charAt(1))) {
                case '0' -> 0x000000;
                case '1' -> 0x0000AA;
                case '2' -> 0x00AA00;
                case '3' -> 0x00AAAA;
                case '4' -> 0xAA0000;
                case '5' -> 0xAA00AA;
                case '6' -> 0xFFAA00;
                case '7' -> 0xAAAAAA;
                case '8' -> 0x555555;
                case '9' -> 0x5555FF;
                case 'a' -> 0x55FF55;
                case 'b' -> 0x55FFFF;
                case 'c' -> 0xFF5555;
                case 'd' -> 0xFF55FF;
                case 'e' -> 0xFFFF55;
                case 'f' -> 0xFFFFFF;
                default -> 0xFFFFFF;
            };
        }
        return 0xFFFFFF;
    }
}

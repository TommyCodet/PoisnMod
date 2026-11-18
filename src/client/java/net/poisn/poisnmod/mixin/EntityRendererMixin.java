package net.poisn.poisnmod.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.Entity;
import net.poisn.poisnmod.PoisnModClient;
import net.poisn.poisnmod.PoisnModClient.MemberEntry;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    @Shadow
    protected abstract Font getFont();

    @Inject(method = "renderNameTag", at = @At("TAIL"))
    private void poisnmod$renderExtraNameplate(Entity entity, Component displayName,
                                               PoseStack poseStack, MultiBufferSource buffers,
                                               int packedLight, CallbackInfo ci) {
        if (!PoisnModClient.isHugoServer() || !entity.isAlive()) return;

        MemberEntry entry = PoisnModClient.find(entity.getName().getString());
        if (entry == null) return;

        Font font = getFont();
        String above = entry.above();
        String below = entry.below();
        if (above.isBlank() && below.isBlank()) return;

        poseStack.pushPose();
        poseStack.translate(0.0F, entity.getBbHeight() + 0.5F, 0.0F);
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(-0.025F, -0.025F, 0.025F);
        Matrix4f matrix = poseStack.last().pose();

        if (!above.isBlank()) {
            int color = parseColor(entry.aboveColor());
            float x = -font.width(above) / 2.0F;
            font.drawInBatch(above, x, -font.lineHeight - 2.0F, color, false,
                    matrix, buffers, Font.DisplayMode.NORMAL, 0, packedLight);
        }
        if (!below.isBlank()) {
            int color = parseColor(entry.belowColor());
            float x = -font.width(below) / 2.0F;
            font.drawInBatch(below, x, font.lineHeight + 2.0F, color, false,
                    matrix, buffers, Font.DisplayMode.NORMAL, 0, packedLight);
        }
        poseStack.popPose();
    }

    private static int parseColor(String raw) {
        if (raw == null || raw.isBlank()) return 0xFFFFFFFF;
        String value = raw.trim();
        if (value.matches("(?i)#[0-9a-f]{6}")) {
            return 0xFF000000 | Integer.parseInt(value.substring(1), 16);
        }
        if (value.startsWith("&") && value.length() >= 2) {
            return switch (value.charAt(1)) {
                case '0' -> 0xFF000000;
                case '1' -> 0xFF0000AA;
                case '2' -> 0xFF00AA00;
                case '3' -> 0xFF00AAAA;
                case '4' -> 0xFFAA0000;
                case '5' -> 0xFFAA00AA;
                case '6' -> 0xFFFFAA00;
                case '7' -> 0xFFAAAAAA;
                case '8' -> 0xFF555555;
                case '9' -> 0xFF5555FF;
                case 'a', 'A' -> 0xFF55FF55;
                case 'b', 'B' -> 0xFF55FFFF;
                case 'c', 'C' -> 0xFFFF5555;
                case 'd', 'D' -> 0xFFFF55FF;
                case 'e', 'E' -> 0xFFFFFF55;
                case 'f', 'F' -> 0xFFFFFFFF;
                default -> 0xFFFFFFFF;
            };
        }
        return 0xFFFFFFFF;
    }
}

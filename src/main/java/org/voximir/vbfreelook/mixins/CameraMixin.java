package org.voximir.vbfreelook.mixins;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.voximir.vbfreelook.freelook.CameraStateAccessor;
import org.voximir.vbfreelook.freelook.FreelookState;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Unique
    boolean needsInitialCameraSync = true;

    @Shadow
    private Entity entity;

    @Shadow
    protected abstract void setRotation(float yRot, float xRot);

    @Inject(
            method = "alignWithEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Camera;setRotation(FF)V",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            )
    )
    public void applyFreelookRotation(float partialTicks, CallbackInfo ci) {
        if (!(this.entity instanceof LocalPlayer)) return;

        if (FreelookState.isActive()) {
            CameraStateAccessor cameraStateAccessor = (CameraStateAccessor) this.entity;

            if (needsInitialCameraSync && Minecraft.getInstance().player != null) {
                cameraStateAccessor.vbfreelook$setFreelookXRot(Minecraft.getInstance().player.getXRot());
                cameraStateAccessor.vbfreelook$setFreelookYRot(Minecraft.getInstance().player.getYRot());
                needsInitialCameraSync = false;
            }
            this.setRotation(cameraStateAccessor.vbfreelook$getFreelookYRot(), cameraStateAccessor.vbfreelook$getFreelookXRot());

        } else needsInitialCameraSync = true;
    }

    @ModifyArg(
            method = "alignWithEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Camera;move(FFF)V",
                    ordinal = 0
            ),
            index = 0
    )
    private float modifyZoomOffset(float original) {
        if (!(this.entity instanceof LocalPlayer) || !FreelookState.isActive()) {
            return original;
        }

        float progress = FreelookState.getZoomingOutProgress();
        if (progress >= 1.0f) {
            return original;
        }

        return Mth.lerp(progress, 0.0f, original);
    }
}

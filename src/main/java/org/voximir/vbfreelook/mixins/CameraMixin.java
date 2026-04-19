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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.voximir.vbfreelook.config.VBFreelookSettings;
import org.voximir.vbfreelook.config.enums.TransitionType;
import org.voximir.vbfreelook.freelook.CameraStateAccessor;
import org.voximir.vbfreelook.freelook.FreelookState;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Unique
    boolean needsInitialCameraSync = true;

    @Shadow
    protected abstract void setRotation(float yRot, float xRot);

    @Shadow
    private Entity entity;

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
        boolean isLocalPlayer = this.entity instanceof LocalPlayer;
        boolean isFreelookActive = FreelookState.isActive();

        if (!isLocalPlayer) return;

        if (isFreelookActive) {
            CameraStateAccessor cameraStateAccessor = (CameraStateAccessor) this.entity;

            if (needsInitialCameraSync && Minecraft.getInstance().player != null) {
                cameraStateAccessor.vbfreelook$setFreelookXRot(Minecraft.getInstance().player.getXRot());
                cameraStateAccessor.vbfreelook$setFreelookYRot(Minecraft.getInstance().player.getYRot());
                needsInitialCameraSync = false;
            }
            this.setRotation(cameraStateAccessor.vbfreelook$getFreelookYRot(), cameraStateAccessor.vbfreelook$getFreelookXRot());
        } else {
            needsInitialCameraSync = true;
        }
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
    private float zoomOutWithTransition(float original) {
        float minCameraDist = 1.0f;

        boolean isLocalPlayer = this.entity instanceof LocalPlayer;
        boolean isFreelookActive = FreelookState.isActive();
        boolean isOriginalCloser = original > -minCameraDist;

        if (!isLocalPlayer || !isFreelookActive || isOriginalCloser) {
            return original;
        }

        double progress = FreelookState.getZoomingOutProgress();
        TransitionType transition = VBFreelookSettings.getInstance().getZoomOutTransition().get();

        return Mth.lerp((float) transition.apply(progress), -minCameraDist, original);
    }

    @Inject(method = "getMaxZoom", at = @At("HEAD"), cancellable = true)
    private void disableCollisionCheck(float cameraDist, CallbackInfoReturnable<Float> cir) {
        boolean isLocalPlayer = this.entity instanceof LocalPlayer;
        boolean isCameraNoClipEnabled = VBFreelookSettings.getInstance().getCameraNoClip().get();

        if (isLocalPlayer && isCameraNoClipEnabled) {
            cir.setReturnValue(cameraDist);
        }
    }
}

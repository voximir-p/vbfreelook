package org.voximir.vbfreelook.mixins;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.voximir.vbfreelook.freelook.CameraStateAccessor;
import org.voximir.vbfreelook.freelook.FreelookState;

@Mixin(Entity.class)
public class EntityMixin implements CameraStateAccessor {
    @Unique
    private float freelookXRot;

    @Unique
    private float freelookYRot;

    @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
    public void captureFreelookLookDelta(double xo, double yo, CallbackInfo ci) {
        // noinspection ConstantValue
        if (FreelookState.isActive() && (Object) this instanceof LocalPlayer) {
            float xDelta = (float) yo * 0.15f;
            float yDelta = (float) xo * 0.15f;

            this.freelookXRot = Mth.clamp(this.freelookXRot + xDelta, -90.0f, 90.0f);
            this.freelookYRot += yDelta;

            ci.cancel();
        }
    }

    @Override
    @Unique
    public float vbfreelook$getFreelookXRot() {
        return this.freelookXRot;
    }

    @Override
    @Unique
    public float vbfreelook$getFreelookYRot() {
        return this.freelookYRot;
    }

    @Override
    @Unique
    public void vbfreelook$setFreelookXRot(float xRot) {
        this.freelookXRot = xRot;
    }

    @Override
    @Unique
    public void vbfreelook$setFreelookYRot(float yRot) {
        this.freelookYRot = yRot;
    }
}

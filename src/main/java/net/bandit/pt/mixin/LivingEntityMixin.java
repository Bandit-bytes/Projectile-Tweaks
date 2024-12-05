package net.bandit.pt.mixin;

import net.bandit.pt.ProjectileTweaksMod;
import net.bandit.pt.utils.EntityTypeUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    private static final ThreadLocal<Boolean> IN_DAMAGE_HANDLER = ThreadLocal.withInitial(() -> false);

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void reduceProjectileDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (IN_DAMAGE_HANDLER.get()) {
            return;
        }

        try {
            IN_DAMAGE_HANDLER.set(true);

            if (source.getSource() instanceof ProjectileEntity) {
                LivingEntity entity = (LivingEntity) (Object) this;

                Identifier entityId = EntityTypeUtils.getId(entity.getType());
                String entityIdStr = entityId.toString();

                Double baseReduction = ProjectileTweaksMod.ENTITY_DAMAGE_REDUCTIONS.get(entityIdStr);
                String difficulty = entity.getWorld().getDifficulty().name().toLowerCase();
                Double difficultyMultiplier = ProjectileTweaksMod.DIFFICULTY_MULTIPLIERS.getOrDefault(difficulty, 0.0);

                if (baseReduction != null) {
                    double effectiveProtection = baseReduction * (1 + difficultyMultiplier);
                    float reducedDamage = (float) (amount * (1 - effectiveProtection));
                    cir.setReturnValue(entity.damage(source, reducedDamage));
                }
            }
        } finally {
            IN_DAMAGE_HANDLER.set(false);
        }
    }
}


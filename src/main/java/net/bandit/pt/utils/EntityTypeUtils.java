package net.bandit.pt.utils;

import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;

public class EntityTypeUtils {
    public static Identifier getId(EntityType<?> entityType) {
        return Registries.ENTITY_TYPE.getId(entityType);
    }
}
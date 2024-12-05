package net.bandit.pt;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

public class ProjectileTweaksMod implements ModInitializer {
    public static final String MOD_ID = "projectiletweaks";

    public static Map<String, Double> ENTITY_DAMAGE_REDUCTIONS = new HashMap<>();
    public static Map<String, Double> DIFFICULTY_MULTIPLIERS = new HashMap<>();

    @Override
    public void onInitialize() {
        loadConfig();
        registerReloadListener();
    }

    private void loadConfig() {
        ConfigManager.ProjectileTweaksConfig config = ConfigManager.loadConfig();
        ENTITY_DAMAGE_REDUCTIONS = config.entities;
        DIFFICULTY_MULTIPLIERS = config.difficulty_multipliers;

        System.out.println("[ProjectileTweaks] Configuration loaded successfully.");
    }

    private void registerReloadListener() {
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            if (success) {
                loadConfig();
                server.getPlayerManager().getPlayerList().forEach(player -> {
                    player.sendMessage(Text.literal("Projectile Tweaks configuration reloaded!"), false);
                });
            }
        });
    }
}

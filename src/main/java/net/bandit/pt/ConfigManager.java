package net.bandit.pt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private static final File CONFIG_FILE = new File("./config/projectile_tweaks.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int CURRENT_VERSION = 2;

    public static class ProjectileTweaksConfig {
        public int version = CURRENT_VERSION;
        public Map<String, Double> entities = new HashMap<>();
        public Map<String, Double> difficulty_multipliers = new HashMap<>();

        public static ProjectileTweaksConfig getDefault() {
            ProjectileTweaksConfig config = new ProjectileTweaksConfig();
            config.entities.put("minecraft:zombie", 0.20);
            config.entities.put("minecraft:skeleton", 0.50);
            config.entities.put("minecraft:creeper", 0.75);

            config.difficulty_multipliers.put("peaceful", -0.05);
            config.difficulty_multipliers.put("easy", -0.25);
            config.difficulty_multipliers.put("normal", 0.0);
            config.difficulty_multipliers.put("hard", 0.25);
            config.difficulty_multipliers.put("hardcore", 0.4);

            return config;
        }
    }

    public static ProjectileTweaksConfig loadConfig() {
        if (!CONFIG_FILE.exists()) {
            System.out.println("[ProjectileTweaks] Config file not found. Creating default config...");
            ProjectileTweaksConfig defaultConfig = ProjectileTweaksConfig.getDefault();
            saveConfig(defaultConfig);
            return defaultConfig;
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            ProjectileTweaksConfig loadedConfig = GSON.fromJson(reader, ProjectileTweaksConfig.class);
            if (loadedConfig.version < CURRENT_VERSION) {
                System.out.println("[ProjectileTweaks] Updating config to version " + CURRENT_VERSION + "...");
                loadedConfig = migrateConfig(loadedConfig);
                saveConfig(loadedConfig);
            }

            return loadedConfig;
        } catch (Exception e) {
            System.err.println("[ProjectileTweaks] Failed to load config. Using defaults.");
            e.printStackTrace();
            return ProjectileTweaksConfig.getDefault();
        }
    }

    private static ProjectileTweaksConfig migrateConfig(ProjectileTweaksConfig oldConfig) {
        ProjectileTweaksConfig defaultConfig = ProjectileTweaksConfig.getDefault();
        defaultConfig.entities.forEach((key, value) -> oldConfig.entities.putIfAbsent(key, value));
        defaultConfig.difficulty_multipliers.forEach((key, value) -> oldConfig.difficulty_multipliers.putIfAbsent(key, value));
        oldConfig.version = CURRENT_VERSION;

        return oldConfig;
    }

    public static void saveConfig(ProjectileTweaksConfig config) {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, writer);
            System.out.println("[ProjectileTweaks] Config saved successfully.");
        } catch (Exception e) {
            System.err.println("[ProjectileTweaks] Failed to save config.");
            e.printStackTrace();
        }
    }
}


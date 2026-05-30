package com.pikmintea.dogecoin.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pikmintea.dogecoin.Dogecoin;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DogecoinConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("dogecoin.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static DogecoinConfig instance;

    public boolean walletEnabled = true;
    public long startingBalance = 0;

    public static DogecoinConfig getInstance() {
        return instance;
    }

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (var reader = Files.newBufferedReader(CONFIG_PATH)) {
                instance = GSON.fromJson(reader, DogecoinConfig.class);
            } catch (IOException e) {
                Dogecoin.LOGGER.error("Failed to load config", e);
                instance = new DogecoinConfig();
            }
        } else {
            instance = new DogecoinConfig();
            save();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(instance));
        } catch (IOException e) {
            Dogecoin.LOGGER.error("Failed to save config", e);
        }
    }
}

package com.conquestreforged.mechanics.config;

import com.conquestreforged.mechanics.Module;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class ConfigHelper {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static Config load(File file) {
        boolean generate = true;
        if (file.exists()) {
            try (Reader reader = new BufferedReader(new FileReader(file))) {
                return gson.fromJson(reader, Config.class);
            } catch (IOException e) {
                e.printStackTrace();
                generate = false;
            }
        }

        Config config = new Config();
        if (generate) {
            for (Module module : Module.REGISTRY) {
                module.addConfigDefaults(config);
            }
            save(config, file);
        }
        return config;
    }

    public static void save(Config config, File file) {
        File dir = file.getParentFile();
        if (dir.exists() || dir.mkdirs()) {
            try (Writer writer = new BufferedWriter(new FileWriter(file))) {
                gson.toJson(config, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

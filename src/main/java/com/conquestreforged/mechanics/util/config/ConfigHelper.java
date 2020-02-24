package com.conquestreforged.mechanics.util.config;

import com.conquestreforged.mechanics.Config;
import com.conquestreforged.mechanics.Module;
import com.google.gson.*;
import com.mojang.datafixers.kinds.Const;

import java.io.*;

public class ConfigHelper {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static Config load(File file) {
        if (file.exists()) {
            try (Reader reader = new BufferedReader(new FileReader(file))) {
                JsonElement element = new JsonParser().parse(reader);
                if (element.isJsonObject()) {
                    JsonObject object = element.getAsJsonObject();
                    Config config = gson.fromJson(object, Config.class);
                    boolean save = false;
                    for (Module module : Module.REGISTRY) {
                        if (!object.has(module.getName())) {
                            save = true;
                            module.addDefaults(config);
                        }
                    }
                    if (save) {
                        save(config, file);
                    }
                    return config;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Config config = new Config();
        for (Module module : Module.REGISTRY) {
            module.addDefaults(config);
        }
        save(config, file);

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

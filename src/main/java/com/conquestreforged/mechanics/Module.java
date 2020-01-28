package com.conquestreforged.mechanics;

import com.conquestreforged.mechanics.config.Config;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

public interface Module {

    List<Module> REGISTRY = new ArrayList<>();

    boolean isEnabled(Config config);

    void addConfigDefaults(Config config);

    void onLoad(MinecraftServer server, Config config);
}

package com.conquestreforged.mechanics;

import com.conquestreforged.mechanics.util.Loggable;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.LinkedList;
import java.util.List;

public interface Module extends Loggable {

    Marker MARKER = MarkerManager.getMarker("Module");

    List<Module> REGISTRY = new LinkedList<>();

    String getName();

    void unload();

    void load(Config config);

    void addDefaults(Config config);

    boolean isEnabled(Config config);
}

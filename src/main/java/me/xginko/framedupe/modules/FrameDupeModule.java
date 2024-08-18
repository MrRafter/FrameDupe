package me.xginko.framedupe.modules;


import java.util.HashSet;
import java.util.Set;

public abstract class FrameDupeModule {

    public abstract boolean shouldEnable();
    public abstract void enable();
    public abstract void disable();

    public static final Set<FrameDupeModule> MODULES = new HashSet<>();

    public static void reloadModules() {
        MODULES.forEach(FrameDupeModule::disable);
        MODULES.clear();

        MODULES.add(new NormalFrameDupe());
        MODULES.add(new GlowFrameDupe());

        MODULES.forEach(module -> {
            if (module.shouldEnable()) module.enable();
        });
    }
}
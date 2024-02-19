package me.MrRafter.framedupe.modules;


import java.util.HashSet;

public interface FrameDupeModule {

    boolean shouldEnable();
    void enable();
    void disable();

    HashSet<FrameDupeModule> modules = new HashSet<>();

    static void reloadModules() {
        modules.forEach(FrameDupeModule::disable);
        modules.clear();

        modules.add(new NormalFrameDupe());
        modules.add(new GlowFrameDupe());

        modules.forEach(module -> {
            if (module.shouldEnable()) module.enable();
        });
    }
}
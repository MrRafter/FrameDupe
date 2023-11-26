package me.MrRafter.framedupe.modules;


import java.util.HashSet;

public interface FrameModule {

    boolean shouldEnable();
    void enable();
    void disable();

    HashSet<FrameModule> modules = new HashSet<>();

    static void reloadModules() {
        modules.forEach(FrameModule::disable);
        modules.clear();

        modules.add(new RegularFrames());
        modules.add(new GlowFrames());

        modules.forEach(module -> {
            if (module.shouldEnable()) module.enable();
        });
    }
}
package me.MrRafter.framedupe;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import io.github.thatsmusic99.configurationmaster.api.Title;

import java.io.File;
import java.util.List;

public class FrameConfig {

    private final ConfigFile config;

    protected FrameConfig() throws Exception {
        // Create plugin folder first if it does not exist yet
        File pluginFolder = FrameDupe.getInstance().getDataFolder();
        if (!pluginFolder.exists() && !pluginFolder.mkdir())
            FrameDupe.getPrefixedLogger().severe("Failed to create plugin folder.");
        // Load config.yml with ConfigMaster
        this.config = ConfigFile.loadConfig(new File(pluginFolder, "config.yml"));
        // Set title with credits
        this.config.setTitle(new Title()
                .withPadding(true)
                .addSolidLine()
                .addLine(" ")
                .addLine("Frame Dupe", Title.Pos.CENTER)
                .addLine(" ")
                .addLine("Compatible with all server versions that have item frames in the game.", Title.Pos.CENTER)
                .addLine("made by mrrafter_ and rewritten by xginko on discord.", Title.Pos.CENTER)
                .addLine(" ")
                .addSolidLine()
        );
    }

    public void saveConfig() {
        try {
            config.save();
        } catch (Exception e) {
            FrameDupe.getPrefixedLogger().severe("Failed to save config file! - " + e.getLocalizedMessage());
        }
    }

    public ConfigFile master() {
        return config;
    }

    public boolean getBoolean(String path, boolean def) {
        config.addDefault(path, def);
        return config.getBoolean(path, def);
    }

    public boolean getBoolean(String path, boolean def, String comment) {
        config.addDefault(path, def, comment);
        return config.getBoolean(path, def);
    }

    public int getInt(String path, int def) {
        config.addDefault(path, def);
        return config.getInteger(path, def);
    }

    public int getInt(String path, int def, String comment) {
        config.addDefault(path, def, comment);
        return config.getInteger(path, def);
    }

    public double getDouble(String path, double def) {
        config.addDefault(path, def);
        return config.getDouble(path, def);
    }

    public double getDouble(String path, double def, String comment) {
        config.addDefault(path, def, comment);
        return config.getDouble(path, def);
    }

    public List<String> getList(String path, List<String> def) {
        config.addDefault(path, def);
        return config.getStringList(path);
    }

    public List<String> getList(String path, List<String> def, String comment) {
        config.addDefault(path, def, comment);
        return config.getStringList(path);
    }
}
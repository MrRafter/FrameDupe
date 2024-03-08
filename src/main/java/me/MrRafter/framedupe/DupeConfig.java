package me.MrRafter.framedupe;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import io.github.thatsmusic99.configurationmaster.api.Title;

import java.io.File;
import java.util.List;

public final class DupeConfig {

    private final ConfigFile config;

    DupeConfig() throws Exception {
        // Create plugin folder first if it does not exist yet
        File pluginFolder = FrameDupe.getInstance().getDataFolder();
        if (!pluginFolder.exists() && !pluginFolder.mkdir())
            FrameDupe.getPrefixedLogger().severe("Failed to create config folder.");
        // Load config.yml with ConfigMaster
        this.config = ConfigFile.loadConfig(new File(pluginFolder, "config.yml"));
        // Set title with credits
        this.config.setTitle(new Title()
                .withPadding(true)
                .addSolidLine()
                .addLine(" ")
                .addLine(" Frame Dupe ", Title.Pos.CENTER)
                .addLine(" ")
                .addLine(" Compatible with all server versions that have item frames in the game. ", Title.Pos.CENTER)
                .addLine(" made by mrrafter_ and rewritten by xginko on discord. ", Title.Pos.CENTER)
                .addLine(" ")
                .addSolidLine()
        );
    }

    public void saveConfig() {
        try {
            this.config.save();
        } catch (Exception e) {
            FrameDupe.getPrefixedLogger().severe("Failed to save config file! - " + e.getLocalizedMessage());
        }
    }

    public ConfigFile master() {
        return this.config;
    }

    public boolean getBoolean(String path, boolean def) {
        this.config.addDefault(path, def);
        return this.config.getBoolean(path, def);
    }

    public boolean getBoolean(String path, boolean def, String comment) {
        this.config.addDefault(path, def, comment);
        return this.config.getBoolean(path, def);
    }

    public int getInt(String path, int def) {
        this.config.addDefault(path, def);
        return this.config.getInteger(path, def);
    }

    public int getInt(String path, int def, String comment) {
        this.config.addDefault(path, def, comment);
        return this.config.getInteger(path, def);
    }

    public double getDouble(String path, double def) {
        this.config.addDefault(path, def);
        return this.config.getDouble(path, def);
    }

    public double getDouble(String path, double def, String comment) {
        this.config.addDefault(path, def, comment);
        return this.config.getDouble(path, def);
    }

    public List<String> getList(String path, List<String> def) {
        this.config.addDefault(path, def);
        return this.config.getStringList(path);
    }

    public List<String> getList(String path, List<String> def, String comment) {
        this.config.addDefault(path, def, comment);
        return this.config.getStringList(path);
    }
}
package me.MrRafter.framedupe;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;

import java.io.File;
import java.util.List;

public class FrameConfig {

    private final ConfigFile config;

    protected FrameConfig() throws Exception {
        this.config = loadConfig(new File(FrameDupe.getInstance().getDataFolder(), "config.yml"));
        config.addComment("Frame dupe from 1.7.x to last update of minecraft\n" + "made by mrrafter_ on discord");
    }

    private ConfigFile loadConfig(File ymlFile) throws Exception {
        File parent = new File(ymlFile.getParent());
        if (!parent.exists() && !parent.mkdir()) FrameDupe.getPrefixedLogger().severe("Unable to create plugin config directory.");
        if (!ymlFile.exists()) ymlFile.createNewFile();
        return ConfigFile.loadConfig(ymlFile);
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

    public String getString(String path, String def) {
        config.addDefault(path, def);
        return config.getString(path, def);
    }

    public String getString(String path, String def, String comment) {
        config.addDefault(path, def, comment);
        return config.getString(path, def);
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
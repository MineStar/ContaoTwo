/*
 * Copyright (C) 2012 MineStar.de 
 * 
 * This file is part of Contao2.
 * 
 * Contao2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * Contao2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Contao2.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.minestar.contao2.units;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.contao2.core.Core;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public class Settings {

    private int maxFreeSlots;
    private int maxSlots;

    private boolean showWelcomeMSG;

    private String serverFullMSG;
    private String disconnectedMSG;
    private String noFreeSlotsMSG;
    private String motd;

    private String jsonFilePath;
    private YamlConfiguration config;
    private File configFile;

    public Settings(File dataFolder) {
        try {
            loadSettings(dataFolder);
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't load settings from config file!");
        }
    }

    private void loadSettings(File dataFolder) throws Exception {
        File temp = new File(dataFolder, "config.yml");
        YamlConfiguration config = new YamlConfiguration();
        if (!temp.exists()) {
            temp.createNewFile();
            config.load(temp);
            createDefaultConfiguration(config);
            ConsoleUtils.printInfo(Core.NAME, "Cannot find config.yml in Plugin folder, plugin is creating a default one");
            config.save(temp);
        }
        config.load(temp);

        maxSlots = config.getInt("maxSlots", 45);
        maxFreeSlots = config.getInt("publicSlots", 6);
        showWelcomeMSG = config.getBoolean("ShowWelcomeMSG", true);
        serverFullMSG = config.getString("ServerFullMSG");
        disconnectedMSG = config.getString("DisconnectedMSG");
        noFreeSlotsMSG = config.getString("NoFreeSlotsMSG");
        motd = config.getString("MOTD");

        jsonFilePath = config.getString("userStatsFile");
        this.config = config;
        this.configFile = temp;
    }

    private void createDefaultConfiguration(YamlConfiguration config) throws Exception {

        config.set("maxSlots", 45);
        config.set("publicSlots", 6);
        config.set("ShowWelcomeMSG", true);
        config.set("ServerFullMSG", "Server ist voll!");
        config.set("DisconnectedMSG", "Ein PayUser hat deinen Platz bekommen, tut uns leid...");
        config.set("NoFreeSlotsMSG", "Alle FreeUserSlots sind belegt.");
        config.set("MOTD", "Wilkommen auf Minestar.");

        config.set("userStatsFile", "userstats.json");
    }

    public int getFreeSlots() {
        return maxFreeSlots;
    }

    public void setFreeSlots(int freeSlots) {
        this.maxFreeSlots = freeSlots;
        config.set("publicSlots", freeSlots);
        try {
            config.save(configFile);
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't save public slots to config!");
        }
    }

    public int getMaxSlots() {
        return maxSlots;
    }

    public void setMaxSlots(int maxSlots) {
        this.maxSlots = maxSlots;
        config.set("maxSlots", maxSlots);
        try {
            config.save(configFile);
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't save max slots to config!");
        }
    }
    public boolean isShowWelcomeMSG() {
        return showWelcomeMSG;
    }

    public String getServerFullMSG() {
        return serverFullMSG;
    }

    public String getDisconnectedMSG() {
        return disconnectedMSG;
    }

    public String getNoFreeSlotsMSG() {
        return noFreeSlotsMSG;
    }

    public String getMotd() {
        return motd;
    }

    public String getJSONFilePath() {
        return jsonFilePath;
    }
}

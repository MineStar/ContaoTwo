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

import de.minestar.contao2.core.Core;
import de.minestar.minestarlibrary.config.MinestarConfig;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public class Settings {

    private static int freeSlots;
    private static int maxSlots;

    private static boolean showWelcomeMsg;

    private static String serverFullMsg;
    private static String kickedForPayMsg;
    private static String noFreeSlotsMsg;
    private static String motd;

    private static String jsonFilePath;

    private static MinestarConfig config;
    private static File configFile;

    private Settings() {

    }

    public static boolean init(File dataFolder) {
        configFile = new File(dataFolder, "config.yml");
        try {
            if (configFile.exists())
                config = new MinestarConfig(dataFolder);
            else
                config = MinestarConfig.copyDefault(Settings.class.getResourceAsStream("/config.yml"), configFile);

            loadValues();
            return true;

        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't load the settings from " + configFile);
            return false;
        }
    }

    private static void loadValues() {

        // HOW MANY PLAYER CAN JOIN THE SERVER
        maxSlots = config.getInt("maxSlots");

        // HOW MANY FREE USER CAN JOIN THE SERVER
        freeSlots = config.getInt("publicSlots");

        // SHALL WE DISPLAY THE WELCOM MESSAGE WHEN A PLAYER JOINED
        showWelcomeMsg = config.getBoolean("ShowWelcomeMSG");

        // MESSAGE SEND TO PLAYER WHEN SERVER IS FULL
        serverFullMsg = config.getString("ServerFullMSG");

        // MESSAGE SEND TO PLAYER WHEN A USER WAS KICKE FOR PAYUSER
        kickedForPayMsg = config.getString("DisconnectedMSG");

        // MESSAGE SEND TO FREE PLAYER WHEN THERE IS NO FREE SLOT AVAILABLE
        noFreeSlotsMsg = config.getString("NoFreeSlotsMSG");

        // THE MESSAGE OF THE DAY
        motd = config.getString("MOTD");

        // THE PATH WHERE THE JSON FILE SHOULD SAVED
        jsonFilePath = config.getString("userStatsFile");
    }

    public static int getFreeSlots() {
        return freeSlots;
    }

    public static void setFreeSlots(int freeSlots) {
        Settings.freeSlots = freeSlots;
        config.set("publicSlots", freeSlots);
        try {
            config.save(configFile);
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't save public slots to config!");
        }
    }

    public static int getMaxSlots() {
        return maxSlots;
    }

    public static void setMaxSlots(int maxSlots) {
        Settings.maxSlots = maxSlots;
        config.set("maxSlots", maxSlots);
        try {
            config.save(configFile);
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't save max slots to the config!");
        }
    }

    public static boolean showWelcomeMsg() {
        return showWelcomeMsg;
    }

    public static String getServerFullMsg() {
        return serverFullMsg;
    }

    public static String getKickedForPayMsg() {
        return kickedForPayMsg;
    }

    public static String getNoFreeSlotsMsg() {
        return noFreeSlotsMsg;
    }

    public static String getMOTD() {
        return motd;
    }

    public static String getJSONFilePath() {
        return jsonFilePath;
    }
}

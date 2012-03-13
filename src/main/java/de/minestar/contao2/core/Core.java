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

package de.minestar.contao2.core;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import de.minestar.contao2.commands.list.cmdList;
import de.minestar.contao2.commands.ppay.cmdPPay;
import de.minestar.contao2.commands.ppay.cmdSet;
import de.minestar.contao2.commands.user.cmdAddProbeTime;
import de.minestar.contao2.commands.user.cmdAddWarning;
import de.minestar.contao2.commands.user.cmdAdmin;
import de.minestar.contao2.commands.user.cmdDefault;
import de.minestar.contao2.commands.user.cmdFree;
import de.minestar.contao2.commands.user.cmdPay;
import de.minestar.contao2.commands.user.cmdProbe;
import de.minestar.contao2.commands.user.cmdRemoveWarning;
import de.minestar.contao2.commands.user.cmdSearch;
import de.minestar.contao2.commands.user.cmdStatus;
import de.minestar.contao2.commands.user.cmdUser;
import de.minestar.contao2.commands.user.cmdX;
import de.minestar.contao2.listener.StatisticListener;
import de.minestar.contao2.listener.PlayerListener;
import de.minestar.contao2.manager.DatabaseManager;
import de.minestar.contao2.manager.PlayerManager;
import de.minestar.contao2.manager.StatisticManager;
import de.minestar.contao2.units.Settings;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.commands.CommandList;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public class Core extends JavaPlugin {

    public static File dataFolder;
    public static String pluginName = "Contao";

    /**
     * Commands
     */
    private CommandList commandList;

    /**
     * Manager
     */
    private PlayerManager playerManager;
    private StatisticManager statisticManager;
    private DatabaseManager databaseManager;

    /**
     * Settings
     */
    private Settings settings;

    /**
     * Listener
     */
    private PlayerListener connectionListener;
    private StatisticListener blockListener;

    @Override
    public void onDisable() {
        // SAVE STATISTICS
        if (this.databaseManager.hasConnection())
            this.statisticManager.saveAllStatistics();

        // PRINT INFO
        ConsoleUtils.printInfo(pluginName, "Disabled v" + this.getDescription().getVersion() + "!");
    }

    @Override
    public void onEnable() {
        // INIT DATAFOLDER
        Core.dataFolder = this.getDataFolder();
        Core.dataFolder.mkdirs();

        // CREATE SETTINGS
        this.createSettings();

        // CREATE MANAGER
        this.createManager();

        // WE NEED A CONNECTION
        if (!this.databaseManager.hasConnection()) {
            ConsoleUtils.printError(Core.pluginName, "Can't reach database! Plugin is disabled!");
            this.setEnabled(false);
            return;
        }

        // CREATE LISTENER, COMMANDS
        this.createListener();
        this.createCommands();

        // REGISTER EVENTS
        this.registerEvents();

        // START THREADS
        this.startThreads();

        // PRINT INFO
        ConsoleUtils.printInfo(pluginName, "Enabled v" + this.getDescription().getVersion() + "!");
    }

    private void startThreads() {
        if (this.databaseManager.hasConnection())
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, this.statisticManager, 20 * 60, 20 * 60);
    }

    private void createSettings() {
        this.settings = new Settings(Core.dataFolder);
    }

    private void createManager() {
        this.databaseManager = new DatabaseManager(Core.pluginName, Core.dataFolder);
        this.playerManager = new PlayerManager(this.settings);
        this.statisticManager = new StatisticManager(this.databaseManager);
        this.databaseManager.initManager(this.playerManager, this.statisticManager);
    }

    private void createListener() {
        this.connectionListener = new PlayerListener(this.playerManager, this.databaseManager, this.statisticManager, this.settings);
        this.blockListener = new StatisticListener(this.statisticManager);
    }

    private void createCommands() {
        //@formatter:off
        AbstractCommand[] commands = new AbstractCommand[] {
                new cmdList("/who", "[PlayerName]", "", this.playerManager),
                new cmdList("/list", "[PlayerName]", "", this.playerManager),
                new cmdList("/online", "[PlayerName]", "", this.playerManager),
                
                new cmdStatus   ("/stats", "", "", this.databaseManager, this.statisticManager),

                new cmdUser     ("/user", "", "",
                    new cmdAdmin        ("admin",   "<ingamename> <dd.mm.yyyy>",    "contao.rights.admin",      this.playerManager, this.databaseManager),
                    new cmdDefault      ("default", "<ingamename>",                 "contao.rights.default",    this.playerManager),
                    new cmdFree         ("free",    "<ingamename>",                 "contao.rights.free",       this.playerManager, this.databaseManager),
                    new cmdPay          ("pay",     "<ingamename> <dd.mm.yyyy>",    "contao.rights.pay",        this.playerManager, this.databaseManager),
                    new cmdProbe        ("probe",   "<ingamename> <contao-id>",     "contao.rights.probe",      this.playerManager, this.databaseManager),
                    new cmdAddProbeTime ("probeadd","<PlayerName> <Days>",          "contao.rights.probeadd",   this.databaseManager),
                    new cmdSearch       ("search",  "<homepagename>",               "contao.rights.search",     this.databaseManager),
                    new cmdStatus       ("status",  "",                             "",                         this.databaseManager, this.statisticManager),
                    new cmdAddWarning   ("awarn",   "<ingamename> <text>",          "contao.rights.awarn" ,     this.databaseManager),
                    new cmdRemoveWarning("rwarn",   "<ingamename> <warningIndex>" , "contao.rights.rwarn",      this.databaseManager),
                    new cmdX            ("x",       "<ingamename>",                 "contao.rights.x",          this.playerManager)
                ),
                
                new cmdPPay      ("/ppay", "", "contao.ppay",
                    new cmdSet          ("set",     "<Option> <Number>",            "contao.ppay",              this.settings)
                )
        };
        //@formatter:on
        this.commandList = new CommandList(commands);
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(this.connectionListener, this);
        Bukkit.getPluginManager().registerEvents(this.blockListener, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (this.commandList != null)
            this.commandList.handleCommand(sender, label, args);
        return true;
    }

    // VALIDATE DATE
    public static boolean validateDate(String date) {

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        format.setLenient(false);
        try {
            format.parse(date.trim());
            return true;
        } catch (ParseException pe) {
            return false;
        }
    }
}

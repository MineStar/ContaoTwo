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

import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

import de.minestar.contao2.commands.list.cmdList;
import de.minestar.contao2.commands.ppay.cmdPPay;
import de.minestar.contao2.commands.ppay.cmdSet;
import de.minestar.contao2.commands.user.cmdAddProbeTime;
import de.minestar.contao2.commands.user.cmdAddWarning;
import de.minestar.contao2.commands.user.cmdAdmin;
import de.minestar.contao2.commands.user.cmdDefault;
import de.minestar.contao2.commands.user.cmdFree;
import de.minestar.contao2.commands.user.cmdMod;
import de.minestar.contao2.commands.user.cmdPay;
import de.minestar.contao2.commands.user.cmdProbe;
import de.minestar.contao2.commands.user.cmdRemoveWarning;
import de.minestar.contao2.commands.user.cmdSearch;
import de.minestar.contao2.commands.user.cmdStatus;
import de.minestar.contao2.commands.user.cmdUnMod;
import de.minestar.contao2.commands.user.cmdUser;
import de.minestar.contao2.commands.user.cmdX;
import de.minestar.contao2.listener.FakePlayerListener;
import de.minestar.contao2.listener.PlayerListener;
import de.minestar.contao2.listener.StatisticListener;
import de.minestar.contao2.manager.DatabaseManager;
import de.minestar.contao2.manager.PlayerManager;
import de.minestar.contao2.manager.StatisticManager;
import de.minestar.contao2.statistics.FreeLoginFailStat;
import de.minestar.contao2.statistics.GroupChangeStat;
import de.minestar.contao2.statistics.LoginStat;
import de.minestar.contao2.statistics.LogoutStat;
import de.minestar.contao2.units.Settings;
import de.minestar.minestarlibrary.AbstractCore;
import de.minestar.minestarlibrary.annotations.UseStatistic;
import de.minestar.minestarlibrary.commands.CommandList;
import de.minestar.minestarlibrary.stats.StatisticHandler;

@UseStatistic
public class Core extends AbstractCore {

    public static final String NAME = "ContaoTwo";

    public Core() {
        super(NAME);
        INSTANCE = this;
    }

    /** Manager */
    private PlayerManager playerManager;
    private StatisticManager statisticManager;
    private DatabaseManager databaseManager;

    /** Listener */
    private PlayerListener connectionListener;
    private StatisticListener blockListener;
    private FakePlayerListener fakePlayerListener;

    public static Core INSTANCE;

    @Override
    protected boolean loadingConfigs(File dataFolder) {
        return Settings.init(dataFolder, NAME, this.getDescription().getVersion());
    }

    @Override
    protected boolean createManager() {

        this.databaseManager = new DatabaseManager(NAME, new File(getDataFolder(), "sqlconfig.yml"));
        this.playerManager = new PlayerManager();
        this.statisticManager = new StatisticManager(this.databaseManager);
        this.databaseManager.initManager(this.playerManager, this.statisticManager);

        return true;
    }

    @Override
    protected boolean createListener() {

        this.connectionListener = new PlayerListener(this.playerManager, this.databaseManager, this.statisticManager);
        this.blockListener = new StatisticListener(this.statisticManager);
        this.fakePlayerListener = new FakePlayerListener(this.playerManager);

        return true;
    }

    @Override
    protected boolean registerEvents(PluginManager pm) {

        pm.registerEvents(this.connectionListener, this);
        pm.registerEvents(this.blockListener, this);
        pm.registerEvents(this.fakePlayerListener, this);

        return true;
    }

    @Override
    protected boolean startThreads(BukkitScheduler scheduler) {

        scheduler.scheduleSyncRepeatingTask(this, this.statisticManager, 20 * 60, 20 * 60);

        return true;
    }

    @Override
    protected boolean createCommands() {
        //@formatter:off
        this.cmdList = new CommandList(NAME,
                new cmdList             ("/who",        "[PlayerName]",                 "",                         this.playerManager),
                new cmdList             ("/list",       "[PlayerName]",                 "",                         this.playerManager),
                new cmdList             ("/online",     "[PlayerName]",                 "",                         this.playerManager),
                
                new cmdStatus           ("/stats",      "",                             "",                         this.databaseManager, this.statisticManager),

                new cmdUser         ("/user", "", "",
                    new cmdAdmin        ("admin",       "<ingamename> <dd.mm.yyyy>",    "contao.rights.admin",      this.playerManager, this.databaseManager),
                    new cmdDefault      ("default",     "<ingamename>",                 "contao.rights.default",    this.playerManager),
                    new cmdFree         ("free",        "<ingamename>",                 "contao.rights.free",       this.playerManager, this.databaseManager),
                    new cmdPay          ("pay",         "<ingamename> <dd.mm.yyyy>",    "contao.rights.pay",        this.playerManager, this.databaseManager),
                    new cmdProbe        ("probe",       "<ingamename> <contao-id>",     "contao.rights.probe",      this.playerManager, this.databaseManager),
                    new cmdAddProbeTime ("probeadd",    "<PlayerName> <Days>",          "contao.rights.probeadd",   this.databaseManager),
                    new cmdSearch       ("search",      "<homepagename>",               "contao.rights.search",     this.databaseManager),
                    new cmdStatus       ("status",      "",                             "",                         this.databaseManager, this.statisticManager),
                    new cmdAddWarning   ("awarn",       "<ingamename> <text>",          "contao.rights.awarn" ,     this.databaseManager),
                    new cmdRemoveWarning("rwarn",       "<ingamename> <warningIndex>" , "contao.rights.rwarn",      this.databaseManager),
                    new cmdX            ("x",           "<ingamename> <reason>",        "contao.rights.x",          this.playerManager),
                    new cmdMod          ("mod",         "<ingamename>",                 "contao.rights.mod",        this.playerManager, this.databaseManager),
                    new cmdUnMod        ("unmod",       "<ingamename>",                 "contao.rights.unmod",      this.playerManager, this.databaseManager)
                ),
                
                new cmdPPay         ("/ppay", "", "contao.ppay",
                    new cmdSet          ("set",     "<Option> <Number>",            "contao.ppay")
                )
        );
        //@formatter:on

        return true;
    }

    @Override
    protected boolean registerStatistics() {

        StatisticHandler.registerStatistic(GroupChangeStat.class);
        StatisticHandler.registerStatistic(LoginStat.class);
        StatisticHandler.registerStatistic(LogoutStat.class);
        StatisticHandler.registerStatistic(FreeLoginFailStat.class);

        return true;
    }

    @Override
    protected boolean commonDisable() {
        if (databaseManager != null) {
            statisticManager.saveAllStatistics();
            databaseManager.closeConnection();
        }

        return true;
    }
}

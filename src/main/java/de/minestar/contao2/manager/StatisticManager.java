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

package de.minestar.contao2.manager;

import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.minestar.contao2.core.Core;
import de.minestar.contao2.units.MCWarning;
import de.minestar.contao2.units.PlayerWarnings;
import de.minestar.contao2.units.Statistic;
import de.minestar.minestarlibrary.utils.ChatUtils;

public class StatisticManager implements Runnable {

    private ConcurrentMap<UUID, Statistic> statistics = new ConcurrentHashMap<>();
    private ConcurrentMap<UUID, PlayerWarnings> warnings = new ConcurrentHashMap<>();
    private DatabaseManager databaseManager;

    public StatisticManager(DatabaseManager dbManager) {
        this.databaseManager = dbManager;
        this.refresh();
    }

    public void refresh() {
        this.loadAllStatistics();
        this.loadAllWarnings();
    }

    public Statistic getPlayersStatistic(UUID playerUUID) {
        Statistic stat = statistics.get(playerUUID);
        if(stat == null) {
            stat = databaseManager.loadStatistics(playerUUID);
            if(stat != null) {
                statistics.put(playerUUID, stat);
            }
        }
        return stat;
    }

    private void loadAllStatistics() {
        statistics.putAll(databaseManager.loadAllStatistics());
    }

    private void loadAllWarnings() {
        warnings.putAll(databaseManager.loadAllWarnings());
    }

    @Override
    public void run() {
        saveAllStatistics();
    }

    public void saveAllStatistics() {
        for (Entry<UUID, Statistic> entry : statistics.entrySet()) {
            Statistic stats = entry.getValue();
            if (stats.hasChanged()) {
                databaseManager.saveStatistics(entry.getKey(), stats.getTotalPlaced(), stats.getTotalBreak());
                stats.setHasChanged(false);
            }
        }
    }

    public PlayerWarnings getWarnings(UUID playerUUID) {
        return warnings.get(playerUUID);
    }

    public void printWarnings(Player player) {
        PlayerWarnings thisWarnings = this.getWarnings(player.getUniqueId());
        if (thisWarnings != null && thisWarnings.getWarnings().size() > 0) {
            ChatUtils.writeMessage(player, "");
            ChatUtils.writeColoredMessage(player, Core.NAME, ChatColor.RED, "Du hast " + thisWarnings.getWarnings().size() + " Verwarnung" + (thisWarnings.getWarnings().size() > 1 ? "en" : "") + "!");
            for (MCWarning warning : thisWarnings.getWarnings()) {
                ChatUtils.writeMessage(player, warning.toString());
            }
        }
    }

    public void printStatistics(Player player) {
        Statistic stats = this.getPlayersStatistic(player.getUniqueId());

        ChatUtils.writeMessage(player, "");
        if (stats == null)
            ChatUtils.writeColoredMessage(player, ChatColor.RED, "Wir konnten leider keine Stats zu deinem Account finden.");
        else {
            ChatUtils.writeColoredMessage(player, ChatColor.BLUE, "Blöcke zerstört: " + stats.getTotalBreak());
            ChatUtils.writeColoredMessage(player, ChatColor.BLUE, "Blöcke gesetzt  : " + stats.getTotalPlaced());
        }
    }
}

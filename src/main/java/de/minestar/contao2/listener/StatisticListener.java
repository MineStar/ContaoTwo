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

package de.minestar.contao2.listener;

import de.minestar.minestarlibrary.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import de.minestar.contao2.manager.StatisticManager;
import de.minestar.contao2.units.Statistic;

import java.util.UUID;

public class StatisticListener implements Listener {

    private StatisticManager statisticManager;

    public StatisticListener(StatisticManager sManager) {
        this.statisticManager = sManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled())
            return;
        UUID playerUUID = event.getPlayer().getUniqueId();
        Statistic stat = statisticManager.getPlayersStatistic(playerUUID);
        if(stat !=  null) {
            stat.incrementBreak();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled())
            return;
        UUID playerUUID = event.getPlayer().getUniqueId();
        Statistic stat = statisticManager.getPlayersStatistic(playerUUID);
        if(stat !=  null) {
            stat.incrementPlace();
        }
    }
}

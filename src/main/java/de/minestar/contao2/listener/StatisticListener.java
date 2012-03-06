/*
 * Copyright (C) 2011 MineStar.de 
 * 
 * This file is part of ContaoPlugin.
 * 
 * ContaoPlugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * ContaoPlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ContaoPlugin.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.minestar.contao2.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import de.minestar.contao2.manager.StatisticManager;
import de.minestar.contao2.units.Statistic;

public class StatisticListener implements Listener {

    private StatisticManager statisticManager;

    public StatisticListener(StatisticManager sManager) {
        this.statisticManager = sManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled())
            return;
        String playerName = event.getPlayer().getName();
        Statistic thisStatistic = statisticManager.getPlayersStatistic(playerName);
        thisStatistic.incrementBreak();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled())
            return;
        String playerName = event.getPlayer().getName();
        Statistic stat = statisticManager.getPlayersStatistic(playerName);
        if (stat != null)
            stat.incrementPlace();
    }
}

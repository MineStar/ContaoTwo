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

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.minestar.contao2.manager.PlayerManager;
import de.minestar.minestarlibrary.events.FakeJoinEvent;
import de.minestar.minestarlibrary.events.FakeQuitEvent;

/**
 * Handeling fake join and quit events from AdminStuff
 */
public class FakePlayerListener implements Listener {

    private PlayerManager pManager;

    public FakePlayerListener(PlayerManager pManager) {
        this.pManager = pManager;
    }

    @EventHandler
    public void onFakeQuitEvent(FakeQuitEvent event) {
        pManager.removePlayer(event.getPlayer().getName());
    }

    @EventHandler
    public void onFakeJoinEvent(FakeJoinEvent event) {
        pManager.updatePlayer(event.getPlayer());
    }
}

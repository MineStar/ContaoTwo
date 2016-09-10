/*
 * Copyright (C) 2016 MineStar.de 
 * 
 * This file is part of ContaoTwo.
 * 
 * ContaoTwo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * ContaoTwo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ContaoTwo.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.minestar.contao2.threading;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerKickThread implements Runnable {

    private final String playerName;
    private final String GroupName;

    public PlayerKickThread(String playerName, String GroupName) {
        this.playerName = playerName;
        this.GroupName = GroupName;
    }

    @Override
    public void run() {
        if (playerName != null) {
            Player currentPlayer = Bukkit.getServer().getPlayer(this.playerName);
            if (currentPlayer != null) {
                currentPlayer.kickPlayer("User der Gruppe " + GroupName + " dürfen hier nicht Joinen");
            }
        }
    }
}

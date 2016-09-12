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

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerKickThread implements Runnable {

    private final UUID playerUUID;
    private final String GroupName;

    public PlayerKickThread(UUID playerUUID, String GroupName) {
        this.playerUUID = playerUUID;
        this.GroupName = GroupName;
    }

    @Override
    public void run() {
        if (playerUUID != null) {
            Player currentPlayer = Bukkit.getServer().getPlayer(this.playerUUID);
            if (currentPlayer != null) {
                currentPlayer.kickPlayer("User der Gruppe " + GroupName + " dürfen hier nicht Joinen");
            }
        }
    }
}

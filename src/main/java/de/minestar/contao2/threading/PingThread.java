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

package de.minestar.contao2.threading;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import de.minestar.contao2.core.Core;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public class PingThread implements Runnable {

    private final static String PING_STATEMENT = "SELECT 1";

    private PreparedStatement pingStatement;

    public PingThread(Connection con) {
        try {
            pingStatement = con.prepareStatement(PING_STATEMENT);
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't initiate the ping statement");
        }
    }

    private boolean pingFailed = false;

    @Override
    public void run() {
        if (pingFailed)
            handlePingFail();
        else {
            if (pingStatement != null) {
                try {
                    ResultSet resultSet = pingStatement.executeQuery();
                    if (!resultSet.next())
                        pingFailed = true;
                } catch (Exception e) {
                    ConsoleUtils.printException(e, Core.NAME, "Ping failed!");
                    pingFailed = true;
                }
            }
        }
    }

    private boolean broadcasted = false;

    private void handlePingFail() {
        if (broadcasted) {
            Bukkit.broadcastMessage(ChatColor.RED + "[SERVER]: Das ContaoPlugin hat die Datenbankverbindung verloren!");
            Bukkit.broadcastMessage(ChatColor.RED + "[SERVER]: Der Server wird in 10 Sekunden heruntergefahren und neugestartet!");
            broadcasted = true;
        } else {
            Bukkit.broadcastMessage(ChatColor.RED + "[SERVER]: Der Server wird wegen Datenbankproblemen neugestart!");
            Bukkit.shutdown();
        }
    }

}

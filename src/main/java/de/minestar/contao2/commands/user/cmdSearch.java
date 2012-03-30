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

package de.minestar.contao2.commands.user;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.minestar.contao2.core.Core;
import de.minestar.contao2.manager.DatabaseManager;
import de.minestar.contao2.units.MCUser;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.ChatUtils;

public class cmdSearch extends AbstractCommand {

    private DatabaseManager databaseManager;

    public cmdSearch(String syntax, String arguments, String node, DatabaseManager databaseManager) {
        super(Core.NAME, syntax, arguments, node);
        this.description = "Zeige alle Contao-IDs mit folgendem Namensteil";
        this.databaseManager = databaseManager;
    }

    @Override
    public void execute(String[] args, Player player) {
        searchUser(args[0], player);
    }

    @Override
    public void execute(String[] args, ConsoleCommandSender console) {
        searchUser(args[0], console);
    }

    private void searchUser(String name, CommandSender sender) {

        // GET INGAME-NAME AND CONTAO-ID
        HashMap<Integer, String> userMap = databaseManager.getContaoID(name);
        if (userMap.values().size() < 1) {
            ChatUtils.writeError(sender, pluginName, "Kein Homepageaccount gefunden!");
            return;
        }

        ChatUtils.writeSuccess(sender, pluginName, userMap.size() + " Treffer:");

        String text = "";
        MCUser user = null;
        for (Entry<Integer, String> entry : userMap.entrySet()) {
            if (databaseManager.isContaoIDInMCTable(entry.getKey())) {
                text = " (registriert)";
                databaseManager.getIngameData(entry.getKey());
                if (user != null && !user.getExpDate().equalsIgnoreCase("11.11.1111"))
                    text = " (Payuser bis : " + user.getExpDate() + ")";
            }

            ChatUtils.writeInfo(sender, "Contao-ID von '" + entry.getValue() + "' : " + entry.getKey() + text);
            text = "";
        }
    }
}

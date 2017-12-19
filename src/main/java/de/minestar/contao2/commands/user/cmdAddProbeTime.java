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

import de.minestar.contao2.units.ContaoGroup;
import de.minestar.minestarlibrary.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.minestar.contao2.core.Core;
import de.minestar.contao2.manager.DatabaseManager;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.ChatUtils;

import java.util.HashMap;
import java.util.UUID;

public class cmdAddProbeTime extends AbstractCommand {

    private HashMap<String, Long> timeMap = new HashMap<>();

    private DatabaseManager databaseManager;

    public cmdAddProbeTime(String syntax, String arguments, String node, DatabaseManager databaseManager) {
        super(Core.LOG_NAME, syntax, arguments, node);
        this.databaseManager = databaseManager;
    }

    @Override
    public void execute(String[] args, Player player) {
        addProbeTime(args, player);
    }

    @Override
    public void execute(String[] args, ConsoleCommandSender console) {
        addProbeTime(args, console);
    }

    private void addProbeTime(String[] args, CommandSender sender) {
        String inputPlayerName = args[0];
        // second argument is not a number
        if (args.length < 2 && !args[1].matches("\\d*")) {
            ChatUtils.writeError(sender, pluginName, getHelpMessage());
            return;
        }
        int additionalDays = Integer.parseInt(args[1]);

        OfflinePlayer player = PlayerUtils.getOnlinePlayer(inputPlayerName);
        if(player == null) {
            OfflinePlayer offlinePlayer = PlayerUtils.getOfflinePlayer(inputPlayerName);
            if(offlinePlayer == null) {
                ChatUtils.writeError(sender, pluginName, "Spieler '" + inputPlayerName + "' wurde nicht gefunden. War der Spieler schon einmal online?");
            } else if(timeMap.containsKey(inputPlayerName) && System.currentTimeMillis() - timeMap.get(inputPlayerName) < 1000 * 60 * 15) {
                timeMap.remove(inputPlayerName);
                player = offlinePlayer;
            } else {
                timeMap.put(inputPlayerName, System.currentTimeMillis());
                ChatUtils.writeError(sender, pluginName, "Spieler '" + inputPlayerName + "' ist offline. Kommando innerhalb von 15 Sekunden widerholen wenn du es trotzdem ausführen möchtest.");
            }
        }

        if(player == null) {
            return;
        }

        UUID uuid = player.getUniqueId();

        if (!databaseManager.isMCUUIDInUser(uuid)) {
            ChatUtils.writeError(sender, pluginName, "Spieler '" + inputPlayerName + "' nicht gefunden!");
            return;
        }

        ContaoGroup group = databaseManager.getContaoGroup(uuid);
        if (!ContaoGroup.PROBE.equals(group)) {
            ChatUtils.writeError(sender, pluginName, "Spieler ist kein Probe user!");
            return;
        }

        if (databaseManager.addProbeTime(additionalDays, uuid))
            ChatUtils.writeSuccess(sender, pluginName, "Probezeit für Spieler '" + inputPlayerName + "' verlängert!");
        else
            ChatUtils.writeError(sender, pluginName, "Fehler beim Verlängern der Probezeit!");

    }
}

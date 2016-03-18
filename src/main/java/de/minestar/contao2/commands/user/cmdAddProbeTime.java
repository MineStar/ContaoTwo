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

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.minestar.contao2.core.Core;
import de.minestar.contao2.manager.DatabaseManager;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.ChatUtils;

public class cmdAddProbeTime extends AbstractCommand {

    private DatabaseManager databaseManager;

    public cmdAddProbeTime(String syntax, String arguments, String node, DatabaseManager databaseManager) {
        super(Core.NAME, syntax, arguments, node);
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
        String playerName = args[0];
        // second argument is not a number
        if (!args[1].matches("\\d*")) {
            ChatUtils.writeError(sender, pluginName, getHelpMessage());
            return;
        }
        int additionalDays = Integer.parseInt(args[1]);

        if (!databaseManager.isMCNickInMCTable(playerName)) {
            ChatUtils.writeError(sender, pluginName, "Spieler '" + playerName + "' nicht gefunden!");
            return;
        }

        if (!databaseManager.isProbeMember(playerName)) {
            ChatUtils.writeError(sender, pluginName, "Spieler ist kein Probe user!");
            return;
        }

        if (databaseManager.addProbeTime(additionalDays, playerName))
            ChatUtils.writeSuccess(sender, pluginName, "Probezeit für Spieler '" + playerName + "' verlängert!");
        else
            ChatUtils.writeError(sender, pluginName, "Fehler beim Verlängern der Probezeit!");

    }
}

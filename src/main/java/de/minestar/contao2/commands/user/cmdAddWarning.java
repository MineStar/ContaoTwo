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

package de.minestar.contao2.commands.user;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.minestar.contao2.core.Core;
import de.minestar.contao2.manager.DatabaseManager;
import de.minestar.minestarlibrary.commands.AbstractExtendedCommand;
import de.minestar.minestarlibrary.utils.ChatUtils;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class cmdAddWarning extends AbstractExtendedCommand {

    private DatabaseManager databaseManager;

    public cmdAddWarning(String syntax, String arguments, String node, DatabaseManager databaseManager) {
        super(Core.pluginName, syntax, arguments, node);
        this.databaseManager = databaseManager;
        this.description = "Verwarnt den angegebenden Spieler";
    }

    @Override
    public void execute(String[] args, Player player) {
        addWarning(args, player);
    }

    @Override
    public void execute(String[] args, ConsoleCommandSender console) {
        addWarning(args, console);
    }

    private void addWarning(String[] args, CommandSender sender) {

        String playerName = args[0];

        if (!databaseManager.isMCNickInMCTable(playerName)) {
            ChatUtils.writeError(sender, pluginName, "Spieler '" + playerName + "' existiert nicht!");
            return;
        }

        String text = ChatUtils.getMessage(args, " ", 1);

        if (databaseManager.addWarning(playerName, text, sender.getName())) {
            ChatUtils.writeSuccess(sender, pluginName, "Spieler '" + playerName + "' wurde verwarnt!");
            Player target = PlayerUtils.getOnlinePlayer(playerName);
            if (target != null) {
                PlayerUtils.sendMessage(target, ChatColor.RED, sender.getName() + " hat dich verwarnt aus folgendem Grund :");
                PlayerUtils.sendMessage(target, ChatColor.RED, text);
            }
        } else
            ChatUtils.writeError(sender, pluginName, "Fehler beim Verwarnen!");
    }
}

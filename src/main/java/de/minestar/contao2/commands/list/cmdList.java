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

package de.minestar.contao2.commands.list;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.minestar.contao2.core.Core;
import de.minestar.contao2.manager.PlayerManager;
import de.minestar.minestarlibrary.commands.AbstractExtendedCommand;
import de.minestar.minestarlibrary.utils.ChatUtils;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class cmdList extends AbstractExtendedCommand {

    private PlayerManager playerManager;

    public cmdList(String syntax, String arguments, String node, PlayerManager pManager) {
        super(Core.pluginName, syntax, arguments, node);
        this.description = "List players";
        this.playerManager = pManager;
    }

    @Override
    public void execute(String[] args, Player player) {
        displayList(args, player);
    }

    @Override
    public void execute(String[] args, ConsoleCommandSender console) {
        displayList(args, console);
    }

    private void displayList(String[] args, CommandSender sender) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                this.playerManager.printOnlineList((Player) sender);
            } else {
                this.playerManager.printOnlineList((ConsoleCommandSender) sender);
            }
        } else if (args.length == 1)
            displayAccountName(sender, args[0]);
        else
            ChatUtils.writeError(sender, pluginName, getHelpMessage());
    }

    private void displayAccountName(CommandSender sender, String name) {

        Player target = PlayerUtils.getOnlinePlayer(name);
        if (target != null)
            ChatUtils.writeMessage(sender, pluginName, "Gesucht '" + ChatColor.BLUE + name + ChatColor.GREEN + "', AccountName '" + ChatColor.BLUE + target.getName() + ChatColor.GREEN + "', Anzeigename '" + ChatColor.BLUE + target.getDisplayName() + "'");
        else
            ChatUtils.writeError(sender, pluginName, "Kein Spieler namens '" + ChatColor.BLUE + name + ChatColor.RED + "' gefunden!");
    }
}

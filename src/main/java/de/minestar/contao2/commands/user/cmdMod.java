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
import de.minestar.contao2.manager.PlayerManager;
import de.minestar.contao2.units.ContaoGroup;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.ChatUtils;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class cmdMod extends AbstractCommand {

    private PlayerManager pManager;

    public cmdMod(String syntax, String arguments, String node, PlayerManager pManager) {
        super(Core.NAME, syntax, arguments, node);
        this.pManager = pManager;
    }

    @Override
    public void execute(String[] args, Player player) {
        modPlayer(args[0], player);
    }

    @Override
    public void execute(String[] args, ConsoleCommandSender console) {
        modPlayer(args[0], console);
    }

    private void modPlayer(String playerName, CommandSender sender) {

        // SEARCH FOR PLAYER
        playerName = PlayerUtils.getCorrectPlayerName(playerName);
        if (playerName == null) {
            ChatUtils.writeError(sender, playerName, "Player '" + playerName + "' not found");
            return;
        }
        // UPDATE GROUP
        pManager.updateGroupManagerGroup(playerName, ContaoGroup.MOD.getName());

        // SEND MESSAGE TO COMMAND CALLER
        ChatUtils.writeSuccess(sender, pluginName, "Der Spieler '" + playerName + "' ist nun Moderator");

        // TRY TO SEND MESSAGE TO PLAYER(NEEDN'T BE ONLINE!)
        Player p = PlayerUtils.getOnlinePlayer(playerName);
        if (p != null)
            PlayerUtils.sendSuccess(p, "Du bist nun Moderator :)");
    }
}

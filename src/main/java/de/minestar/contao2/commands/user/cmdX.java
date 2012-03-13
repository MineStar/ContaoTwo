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

public class cmdX extends AbstractCommand {

    private PlayerManager playerManager;

    public cmdX(String syntax, String arguments, String node, PlayerManager playerManager) {
        super(Core.pluginName, syntax, arguments, node);
        this.description = "X-User hinzufügen";
        this.playerManager = playerManager;
    }

    @Override
    public void execute(String[] args, Player player) {
        addX(args, player);
    }

    @Override
    public void execute(String[] args, ConsoleCommandSender console) {
        addX(args, console);
    }

    private void addX(String[] args, CommandSender sender) {

        String ingameName = PlayerUtils.getCorrectPlayerName(args[0]);

        if (ingameName == null) {
            ChatUtils.writeError(sender, pluginName, "Spieler '" + args[0] + "' existiert nicht!");
            return;
        }

        ChatUtils.writeSuccess(sender, pluginName, "Spieler '" + ingameName + "' ist nun in Gruppe 'X'!");
        this.playerManager.updateGroupManagerGroup(ingameName, ContaoGroup.X.name());
    }
}

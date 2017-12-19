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

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.minestar.contao2.core.Core;
import de.minestar.contao2.manager.PlayerManager;
import de.minestar.contao2.units.ContaoGroup;
import de.minestar.core.MinestarCore;
import de.minestar.core.units.MinestarPlayer;
import de.minestar.minestarlibrary.commands.AbstractExtendedCommand;
import de.minestar.minestarlibrary.utils.ChatUtils;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class cmdX extends AbstractExtendedCommand {

    private HashMap<String, Long> timeMap = new HashMap<String, Long>();
    private PlayerManager playerManager;

    public cmdX(String syntax, String arguments, String node, PlayerManager playerManager) {
        //TODO push in DB
        super(Core.LOG_NAME, syntax, arguments, node);
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
            ingameName = args[0];
            if (!timeMap.containsKey(ingameName) || timeMap.get(ingameName) < System.currentTimeMillis()) {
                ChatUtils.writeInfo(sender, pluginName, "WARNUNG: Der Spieler mit dem Namen '" + args[0] + "' ist offline. Befehl innerhalb der nächsten 10 Sekunden neu eingeben, falls der Name richtig war!");
                timeMap.put(ingameName, System.currentTimeMillis() + 10 * 1000);
                return;
            }

            timeMap.remove(ingameName);
        }

        // Store information why he is x user and who has x used him
        MinestarPlayer mPlayer = MinestarCore.getPlayer(ingameName);
        mPlayer.setString("contao.xreason", ChatUtils.getMessage(args, 1));
        mPlayer.setString("contao.xadmin", sender.getName());

        ChatUtils.writeSuccess(sender, pluginName, "Spieler '" + ingameName + "' ist nun in Gruppe 'X'!");
        this.playerManager.updateGroupManagerGroup(ingameName, ContaoGroup.X);
    }
}

/*
 * Copyright (C) 2011 MineStar.de 
 * 
 * This file is part of 'ContaoPlugin'.
 * 
 * 'ContaoPlugin' is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * 'ContaoPlugin' is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with 'ContaoPlugin'.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * AUTHOR: GeMoschen
 * 
 */

package de.minestar.contao2.commands.user;

import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.minestar.contao2.core.Core;
import de.minestar.contao2.manager.DatabaseManager;
import de.minestar.contao2.manager.PlayerManager;
import de.minestar.contao2.units.ContaoGroup;
import de.minestar.contao2.units.MCUser;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.ChatUtils;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class cmdFree extends AbstractCommand {

    private PlayerManager playerManager;
    private DatabaseManager databaseManager;

    private HashMap<String, String> possibleFreeUser = new HashMap<String, String>();

    public cmdFree(String syntax, String arguments, String node, PlayerManager playerManager, DatabaseManager databaseManager) {
        super(Core.pluginName, syntax, arguments, node);
        this.description = "Freeuser hinzufügen";
        this.playerManager = playerManager;
        this.databaseManager = databaseManager;
    }

    @Override
    public void execute(String[] args, Player player) {
        freeMember(args, player);
    }

    @Override
    public void execute(String[] args, ConsoleCommandSender console) {
        freeMember(args, console);
    }

    private void freeMember(String[] args, CommandSender sender) {
        MCUser user = databaseManager.getIngameData(args[0]);
        if (user == null) {
            ChatUtils.writeError(sender, pluginName, "Fehler: Minecraftnick nicht gefunden!");
            return;
        }
        String ingameName = user.getNickname();
        int contaoID = user.getContaoID();

        if (databaseManager.isInProbation(ingameName)) {
            String target = possibleFreeUser.get(sender.getName());
            if (target == null || !target.equalsIgnoreCase(ingameName)) {
                possibleFreeUser.put(sender.getName(), ingameName);
                ChatUtils.writeError(sender, pluginName, "Spieler '" + ingameName + "' befindet sich noch in der ProbeZeit!");
                ChatUtils.writeError(sender, pluginName, "Gebe nochmal /user free " + ingameName + " ein, um ihn dennoch freizuschalten!");
                return;
            }
            possibleFreeUser.remove(sender.getName());
        }

        // remove pay status
        databaseManager.setExpDateInMCTable("11.11.1111", contaoID);

        // CONTAO GRUPPE AUF FREE SETZEN
        databaseManager.updateContaoGroup(ContaoGroup.FREE, contaoID);

        // remove probe status
        databaseManager.deleteProbeStatus(ingameName);

        ChatUtils.writeSuccess(sender, pluginName, "Spieler '" + ingameName + "' ist nun Freeuser!");

        Player target = PlayerUtils.getOnlinePlayer(ingameName);
        if (target != null)
            PlayerUtils.sendSuccess(target, "Du bist nun Freeuser!");

        // UPDATE GROUPMANAGER-GROUP
        this.playerManager.updateGroupManagerGroup(ingameName, ContaoGroup.FREE.getName());
    }
}

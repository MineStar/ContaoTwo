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

import de.minestar.contao2.core.Core;
import de.minestar.contao2.manager.DatabaseManager;
import de.minestar.contao2.manager.PlayerManager;
import de.minestar.contao2.units.ContaoGroup;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.ChatUtils;
import de.minestar.minestarlibrary.utils.PlayerUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class cmdFree extends AbstractCommand {

    private PlayerManager playerManager;
    private DatabaseManager databaseManager;

    private HashMap<String, String> possibleFreeUser = new HashMap<>();

    public cmdFree(String syntax, String arguments, String node, PlayerManager playerManager, DatabaseManager databaseManager) {
        super(Core.LOG_NAME, syntax, arguments, node);
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
        String inputPlayerName = args[0];

        Player player = PlayerUtils.getOnlinePlayer(inputPlayerName);
        if(player == null) {
            ChatUtils.writeError(sender, pluginName, "Spieler '" + inputPlayerName + "' ist nicht online. Offline Beförderungen aktuell nicht möglich.");
            return;
        }

        String ingameName = player.getName();

        UUID uuid = player.getUniqueId();
        ContaoGroup oldGroup = databaseManager.getContaoGroup(uuid);
        int forumID = databaseManager.getForumId(uuid);

        if(!ContaoGroup.PROBE.equals(oldGroup)) {
            ChatUtils.writeError(sender, pluginName, "Spieler '" + ingameName + "' ist kein ProbeUser!");
            return;
        }

        if (databaseManager.isInProbation(uuid)) {
            String target = possibleFreeUser.get(sender.getName());
            if (target == null || !target.equalsIgnoreCase(ingameName)) {
                possibleFreeUser.put(sender.getName(), ingameName);
                ChatUtils.writeError(sender, pluginName, "Spieler '" + ingameName + "' befindet sich noch in der ProbeZeit!");
                ChatUtils.writeError(sender, pluginName, "Gebe nochmal /user free " + ingameName + " ein, um ihn dennoch freizuschalten!");
                return;
            }
            possibleFreeUser.remove(sender.getName());
        }

        if(!databaseManager.setUserFree(forumID)) {
            ChatUtils.writeError(sender, pluginName, "Technischer Fehler beim Updaten der ForenInfos!");
            return;
        }

        ChatUtils.writeSuccess(sender, pluginName, "Spieler '" + ingameName + "' ist nun Freeuser!");

        Player target = PlayerUtils.getOnlinePlayer(ingameName);
        if (target != null)
            PlayerUtils.sendSuccess(target, "Du bist nun Freeuser!");

        // UPDATE GROUPMANAGER-GROUP
        this.playerManager.updateGroupManagerGroup(ingameName, ContaoGroup.FREE);
    }
}

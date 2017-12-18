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
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.minestar.contao2.core.Core;
import de.minestar.contao2.manager.DatabaseManager;
import de.minestar.contao2.manager.PlayerManager;
import de.minestar.contao2.units.ContaoGroup;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.ChatUtils;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class cmdProbe extends AbstractCommand {

    private HashMap<String, Long> timeMap = new HashMap<String, Long>();

    private PlayerManager playerManager;
    private DatabaseManager databaseManager;

    public cmdProbe(String syntax, String arguments, String node, PlayerManager playerManager, DatabaseManager dbHandler) {
        super(Core.NAME, syntax, arguments, node);
        this.description = "Probeuser hinzufügen";
        this.playerManager = playerManager;
        this.databaseManager = dbHandler;
    }

    @Override
    public void execute(String[] args, Player player) {
        addProbe(args, player);
    }

    @Override
    public void execute(String[] args, ConsoleCommandSender console) {
        addProbe(args, console);
    }

    private void addProbe(String[] args, CommandSender sender) {
        // SET CONTAO GROUP
        String inputPlayerName = args[0];

        Player player = PlayerUtils.getOnlinePlayer(inputPlayerName);
        if(player == null) {
            OfflinePlayer offlinePlayer = PlayerUtils.getOfflinePlayer(inputPlayerName);
            if(offlinePlayer == null) {
                ChatUtils.writeError(sender, pluginName, "Spiler '" + inputPlayerName + "' wurde nicht gefunden. War der Spieler schon einmal online?");
            } else if(args.length > 2 && "offline".equals(args[2])) {
                player = offlinePlayer.getPlayer();
            } else {
                ChatUtils.writeError(sender, pluginName, "Spieler '" + inputPlayerName + "' ist offline. Kommando mit offline wiederholen.");
            }
        }

        if(player == null) {
            return;
        }

        String ingameName = player.getName();

        UUID uuid = player.getUniqueId();

        int inputUserID;
        try {
            inputUserID = Integer.parseInt(args[1]);
        } catch (Exception e) {
            ChatUtils.writeError(sender, pluginName, args[1] + " ist keine Zahl!");
            return;
        }

        if(!checkUser(inputUserID, sender, inputPlayerName, uuid)) {
            return;
        }


        databaseManager.setToProbe(uuid);

        // PRINT INFO
        ChatUtils.writeSuccess(sender, pluginName, "Spieler '" + ingameName + "' ist nun Probeuser!");
        if (player.isOnline()) {
            PlayerUtils.sendSuccess(player, "Du bist nun Probeuser!");
            PlayerUtils.sendSuccess(player, "Herzlich Willkommen auf Minestar.de");
            PlayerUtils.sendSuccess(player, "Viel Spass im Probegebiet :)");
        }
        // UPDATE GROUPMANAGER-GROUP
        this.playerManager.updateGroupManagerGroup(player.getName(), ContaoGroup.PROBE);
    }

    private boolean checkUser(int userID, CommandSender sender, String mcName, UUID uuid) {


        // USER IS IN X
        if (playerManager.isInGroup(mcName, ContaoGroup.X)) {
            ChatUtils.writeError(sender, pluginName, "Fehler: User ist in Gruppe X.");
            ChatUtils.writeInfo(sender, pluginName, "Er wurde bereits abgelehnt!");
            return false;
        }

        ContaoGroup contaoGroup = databaseManager.getContaoGroup(userID);

        if(!ContaoGroup.DEFAULT.equals(contaoGroup) && !ContaoGroup.FREE.equals(contaoGroup)) {
            ChatUtils.writeError(sender, pluginName, "Spieler '" + mcName + "' ist weder Free noch Default! Aktuelle Gruppe: " + contaoGroup);
            return false;
        }

        HashMap<Integer, String> forumIDs = databaseManager.getForumIDs(mcName);

        if(forumIDs == null || forumIDs.isEmpty()) {
            ChatUtils.writeError(sender, pluginName, "Fehler: MinecraftNick nicht gefunden.");
            return false;
        }
        else if(forumIDs.size() > 1) {
            ChatUtils.writeError(sender, pluginName, "Fehler: Mehrere ForenAccounts mit gleichem MinecraftNick gefunden.");
            return false;
        }
        else if(!forumIDs.containsKey(userID)) {
            ChatUtils.writeError(sender, pluginName, "Fehler: MCNick gefunden, aber unter anderen ForenIDs.");
            for(Integer i : forumIDs.keySet()) {
                ChatUtils.writeError(sender, pluginName, Integer.toString(i));
            }
            return false;
        }

        if (!databaseManager.isForumAccountActive(userID)) {
            ChatUtils.writeError(sender, pluginName, "Fehler: Bestätigungsemail wurde noch nicht bestätigt.");
            ChatUtils.writeInfo(sender, pluginName, "Bitte dem User bescheid sagen das er bestätigen muss.");
            return false;
        }


        List<String> forumNames = databaseManager.getForumNames(uuid);
        if(forumNames == null) {
            ChatUtils.writeInfo(sender, pluginName, "Fehler: Konnte nicht prüfen ob UUID bereits eingetragen ist.");
            return false;
        }

        if(!forumNames.isEmpty()) {
            ChatUtils.writeError(sender, pluginName, "Fehler: UUID ist bereits für folgende ForenAccounts hinterlegt.");
            for(String dbName : forumNames) {
                ChatUtils.writeError(sender, pluginName, dbName);
            }
            return false;
        }

        return true;
    }
}

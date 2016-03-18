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

import org.bukkit.ChatColor;
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

        int contaoID = 0;
        try {
            contaoID = Integer.parseInt(args[1]);
        } catch (Exception e) {
            ChatUtils.writeError(sender, pluginName, args[1] + " ist keine Zahl!");
        }

        Player newUser = PlayerUtils.getOnlinePlayer(ingameName);

        // Degrade
        ContaoGroup group = playerManager.getGroup(ingameName);
        if (group != null) {
            if (group.equals(ContaoGroup.FREE) || group.equals(ContaoGroup.PAY) || group.equals(ContaoGroup.MOD) || group.equals(ContaoGroup.ADMIN)) {
                handleDegrade(ingameName, contaoID, sender, newUser);
                return;
            }
        }

        if (!checkPlayer(ingameName, contaoID, sender, newUser))
            return;

        // ADD USER TO MC-DB
        if (databaseManager.getIngameData(contaoID) == null)
            databaseManager.addProbe(ingameName, contaoID, "11.11.1111", sender.getName());

        // CONTAO GRUPPE AUF PROBE SETZEN
        databaseManager.updateContaoGroup(ContaoGroup.PROBE, contaoID);

        // PRINT INFO
        ChatUtils.writeSuccess(sender, pluginName, "Spieler '" + ingameName + "' ist nun Probeuser!");
        if (newUser != null) {
            PlayerUtils.sendSuccess(newUser, "Du bist nun Probeuser!");
            PlayerUtils.sendSuccess(newUser, "Herzlich Willkommen auf Minestar.de");
            PlayerUtils.sendSuccess(newUser, "Viel Spass im Probegebiet :)");
        }
        // UPDATE GROUPMANAGER-GROUP
        this.playerManager.updateGroupManagerGroup(ingameName, ContaoGroup.PROBE);
    }

    private boolean checkPlayer(String ingameName, int contaoID, CommandSender sender, Player target) {
        // CONTAO-ID & MC-NICK IN 'mc_pay' = return
        if (databaseManager.isContaoIDInMCTable(contaoID) || databaseManager.isMCNickInMCTable(ingameName)) {
            ChatUtils.writeError(sender, pluginName, "Fehler: Minecraftnick oder ContaoID doppelt.");
            return false;
        }

        // CONTAO-ACCOUNT INAKTIV = return
        if (!databaseManager.isContaoAccountActive(contaoID)) {
            ChatUtils.writeError(sender, pluginName, "Fehler: Bestätigungsemail wurde noch nicht bestätigt.");
            ChatUtils.writeInfo(sender, pluginName, "Bitte dem User bescheid sagen das er bestätigen muss.");
            return false;
        }

        // USER IS IN X
        if (target == null && playerManager.isOfflinePlayerInGroup(ingameName, ContaoGroup.X)) {
            ChatUtils.writeError(sender, pluginName, "Fehler: User ist in Gruppe X.");
            ChatUtils.writeInfo(sender, pluginName, "Er wurde bereits abgelehnt!");
            return false;
        } else if (target != null && playerManager.isInGroup(target, ContaoGroup.X)) {
            ChatUtils.writeError(sender, pluginName, "Fehler: User ist in Gruppe X.");
            ChatUtils.writeInfo(sender, pluginName, "Er wurde bereits abgelehnt!");
            return false;
        }
        return true;
    }
    private void handleDegrade(String ingameName, int contaoID, CommandSender sender, Player newUser) {
        databaseManager.degradeFree(ingameName);

        // CONTAO GRUPPE AUF PROBE SETZEN
        databaseManager.updateContaoGroup(ContaoGroup.PROBE, contaoID);

        // PRINT INFO
        ChatUtils.writeSuccess(sender, pluginName, "Spieler '" + ingameName + "' ist nun Probeuser!");
        if (newUser != null)
            PlayerUtils.sendMessage(newUser, ChatColor.RED, "Du bist nun Probeuser!");

        // UPDATE GROUPMANAGER-GROUP
        this.playerManager.updateGroupManagerGroup(ingameName, ContaoGroup.PROBE);
    }
}

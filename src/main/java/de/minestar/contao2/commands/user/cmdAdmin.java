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

import java.text.SimpleDateFormat;

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

public class cmdAdmin extends AbstractCommand {

    private PlayerManager playerManager;
    private DatabaseManager databaseManager;

    public cmdAdmin(String syntax, String arguments, String node, PlayerManager playerManager, DatabaseManager databaseManager) {
        super(Core.NAME, syntax, arguments, node);
        this.description = "Admin hinzufügen";
        this.playerManager = playerManager;
        this.databaseManager = databaseManager;
    }

    @Override
    public void execute(String[] args, Player player) {
        addAdmin(args, player);
    }

    @Override
    public void execute(String[] args, ConsoleCommandSender console) {
        addAdmin(args, console);
    }

    private final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    static {
        FORMAT.setLenient(false);
    }

    private void addAdmin(String[] args, CommandSender sender) {

        String date = args[1];
        // if date is not in dd.MM.yyyy format
        if (!validateDate(date))
            return;

        MCUser user = databaseManager.getIngameData(args[0]);
        if (user == null) {
            ChatUtils.writeError(sender, pluginName, "Fehler: Minecraftnick nicht gefunden!");
            return;
        }

        String ingameName = user.getNickname();
        int contaoID = user.getContaoID();

        // ADD USER TO MC-DB
        databaseManager.setExpDateInMCTable(date, contaoID);

        // CONTAO GRUPPE AUF ADMIN SETZEN
        databaseManager.updateContaoGroup(ContaoGroup.ADMIN, contaoID);

        // PRINT INFO
        ChatUtils.writeSuccess(sender, pluginName, "Spieler '" + ingameName + "' ist nun Admin (bezahlt bis '" + date + "')!");

        Player target = PlayerUtils.getOnlinePlayer(ingameName);
        if (target != null)
            PlayerUtils.sendSuccess(target, "Du bist nun Admin (bezahlt bis '" + date + "')!");

        // UPDATE GROUPMANAGER-GROUP
        this.playerManager.updateGroupManagerGroup(ingameName, ContaoGroup.ADMIN);
    }

    private boolean validateDate(String date) {
        try {
            FORMAT.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

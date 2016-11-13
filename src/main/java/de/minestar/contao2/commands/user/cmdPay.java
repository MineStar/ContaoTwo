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

public class cmdPay extends AbstractCommand {

    private PlayerManager playerManager;
    private DatabaseManager databaseManager;

    public cmdPay(String syntax, String arguments, String node, PlayerManager playerManager, DatabaseManager dbHandler) {
        super(Core.NAME, syntax, arguments, node);
        this.description = "Payuser hinzufügen";
        this.playerManager = playerManager;
        this.databaseManager = dbHandler;
    }

    @Override
    public void execute(String[] args, Player player) {
        addPay(args, player);
    }

    @Override
    public void execute(String[] args, ConsoleCommandSender console) {
        addPay(args, console);
    }

    private final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    static {
        FORMAT.setLenient(false);
    }

    private void addPay(String[] args, CommandSender sender) {

        String date = args[1];
        // if date is not in dd.MM.yyyy format
        if (!validateDate(date))
            return;

        MCUser user = databaseManager.getIngameData(args[0]);
        if (user == null) {
            ChatUtils.writeError(sender, pluginName, "Fehler: Minecraftnick nicht gefunden");
            return;
        }
        String ingameName = user.getNickname();
        int contaoID = user.getContaoID();

        // UPDATE DATE WHEN PAY IS EXPIRED!
        databaseManager.setExpDateInMCTable(date, contaoID);

        // CONTAO GRUPPE AUF PAY SETZEN
        databaseManager.updateContaoGroup(ContaoGroup.PAY, contaoID);

        ChatUtils.writeSuccess(sender, pluginName, "Spieler '" + ingameName + "' ist nun Payuser (bezahlt bis '" + date + "')!");

        Player target = PlayerUtils.getOnlinePlayer(ingameName);
        if (target != null)
            PlayerUtils.sendSuccess(target, "Du bist nun Payuser (bezahlt bis '" + date + "')!");

        // UPDATE GROUPMANAGER-GROUP
        this.playerManager.updateGroupManagerGroup(ingameName, ContaoGroup.PAY);
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

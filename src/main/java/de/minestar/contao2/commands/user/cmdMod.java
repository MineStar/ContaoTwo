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
import de.minestar.contao2.manager.DatabaseManager;
import de.minestar.contao2.manager.PlayerManager;
import de.minestar.contao2.units.ContaoGroup;
import de.minestar.contao2.units.MCUser;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.ChatUtils;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class cmdMod extends AbstractCommand {

    private PlayerManager pManager;
    private DatabaseManager dbManager;

    public cmdMod(String syntax, String arguments, String node, PlayerManager pManager, DatabaseManager dbManager) {
        super(Core.NAME, syntax, arguments, node);
        this.pManager = pManager;
        this.dbManager = dbManager;
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

        MCUser user = dbManager.getIngameData(playerName);
        if (user == null) {
            ChatUtils.writeError(sender, pluginName, "Fehler: Minecraftnick nicht gefunden!");
            return;
        }

        String ingameName = user.getNickname();
        int contaoID = user.getContaoID();

        dbManager.setExpDateInMCTable(user.getExpDate(), contaoID);

        // CONTAO GRUPPE AUF ADMIN SETZEN
        dbManager.updateContaoGroup(ContaoGroup.MOD, contaoID);

        // PRINT INFO
        ChatUtils.writeSuccess(sender, pluginName, "Spieler '" + ingameName + "' ist nun Moderator!");

        Player target = PlayerUtils.getOnlinePlayer(ingameName);
        if (target != null)
            PlayerUtils.sendSuccess(target, "Du bist nun Moderator.");

        // UPDATE GROUPMANAGER-GROUP
        pManager.updateGroupManagerGroup(ingameName, ContaoGroup.MOD);
    }
}

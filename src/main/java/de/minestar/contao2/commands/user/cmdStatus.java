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

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.minestar.contao2.units.*;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.minestar.contao2.core.Core;
import de.minestar.contao2.manager.DatabaseManager;
import de.minestar.contao2.manager.StatisticManager;
import de.minestar.core.MinestarCore;
import de.minestar.core.units.MinestarGroup;
import de.minestar.core.units.MinestarPlayer;
import de.minestar.minestarlibrary.commands.AbstractExtendedCommand;
import de.minestar.minestarlibrary.utils.ChatUtils;
import de.minestar.minestarlibrary.utils.ConsoleUtils;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class cmdStatus extends AbstractExtendedCommand {

    private DatabaseManager databaseManager;
    private StatisticManager statisticManager;

    public cmdStatus(String syntax, String arguments, String node, DatabaseManager databaseManager, StatisticManager statisticManager) {
        super(Core.LOG_NAME, syntax, arguments, node);
        this.description = "Userstatus abfragen";
        this.databaseManager = databaseManager;
        this.statisticManager = statisticManager;
    }

    @Override
    public void execute(String[] args, Player player) {
        if (args.length == 0)
            getStatus(player.getName(), player);
        else if (checkSpecialPermission(player, "contao.rights.status"))
            getStatus(args[0], player);
    }
    @Override
    public void execute(String[] args, ConsoleCommandSender console) {
        if (args.length == 0)
            ConsoleUtils.printError(pluginName, "You don't have any stats...");
        else
            getStatus(args[0], console);
    }

    private void getStatus(String targetName, CommandSender sender) {
        // DISPLAY X REASON FOR X-USER
        if (MinestarCore.getPlayer(targetName).getMinestarGroup().equals(MinestarGroup.X)) {
            printXReason(sender, targetName);
            printBanned(sender, targetName);
            return;
        }

        OfflinePlayer player = PlayerUtils.getOnlinePlayer(targetName);
        if(player == null) {
            OfflinePlayer offlinePlayer = PlayerUtils.getOfflinePlayer(targetName);
            if(offlinePlayer == null) {
                ChatUtils.writeError(sender, pluginName, "Spieler '" + targetName + "' wurde nicht gefunden. War der Spieler schon einmal online?");
            } else {
                player = offlinePlayer;
            }
        }

        if(player == null) {
            return;
        }

        UUID uuid = player.getUniqueId();

        MCUser user = databaseManager.getIngameData(uuid);
        if (user == null) {
            // USER IS NOT A X USER AND NOT IN DATABASE
            ChatUtils.writeError(sender, pluginName, "Der Spieler '" + targetName + "' befindet sich nicht in der Datenbank!");
            this.printBanned(sender, targetName);
            return;
        }

        MinestarPlayer msPlayer = MinestarCore.getPlayer(targetName);
        if (msPlayer.isOffline()) {
            OfflinePlayer offlinePlayer = PlayerUtils.getOfflinePlayer(msPlayer.getPlayerName());
            if (offlinePlayer != null) {
                targetName = offlinePlayer.getName();
            }
        } else {
            targetName = PlayerUtils.getOnlinePlayer(targetName).getName();
        }
        ChatUtils.writeColoredMessage(sender, pluginName, ChatColor.GOLD, "Informationen ueber " + targetName);

        // We have checked whether the player exists in the database, so there
        // is no null pointer check necessary!
        printGroup(sender, user.getUserID());
        printAccountDates(sender, uuid, user.getUserID(), user.getExpDate());
        printWarnings(sender, uuid);
        printStatistics(sender, uuid);
        printBanned(sender, targetName);

    }
    private final static DateFormat FORMAT = DateFormat.getDateTimeInstance();

    private void printBanned(CommandSender sender, String targetName) {
        BanList banlist = Bukkit.getBanList(Type.NAME);
        if (!banlist.isBanned(targetName)) {
            ChatUtils.writeInfo(sender, "Der Spieler ist nicht gebannt!");
            return;
        } else {
            BanEntry banEntry = banlist.getBanEntry(targetName);
            StringBuilder msg = new StringBuilder("Der Spieler ist gebannt.");
            // Fill message with information
            if (banEntry != null) {
                // Reason
                msg.append("Grund: ").append(banEntry.getReason());
                msg.append(" ,");
                // Source == Who banned the player
                msg.append("von: ").append(banEntry.getSource());
                msg.append(", ");

                // Expiration date
                msg.append("bis: ");
                Date d = banEntry.getExpiration();
                if (d != null)
                    msg.append(FORMAT.format(d));
                else
                    msg.append("Ende aller Tage");
            }
            // Close message and send to player
            msg.append('!');
            ChatUtils.writeInfo(sender, msg.toString());
        }
    }

    private void printGroup(CommandSender caller, int contaoID) {
        ContaoGroup group = databaseManager.getContaoGroup(contaoID);

        if (group == null)
            ChatUtils.writeError(caller, "Wurde bisher keiner Gruppe zugewiesen.");
        else {
            ChatUtils.writeColoredMessage(caller, ChatColor.BLUE, "Contao Gruppe: '" + group + "'.");
        }
    }

    private void printAccountDates(CommandSender sender, UUID playerUUID, int userID, String payEndDate) {
        String[] dates = databaseManager.getAccountDates(playerUUID);
        ContaoGroup group = databaseManager.getContaoGroup(userID);
        ChatUtils.writeColoredMessage(sender, ChatColor.BLUE, "Seit dem " + dates[0] + " auf dem Server.");
        // is probe member
        if (ContaoGroup.PROBE.equals(group) && dates[1] != null)
            ChatUtils.writeColoredMessage(sender, ChatColor.BLUE, "Probeuser bis zum " + dates[1]);
        // is pay user
        if (payEndDate != null && !payEndDate.isEmpty())
            ChatUtils.writeColoredMessage(sender, ChatColor.BLUE, "Payuser bis zum " + payEndDate);
    }

    private void printWarnings(CommandSender sender, UUID playerUUID) {

        PlayerWarnings warnings = databaseManager.getsManager().getWarnings(playerUUID);
        // user has no warnings or have in the same session warnings lost
        if (warnings == null || warnings.isEmpty())
            ChatUtils.writeColoredMessage(sender, ChatColor.BLUE, "Keine Verwarnungen. Guter Junge :)");
        // user has warnings -> list them
        else {
            List<MCWarning> warnList = warnings.getWarnings();
            ChatUtils.writeColoredMessage(sender, ChatColor.RED, "Wurde " + warnList.size() + "x verwarnt!");
            for (MCWarning warning : warnList)
                ChatUtils.writeColoredMessage(sender, ChatColor.RED, warning.toString());
        }
    }

    private void printStatistics(CommandSender sender, UUID playerUUID) {
        Statistic stats = statisticManager.getPlayersStatistic(playerUUID);

        if (stats == null)
            ChatUtils.writeColoredMessage(sender, ChatColor.RED, "Hat keine Statistiken!");
        else {
            ChatUtils.writeColoredMessage(sender, ChatColor.BLUE, "Blöcke zerstört : " + stats.getTotalBreak());
            ChatUtils.writeColoredMessage(sender, ChatColor.BLUE, "Blöcke gesetzt : " + stats.getTotalPlaced());
        }
    }

    private void printXReason(CommandSender sender, String targetName) {

        MinestarPlayer mPlayer = MinestarCore.getPlayer(targetName);
        String xReason = mPlayer.getString("contao.xreason");
        if (xReason != null) {
            // ONLY PEOPLE WITH ENOUGH PERMISSION CAN SEE THE REASON
            // (FOR EXAMPLE MODS AND ADMINS)
            if (sender instanceof Player && MinestarCore.getPlayer((Player) sender).getMinestarGroup().isGroupHigher(MinestarGroup.PAY))
                ChatUtils.writeInfo(sender, "X-User Grund: " + xReason);

            ChatUtils.writeInfo(sender, "X-User Admin: " + mPlayer.getString("contao.xadmin"));
        } else {
            ChatUtils.writeError(sender, "Kein X-Grund gefunden!");
        }
    }
}

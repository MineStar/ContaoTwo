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

package de.minestar.contao2.manager;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;

import de.minestar.contao2.core.Core;
import de.minestar.contao2.units.ContaoGroup;
import de.minestar.contao2.units.MCUser;
import de.minestar.contao2.units.PlayerWarnings;
import de.minestar.contao2.units.Statistic;
import de.minestar.minestarlibrary.database.AbstractMySQLHandler;
import de.minestar.minestarlibrary.utils.ConsoleUtils;
import org.apache.commons.lang3.tuple.Triple;

public class DatabaseManager extends AbstractMySQLHandler {

    private PlayerManager playerManager;
    private StatisticManager sManager;

//    private PreparedStatement insertMCPay;
//    private PreparedStatement updateExpireDate;
//    private PreparedStatement updateGroup;
//    private PreparedStatement updateMCNick;
    private PreparedStatement addGroup;
    private PreparedStatement removeGroup;
    private PreparedStatement selectMCPPlayerByUUID;
    private PreparedStatement getPayEndDateByForumId;
    private PreparedStatement selectMCPlayerByForumId;
//    private PreparedStatement selectMCPayByName;
//    private PreparedStatement selectMCPayById;
    private PreparedStatement checkAccount;
//    private PreparedStatement selectGroup;
    private PreparedStatement selectGroups;
    private PreparedStatement selectFinalGroup;
//    private PreparedStatement selectContaoId;
    private PreparedStatement selectForumIds;
//    private PreparedStatement checkContaoId;
    private PreparedStatement checkForumId;
//    private PreparedStatement checkMCNick;
    private PreparedStatement checkMCUUID;
    private PreparedStatement checkMCNick;

    private PreparedStatement getAccountDates;
    private PreparedStatement isInProbation;
    private PreparedStatement deleteProbeStatus;
    private PreparedStatement addProbeDate;
    private PreparedStatement isProbeMember;
    private PreparedStatement convertFreeToProbe;

//    private PreparedStatement addWarning;
//    private PreparedStatement deleteWarning;

    private PreparedStatement selectAllStatistics;
//    private PreparedStatement selectAllWarnings;
    private PreparedStatement saveStatistics;

    private PreparedStatement canBeFree;
    private PreparedStatement hasUsedFreeWeek, setFreeWeekUsed;

    private PreparedStatement selectForumName;
    private PreparedStatement selectForumIdByUUID;

    private PreparedStatement promoteProbeToFree;

    private final static String minecraftNickOptionStr = "userOption7";
    private final static String minecraftUUIDOptionStr = "userOption39";
    private final static String minecraftTotalBreakOptionStr = "userOption40";
    private final static String minecraftTotalPlacedOptionStr = "userOption41";
    private final static String probeStartOptionStr = "userOption42";
    private final static String probeEndOptionStr = "userOption43";
    private final static String hasUsedFreeWeekOptionStr = "userOption45";
    private final static String forumPayUserGroupId = "32";

    public final static int GROUP_ID_PROBE = 34;
    public final static int GROUP_ID_FREE = 33;
    public final static int GROUP_ID_PAY = 32;
    public final static int GROUP_ID_MOD = 37;
    public final static int GROUP_ID_ADMIN = 13;

    private final static int PROBE_TIME = 7;

    public DatabaseManager(String NAME, File SQLConfigFile) {
        super(NAME, SQLConfigFile);
    }

    @Override
    protected void createStructure(String NAME, Connection con) throws Exception {
        // Do nothing - structure is given
    }

    @Override
    protected void createStatements(String NAME, Connection con) throws Exception {

//        insertMCPay = con.prepareStatement("INSERT INTO mc_pay (contao_user_id, minecraft_nick, expire_date, admin_nick, startDate, probeEndDate, usedFreePayWeek) VALUES (?,?,STR_TO_DATE(?,'%d.%m.%Y'),?, NOW(), ADDDATE(NOW(), INTERVAL " + PROBE_TIME + " DAY), ?)");

//        updateExpireDate = con.prepareStatement("UPDATE mc_pay SET expire_date = STR_TO_DATE(?,'%d.%m.%Y') WHERE contao_user_id = ?");

//
//         updateMCNick = con.prepareStatement("UPDATE mc_pay SET minecraft_nick = ? WHERE minecraft_nick = ?");

        addGroup = con.prepareStatement("INSERT INTO wcf1_user_to_group (userID, groupID) VALUES(?, ?)");

        removeGroup = con.prepareStatement("DELETE FROM wcf1_user_to_group WHERE userID=? AND groupID=?");

        selectMCPPlayerByUUID = con.prepareStatement("SELECT "+minecraftUUIDOptionStr+", userID FROM wcf1_user_option_value WHERE " + minecraftUUIDOptionStr + " = ? LIMIT 1");

        selectMCPlayerByForumId = con.prepareStatement("SELECT "+minecraftUUIDOptionStr+", userID FROM wcf1_user_option_value WHERE userID = ? LIMIT 1");

        getPayEndDateByForumId = con.prepareStatement("SELECT date(endDate) FROM wcf1_paid_subscription_user WHERE userID = ? AND isActive = 1 AND subscriptionID in (SELECT subscriptionID FROM wcf1_paid_subscription where groupIDs = "+forumPayUserGroupId+") LIMIT 1");

        checkAccount = con.prepareStatement("SELECT banned FROM wcf1_user WHERE userID = ? LIMIT 1");

        selectGroups = con.prepareStatement("SELECT groupID FROM wcf1_user_to_group WHERE userID = ?");

        selectForumIds = con.prepareStatement("SELECT userID, "+minecraftNickOptionStr+" FROM wcf1_user_option_value WHERE "+minecraftNickOptionStr+" LIKE ?");

        selectFinalGroup = con.prepareStatement("SELECT groupID FROM user_group_final WHERE userID = ?");

        checkForumId = con.prepareStatement("SELECT 1 FROM wcf1_user WHERE userID = ? LIMIT 1");

        selectForumIdByUUID = con.prepareStatement("SELECT userID FROM wcf1_user_option_value WHERE " + minecraftUUIDOptionStr + " = ?");

        checkMCUUID = con.prepareStatement("SELECT 1 FROM wcf1_user_option_value WHERE "+minecraftUUIDOptionStr+" = ? LIMIT 1");

        checkMCNick = con.prepareStatement("SELECT 1 FROM wcf1_user_option_value WHERE "+minecraftNickOptionStr+" = ? LIMIT 1");

        getAccountDates = con.prepareStatement("SELECT DATE_FORMAT("+ probeStartOptionStr +", '%d.%m.%Y %H:%i:%s'), DATE_FORMAT("+probeEndOptionStr+", '%d.%m.%Y %H:%i:%s') FROM wcf1_user_option_value WHERE "+minecraftUUIDOptionStr+" = ? LIMIT 1");

        deleteProbeStatus = con.prepareStatement("UPDATE wcf1_user_option_value SET "+probeEndOptionStr+" = NULL WHERE "+minecraftUUIDOptionStr+" = ?");

        convertFreeToProbe = con.prepareStatement("UPDATE wcf1_user_option_value SET "+probeEndOptionStr+" = ADDDATE(NOW(), INTERVAL " + PROBE_TIME + " DAY) WHERE minecraft_nick = ?");

        addProbeDate = con.prepareStatement("UPDATE wcf1_user_option_value SET probeEndDate = ADDDATE("+probeEndOptionStr+", INTERVAL ? DAY) WHERE userID = ?");

//        addWarning = null; //TODO con.prepareStatement("INSERT INTO mc_warning (mc_pay_id,reason,date,adminnickname) VALUES ((SELECT id FROM mc_pay WHERE minecraft_nick = ?), ?, STR_TO_DATE(?,'%d.%m.%Y %H:%i:%s'), ?)");

//        selectAllWarnings = null; //TODO con.prepareStatement("SELECT minecraft_nick, mc_warning.reason, DATE_FORMAT(date, '%d.%m.%Y %H:%i:%s'),adminnickname FROM mc_warning,mc_pay WHERE mc_warning.mc_pay_id = mc_pay.id ORDER BY minecraft_nick,mc_warning.date");

        // deleteWarning = con.prepareStatement("DELETE FROM mc_warning WHERE mc_pay_id = (SELECT id FROM mc_pay WHERE minecraft_nick = ?) AND DATE_FORMAT(date,'%d.%m.%Y %H:%i:%s') = ?");

        isProbeMember = con.prepareStatement("SELECT 1 FROM wcf1_user_option_value WHERE "+minecraftUUIDOptionStr+" = ? AND "+probeEndOptionStr+" IS NULL");

        isInProbation = con.prepareStatement("SELECT 1 FROM mc_pay WHERE "+minecraftUUIDOptionStr+" = ? AND DATEDIFF(NOW(),probeEndDate) < 0");

        selectAllStatistics = con.prepareStatement("SELECT "+minecraftUUIDOptionStr+", "+minecraftTotalBreakOptionStr+", "+minecraftTotalPlacedOptionStr+" FROM wcf1_user_option_value where "+minecraftUUIDOptionStr+" IS NOT NULL");

        saveStatistics = con.prepareStatement("UPDATE wcf1_user_option_value SET "+minecraftTotalPlacedOptionStr+" = ?, "+minecraftTotalBreakOptionStr+" = ? WHERE "+minecraftUUIDOptionStr+" = ?");

        canBeFree = con.prepareStatement("SELECT 1 FROM wcf1_user_option_value WHERE userID = ? AND "+minecraftTotalBreakOptionStr+" + "+minecraftTotalPlacedOptionStr+" >= 10000 AND DATEDIFF(NOW(), "+probeEndOptionStr+") >= 7");

        hasUsedFreeWeek = con.prepareStatement("SELECT "+hasUsedFreeWeekOptionStr+" FROM wcf1_user_option_value WHERE "+minecraftUUIDOptionStr+" = ?");

        setFreeWeekUsed = con.prepareStatement("UPDATE wcf1_user_option_value SET "+hasUsedFreeWeekOptionStr+" = 1 WHERE "+minecraftUUIDOptionStr+" = ?");

        selectForumName = con.prepareStatement("SELECT username FROM wcf1_user WHERE userID = ?");
    }

    public boolean hasUsedFreeWeek(UUID playerUUID) {
        try {
            hasUsedFreeWeek.setString(1, playerUUID.toString());
            ResultSet result = hasUsedFreeWeek.executeQuery();
            if (result.next()) {
                return result.getBoolean("usedFreePayWeek");
            }
            return true;
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't fetch results for hasUsedFreeWeek! PlayerName=" + playerUUID);
        }
        return true;
    }

    public void setFreeWeekUsed(UUID playerUUID) {
        try {
            setFreeWeekUsed.setString(1, playerUUID.toString());
            setFreeWeekUsed.executeUpdate();
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't update hasUsedFreeWeek! PlayerUUID=" + playerUUID);
        }
    }

//    public void addProbe(String playerName, int contaoID, String expDate, String modPlayer) {
//
//        // INSERT INTO mc_pay (contao_user_id, minecraft_nick, expire_date,
//        // admin_nick, startDate, probeEndDate) VALUES
//        // (?,?,STR_TO_DATE(?,'%d.%m.%Y'),?, NOW(),
//        // ADDDATE(NOW(), INTERVAL PROBE_TIME DAY))
//        try {
//            insertMCPay.setInt(1, contaoID);
//            insertMCPay.setString(2, playerName);
//            insertMCPay.setString(3, expDate);
//            insertMCPay.setString(4, modPlayer);
//            insertMCPay.setBoolean(5, false);
//            insertMCPay.executeUpdate();
//        } catch (Exception e) {
//            ConsoleUtils.printException(e, Core.NAME, "Can't insert data in the mc_pay table! PlayerName=" + playerName + ",ContaoID=" + contaoID + ",ExpDate=" + expDate + ",modPlayer=" + modPlayer);
//        }
//    }

//    public void setExpDateInMCTable(String date, int contaoID) {
//
//        // UPDATE mc_pay SET expire_date = STR_TO_DATE(?,'%d.%m.%Y') WHERE
//        // contao_user_id = ?
//        try {
//            updateExpireDate.setString(1, date);
//            updateExpireDate.setInt(2, contaoID);
//            updateExpireDate.executeUpdate();
//        } catch (Exception e) {
//            ConsoleUtils.printException(e, Core.NAME, "Can't update expire date! Date=" + date + ",ContaoID=" + contaoID);
//        }
//    }

    // SET CONTAO-GROUP IN 'tl_member'
    public void updateContaoGroup(ContaoGroup group, int contaoID) {
        //TODO
        // UPDATE tl_member SET groups = ? WHERE ID = ?
//        try {
//            updateGroup.setInt(1, group.groupID());
//            updateGroup.setInt(2, contaoID);
//            updateGroup.executeUpdate();
//        } catch (Exception e) {
//            ConsoleUtils.printException(e, Core.NAME, "Can't update Contao Member Group! GroupManagerGroup=" + group.getName() + ",ContaoGroupString=" + group.groupID() + ",ContaoID=" + contaoID);
//        }
    }

    //TODO REMOVEME KILLME
    @Deprecated
    public void promoteProbeToFree(int forumID) {

    }

    // GET INGAME DATA FROM DB
    public MCUser getIngameData(UUID playerUUID) {

        String name;
        int forumID;
        String payEndDate;

        try {
            selectMCPPlayerByUUID.setString(1, playerUUID.toString());
            ResultSet result = selectMCPPlayerByUUID.executeQuery();
            if (result.next()) {
                name = result.getString(1);
                forumID = result.getInt(2);
            } else {
                return null;
            }
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't select MCUser from wcf1_user_option_value by name! PlayerUUID=" + playerUUID);
            return null;
        }

        try {
            getPayEndDateByForumId.setInt(1, forumID);
            ResultSet result = getPayEndDateByForumId.executeQuery();
            if (result.next()) {
                payEndDate = result.getString(1);
            } else {
                return null;
            }
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't select PayEndDate from wcf1_paid_subscription by forumId! forumId=" + forumID);
            return null;
        }

        return new MCUser(name, forumID, payEndDate);

    }

    public String getForumName(int forumID) {
        String forumName = null;
        try {
            selectForumName.setInt(1, forumID);
            ResultSet result = selectForumName.executeQuery();
            if(result.next()) {
                forumName = result.getString("username");
            }
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't find forum name for forumId! forumID=" + forumID);
            return null;
        }
        return forumName;
    }



    public int getForumId(UUID playerUUID) {
        int userID = -1;
        try {
            selectForumIdByUUID.setString(1, playerUUID.toString());
            ResultSet result = selectForumIdByUUID.executeQuery();
            if(result.next()) {
                userID = result.getInt("userID");
            }
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't find forum id for playerUUID! playerUUID=" + playerUUID);
            return -1;
        }
        return userID;
    }

    // GET INGAME DATA FROM DB
//    public MCUser getIngameData(int forumID) {
//
//        String name;
//
//        try {
//            selectMCPlayerByForumId.setInt(1, forumID);
//            ResultSet result = selectMCPlayerByForumId.executeQuery();
//            if (result.next()) {
//                name = result.getString(1);
//                forumID = result.getInt(2);
//            } else {
//                return null;
//            }
//        } catch (Exception e) {
//            ConsoleUtils.printException(e, Core.NAME, "Can't select MCUser from wcf1_user_option_value by name! PlayerUUID=" + playerUUID);
//            return null;
//        }
//
//        String payEndDate = getPayEndDate(forumID);
//
//        return new MCUser(name, forumID, payEndDate);
//    }

    public String getPayEndDate(int forumID) {
        try {
            getPayEndDateByForumId.setInt(1, forumID);
            ResultSet result = getPayEndDateByForumId.executeQuery();
            if (result.next()) {
                return result.getString(1);
            } else {
                return null;
            }
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't select PayEndDate from wcf1_paid_subscription by forumId! forumId=" + forumID);
            return null;
        }
    }

    // IS ACCOUNT ACTIVATED
    public boolean isForumAccountActive(int forumId) {

        // SELECT disable FROM tl_member WHERE id = ? LIMIT 1
        try {
            checkAccount.setInt(1, forumId);
            ResultSet result = checkAccount.executeQuery();
            return result.next() && !result.getBoolean(1);
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't check whether ForumAccount is active! forumId=" + forumId);
        }

        return false;
    }

    public String getContaoGroup(int forumId) {
        try {
            selectFinalGroup.setInt(1, forumId);
            ResultSet result = selectFinalGroup.executeQuery();

            if (result.next()) {
                int forumGroup = result.getInt(1);
                return getMCGroupName(forumGroup);
            }
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't get ContaoGroupName from tl_member! ContaoID=" + forumId);
        }

        return null;
    }

    // GET MC GROUP FROM CONTAO-GROUPID
    private String getMCGroupName(int id) {
        if (id == GROUP_ID_FREE)
            return ContaoGroup.FREE.getName();
        else if (id == GROUP_ID_PAY)
            return ContaoGroup.PAY.getName();
        else if (id == GROUP_ID_ADMIN)
            return ContaoGroup.ADMIN.getName();
        else if (id == GROUP_ID_PROBE)
            return ContaoGroup.PROBE.getName();
        else if (id == GROUP_ID_MOD)
            return ContaoGroup.MOD.getName();
        return ContaoGroup.DEFAULT.getName();
    }

    // GET CONTAO ID
    public HashMap<Integer, String> getForumIDs(String username) {

        // SELECT id, username FROM tl_member WHERE username LIKE %?%
        HashMap<Integer, String> map = new HashMap<Integer, String>();

        try {
            selectForumIds.setString(1, "%" + username + "%");
            ResultSet result = selectForumIds.executeQuery();
            while (result.next())
                map.put(result.getInt("id"), result.getString(minecraftNickOptionStr));

        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't get ForumID from tl_member! Minecraft Nick=" + username);
        }

        return map;
    }

    // IS CONTAO-ID IN MC-TABLE
    public boolean checkIDInUserTable(int id) {

        try {
            checkForumId.setInt(1, id);
            return checkForumId.executeQuery().next();
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't check wether userID is existing! userID=" + id);
        }

        return false;
    }

    // IS MCNICK IN MC-TABLE
    public boolean isMCNickInUser(String name) {

        try {
            checkMCNick.setString(1, name);
            return checkMCNick.executeQuery().next();
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't check whether playerName is in wcf1_user_option! Name=" + name);
        }

        return false;
    }

    // IS MCNICK IN MC-TABLE
    public boolean isMCUUIDInUser(UUID uuid) {

        try {
            checkMCUUID.setString(1, uuid.toString());
            return checkMCUUID.executeQuery().next();
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't check whether UUID is in wcf1_user_option! UUID=" + uuid);
        }

        return false;
    }

//    public String[] getAccountDates(UUID playerUUID) {
//        try {
//            getAccountDates.setString(1, playerUUID.toString());
//            ResultSet rs = getAccountDates.executeQuery();
//            if (rs.next()) {
//                return new String[]{rs.getString(1), rs.getString(2), rs.getString(3)};
//            } else
//                return null;
//        } catch (Exception e) {
//            ConsoleUtils.printException(e, Core.NAME, "Can't select dates from mc_pay! PlayerName=" + playerName);
//        }
//
//        return null;
//    }

    public boolean addProbeTime(int days, UUID playerUUID) {

        // UPDATE mc_pay SET probeEndDate = ADDDATE(probeEndDate, INTERVAL ?
        // DAY) WHERE minecraft_nick = ?
        try {
            addProbeDate.setInt(1, days);
            addProbeDate.setString(2, playerUUID.toString());
            return addProbeDate.executeUpdate() == 1;
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't update probeEndDate in wcf1_user_options! UUID=" + playerUUID + ",Days=" + days);
        }

        return false;
    }

//    public void deleteProbeStatus(UUID playerUUID) {
//
//        // UPDATE mc_pay SET probeEndDate = NULL WHERE minecraft_nick = ?
//        try {
//            deleteProbeStatus.setString(1, playerName);
//            deleteProbeStatus.executeUpdate();
//        } catch (Exception e) {
//            ConsoleUtils.printException(e, Core.NAME, "Can't delete probe status by setting probeEndDate to NULL! PlayerName=" + playerName);
//        }
//    }

    public boolean isProbeMember(UUID playerUUID) {

        try {
            isProbeMember.setString(1, playerUUID.toString());
            return !isProbeMember.executeQuery().next();
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't check whether player is probe member! UUID=" + playerUUID);
        }

        return false;
    }

//    public boolean isInProbation(UUID playerUUID) {
//
//        try {
//            isInProbation.setString(1, playerName);
//            return isInProbation.executeQuery().next();
//        } catch (Exception e) {
//            ConsoleUtils.printException(e, Core.NAME, "Can't check whether player is in probation time! PlayerName=" + playerName);
//        }
//        return false;
//    }

//    public boolean degradeFree(UUID playerUUID) {
//
//        try {
//            convertFreeToProbe.setString(1, playerName);
//            return convertFreeToProbe.executeUpdate() == 1;
//        } catch (Exception e) {
//            ConsoleUtils.printException(e, Core.NAME, "Can't degrade a free user to probe user! PlayerName=" + playerName);
//        }
//
//        return false;
//    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

//    public boolean addWarning(UUID playerUUID, String reason, String adminName) {
//
//        //TODO
////        String date = dateFormat.format(new Date());
////        sManager.addWarning(playerName, new MCWarning(reason, date, adminName));
////
////        try {
////            addWarning.setString(1, playerName);
////            addWarning.setString(2, reason);
////            addWarning.setString(3, date);
////            addWarning.setString(4, adminName);
////            return addWarning.executeUpdate() == 1;
////        } catch (Exception e) {
////            ConsoleUtils.printException(e, Core.NAME, "Can't add a warning to a player! PlayerName=" + playerName + ",adminName=" + adminName + ",text=" + reason);
////        }
//
//        return false;
//    }

//    public boolean removeWarning(UUID playerUUID, String date) {
//        //TODO
//        try {
//            deleteWarning.setString(1, playerName);
//            deleteWarning.setString(2, date);
//            return deleteWarning.executeUpdate() == 1;
//        } catch (Exception e) {
//            ConsoleUtils.printException(e, Core.NAME, "Can't remove a warning from mc_pay! PlayerName=" + playerName + ",WarningDate=" + date);
//        }
//        return false;
//    }

    public HashMap<UUID, Statistic> loadAllStatistics() {
        HashMap<UUID, Statistic> statistics = new HashMap<>();
        try {
            ResultSet result = selectAllStatistics.executeQuery();
            while (result.next()) {
                UUID uuid;
                String uuidStr = result.getString(minecraftUUIDOptionStr);
                try {
                    uuid = UUID.fromString(uuidStr);
                } catch (IllegalArgumentException e) {
                    ConsoleUtils.printException(e, Core.NAME, "Not a UUID!!!!!! uuid : " + uuidStr);
                    continue;
                }
                statistics.put(uuid, new Statistic(result.getInt(minecraftTotalPlacedOptionStr), result.getInt(minecraftTotalBreakOptionStr)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statistics;
    }

    public HashMap<UUID, PlayerWarnings> loadAllWarnings() {
        HashMap<UUID, PlayerWarnings> warnings = new HashMap<>();
        //TODO
//        try {
//            ResultSet result = selectAllWarnings.executeQuery();
//            PlayerWarnings thisPlayer = null;
//            String playerName = null;
//            while (result.next()) {
//                playerName = result.getString("minecraft_nick").toLowerCase();
//                thisPlayer = warnings.get(playerName);
//                if (thisPlayer == null) {
//                    thisPlayer = new PlayerWarnings();
//                    warnings.put(playerName, thisPlayer);
//                }
//                thisPlayer.addWarning(new MCWarning(result.getString(2), result.getString(3), result.getString(4)));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return warnings;
    }

    public void saveStatistics(UUID playerUUID, int totalPlaced, int totalBreak) {
        try {
            saveStatistics.setInt(1, totalPlaced);
            saveStatistics.setInt(2, totalBreak);
            saveStatistics.setString(3, playerUUID.toString());
            saveStatistics.executeUpdate();
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't store statistics to database! playerUUID=" + playerUUID + ",totalPlaced=" + totalPlaced + ",totalBreak=" + totalBreak);
        }
    }

//    public void performContaoCheck(UUID playerUUID, String group) {
//        // GET INGAME-NAME AND CONTAO-ID
//        MCUser user = getIngameData(playerUUID);
//        if (user == null) {
//            // NO NICK FOUND = return;
//            return;
//        }
//
//        // GET CONTAO GROUP
//        String contaoGroup = getContaoGroup(user.getUserID());
//        if (contaoGroup == null) {
//            // NO CONTAOUSER FOUND = RETURN
//            return;
//        }
//
//        // CONTAO GROUP DIFFERS = SET MC GROUP TO CONTAO GROUP
//        if (!contaoGroup.equalsIgnoreCase(group)) {
//            String oldGroup = group;
//            group = playerManager.updateGroupManagerGroup(playerName, contaoGroup);
//            ConsoleUtils.printWarning(Core.NAME, "Player '" + playerName + "'(MCNick is '" + user.getNickname() + "' ) has a different contao( " + contaoGroup + " ) and groupmanager( " + oldGroup + " )-group!");
//        }
//
//        // Check if paytime is expired
////        if (group.equals(ContaoGroup.PAY.getName()))
////            checkPayUser(playerName, user);
//        // Check if probe user has ended probationtime with 10k changed
//        // blocks and no warnings
//        else if (group.equals(ContaoGroup.PROBE.getName()))
//            checkProbeUser(playerName, user);
//    }

//    private void checkPayUser(String playerName, MCUser user) {
//
//        if (user.getExpDate().equalsIgnoreCase("11.11.1111"))
//            throw new RuntimeException(user.getNickname() + " is payUser, but has expireDate 11.11.1111!");
//
//        // CHECK DATES
//        try {
//            DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
//            Date expDate = (Date) formatter.parse(user.getExpDate());
//            expDate = getRelativeDate(expDate, 1);
//            Date now = new Date();
//
//            // STILL PAY-ACCOUNT
//            if (expDate.after(now))
//                return;
//
//            // MOVE PAY TO FREE
//            playerManager.updateGroupManagerGroup(playerName, ContaoGroup.FREE);
//
//            setExpDateInMCTable("11.11.1111", user.getUserID());
//            updateContaoGroup(ContaoGroup.FREE, user.getUserID());
//            ConsoleUtils.printInfo(Core.NAME, "Player '" + playerName + "'s Payaccount has expired! Moving to free member!");
//        } catch (ParseException e) {
//            ConsoleUtils.printException(e, Core.NAME, "Can't parse expire date! PlayerName=" + playerName + ",Date=" + user.getExpDate());
//        }
//    }

    public boolean canBeFree(int forumID) {
        boolean can = false;
        try {
            canBeFree.setInt(1, forumID);
            ResultSet result = canBeFree.executeQuery();
            if(result.next() && result.getBoolean(1)) {
                can = true;
            }
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't find forum name for forumId! forumID=" + forumID);
            return false;
        }
        return can;
    }

//    private void checkProbeUser(UUID playerUUID, MCUser user) {
//
//        try {
//            // SELECT 1 FROM mc_pay WHERE minecraft_nick = ? AND totalBreak +
//            // totalPlaced >= 10000 AND DATEDIFF(NOW(), probeEndDate) >= 7
//            canBeFree.setInt(1, user.getUserID());
//            ResultSet rs = canBeFree.executeQuery();
//
//            // Query return nothing or that the user doesn't accomblish the
//            // conditions
//            if (!(rs.next() && rs.getBoolean(1)))
//                return;
//
//            // Check warning status
//
//            // Returns an empty resultset if the user has no warnings
//            if (this.sManager.getWarnings(playerName) != null && !this.sManager.getWarnings(playerName).isEmpty())
//                return;
//
//            // ProbeUser did enough to be a free user
//            playerManager.updateGroupManagerGroup(playerName, ContaoGroup.FREE);
//            updateContaoGroup(ContaoGroup.FREE, user.getUserID());
//        } catch (Exception e) {
//            ConsoleUtils.printException(e, Core.NAME, "Can't check probe user whether he can be a free member! PlayerName=" + playerName);
//        }
//    }

    // GET RELATIVE DAYS
//    private Date getRelativeDate(Date thisDate, int days) {
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(thisDate);
//        cal.add(Calendar.DAY_OF_YEAR, days);
//        return cal.getTime();
//    }

    public void initManager(PlayerManager pManager, StatisticManager sManager) {
        this.playerManager = pManager;
        this.sManager = sManager;
    }

    /**
     * @return the pManager
     */
    public PlayerManager getpManager() {
        return playerManager;
    }

    /**
     * @return the sManager
     */
    public StatisticManager getsManager() {
        return sManager;
    }
    
//    public boolean updateMCNick(String oldPlayer, String newPlayer) {
//        try {
//            // UPDATE THE WARP NAME
//            updateMCNick.setString(1, newPlayer);
//            updateMCNick.setString(2, oldPlayer);
//            updateMCNick.executeUpdate();
//            return true;
//        } catch (Exception e) {
//            ConsoleUtils.printException(e, Core.NAME, "Can't change MC-Nick " + oldPlayer + " to " + newPlayer);
//            return false;
//        }
//    }
}
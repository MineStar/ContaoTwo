package de.minestar.contao2.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.bukkit.gemo.utils.UtilPermissions;

import de.minestar.contao2.units.ContaoGroup;
import de.minestar.contao2.units.Settings;
import de.minestar.core.MinestarCore;
import de.minestar.core.units.MinestarPlayer;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class PlayerManager {

    private Settings settings;

    private HashMap<ContaoGroup, String> onlineList;
    private HashMap<ContaoGroup, HashSet<String>> groupMap;
    private ConcurrentHashMap<String, ContaoGroup> playerMap;

    public PlayerManager(Settings settings) {
        this.settings = settings;
        this.onlineList = new HashMap<ContaoGroup, String>();
        this.groupMap = new HashMap<ContaoGroup, HashSet<String>>();
        this.playerMap = new ConcurrentHashMap<String, ContaoGroup>();
        this.initGroupMap();
        this.onReload();
    }

    private void initGroupMap() {
        for (ContaoGroup group : ContaoGroup.values()) {
            this.groupMap.put(group, new HashSet<String>());
        }
    }

    private void onReload() {
        Player[] playerList = Bukkit.getOnlinePlayers();
        for (Player player : playerList) {
            this.updatePlayer(player);
        }
        // UPDATE ONLINE-LISTS
        this.updateOnlineLists();
    }

    public void updatePlayer(String playerName) {
        // REMOVE PLAYER FROM THE GROUPMAP
        this.removePlayer(playerName);

        // ADD PLAYER
        this.addPlayer(playerName);
    }

    public void updatePlayer(Player player) {
        this.updatePlayer(player.getName());
    }

    public void printOnlineList(Player player) {
        this.printSingleGroup(ChatColor.RED, ContaoGroup.ADMIN, player);
        this.printSingleGroup(ChatColor.AQUA, ContaoGroup.PAY, player);
        this.printSingleGroup(ChatColor.GREEN, ContaoGroup.FREE, player);
        this.printSingleGroup(ChatColor.DARK_PURPLE, ContaoGroup.PROBE, player);
        this.printSingleGroup(ChatColor.GRAY, ContaoGroup.DEFAULT, player);
        this.printSingleGroup(ChatColor.DARK_GRAY, ContaoGroup.X, player);
    }

    private void printSingleGroup(ChatColor color, ContaoGroup group, Player player) {
        if (this.groupMap.get(group).size() > 0)
            PlayerUtils.sendBlankMessage(player, color + group.name() + " ( " + this.groupMap.get(group).size() + " ) : " + this.onlineList.get(group));
    }

    public void printOnlineList(ConsoleCommandSender sender) {
        this.printSingleGroup(ContaoGroup.ADMIN, sender);
        this.printSingleGroup(ContaoGroup.PAY, sender);
        this.printSingleGroup(ContaoGroup.FREE, sender);
        this.printSingleGroup(ContaoGroup.PROBE, sender);
        this.printSingleGroup(ContaoGroup.DEFAULT, sender);
        this.printSingleGroup(ContaoGroup.X, sender);
    }

    public String updateGroupManagerGroup(String playerName, String groupName) {
        String newGroup = MinestarCore.getPlayer(playerName).setGroup(groupName);
        this.updatePlayer(playerName);
        this.updateOnlineLists();
        return newGroup;
    }

    private void printSingleGroup(ContaoGroup group, ConsoleCommandSender sender) {
        sender.sendMessage(group.name() + " ( " + this.groupMap.get(group).size() + " ) : " + this.onlineList.get(group));
    }

    public void updateOnlineLists() {
        for (Map.Entry<ContaoGroup, HashSet<String>> entry : this.groupMap.entrySet()) {
            String thisString = "";
            for (String thisPlayer : entry.getValue()) {
                thisString += thisPlayer;
                thisString += ", ";
            }
            if (thisString.length() > 2)
                thisString = thisString.substring(0, thisString.length() - 2);
            this.onlineList.put(entry.getKey(), thisString);
        }
    }

    private void addPlayer(String playerName) {
        // GET GROUP & PLAYER
        MinestarPlayer thisPlayer = MinestarCore.getPlayer(playerName);
        ContaoGroup thisGroup = ContaoGroup.getGroup(thisPlayer.getGroup());
        if (thisGroup == null)
            thisGroup = ContaoGroup.DEFAULT;

        // FINALLY ADD THE PLAYER
        this.playerMap.put(playerName, thisGroup);
        this.groupMap.get(thisGroup).add(playerName);
    }

    public void removePlayer(String playerName) {
        // REMOVE PLAYER FROM THE GROUPMAP
        ContaoGroup thisGroup = this.playerMap.get(playerName);
        if (thisGroup != null)
            this.groupMap.get(thisGroup).remove(playerName);

        // REMOVE PLAYER FROM THE PLAYERMAP
        this.playerMap.remove(playerName);
    }

    public ContaoGroup getGroup(Player player) {
        return this.getGroup(player.getName());
    }

    public ContaoGroup getGroup(String playerName) {
        return this.playerMap.get(playerName);
    }

    /**
     * Use this for player who <b>are not</b> in the HashSets! These are people
     * who are connecting the to server or they are offline!
     * 
     * @param playerName
     *            The name of the player
     * @param group
     * @return True when player is in group
     */
    public boolean isOfflinePlayerInGroup(String playerName, ContaoGroup group) {
        String groupName = UtilPermissions.getGroupName(playerName, "world");
        switch (group) {
            case ADMIN :
                return groupName.equalsIgnoreCase(ContaoGroup.ADMIN.getName());
            case MOD :
                return groupName.equalsIgnoreCase(ContaoGroup.MOD.getName());
            case PAY :
                return groupName.equalsIgnoreCase(ContaoGroup.PAY.getName()) || groupName.equalsIgnoreCase(ContaoGroup.MOD.getName()) || groupName.equalsIgnoreCase(ContaoGroup.ADMIN.getName());
            case FREE :
                return groupName.equalsIgnoreCase(ContaoGroup.FREE.getName());
            case PROBE :
                return groupName.equalsIgnoreCase(ContaoGroup.PROBE.getName());
            case DEFAULT :
                return groupName.equalsIgnoreCase(ContaoGroup.DEFAULT.getName());
            case X :
                return groupName.equalsIgnoreCase(ContaoGroup.X.getName());
            default :
                throw new RuntimeException("Wrong groupId " + group.ordinal());
        }
    }

    public boolean isInGroup(Player player, ContaoGroup group) {
        return this.isInGroup(player.getName(), group);
    }

    public boolean isInGroup(String playerName, ContaoGroup group) {
        return this.getGroup(playerName).equals(group);
    }

    public int getFreeSlots() {
        return this.settings.getFreeSlots() - this.groupMap.get(ContaoGroup.FREE).size();
    }
}

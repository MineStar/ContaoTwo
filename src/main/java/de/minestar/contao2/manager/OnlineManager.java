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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.minestar.contao2.core.Core;
import de.minestar.contao2.units.ContaoGroup;
//import de.minestar.contao2.units.Settings;
import de.minestar.minestarlibrary.database.AbstractMySQLHandler;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public class OnlineManager extends AbstractMySQLHandler {

    private PlayerManager playerManager;

    private PreparedStatement updateServer;
    
    //private PreparedStatement insertMCPay;
    
    private String serverName;

    public OnlineManager(String NAME, File SQLConfigFile)
    {
        super(NAME, SQLConfigFile);
    }

    @Override
    protected void createStructure(String NAME, Connection con) throws Exception
    {
        // Do nothing - structure is given
    }

    @Override
    protected void createStatements(String NAME, Connection con) throws Exception
    {
        updateServer = con.prepareStatement("UPDATE ServerStatus set active_Users=?, max_Users=?, Port=?, Users=?, lastupdate=? WHERE Int_Name=?");   
    }
 
    public void initManager(PlayerManager pManager)
    {
        this.playerManager = pManager;
        this.serverName = Bukkit.getServerName();
    }

    /**
     * @return the pManager
     */
    public PlayerManager getpManager()
    {
        return playerManager;
    }
    
    
    public void updatePlayerList(Player quitPlayer)
    {
        try
        {
            String Users = "";
            DateFormat df = new SimpleDateFormat("dd.MM.yyyy kk:mm");
            String upDate = df.format(new Date()).toString();
            String prefix = "";
            StringBuilder sb = new StringBuilder();
          
            for(Player player : Bukkit.getOnlinePlayers())
            {
                if(quitPlayer != null && quitPlayer.getName() == player.getName())
                {
                    // nop :)  
                }
                else
                {
                    ContaoGroup group = playerManager.getGroup(player);
                    sb.append(prefix);
                    prefix = ",";
                    sb.append(group.getShort() + " " + player.getName());
                }
            }
            Users = sb.toString();
            if (quitPlayer == null)
                updateServer.setInt(1, Bukkit.getOnlinePlayers().size());
            else
                updateServer.setInt(1, Bukkit.getOnlinePlayers().size() - 1);
            updateServer.setInt(2, Bukkit.getMaxPlayers());
            updateServer.setInt(3, Bukkit.getPort());
            updateServer.setString(4, Users);
            updateServer.setString(5, upDate);
            updateServer.setString(6, this.serverName);
            updateServer.executeUpdate();
        }
        catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Something went wrong");
        }
    }
}
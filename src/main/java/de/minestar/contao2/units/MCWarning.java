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

package de.minestar.contao2.units;

import org.bukkit.ChatColor;

public class MCWarning {

    private final String reason;
    private final String date;
    private final String admin;

    // (AdminName) + Date in dd.MM.yyyy HH.mm + Reason
    private static String message = ChatColor.WHITE + "(%s) " + ChatColor.BLUE + "%s : " + ChatColor.RED + "%s";

    public MCWarning(String reason, String date, String admin) {
        this.reason = reason;
        this.date = date;
        this.admin = admin;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return String.format(message, admin, date, reason);
    }
}

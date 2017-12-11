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

public class MCUser {
    private final String expDate;
    private final String nickname;
    private final int userID;

    public MCUser(String nickname, int userID, String expDate) {
        this.nickname = nickname;
        this.userID = userID;
        this.expDate = expDate;
    }

    public String getExpDate() {
        return expDate;
    }

    public String getNickname() {
        return nickname;
    }

    public int getUserID() {
        return userID;
    }
}

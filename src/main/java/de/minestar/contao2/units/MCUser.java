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
    private final int contaoID;

    public MCUser(String nickname, int contaoID, String expDate) {
        this.nickname = nickname;
        this.contaoID = contaoID;
        this.expDate = expDate;
    }

    public String getExpDate() {
        return expDate;
    }

    public String getNickname() {
        return nickname;
    }

    public int getContaoID() {
        return contaoID;
    }
}

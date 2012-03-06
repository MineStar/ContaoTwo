/*
 * Copyright (C) 2011 MineStar.de 
 * 
 * This file is part of 'ContaoPlugin'.
 * 
 * 'ContaoPlugin' is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * 'ContaoPlugin' is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with 'ContaoPlugin'.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * AUTHOR: GeMoschen
 * 
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

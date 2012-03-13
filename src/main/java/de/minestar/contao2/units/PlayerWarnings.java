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

import java.util.ArrayList;

public class PlayerWarnings {
    private ArrayList<MCWarning> warnList = new ArrayList<MCWarning>();

    public void addWarning(MCWarning warning) {
        this.warnList.add(warning);
    }

    public ArrayList<MCWarning> getWarnings() {
        return this.warnList;
    }

    public void removeWarning(int ID) {
        if (ID < 0 || ID >= this.warnList.size())
            return;
        this.warnList.remove(ID);
    }

    public MCWarning getWarning(int ID) {
        if (ID < 0 || ID >= this.warnList.size())
            return null;
        else
            return this.warnList.get(ID);
    }

    public boolean isEmpty() {
        return warnList.isEmpty();
    }
}

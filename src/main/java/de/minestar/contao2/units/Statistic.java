/*
 * Copyright (C) 2011 MineStar.de 
 * 
 * This file is part of ContaoPlugin.
 * 
 * ContaoPlugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * ContaoPlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ContaoPlugin.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.minestar.contao2.units;

public class Statistic {

    private int totalPlaced;
    private int totalBreak;
    private boolean hasChanged = false;

    public Statistic(int totalPlaced, int totalBreak) {
        this.totalPlaced = totalPlaced;
        this.totalBreak = totalBreak;
    }

    public int getTotalPlaced() {
        return totalPlaced;
    }

    public int getTotalBreak() {
        return totalBreak;
    }

    public void incrementPlace() {
        ++totalPlaced;
        this.setHasChanged(true);
    }

    public void incrementBreak() {
        ++totalBreak;
        this.setHasChanged(true);
    }

    /**
     * @return the hasChanged
     */
    public boolean hasChanged() {
        return hasChanged;
    }

    /**
     * @param hasChanged
     *            the hasChanged to set
     */
    public void setHasChanged(boolean hasChanged) {
        this.hasChanged = hasChanged;
    }
}

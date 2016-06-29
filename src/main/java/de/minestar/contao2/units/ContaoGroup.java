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

import de.minestar.core.units.MinestarGroup;

public enum ContaoGroup {

    //@formatter:off
    ADMIN   (MinestarGroup.ADMIN,   "a:2:{i:0;s:1:\"3\";i:1;s:1:\"2\";}"),
    MOD     (MinestarGroup.MOD,     "a:2:{i:0;s:1:\"6\";i:1;s:1:\"2\";}"),
    PAY     (MinestarGroup.PAY,     "a:1:{i:0;s:1:\"2\";}"),
    FREE    (MinestarGroup.FREE,    "a:1:{i:0;s:1:\"1\";}"),
    PROBE   (MinestarGroup.PROBE,   "a:1:{i:0;s:1:\"5\";}"),
    DEFAULT (MinestarGroup.DEFAULT, "a:1:{i:0;s:1:\"4\";}"),
    X       (MinestarGroup.X,       "a:1:{i:0;s:1:\"4\";}");
    //@formatter:on

    private final MinestarGroup group;
    // The serialized string in contao database
    private final String contaoString;

    private ContaoGroup(MinestarGroup group, String contaoString) {
        this.contaoString = contaoString;
        this.group = group;
    }

    /** @return The GroupManager group name as defined in the group.yml */
    public String getName() {
        return group.getName();
    }

    public String getShort() {
        return group.getShort();
    }
    
    /**
     * @return The serialized String in the contao database representing the
     *         group of member
     */
    public String getContaoString() {
        return contaoString;
    }

    public static ContaoGroup getGroup(String groupName) {
        for (ContaoGroup group : ContaoGroup.values())
            if (group.getName().equalsIgnoreCase(groupName))
                return group;
        return ContaoGroup.DEFAULT;
    }

    public boolean isHigher(ContaoGroup that) {
        return this.ordinal() < that.ordinal();
    }
}

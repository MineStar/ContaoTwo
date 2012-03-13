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

public enum ContaoGroup {
    //@formatter:off
    ADMIN   ("admins",  "a:2:{i:0;s:1:\"3\";i:1;s:1:\"2\";}", 50),
    MOD     ("mods",    "a:2:{i:0;s:1:\"6\";i:1;s:1:\"2\";}", 40),
    PAY     ("pay",     "a:1:{i:0;s:1:\"2\";}", 30),
    FREE    ("vip",     "a:1:{i:0;s:1:\"1\";}", 20),
    PROBE   ("probe",   "a:1:{i:0;s:1:\"5\";}", 10),
    DEFAULT ("default", "a:1:{i:0;s:1:\"4\";}", 0),
    X       ("X",       "a:1:{i:0;s:1:\"4\";}", -10);
    //@formatter:on

    // The groupmnanger groupname
    private String name;
    // The serialized string in contao database
    private String contaoString;
    // The level of this group: high levels are groups with more permissions
    private int level;

    private ContaoGroup(String name, String contaoString, int level) {
        this.name = name;
        this.contaoString = contaoString;
        this.level = level;
    }

    /** @return The GroupManager group name as defined in the group.yml */
    public String getName() {
        return name;
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

    public int getLevel() {
        return this.level;
    }

    public boolean isGroupHigher(ContaoGroup otherGroup) {
        if (otherGroup == null)
            return false;
        return this.level > otherGroup.getLevel();
    }
}

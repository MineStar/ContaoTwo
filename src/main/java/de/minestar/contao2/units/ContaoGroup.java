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

import de.minestar.contao2.manager.DatabaseManager;
import de.minestar.core.units.MinestarGroup;

public enum ContaoGroup {

    //@formatter:off
    ADMIN   (MinestarGroup.ADMIN,   DatabaseManager.GROUP_ID_ADMIN),
    MOD     (MinestarGroup.MOD,     DatabaseManager.GROUP_ID_MOD),
    PAY     (MinestarGroup.PAY,     DatabaseManager.GROUP_ID_PAY),
    FREE    (MinestarGroup.FREE,    DatabaseManager.GROUP_ID_FREE),
    PROBE   (MinestarGroup.PROBE,   DatabaseManager.GROUP_ID_PROBE),
    DEFAULT (MinestarGroup.DEFAULT, -1),
    X       (MinestarGroup.X,       -2);
    //@formatter:on

    private final MinestarGroup group;

    private final int groupID;

    private ContaoGroup(MinestarGroup group, int groupID) {
        this.groupID = groupID;
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
     * @return The DB Group ID
     */
    public int groupID() {
        return groupID;
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

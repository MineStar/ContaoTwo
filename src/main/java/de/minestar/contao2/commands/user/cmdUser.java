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

package de.minestar.contao2.commands.user;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.minestar.contao2.core.Core;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.commands.AbstractSuperCommand;

public class cmdUser extends AbstractSuperCommand {

    public cmdUser(String syntax, String arguments, String node, AbstractCommand... subCommands) {
        super(Core.pluginName, syntax, arguments, node, false, subCommands);
    }

    @Override
    public void execute(String[] args, Player player) {
        // Do nothing
    }

    @Override
    public void execute(String[] args, ConsoleCommandSender console) {
        // Do nothing
    }
}

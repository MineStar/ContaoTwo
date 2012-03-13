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

package de.minestar.contao2.commands.ppay;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.minestar.contao2.core.Core;
import de.minestar.contao2.units.Settings;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.ChatUtils;

public class cmdSet extends AbstractCommand {

    private Settings setting;

    public cmdSet(String syntax, String arguments, String node, Settings setting) {
        super(Core.pluginName, syntax, arguments, node);
        this.setting = setting;
    }

    @Override
    public void execute(String[] args, Player player) {
        setSlots(args, player);
    }

    @Override
    public void execute(String[] args, ConsoleCommandSender console) {
        setSlots(args, console);
    }

    private void setSlots(String[] args, CommandSender sender) {
        String option = args[0];
        if (!args[1].matches("\\d*")) {
            ChatUtils.writeError(sender, pluginName, getHelpMessage());
            return;
        }
        int slots = Integer.parseInt(args[1]);

        if (option.equalsIgnoreCase("freeslots")) {
            setting.setFreeSlots(slots);
            ChatUtils.writeSuccess(sender, pluginName, "FreeUser-Slots set to " + slots);
        } else if (option.equalsIgnoreCase("maxSlots")) {
            setting.setMaxSlots(slots);
            ChatUtils.writeSuccess(sender, pluginName, "PublicSlots set to " + slots);
        } else
            ChatUtils.writeError(sender, pluginName, "Use for option 'freeslots' or 'maxSlots'");
    }
}

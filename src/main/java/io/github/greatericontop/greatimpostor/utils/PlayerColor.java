package io.github.greatericontop.greatimpostor.utils;

/*
 * Copyright (C) 2023-present greateric.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty  of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

public enum PlayerColor {
    DARK_BLUE("§x§2§2§2§2§c§cDark Blue", 0x0000aa),
    DARK_GREEN("§2Dark Green", 0x00aa00),
    DARK_AQUA("§x§2§2§c§c§c§cDark Aqua", 0x009999),
    DARK_RED("§4Dark Red", 0xaa0000),
    DARK_PURPLE("§5Dark Purple", 0xaa00aa),
    GOLD("§6Gold", 0xffaa00),
    GRAY("§7Gray", 0x999999),
    DARK_GRAY("§8Dark Gray", 0x333333),
    BLUE("§9Blue", 0x5555ff),
    GREEN("§aGreen", 0x55ff55),
    AQUA("§bAqua", 0x44ffff),
    RED("§cRed", 0xff5555),
    LIGHT_PURPLE("§dPink", 0xff55ff),
    YELLOW("§eYellow", 0xffff55),
    WHITE("§fWhite", 0xffffff)

    ;

    private final String displayName;
    private final int colorCode;

    public String getDisplayName() {
        return displayName;
    }
    public int getColorCode() {
        return colorCode;
    }

    PlayerColor(String displayName, int colorCode) {
        this.displayName = displayName;
        this.colorCode = colorCode;
    }

}

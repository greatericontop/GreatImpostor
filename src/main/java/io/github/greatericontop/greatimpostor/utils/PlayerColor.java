package io.github.greatericontop.greatimpostor.utils;

public enum PlayerColor {
    DARK_GREEN("§2Dark Green", 0x00aa00),
    DARK_AQUA("§3Dark Aqua", 0x00aaaa),
    DARK_RED("§4Dark Red", 0xaa0000),
    DARK_PURPLE("§5Dark Purple", 0xaa00aa),
    GOLD("§6Gold", 0xffaa00),
    GRAY("§7Gray", 0x999999),
    DARK_GRAY("§8Dark Gray", 0x333333),
    BLUE("§9Blue", 0x5555ff),
    GREEN("§aGreen", 0x55ff55),
    AQUA("§bAqua", 0x55ffff),
    RED("§cRed", 0xff5555),
    LIGHT_PURPLE("§dLight Purple", 0xff55ff),
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

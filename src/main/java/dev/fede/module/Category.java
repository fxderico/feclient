package dev.fede.module;

/**
 * All module categories in feClient.
 * iconId maps to assets/feclient/icons/<iconId>.svg
 */
public enum Category {
    COMBAT   ("Combat",    "combat"),
    MOVEMENT ("Movement",  "movement"),
    PLAYER   ("Player",    "player"),
    RENDER   ("Render",    "render"),
    WORLD    ("World",     "world"),
    DONUT    ("Donut",     "misc"),      // server-specific (DonutSMP etc.)
    ADDONS   ("Addons",    "visuals"),
    CLIENT   ("Client",    "client"),
    MISC     ("Misc",      "misc"),     // legacy bucket, kept for compat
    THEMES   ("Themes",   "themes");   // added for Zenith UI MenuThemeElement

    private final String displayName;
    private final String iconId;

    Category(String displayName, String iconId) {
        this.displayName = displayName;
        this.iconId      = iconId;
    }

    public String getDisplayName() { return displayName; }
    public String getIconId()      { return iconId; }

    /** Alias for getDisplayName() — used by Zenith UI. */
    public String getName()  { return displayName; }
    /** Alias for getIconId() — used by Zenith UI. */
    public String getIcon()  { return iconId; }
}

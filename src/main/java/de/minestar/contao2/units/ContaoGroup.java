package de.minestar.contao2.units;

public enum ContaoGroup {
    //@formatter:off
    ADMIN   ("admins",  "a:2:{i:0;s:1:\"3\";i:1;s:1:\"2\";}"),
    MOD     ("mods",    "a:2:{i:0;s:1:\"6\";i:1;s:1:\"2\";}"),
    PAY     ("pay",     "a:1:{i:0;s:1:\"2\";}"),
    FREE    ("vip",     "a:1:{i:0;s:1:\"1\";}"),
    PROBE   ("probe",   "a:1:{i:0;s:1:\"5\";}"),
    DEFAULT ("default", "a:1:{i:0;s:1:\"4\";}"),
    X       ("X",       "a:1:{i:0;s:1:\"4\";}");
    //@formatter:on

    // The groupmnanger groupname
    private String name;
    // The serialized string in contao database
    private String contaoString;

    private ContaoGroup(String name, String contaoString) {
        this.name = name;
        this.contaoString = contaoString;
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
        throw new RuntimeException("Unknown group name '" + groupName + "'!");
    }
}

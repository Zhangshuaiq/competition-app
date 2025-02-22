package competition.serial;

public enum Command {

    HEAT("HEAT"),

    SMOD("SMOD"),

    SKEY("SKEY"),

    SDSP("SDSP"),

    SHOW("SHOW"),

    SMAT("SMAT"),

    SKUP("SKUP"),

    SBLK("SBLK"),

    STAR("STAR"),

    EEND("EEND"),

    SDUP("SDUP"),

    UKEY("UKEY"),
    ;

    private String cmd;

    Command(String cmd) {
        this.cmd = cmd;
    }

    public String getCmd() {
        return cmd;
    }

    public static Command of(String cmd) {
        for (Command value : values()) {
            if (value.cmd.equals(cmd)) {
                return value;
            }
        }
        return null;
    }
}

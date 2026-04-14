package backend;

public enum WindowId {
    MAIN("main") {
        @Override
        public WindowDefaults getDefaults() {
            return new WindowDefaults(true, 0, 0, 0, 0);
        }
    },
    LOGGER("logger") {
        @Override
        public WindowDefaults getDefaults() {
            return new WindowDefaults(false, 10, 10, 300, 800);
        }
    },
    GAME("gameWindow") {
        @Override
        public WindowDefaults getDefaults() {
            return new WindowDefaults(false, 320, 10, 400, 400);
        }
    },
    COORDS("coords") {
        @Override
        public WindowDefaults getDefaults() {
            return new WindowDefaults(false, 650, 10, 150, 80);
        }
    };

    private final String key;

    WindowId(String key) { this.key = key; }
    public String getKey() { return key; }

    public abstract WindowDefaults getDefaults();

    public record WindowDefaults(boolean maximized, int locX, int locY, int width, int height) {}
}
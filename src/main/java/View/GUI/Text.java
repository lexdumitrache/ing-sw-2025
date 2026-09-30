package View.GUI;

/**
 * Small helpers to turn identifiers and terminal output into readable GUI text.
 */
final class Text {

    private Text() {}

    /**
     * Removes the ANSI color codes used by the TUI.
     */
    static String stripAnsi(String text) {
        return text == null ? null : text.replaceAll("\u001B\\[[;\\d]*m", "");
    }

    /**
     * "PlaceComponent" -> "Place Component", "OpenSpaceEngineDeclarationState" -> "Open Space Engine Declaration"
     */
    static String humanize(String identifier) {
        if (identifier == null) {
            return "";
        }
        String name = identifier.endsWith("State") && identifier.length() > "State".length()
                ? identifier.substring(0, identifier.length() - "State".length())
                : identifier;
        name = name.replace('_', ' ');
        return name.replaceAll("(?<=[a-z0-9])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])", " ").trim();
    }

    /**
     * BATTERY_COMPARTMENT -> "Battery compartment"
     */
    static String enumName(Enum<?> value) {
        if (value == null) {
            return "";
        }
        final String name = value.name().replace('_', ' ').toLowerCase();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}

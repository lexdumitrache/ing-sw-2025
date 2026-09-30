package Model.Utils;

import Model.Enums.CardLevel;

/**
 * Utility class for mapping between JSON level strings and CardLevel enum values.
 */
public class CardLevelMapper {
    
    /**
     * Maps JSON level string to CardLevel enum
     * @param jsonLevel the level string from JSON
     * @return the corresponding CardLevel enum value
     */
    public static CardLevel mapJsonLevelToCardLevel(String jsonLevel) {
        if (jsonLevel == null) {
            throw new NullPointerException("JSON level cannot be null");
        }
        
        return switch (jsonLevel) {
            case "TRIAL" -> CardLevel.LEARNER;
            case "LEVEL1" -> CardLevel.LEVEL_ONE;
            case "LEVEL2" -> CardLevel.LEVEL_TWO;
            case "LEVEL3" -> CardLevel.LEVEL_THREE;
            default -> throw new IllegalArgumentException("Unknown card level: " + jsonLevel);
        };
    }
}
import common.DataUtilityProperties;

import java.util.Properties;

/**
 * These tests currently mutate the production database
 * Mocking the database and Data Access Objects will be
 * necessary to unit test this class properly
 */
public class PiqueDataTest {
    private final Properties prop = DataUtilityProperties.getProperties();
}

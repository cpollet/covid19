package net.cpollet.covid19.statsloader;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Properties;

public final class BuildPropertiesFactory {
    private static Properties properties;

    private BuildPropertiesFactory() {
        // nothing
    }

    public static Properties properties() {
        try {
            if (properties == null) {
                properties = new Properties();
                properties.load(App.class.getResourceAsStream("/build.properties"));
            }
            return properties;
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

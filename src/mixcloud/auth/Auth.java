package mixcloud.auth;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class Auth {

    private static final String ACCESS_TOKEN_KEY = "ACCESS_TOKEN";
    private static final String TAGS_KEY = "TAGS";

    private static Set<String> tags = null;

    public static Properties properties = null;

    /**
     * @param propertiesFile
     */
    public static void init(String propertiesFile) {

        properties = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream(propertiesFile);
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @return
     */
    public static String getAccessToken() {
        return properties.getProperty(ACCESS_TOKEN_KEY);
    }

    /**
     * @return
     */
    public static Set<String> getTags() {
        if (tags == null) {
            tags = new HashSet<>(Arrays.asList(properties.getProperty(TAGS_KEY).split(",")));
        }
        return tags;
    }
}

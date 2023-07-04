package com.activity_tracker.frontend.misc;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager
{
    private static final String CONFIG_FILE_NAME = "config.properties";
    private static Properties properties;

    public static void loadProperties(Context context)
    {
        if (properties != null)
        {
            return;
        }

        properties = new Properties();
        try
        {
            InputStream inputStream = context.getAssets().open(CONFIG_FILE_NAME);
            properties.load(inputStream);
            inputStream.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Error loading properties file: " + CONFIG_FILE_NAME, e);
        }
    }

    public static String getProperty(String key)
    {
        if (properties == null)
        {
            throw new IllegalStateException("Properties not loaded yet - call loadProperties() first");
        }
        return properties.getProperty(key);
    }
}

package me.hp888.remapper.config;

import lombok.RequiredArgsConstructor;
import me.hp888.remapper.Bootstrap;
import me.hp888.remapper.api.Remapper;
import me.hp888.remapper.api.config.ConfigurationLoader;
import me.hp888.remapper.utils.IOUtils;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * @author hp888 on 14.09.2019.
 */

@RequiredArgsConstructor
public class RemapperConfigurationLoader implements ConfigurationLoader
{
    private final File configFile = new File("config.json");
    private final Remapper remapper;

    @Override
    public void load() throws IOException {
        if (!configFile.exists())
            IOUtils.copy(Bootstrap.class.getResourceAsStream("/config.json"), new FileOutputStream(configFile));

        remapper.setConfiguration(remapper.getGson().fromJson(IOUtils.readJsonString(new FileInputStream(configFile)), RemapperConfiguration.class));
    }
}
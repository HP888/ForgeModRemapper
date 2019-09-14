package me.hp888.remapper.api;

import com.google.gson.Gson;
import me.hp888.remapper.api.config.Configuration;
import me.hp888.remapper.api.mappings.MappingObject;
import me.hp888.remapper.api.mappings.MappingType;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * @author hp888 on 14.09.2019.
 */

public interface Remapper
{
    MappingType getMappingType(@NotNull final String ldc);

    Optional<MappingObject> getMapping(@NotNull final MappingType mappingType, @NotNull final String obfuscatedName);

    Configuration getConfiguration();

    Logger getLogger();

    Gson getGson();

    void remap();

    void loadMappings();

    void loadConfiguration();

    void safeExecute(@NotNull final SafeExecutor safeExecutor);

    void setConfiguration(@NotNull final Configuration configuration);

    void setMappings(@NotNull final Map<MappingType, List<MappingObject>> mappings);
}
package me.hp888.remapper;

import me.hp888.remapper.api.Remapper;

/**
 * @author hp888 on 14.09.2019.
 */

public final class Bootstrap
{
    public static void main(final String... args) {
        final Remapper remapper = new ModRemapper();
        remapper.loadConfiguration();
        remapper.loadMappings();
        remapper.remap();
    }
}
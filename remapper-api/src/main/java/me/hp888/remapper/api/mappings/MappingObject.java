package me.hp888.remapper.api.mappings;

import lombok.Data;

/**
 * @author hp888 on 14.09.2019.
 */

@Data
public class MappingObject
{
    private final String obfuscatedName, originalName;
}
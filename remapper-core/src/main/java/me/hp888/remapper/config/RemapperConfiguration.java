package me.hp888.remapper.config;

import lombok.Data;
import me.hp888.remapper.api.config.Configuration;

/**
 * @author hp888 on 14.09.2019.
 */

@Data
class RemapperConfiguration implements Configuration
{
    private String mcVersion, modPath;
}
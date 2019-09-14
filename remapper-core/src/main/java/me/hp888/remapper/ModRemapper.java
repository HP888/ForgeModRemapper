package me.hp888.remapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import me.hp888.remapper.api.Remapper;
import me.hp888.remapper.api.SafeExecutor;
import me.hp888.remapper.api.asm.ClassWrapper;
import me.hp888.remapper.api.config.Configuration;
import me.hp888.remapper.api.mappings.MappingObject;
import me.hp888.remapper.api.mappings.MappingType;
import me.hp888.remapper.config.RemapperConfigurationLoader;
import me.hp888.remapper.mappings.MappingsLoaderImpl;
import me.hp888.remapper.utils.JarUtils;
import me.hp888.remapper.visitor.RemapperClassVisitor;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * @author hp888 on 14.09.2019.
 */

@Getter
@Setter
public class ModRemapper implements Remapper
{
    private final Gson gson;
    private final Logger logger;

    ModRemapper() {
        gson = new GsonBuilder().create();
        logger = Logger.getLogger("ForgeModRemapper");
    }

    private Map<MappingType, List<MappingObject>> mappings;
    private Configuration configuration;

    @Override
    public void remap() {
        final File modFile = new File(configuration.getModPath());
        if (!modFile.exists()) {
            logger.severe("ForgeMod's path written in configuration is wrong.");
            return;
        }

        this.safeExecute(() -> {
            final String path = modFile.getAbsolutePath();
            final int index = path.lastIndexOf(File.separator);
            final Map<String, ClassWrapper> classes = JarUtils.readClasses(modFile);
            final Map<String, byte[]> otherFiles = JarUtils.readFiles(modFile);
            classes.forEach((className, classWrapper) -> {
                final ClassNode copy = new ClassNode();
                classWrapper.getClassNode().accept(new RemapperClassVisitor(this, copy));
                classWrapper.setClassNode(copy);
            });

            JarUtils.saveJar(new File(path.substring(0, index), path.substring(index + 1).replace(".jar", "-remapped.jar")), classes, otherFiles);
        });
    }

    @Override
    public void loadMappings() {
        this.safeExecute(() -> new MappingsLoaderImpl(this).load());
    }

    @Override
    public void loadConfiguration() {
        this.safeExecute(() -> new RemapperConfigurationLoader(this).load());
    }

    @Override
    public void safeExecute(@NotNull final SafeExecutor safeExecutor) {
        try {
            safeExecutor.execute();
        } catch (final Throwable throwable) {
            logger.log(Level.SEVERE, "Something went wrong..", throwable);
            System.exit(-1);
        }
    }

    @Override
    public MappingType getMappingType(@NotNull String ldc) {
        return ldc.startsWith("func_")
                ? MappingType.METHODS
                : MappingType.FIELDS;
    }

    @Override
    public Optional<MappingObject> getMapping(@NotNull MappingType mappingType, @NotNull String obfuscatedName) {
        return mappings.getOrDefault(mappingType, new ArrayList<>()).stream()
                .filter(mappingObject -> mappingObject.getObfuscatedName().equals(obfuscatedName))
                .findFirst();
    }
}
package me.hp888.remapper.utils;

import me.hp888.remapper.api.asm.ClassWrapper;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * @author hp888 on 14.09.2019.
 */

public final class JarUtils
{
    private JarUtils() {}

    public static Map<String, ClassWrapper> readClasses(@NotNull final File file) throws IOException {
        final Map<String, ClassWrapper> classes = new HashMap<>();
        final JarInputStream jarInputStream = new JarInputStream(new FileInputStream(file));

        JarEntry jarEntry;
        while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
            if (!jarEntry.getName().endsWith(".class"))
                continue;

            try {
                final ClassNode classNode = new ClassNode();
                final ClassReader classReader = new ClassReader(IOUtils.silentReadBytes(jarInputStream));
                classReader.accept(classNode, 0);
                classes.put(classNode.name, new ClassWrapper(classNode));
            } catch (final Throwable ignored) {}
        }

        return classes;
    }

    public static Map<String, byte[]> readFiles(@NotNull final File file) throws IOException {
        final Map<String, byte[]> files = new HashMap<>();
        final JarInputStream jarInputStream = new JarInputStream(new FileInputStream(file));

        JarEntry jarEntry;
        while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
            if (jarEntry.getName().endsWith(".class"))
                continue;

            files.put(jarEntry.getName(), IOUtils.silentReadBytes(jarInputStream));
        }

        return files;
    }

    public static void saveJar(@NotNull final File file, @NotNull final Map<String, ClassWrapper> classes, @NotNull final Map<String, byte[]> otherFiles) throws IOException {
        final JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(file));
        for (final ClassWrapper classWrapper : classes.values()) {
            jarOutputStream.putNextEntry(new ZipEntry(classWrapper.getClassNode().name + ".class"));
            jarOutputStream.write(classWrapper.toByteArray());
            jarOutputStream.closeEntry();
        }

        for (final Map.Entry<String, byte[]> otherFile : otherFiles.entrySet()) {
            jarOutputStream.putNextEntry(new ZipEntry(otherFile.getKey()));
            jarOutputStream.write(otherFile.getValue());
            jarOutputStream.closeEntry();
        }

        jarOutputStream.close();

        System.out.println("Saved " + classes.size() + " classes.");
    }
}
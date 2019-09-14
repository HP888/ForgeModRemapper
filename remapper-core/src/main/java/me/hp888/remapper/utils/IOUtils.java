package me.hp888.remapper.utils;

import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.jetbrains.annotations.NotNull;
import java.io.*;

/**
 * @author hp888 on 14.09.2019.
 */

public final class IOUtils
{
    private static final JsonParser jsonParser = new JsonParser();

    private IOUtils() {}

    public static String readJsonString(@NotNull final InputStream inputStream) {
        return jsonParser.parse(new JsonReader(new InputStreamReader(inputStream))).toString();
    }

    public static void copy(@NotNull final InputStream inputStream, @NotNull final OutputStream outputStream) throws IOException {
        outputStream.write(readBytes(inputStream));
        outputStream.close();
    }

    public static void silentCopy(@NotNull final InputStream inputStream, @NotNull final OutputStream outputStream) throws IOException {
        outputStream.write(silentReadBytes(inputStream));
        outputStream.close();
    }

    public static byte[] silentReadBytes(@NotNull final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int read;
        while ((read = inputStream.read()) != -1) {
            byteArrayOutputStream.write(read);
        }

        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] readBytes(@NotNull final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int read;
        while ((read = inputStream.read()) != -1) {
            byteArrayOutputStream.write(read);
        }

        inputStream.close();
        return byteArrayOutputStream.toByteArray();
    }
}
package me.hp888.remapper.mappings;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import me.hp888.remapper.api.Remapper;
import me.hp888.remapper.api.mappings.MappingObject;
import me.hp888.remapper.api.mappings.MappingType;
import me.hp888.remapper.api.mappings.MappingsLoader;
import me.hp888.remapper.utils.IOUtils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author hp888 on 14.09.2019.
 */

@RequiredArgsConstructor
public class MappingsLoaderImpl implements MappingsLoader
{
    private final Remapper remapper;

    @Override
    public void load() throws IOException {
        final File baseFolder = new File("mappings" + File.separator + remapper.getConfiguration().getMcVersion());
        if (!baseFolder.exists()) {
            final ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(this.downloadMappings()));
            baseFolder.mkdirs();

            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.getName().equals("params.csv"))
                    continue;

                IOUtils.silentCopy(zipInputStream, new FileOutputStream(new File(baseFolder, zipEntry.getName())));
            }

            zipInputStream.close();
            remapper.getLogger().info("Successfully downloaded mappings.");
        }

        baseFolder.mkdirs();
        remapper.setMappings(ImmutableMap.of(
                MappingType.METHODS, this.readMappings(new File(baseFolder, "methods.csv")),
                MappingType.FIELDS, this.readMappings(new File(baseFolder, "fields.csv")))
        );
    }

    private byte[] downloadMappings() throws IOException {
        final String urlString = this.getMappingsUrl(remapper.getConfiguration().getMcVersion());
        if (urlString.startsWith("Version ")) {
            remapper.getLogger().severe(urlString);
            System.exit(-1);
            return new byte[0];
        }

        final HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(urlString).openConnection();
        try {
            return IOUtils.readBytes(httpURLConnection.getInputStream());
        } finally {
            httpURLConnection.disconnect();
        }
    }

    private String getMappingsUrl(final String mcVersion) {
        return switch (mcVersion) {
            case "1.14.2" -> "http://export.mcpbot.bspk.rs/mcp_stable/53-1.14.2/mcp_stable-53-1.14.2.zip";
            case "1.14.1" -> "http://export.mcpbot.bspk.rs/mcp_stable/51-1.14.1/mcp_stable-51-1.14.1.zip";
            case "1.14" -> "http://export.mcpbot.bspk.rs/mcp_stable/49-1.14/mcp_stable-49-1.14.zip";
            case "1.13.2" -> "http://export.mcpbot.bspk.rs/mcp_stable/47-1.13.2/mcp_stable-47-1.13.2.zip";
            case "1.13.1" -> "http://export.mcpbot.bspk.rs/mcp_stable/45-1.13.1/mcp_stable-45-1.13.1.zip";
            case "1.13" -> "http://export.mcpbot.bspk.rs/mcp_stable/43-1.13/mcp_stable-43-1.13.zip";
            case "1.12" -> "http://export.mcpbot.bspk.rs/mcp_stable/39-1.12/mcp_stable-39-1.12.zip";
            case "1.11" -> "http://export.mcpbot.bspk.rs/mcp_stable/32-1.11/mcp_stable-32-1.11.zip";
            case "1.10.2" -> "http://export.mcpbot.bspk.rs/mcp_stable/29-1.10.2/mcp_stable-29-1.10.2.zip";
            case "1.9.4" -> "http://export.mcpbot.bspk.rs/mcp_stable/26-1.9.4/mcp_stable-26-1.9.4.zip";
            case "1.9" -> "http://export.mcpbot.bspk.rs/mcp_stable/24-1.9/mcp_stable-24-1.9.zip";
            case "1.8.9" -> "http://export.mcpbot.bspk.rs/mcp_stable/22-1.8.9/mcp_stable-22-1.8.9.zip";
            case "1.8.8" -> "http://export.mcpbot.bspk.rs/mcp_stable/20-1.8.8/mcp_stable-20-1.8.8.zip";
            case "1.8" -> "http://export.mcpbot.bspk.rs/mcp_stable/18-1.8/mcp_stable-18-1.8.zip";
            case "1.7.10" -> "http://export.mcpbot.bspk.rs/mcp_stable/12-1.7.10/mcp_stable-12-1.7.10.zip";
            default -> "Client version " + mcVersion + " not found.";
        };
    }

    private List<MappingObject> readMappings(final File file) throws IOException {
        final List<MappingObject> mappingObjects = new ArrayList<>();
        try (final Scanner scanner = new Scanner(file)) {
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                final String[] split = scanner.nextLine().split(",");
                mappingObjects.add(new MappingObject(split[0], split[1]));
            }
        }

        return mappingObjects;
    }
}
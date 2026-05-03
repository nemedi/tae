package com.example.order2cash.akka.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class XmlOutputWriter {
	
	private static final Logger log = LoggerFactory.getLogger(XmlOutputWriter.class);

    private static final String DATA_PATH =
            System.getenv().getOrDefault("DATA_PATH", "data");

    public static void write(String workflowId, String filename, String xml) throws IOException {
    	try {
            Path outputDir = Path.of(DATA_PATH, "output", workflowId);
            Files.createDirectories(outputDir);
            Path file = outputDir.resolve(filename);
            Files.writeString(file, xml, StandardCharsets.UTF_8);
            log.info("│  Written : {}", file);
        } catch (IOException e) {
            log.warn("│  Could not write {} : {}", filename, e.getMessage());
        }
    }

    private XmlOutputWriter() {}
}

package com.example.order2cash.kafka.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class XmlOutputWriter {

    private static final Logger log = LoggerFactory.getLogger(XmlOutputWriter.class);

    @Value("${application.data-path}")
    private String dataPath;

    public void write(String workflowId, String filename, String xml) {
        try {
            Path outputDir = Path.of(dataPath, "output", workflowId);
            Files.createDirectories(outputDir);
            Path file = outputDir.resolve(filename);
            Files.writeString(file, xml, StandardCharsets.UTF_8);
            log.info("│  Written : {}", file);
        } catch (IOException e) {
            log.warn("│  Could not write {} : {}", filename, e.getMessage());
        }
    }
}

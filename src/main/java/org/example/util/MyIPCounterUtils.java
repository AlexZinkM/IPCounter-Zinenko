package org.example.util;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@UtilityClass
public class MyIPCounterUtils {

    private final Object PRESENT = new Object();

    public int countUniqueIPs(Path filePath) throws IOException {

        ConcurrentHashMap<String, Object> ipMap = new ConcurrentHashMap<>();

        try (Stream<String> lines = Files.lines(filePath).parallel()) {
            lines.forEach(line -> ipMap.putIfAbsent(line.trim(), PRESENT));
        }

        return ipMap.size();
    }
}

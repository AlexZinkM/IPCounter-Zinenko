package org.example.util;

import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;


//Primitive approach
@UtilityClass
public class BaseIPCounterUtils {

    public long baseIPCounter(Path filePath) throws IOException {
        BufferedReader reader = Files.newBufferedReader(filePath);
        String line;
        Set<String> uniqueIPs = new HashSet<>();

        while ((line = reader.readLine()) != null) {
            uniqueIPs.add(line.trim());
        }
        return uniqueIPs.size();
    }
}

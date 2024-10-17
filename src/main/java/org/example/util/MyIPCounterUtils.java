package org.example.util;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.concurrent.Executors;

@UtilityClass
public class MyIPCounterUtils {

    private final Object PRESENT = new Object();

    public int processIPAddresses(Path filePath, int numChunks) throws IOException {

        ConcurrentHashMap<String, Object> ipMap = new ConcurrentHashMap<>();

        long fileSizeInBytes = Files.size(filePath);

        long chunkSize = fileSizeInBytes / numChunks;

        // Create a Virtual Thread executor for parallel processing
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < numChunks; i++) {
                long startOffset = i * chunkSize;
                long endOffset = (i == numChunks - 1) ? fileSizeInBytes : (i + 1) * chunkSize;

                // Submit a task for each chunk of the file
                executor.submit(() -> {
                    try {
                        processFileChunk(filePath, startOffset, endOffset, ipMap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }

        return ipMap.size();
    }

    // Method to process a chunk of the file based on byte offsets
    private void processFileChunk(Path filePath, long startOffset, long endOffset, ConcurrentHashMap<String, Object> ipMap) throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(filePath.toFile(), "r");
             BufferedReader reader = new BufferedReader(new InputStreamReader(new java.io.FileInputStream(file.getFD())))) {

            file.seek(startOffset); // Move to the start offset

            // Skip the partial line at the beginning if not at the start
            if (startOffset != 0) {
                reader.readLine(); // Skip partial line
            }

            String line;
            long currentPosition = startOffset;

            while (currentPosition < endOffset && (line = reader.readLine()) != null) {
                ipMap.putIfAbsent(line.trim(), PRESENT);
                currentPosition = file.getFilePointer();
            }
        }
    }
}


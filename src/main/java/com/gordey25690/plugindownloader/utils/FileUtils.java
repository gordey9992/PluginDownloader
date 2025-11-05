package com.gordey25690.plugindownloader.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileUtils {
    
    public static boolean createBackup(File file, File backupDir) {
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
        
        String backupName = file.getName() + ".backup." + System.currentTimeMillis();
        File backupFile = new File(backupDir, backupName);
        
        try {
            Files.copy(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    public static boolean isValidPluginFile(File file) {
        return file.exists() && file.isFile() && file.getName().endsWith(".jar") && file.length() > 0;
    }
    
    public static String getFileSizeMB(File file) {
        long sizeInBytes = file.length();
        double sizeInMB = sizeInBytes / (1024.0 * 1024.0);
        return String.format("%.2f", sizeInMB);
    }
}

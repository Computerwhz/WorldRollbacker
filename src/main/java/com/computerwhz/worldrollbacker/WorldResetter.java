package com.computerwhz.worldrollbacker;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;

import static java.nio.file.FileVisitResult.*;

public final class WorldResetter {
    // Top-level names to skip AND any nested path element with these names
    private static final Set<String> BLACKLIST = Set.of(
            "session.lock",
            "uid.dat",
            "playerdata",
            "stats"
    );

    public static void resetWorld(File targetWorldFile, File snapshotWorldFile) {
        try {
            // 1) delete target
            deleteDirectory(targetWorldFile.toPath());

            // 2) copy, skipping blacklisted subtrees/files
            copyDirectory(snapshotWorldFile.toPath(), targetWorldFile.toPath());

            // 3) done
            org.bukkit.Bukkit.getLogger().info("World restored successfully.");
        } catch (IOException e) {
            org.bukkit.Bukkit.getLogger().severe("Could not restore world");
            e.printStackTrace(); // don't use getStackTrace().toString()
            org.bukkit.Bukkit.getPluginManager().disablePlugin(WorldRollbacker.getInstance());
        }
    }

    private static boolean isBlacklisted(Path relative) {
        // skip if ANY path segment is blacklisted
        for (Path part : relative) {
            if (BLACKLIST.contains(part.toString())) return true;
        }
        return false;
    }

    private static void copyDirectory(Path source, Path target) throws IOException {
        if (!Files.exists(source)) {
            throw new NoSuchFileException("Source does not exist: " + source);
        }
        Files.walkFileTree(source, new SimpleFileVisitor<>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path rel = source.relativize(dir);
                if (!rel.toString().isEmpty() && isBlacklisted(rel)) {
                    // Skip whole subtree (e.g., stats/, playerdata/)
                    return SKIP_SUBTREE;
                }
                Path destDir = target.resolve(rel);
                Files.createDirectories(destDir);
                return CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path rel = source.relativize(file);
                if (isBlacklisted(rel)) {
                    return CONTINUE; // skip single blacklisted files like session.lock, uid.dat
                }
                Path dest = target.resolve(rel);
                // Ensure parent exists even if source traversal order is odd
                Files.createDirectories(dest.getParent());
                Files.copy(file, dest, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                return CONTINUE;
            }
        });
    }

    private static void deleteDirectory(Path path) throws IOException {
        if (!Files.exists(path)) return;
        Files.walk(path)
                .sorted((a, b) -> b.compareTo(a)) // children first
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}

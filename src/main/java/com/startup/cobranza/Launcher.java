package com.startup.cobranza;

import java.io.*;
import java.util.*;

/**
 * Launcher que carga .env y levanta Spring Boot.
 * Busca .env desde el directorio del JAR hacia arriba.
 */
public class Launcher {

    public static void main(String[] args) throws Exception {
        File envFile = findEnvFile();

        if (envFile != null && envFile.exists()) {
            System.out.println("[Launcher] Cargando .env: " + envFile.getAbsolutePath());
            Properties env = new Properties();
            try (InputStream is = new FileInputStream(envFile)) {
                env.load(is);
            }
            for (String key : env.stringPropertyNames()) {
                String value = env.getProperty(key);
                if (System.getProperty(key) == null && System.getenv(key) == null) {
                    System.setProperty(key, value);
                    System.out.println("  " + key + "=" + mask(key, value));
                }
            }
            System.out.println("[Launcher] Listo.\n");
        } else {
            System.err.println("[Launcher] .env no encontrado. Buscando desde: " + getJarDir());
            System.err.println("[Launcher] La aplicacion usara configuracion por defecto.\n");
        }

        CobranzaApplication.main(args);
    }

    private static String mask(String key, String value) {
        if (key.toLowerCase().contains("password") ||
            key.toLowerCase().contains("secret") ||
            key.toLowerCase().contains("token")) {
            return value.substring(0, Math.min(3, value.length())) + "***";
        }
        return value;
    }

    private static File getJarDir() {
        try {
            java.net.URL codeSource = Launcher.class.getProtectionDomain().getCodeSource().getLocation();
            String protocol = codeSource.getProtocol();

            // Solo procesar si es un JAR (protocolo "jar") o un archivo "file"
            if (!"jar".equals(protocol) && !"file".equals(protocol)) {
                return null;
            }

            String path = codeSource.getPath();
            if (path == null || path.isEmpty()) {
                return null;
            }

            String jarPath = path;

            // Si es un URL de JAR (jar:file:/path/to/app.jar!/BOOT-INF/classes!/com/...)
            // el primer !/ marca el fin del path del JAR
            if ("jar".equals(protocol)) {
                int jarEnd = path.indexOf("!/");
                if (jarEnd > 0) {
                    jarPath = path.substring(0, jarEnd); // todo antes de !
                    // Quitar "file:" si esta al inicio
                    if (jarPath.startsWith("file:")) {
                        jarPath = jarPath.substring(5);
                    }
                }
            }

            // Decodificar URL-encoded characters (espacios, %20, etc.)
            try {
                jarPath = new java.net.URI(jarPath).getPath();
            } catch (Exception ignored) {
                try {
                    jarPath = java.net.URLDecoder.decode(jarPath, "UTF-8");
                } catch (Exception ignored2) {}
            }

            File jarFile = new File(jarPath);
            File jarDir = jarFile.isAbsolute() ? jarFile.getParentFile() : jarFile;

            // Normalizar para Windows (remover leading / si es una ruta absoluta de Windows como /D:/...)
            if (jarDir != null && jarDir.getPath().startsWith("/")) {
                String normalized = jarDir.getPath();
                if (normalized.matches("^/[A-Za-z]:/.*")) {
                    normalized = normalized.substring(1); // quitar / inicial de /D:/...
                }
                jarDir = new File(normalized);
            }

            return jarDir;
        } catch (Exception e) {
            System.err.println("[Launcher] getJarDir exception: " + e.getClass().getName() + " " + e.getMessage());
            return null;
        }
    }

    /**
     * Busca .env desde el directorio del JAR hacia arriba en el filesystem.
     */
    private static File findEnvFile() {
        File jarDir = getJarDir();
        if (jarDir != null) {
            // Buscar desde el dir del JAR hacia arriba
            File current = jarDir;
            while (current != null) {
                File env = new File(current, ".env");
                if (env.exists()) {
                    return env;
                }
                current = current.getParentFile();
            }
        }

        // Fallback: user.dir
        return new File(System.getProperty("user.dir"), ".env");
    }
}

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
            String path = codeSource.getPath();

            // Spring Boot fat JAR: "jar:file:/path/app.jar!/BOOT-INF/classes!/com/..."
            if (path.contains("!/")) {
                int jarEnd = path.indexOf("!/");
                path = path.substring(5, jarEnd); // quitar "file:"
            }

            try {
                path = java.net.URLDecoder.decode(path, "UTF-8");
            } catch (Exception ignored) {}

            File jarFile = new File(path);
            return jarFile.getParentFile();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Busca .env desde el directorio del JAR hacia arriba en el filesystem.
     * No depende de user.dir.
     */
    private static File findEnvFile() {
        File jarDir = getJarDir();
        if (jarDir == null) {
            // Fallback: user.dir
            return new File(System.getProperty("user.dir"), ".env");
        }

        // Buscar desde el dir del JAR hacia arriba
        File current = jarDir;
        while (current != null) {
            File env = new File(current, ".env");
            if (env.exists()) {
                return env;
            }
            current = current.getParentFile();
        }

        return null;
    }
}

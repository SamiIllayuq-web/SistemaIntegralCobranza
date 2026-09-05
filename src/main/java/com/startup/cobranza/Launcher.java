package com.startup.cobranza;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Launcher que carga .env y levanta Spring Boot.
 * Debe ser la clase principal en el JAR (Main-Class en MANIFEST).
 */
public class Launcher {

    public static void main(String[] args) throws Exception {
        File envFile = findEnvFile();

        if (envFile != null && envFile.exists()) {
            System.out.println("[Launcher] Cargando variables desde: " + envFile.getAbsolutePath());
            Properties env = new Properties();
            try (InputStream is = new FileInputStream(envFile)) {
                env.load(is);
            }
            for (String key : env.stringPropertyNames()) {
                String value = env.getProperty(key);
                if (System.getProperty(key) == null && System.getenv(key) == null) {
                    System.setProperty(key, value);
                    System.out.println("  " + key + "=" + value);
                }
            }
            System.out.println("[Launcher] Variables cargadas.\n");
        } else {
            System.err.println("[Launcher] ADVERTENCIA: .env no encontrado.");
            System.err.println("[Launcher] La aplicacion usara configuracion por defecto.\n");
        }

        CobranzaApplication.main(args);
    }

    /**
     * Busca .env buscando hacia arriba desde el directorio del JAR o del directorio de trabajo.
     */
    private static File findEnvFile() {
        // Intentar primero en el directorio de trabajo (para desarrollo y jpackage)
        File workingDir = new File(System.getProperty("user.dir"));
        File envInWorkDir = new File(workingDir, ".env");
        if (envInWorkDir.exists()) {
            return envInWorkDir;
        }

        // Intentar en el directorio del JAR (jpackage embeibe .env alla)
        try {
            java.net.URL codeSource = Launcher.class.getProtectionDomain().getCodeSource().getLocation();
            String protocol = codeSource.getProtocol();

            if ("file".equals(protocol)) {
                // Ejecucion desde directorio de clases (desarrollo) o desde JAR
                String path = codeSource.getPath();

                if (path.contains("!/")) {
                    // Dentro de un JAR anidado (Spring Boot fat JAR): extraer el path del JAR externo
                    int jarEnd = path.indexOf("!/");
                    path = path.substring(5, jarEnd); //去掉 "file:" prefix y "!/"
                }

                try {
                    // Decode URL-encoded characters (espacios, etc.)
                    path = java.net.URLDecoder.decode(path, "UTF-8");
                } catch (Exception ignored) {}

                File jarFile = new File(path);
                File jarDir = jarFile.getParentFile();
                if (jarDir != null) {
                    File envInJarDir = new File(jarDir, ".env");
                    if (envInJarDir.exists()) {
                        return envInJarDir;
                    }
                }

                // Buscar hacia arriba en el filesystem por si el .env esta en un parent
                File parent = jarDir;
                while (parent != null) {
                    File envUp = new File(parent, ".env");
                    if (envUp.exists()) {
                        return envUp;
                    }
                    parent = parent.getParentFile();
                }
            }
        } catch (Exception e) {
            System.err.println("[Launcher] No se pudo detectar directorio del JAR: " + e.getMessage());
        }

        return null;
    }
}

package com.startup.cobranza;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Launcher que carga .env y levanta Spring Boot.
 * Debe ser la clase principal en el JAR (Main-Class en MANIFEST).
 * jpackage usa este launcher; el .bat de startup no es necesario.
 */
public class Launcher {

    public static void main(String[] args) throws Exception {
        // Buscar .env al lado del JAR
        File jarFile = new File(Launcher.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI());
        File jarDir = jarFile.getParentFile();
        File envFile = new File(jarDir, ".env");

        if (envFile.exists()) {
            System.out.println("[Launcher] Cargando variables desde: " + envFile.getAbsolutePath());
            Properties env = new Properties();
            try (InputStream is = new FileInputStream(envFile)) {
                env.load(is);
            }
            for (String key : env.stringPropertyNames()) {
                String value = env.getProperty(key);
                // No sobrescribir si ya existe (permite override por línea de comandos)
                if (System.getProperty(key) == null && System.getenv(key) == null) {
                    System.setProperty(key, value);
                    System.out.println("  " + key + "=" + value);
                }
            }
            System.out.println("[Launcher] Variables cargadas.\n");
        } else {
            System.err.println("[Launcher] ADVERTENCIA: .env no encontrado en " + jarDir);
            System.err.println("[Launcher] La aplicacion usara configuracion por defecto.\n");
        }

        // Delegar a Spring Boot Application
        CobranzaApplication.main(args);
    }
}

package com.startup.cobranza;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Launcher que carga .env y levanta Spring Boot.
 */
public class Launcher {

    public static void main(String[] args) throws Exception {
        System.out.println("[Launcher] user.dir = " + System.getProperty("user.dir"));
        System.out.println("[Launcher] java.class.path = " + System.getProperty("java.class.path"));
        System.out.println("[Launcher] sun.java.command = " + System.getProperty("sun.java.command"));

        File envFile = findEnvFile();

        if (envFile != null && envFile.exists()) {
            System.out.println("[Launcher] .env encontrado en: " + envFile.getAbsolutePath());
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
            System.out.println("[Launcher] Variables cargadas.\n");
        } else {
            System.err.println("[Launcher] ADVERTENCIA: .env no encontrado.");
            System.err.println("[Launcher] user.dir = " + System.getProperty("user.dir"));
            System.err.println("[Launcher] La aplicacion usara configuracion por defecto.\n");
        }

        CobranzaApplication.main(args);
    }

    private static String mask(String key, String value) {
        if (key.toLowerCase().contains("password") ||
            key.toLowerCase().contains("secret") ||
            key.toLowerCase().contains("token") ||
            key.toLowerCase().contains("key") && value.length() > 4) {
            return value.substring(0, 3) + "***";
        }
        return value;
    }

    private static File findEnvFile() {
        // 1. Intentar en user.dir (directorio de trabajo)
        File workingDir = new File(System.getProperty("user.dir"));
        System.out.println("[Launcher] Buscando .env en user.dir: " + workingDir);
        File envInWorkDir = new File(workingDir, ".env");
        if (envInWorkDir.exists()) {
            System.out.println("[Launcher]   -> ENCONTRADO en user.dir");
            return envInWorkDir;
        }
        System.out.println("[Launcher]   -> NO existe en user.dir");

        // 2. Intentar en directorio del JAR
        try {
            java.net.URL codeSource = Launcher.class.getProtectionDomain().getCodeSource().getLocation();
            System.out.println("[Launcher] codeSource = " + codeSource);
            String protocol = codeSource.getProtocol();
            System.out.println("[Launcher] codeSource protocol = " + protocol);
            System.out.println("[Launcher] codeSource path = " + codeSource.getPath());

            if ("file".equals(protocol)) {
                String path = codeSource.getPath();

                // Spring Boot fat JAR: "jar:file:/path/to/app.jar!/BOOT-INF/classes!/com/..."
                if (path.contains("!/")) {
                    int jarEnd = path.indexOf("!/");
                    path = path.substring(5, jarEnd); // quitar "file:"
                }

                try {
                    path = java.net.URLDecoder.decode(path, "UTF-8");
                } catch (Exception ignored) {}

                File jarFile = new File(path);
                File jarDir = jarFile.getParentFile();
                System.out.println("[Launcher] JAR dir = " + jarDir);

                if (jarDir != null) {
                    File envInJarDir = new File(jarDir, ".env");
                    System.out.println("[Launcher]   buscando .env en JAR dir: " + envInJarDir);
                    if (envInJarDir.exists()) {
                        System.out.println("[Launcher]   -> ENCONTRADO en JAR dir");
                        return envInJarDir;
                    }

                    // Buscar hacia arriba
                    File parent = jarDir;
                    while (parent != null) {
                        File envUp = new File(parent, ".env");
                        if (envUp.exists()) {
                            System.out.println("[Launcher]   -> ENCONTRADO en parent: " + parent);
                            return envUp;
                        }
                        parent = parent.getParentFile();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[Launcher] Error detectando dir del JAR: " + e.getMessage());
        }

        return null;
    }
}

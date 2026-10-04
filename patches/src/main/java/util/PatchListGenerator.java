/*
 * Copyright 2025 Morphe.
 * https://github.com/MorpheApp/morphe-patches-template
 */

package util;

import app.morphe.patcher.patch.Patch;
import app.morphe.patcher.patch.PatcherUtilsKt;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

public class PatchListGenerator {

    public static void main(String[] args) {
        try {
            File libsDir = new File("build/libs/");
            File[] files = libsDir.listFiles(file -> {
                String name = file.getName();
                return !name.contains("javadoc") && !name.contains("sources") && name.endsWith(".mpp");
            });

            if (files == null || files.length == 0) {
                System.err.println("Nenhum arquivo .mpp encontrado em build/libs/");
                return;
            }

            File firstMpp = files[0];
            Set<File> patchFiles = Collections.singleton(firstMpp);

            // Carrega os patches utilizando o utilitário nativo embutido do ecossistema Morphe
            Set<Patch<?>> loadedPatches = PatcherUtilsKt.loadPatchesFromJar(patchFiles);

            URL[] urls = new URL[]{ firstMpp.toURI().toURL() };
            try (URLClassLoader patchClassLoader = new URLClassLoader(urls)) {
                Enumeration<URL> manifests = patchClassLoader.getResources("META-INF/MANIFEST.MF");

                while (manifests.hasMoreElements()) {
                    try (var stream = manifests.nextElement().openStream()) {
                        Manifest manifest = new Manifest(stream);
                        String version = manifest.getMainAttributes().getValue("Version");
                        if (version != null) {
                            generatePatchList(version, loadedPatches);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao executar PatchListGenerator: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void generatePatchList(String version, Set<Patch<?>> patches) throws IOException {
        File listJson = new File("../patches-list.json");

        List<Patch<?>> sortedPatches = patches.stream()
                .sorted((p1, p2) -> {
                    String n1 = p1.getName() != null ? p1.getName() : "";
                    String n2 = p2.getName() != null ? p2.getName() : "";
                    return n1.compareTo(n2);
                })
                .collect(Collectors.toList());

        List<JsonPatch> patchesMap = new ArrayList<>();
        for (Patch<?> patch : sortedPatches) {
            List<String> dependencies = patch.getDependencies().stream()
                    .map(dep -> dep.getClass().getSimpleName())
                    .collect(Collectors.toList());

            List<JsonCompatibility> compatiblePackages = null;
            if (patch.getCompatibility() != null) {
                compatiblePackages = new ArrayList<>();
                for (var compat : patch.getCompatibility()) {
                    List<JsonCompatibility.Target> targets = new ArrayList<>();
                    for (var target : compat.getTargets()) {
                        Map<String, Integer> versionCodes = null;
                        if (target.getVersionCodes() != null) {
                            versionCodes = new HashMap<>();
                            for (var entry : target.getVersionCodes().entrySet()) {
                                versionCodes.put(entry.getKey().name(), entry.getValue());
                            }
                        }

                        targets.add(new JsonCompatibility.Target(
                                target.getVersion(),
                                versionCodes,
                                target.isExperimental(),
                                target.getMinSdk(),
                                target.getDescription()
                        ));
                    }

                    String apkFileType = compat.getApkFileType() != null ? compat.getApkFileType().name() : null;
                    String appIconColor = null;
                    if (compat.getAppIconColor() != null) {
                        appIconColor = String.format("#%06X", compat.getAppIconColor());
                    }

                    compatiblePackages.add(new JsonCompatibility(
                            compat.getPackageName(),
                            compat.getName(),
                            compat.getDescription(),
                            apkFileType,
                            appIconColor,
                            compat.getSignatures() != null ? new HashSet<>(compat.getSignatures()) : null,
                            targets
                    ));
                }
            }

            List<JsonPatch.Option> options = new ArrayList<>();
            for (var option : patch.getOptions().values()) {
                options.add(new JsonPatch.Option(
                        option.getKey(),
                        option.getTitle(),
                        option.getDescription(),
                        option.getRequired(),
                        option.getType().toString(),
                        option.getDefault(),
                        option.getValues()
                ));
            }

            patchesMap.add(new JsonPatch(
                    patch.getName(),
                    patch.getDescription(),
                    patch.getDefault(),
                    patch.getCategory(),
                    dependencies,
                    compatiblePackages,
                    options
            ));
        }

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(
                "NOTE",
                "Do NOT manually edit this file. This file is automatically updated when " +
                        "semantic release (release.yml) runs. Manually editing this file can break " +
                        "your releases and break third party tools that use this file."
        );
        jsonObject.addProperty("version", version);
        jsonObject.add("patches", gson.toJsonTree(patchesMap));

        try (FileWriter writer = new FileWriter(listJson, StandardCharsets.UTF_8)) {
            gson.toJson(jsonObject, writer);
        }
    }

    private static class JsonPatch {
        final String name;
        final String description;
        final boolean isDefault;
        final String category;
        final List<String> dependencies;
        final List<JsonCompatibility> compatiblePackages;
        final List<Option> options;

        JsonPatch(String name, String description, boolean isDefault, String category,
                  List<String> dependencies, List<JsonCompatibility> compatiblePackages, List<Option> options) {
            this.name = name;
            this.description = description;
            this.isDefault = isDefault;
            this.category = category;
            this.dependencies = dependencies;
            this.compatiblePackages = compatiblePackages;
            this.options = options;
        }

        static class Option {
            final String key;
            final String title;
            final String description;
            final boolean required;
            final String type;
            final Object defaultValue;
            final Map<String, Object> values;

            Option(String key, String title, String description, boolean required,
                   String type, Object defaultValue, Map<String, Object> values) {
                this.key = key;
                this.title = title;
                this.description = description;
                this.required = required;
                this.type = type;
                this.defaultValue = defaultValue;
                this.values = values;
            }
        }
    }

    private static class JsonCompatibility {
        final String packageName;
        final String name;
        final String description;
        final String apkFileType;
        final String appIconColor;
        final Set<String> signatures;
        final List<Target> targets;

        JsonCompatibility(String packageName, String name, String description, String apkFileType,
                          String appIconColor, Set<String> signatures, List<Target> targets) {
            this.packageName = packageName;
            this.name = name;
            this.description = description;
            this.apkFileType = apkFileType;
            this.appIconColor = appIconColor;
            this.signatures = signatures;
            this.targets = targets;
        }

        static class Target {
            final String version;
            final Map<String, Integer> versionCodes;
            final boolean isExperimental;
            final Integer minSdk;
            final String description;

            Target(String version, Map<String, Integer> versionCodes, boolean isExperimental,
                   Integer minSdk, String description) {
                this.version = version;
                this.versionCodes = versionCodes;
                this.isExperimental = isExperimental;
                this.minSdk = minSdk;
                this.description = description;
            }
        }
    }
}

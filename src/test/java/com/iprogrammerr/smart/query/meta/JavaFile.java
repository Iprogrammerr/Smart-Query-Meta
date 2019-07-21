package com.iprogrammerr.smart.query.meta;

import java.nio.file.Files;
import java.nio.file.Paths;

public class JavaFile {

    private final String name;

    public JavaFile(String name) {
        this.name = name;
    }

    public String content() throws Exception {
        return new String(Files.readAllBytes(Paths.get(getClass()
            .getResource(String.format("/%s.java", name)).toURI())));
    }
}

package com.bryanbatanes.scrabble.util;

import com.bryanbatanes.scrabble.dto.LetterPointsResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Slf4j
public class TestUtil {

    public static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static <T> T getObjectFromJson(String jsonPath, Class<T> classType) throws IOException {
        String jsonString = getJsonStringFromFile(jsonPath);
        return jsonToPojo(jsonString, classType);
    }

    public static <T> T jsonToPojo(String json, Class<T> classType) throws IOException {
        return objectMapper.readValue(json, classType);
    }

    //overload to define type for complex type
    public static <T> T getObjectFromJson(String jsonPath, TypeReference <T> typeRef) throws IOException {
        String jsonString = getJsonStringFromFile(jsonPath);
        return jsonToPojo(jsonString, typeRef);
    }

    public static <T> T jsonToPojo(String json, TypeReference <T> typeRef) throws IOException {
        return objectMapper.readValue(json, typeRef);
    }

    public static File getFile(String filename) {
        ClassLoader classLoader = TestUtil.class.getClassLoader();
        URL resourceUrl = classLoader.getResource(filename);

        if (resourceUrl == null) {
            throw new IllegalArgumentException("Resource not found: " + filename);
        }

        return new File(resourceUrl.getFile());
    }

    public static String getJsonStringFromFile(String filename) throws IOException {
        Object json = objectMapper.readValue(getFile(filename), Object.class);
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
    }

    public static String getJsonStringFromObject(Object obj) throws IOException {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }
}

package ec.com.company.core.microservices.microserviciocalculodesahucio.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class JsonUtils {
    public static String readJson(String fileName) throws IOException {
        ClassLoader classLoader = JsonUtils.class.getClassLoader();
        File file = new File(classLoader.getResource("json/" + fileName).getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }
}

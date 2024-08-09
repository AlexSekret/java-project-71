package hexlet.code;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Differ {
    public static String generate(String filepath1, String filepath2) throws Exception {
        Path path1 = Paths.get(filepath1).toAbsolutePath().normalize();
        if (!Files.exists(path1)) {
            throw new Exception("File '" + path1 + "' does not exist");
        }

        Path path2 = Paths.get(filepath2).toAbsolutePath().normalize();
        if (!Files.exists(path2)) {
            throw new Exception("File '" + path2 + "' does not exist");
        }

        Map<String, Object> firstData = getObjectMap(path1);
        Map<String, Object> secondData = getObjectMap(path2);
        Set<String> setOfKeys = new TreeSet<>(firstData.keySet());
        setOfKeys.addAll(secondData.keySet());
        TreeMap<String, Difference> diff = new TreeMap<>();
        for (var s : setOfKeys) {
            if (firstData.containsKey(s) && secondData.containsKey(s)) {
                var value = firstData.get(s);
                if (firstData.get(s).equals(secondData.get(s))) {
                    diff.put(s, new Difference<>("not-changed", value));
                } else {
                    var oldValue = firstData.get(s);
                    var newValue = secondData.get(s);
                    diff.put(s, new Difference<>("changed", oldValue, newValue));
                }
            } else if (firstData.containsKey(s) && !secondData.containsKey(s)) {
                var deletedValue = firstData.get(s);
                diff.put(s, new Difference<>("deleted", deletedValue));
            } else if (!firstData.containsKey(s) && secondData.containsKey(s)) {
                var addedValue = secondData.get(s);
                diff.put(s, new Difference<>("deleted", addedValue));
            }
        }
        return "Нихрена непонятно, но очень интересно\n" + diff.toString();
    }

    //deside to extract file mapping into a method
    private static Map<String, Object> getObjectMap(Path pathFile) throws IOException {
        ObjectMapper dataFile = new ObjectMapper();
        return dataFile.readValue(
                pathFile.toFile(),
                new TypeReference<Map<String, Object>>() {
                });
    }
}

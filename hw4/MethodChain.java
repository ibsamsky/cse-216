import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MethodChain {
    public static <K, V> List<String> flatten(Map<K, V> aMap) {
        return aMap.entrySet().stream().map(e -> String.format("%s -> %s", e.getKey(), e.getValue())).collect(Collectors.toList());
    }
}

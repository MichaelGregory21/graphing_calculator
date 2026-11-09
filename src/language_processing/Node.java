package language_processing;

import java.util.List;
import java.util.Map;

public record Node(String type, String value, List<Node> children, Map<String, Object> attributes) {

    public String pretty() {
        return pretty(0);
    }

    private String pretty(int depth) {
        String indentation = "    ".repeat(depth);

        StringBuilder result = new StringBuilder();
        result.append(indentation)
              .append(type.toUpperCase())
              .append(": ")
              .append(value);

        if (children == null) {
            return result.toString();
        }
        
        for (Node child : children) {
            result.append("\n")
                  .append(child.pretty(depth + 1));
        }

        return result.toString();
    }
}

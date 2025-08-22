import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Color_Object {
    private String name;
    private String color;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Color_Object that = (Color_Object) o;
        return Objects.equals(name, that.name) && Objects.equals(color, that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color);
    }

    static public void saveToJSON(List<Color_Object> objects) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File("color.json"), objects);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static public List<Color_Object> loadFromJSON() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("color.json");

        if (!file.exists()) {
            return Collections.emptyList();
        }

        return objectMapper.readValue(file, new TypeReference<List<Color_Object>>() {});
    }

    public Color_Object(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Color_Object() {
    }
}

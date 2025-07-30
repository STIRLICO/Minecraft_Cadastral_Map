import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ApplicationService {
    static private int all_id = 0;
    private int id;
    private int area_id;
    private String applicant;
    
    private boolean published;
    private String text;


    public static int getAll_id() {
        return all_id;
    }

    public static void setAll_id(int all_id) {
        ApplicationService.all_id = all_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getArea_id() {
        return area_id;
    }

    public void setArea_id(int area_id) {
        this.area_id = area_id;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationService that = (ApplicationService) o;
        return id == that.id && area_id == that.area_id && published == that.published && Objects.equals(applicant, that.applicant) && Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, area_id, applicant, published, text);
    }

    public ApplicationService(int area_id, String applicant, String text) {
        this.id = all_id;
        this.area_id = area_id;
        this.applicant = applicant;
        this.published = false;
        this.text = text;
        ApplicationService.setAll_id(all_id+1);
    }

    static public void saveToJSON(List<ApplicationService> objects) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("lastId", all_id);
            root.set("applications", objectMapper.valueToTree(objects));

            objectMapper.writeValue(new File("applications.json"), root);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save applications to JSON", e);
        }
    }

    static public List<ApplicationService> loadFromJSON() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("applications.json");

        if (!file.exists()) {
            return Collections.emptyList();
        }

        // Читаем как JsonNode
        JsonNode root = objectMapper.readTree(file);
        // Восстанавливаем all_id
        if (root.has("lastId")) {
            all_id = root.get("lastId").asInt();
        }
        // Получаем список приложений
        JsonNode appsNode = root.get("applications");
        if (appsNode != null && appsNode.isArray()) {
            return objectMapper.readValue(appsNode.traverse(),
                    new TypeReference<List<ApplicationService>>() {});
        }
        return Collections.emptyList();
    }


    public ApplicationService() {
    }
}

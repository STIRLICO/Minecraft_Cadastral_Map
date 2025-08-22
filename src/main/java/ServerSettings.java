import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ServerSettings {
    private String ip;
    private int port;

    private String fileName;
    private String key;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerSettings that = (ServerSettings) o;
        return port == that.port && Objects.equals(ip, that.ip) && Objects.equals(fileName, that.fileName) && Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port, fileName, key);
    }

    static public void saveToJSON(ServerSettings object) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File("ServerSettings.json"), object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static public ServerSettings loadFromJSON() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("ServerSettings.json");

        return objectMapper.readValue(file, ServerSettings.class);
    }

    public ServerSettings(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public ServerSettings() {
    }
}

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ServerSettings {
    private String ip;
    private int port;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerSettings that = (ServerSettings) o;
        return Objects.equals(ip, that.ip) && Objects.equals(port, that.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
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

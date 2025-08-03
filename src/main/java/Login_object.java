import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Login_object {
    private String username;
    private String password;
    private Long telegram_id;
    private String telegram_token;

    public Login_object(String username, String password, Long telegram_id, String telegram_token) {
        this.username = username;
        this.password = password;
        this.telegram_id = telegram_id;
        this.telegram_token = telegram_token;
    }

    public Login_object() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public Long getTelegram_id() {
        return telegram_id;
    }

    public void setTelegram_id(Long telegram_id) {
        this.telegram_id = telegram_id;
    }

    public String getTelegram_token() {
        return telegram_token;
    }

    public void setTelegram_token(String telegram_token) {
        this.telegram_token = telegram_token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Login_object that = (Login_object) o;
        return Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(telegram_id, that.telegram_id) && Objects.equals(telegram_token, that.telegram_token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, telegram_id, telegram_token);
    }

    static public void saveToJSON(Login_object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File("admin.json"), object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static public Login_object loadFromJSON() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("admin.json");

        return objectMapper.readValue(file, Login_object.class);
    }
}

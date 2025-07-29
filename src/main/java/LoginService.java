import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Сервис входа: проверяет логин и пароль, на соответствие записанным в базу данных.
 */
public class LoginService {
    static private Map<String, String> activeTokens = new ConcurrentHashMap<>();

    public boolean login(String username, String password) throws IOException {
        Login_object real = Login_object.loadFromJSON();
        if (real.getUsername().equals(username) && real.getPassword().equals(password)) {
            return true;
        }
        return false;
    }

    public String generateToken(String username) {
        String token = UUID.randomUUID().toString();
        activeTokens.put(token, username);
        return token;
    }

    static public boolean isValidToken(String token) {
        System.out.println("Checking token: " + token);
        System.out.println("Active tokens: " + activeTokens);
        return activeTokens.containsKey(token);
    }
}
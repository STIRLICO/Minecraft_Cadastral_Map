

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Web-приложение в котором регистрируются все ресурсы.
 */
public class RestApplication extends Application {

    public RestApplication() {

        List<Cadastral_Object> Map = null;
        //Cadastral_Object.generateMap();
    }

    /**
     * Возвращает список всех ресурсов web-приложения.
     * @return список всех ресурсов web-приложения.
     */
    @Override
    public Set<Object> getSingletons() {
        Set<Object> resources = new HashSet<>();
        //resources.add(new LoginController());
        resources.add(new CadastralController());
        return resources;
    }
}
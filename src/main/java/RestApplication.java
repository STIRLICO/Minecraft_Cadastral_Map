import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class RestApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(CadastralController.class);
        classes.add(LoginController.class);
        classes.add(ApplicationController.class);
        classes.add(CharsetFilter.class);
        return classes;
    }

    @Override
    public Set<Object> getSingletons() {

        //Login_object admin = new Login_object("admin","admin",123456789L,"token");
        //Login_object.saveToJSON(admin);
        Set<Object>  resources = new HashSet<>();
        resources.add(new LoginController());
        resources.add(new CadastralController());
        resources.add(new ApplicationController());
        resources.add(new CharsetFilter());
        resources.add(new CharsetRequestFilter());

        return resources;
    }
}
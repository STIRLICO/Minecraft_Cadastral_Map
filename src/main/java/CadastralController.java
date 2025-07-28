import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Path("/")
public class CadastralController {
    private Cadastral_Object dummy = new Cadastral_Object();

    private List<Cadastral_Object> Map;






    @GET
    @Produces("text/html")
    public String showGrid() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><title>Cadastral Map</title>");
        html.append("<style>");
        html.append(".grid { display: grid; grid-template-columns: repeat(10, 50px); gap: 2px; }");
        html.append(".cell { width: 50px; height: 50px; background: #eee; display: flex; justify-content: center; align-items: center; }");
        html.append(".cell:hover { background: #ddd; }");
        html.append("</style></head><body>");
        html.append("<h1>Cadastral Map</h1>");
        html.append("<div class='grid'>");

        for (int i = 1; i <= 100; i++) {
            html.append("<a href='/map/area/").append(i).append("' class='cell'>").append(i).append("</a>");
        }

        html.append("</div></body></html>");
        try {
            Map = Cadastral_Object.loadFromJSON();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return html.toString();
    }

    @GET
    @Path("/map/area/{id}")
    public String handleClick(@PathParam("id") int id) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><title>Cadastral Map Area "+id+"</title></head>");
        html.append("<body>");
        html.append("<h1>Участок"+id+"</h1>");
        html.append("Владелец: "+ Map.get(id - 1).getOwner());

        html.append("/<body>");
        return html.toString();
    }
}
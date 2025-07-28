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

    {
        try {
            Map = Cadastral_Object.loadFromJSON();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


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
        html.append("<h1>Участок "+id+"</h1>");
        html.append("<p>Владелец: "+ Map.get(id - 1).getOwner());
        html.append("<p>Бывшие владельцы: "+ Map.get(id - 1).getSellers().toString());
        html.append("<p>Дата покупки: "+ Map.get(id - 1).getPurchase_date());
        html.append("<p>Цена: "+ Map.get(id - 1).getPrice()+" "+Map.get(id - 1).getCurrency());
        html.append("<p>Статус: "+ Map.get(id - 1).getStatus());
        //html.append(Map.get(id - 1).getPicture());
        html.append("<p><a href=\""+id+"/edit/\" + child.id + \"\">Редактировать</a>");
        html.append("</body>");
        return html.toString();
    }

    @GET
    @Path("/map/area/{id}/edit")
    public String editArea(@PathParam("id") int id) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><title>Cadastral Map Area "+id+"</title></head>");
        html.append("<body>");
        html.append("<h1>Участок "+id+"</h1>");

        html.append("<p>Владелец: ");
        html.append("<form method=\"post\" action=\"/editOwner/" + id + "\">");
        html.append("<input type=\"text\" name=\"value\" value=\"" + Map.get(id - 1).getOwner() +"\"/>");
        html.append("<input type=\"submit\"/></form>");

        html.append("<p>Бывшие владельцы: "+ Map.get(id - 1).getSellers().toString());
        html.append("<form method=\"post\" action=\"/addSeller/" + id + "\">");
        html.append("<input type=\"text\" name=\"value\" value=\"" + Map.get(id - 1).getOwner() +"\"/>");
        html.append("<input type=\"submit\"/></form>");

        html.append("<p>Дата покупки: ");
        html.append("<form method=\"post\" action=\"/editDate/" + id + "\">");
        html.append("<input type=\"text\" name=\"value\" value=\"" + Map.get(id - 1).getPurchase_date() +"\"/>");
        html.append("<input type=\"submit\"/></form>");

        html.append("<p>Цена: ");
        html.append("<form method=\"post\" action=\"/editPrice/" + id + "\">");
        html.append("<input type=\"text\" name=\"value\" value=\"" + Map.get(id - 1).getPrice() +"\"/>");
        html.append("<input type=\"submit\"/></form>");
        html.append("<form method=\"post\" action=\"/editCurrency/" + id + "\">");
        html.append("<input type=\"text\" name=\"value\" value=\"" + Map.get(id - 1).getCurrency() +"\"/>");
        html.append("<input type=\"submit\"/></form>");


        html.append("<p>Статус: ");
        html.append("<form method=\"post\" action=\"/editStatus/" + id + "\">");
        html.append("<input type=\"text\" name=\"value\" value=\"" + Map.get(id - 1).getStatus() +"\"/>");
        html.append("<input type=\"submit\"/></form>");

        html.append("<p>Картинка ");
        html.append("<form method=\"post\" action=\"/editPicture/" + id + "\">");
        html.append("<input type=\"text\" name=\"value\" value=\"" + Map.get(id - 1).getPicture() +"\"/>");
        html.append("<input type=\"submit\"/></form>");

        html.append("</body>");
        return html.toString();
    }
}
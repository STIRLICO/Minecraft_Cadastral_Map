import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Path("/")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class CadastralController {
    private Cadastral_Object dummy = new Cadastral_Object();


    private List<Cadastral_Object> map;

    {
        try {
            //Cadastral_Object.generateMap();
            map = Cadastral_Object.loadFromJSON();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @GET
    @Produces(MediaType.TEXT_HTML + "; charset=UTF-8")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + "; charset=UTF-8")
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
            map = Cadastral_Object.loadFromJSON();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return html.toString();
    }

    @GET
    @Path("/map/area/{id}")
    @Produces(MediaType.TEXT_HTML + "; charset=UTF-8")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + "; charset=UTF-8")
    public String handleClick(@PathParam("id") int id) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><title>Cadastral Map Area "+id+"</title><meta charset=\"UTF-8\"></head>");
        html.append("<body>");
        html.append("<h1>Участок "+id+"</h1>");
        html.append("<p>Владелец: "+ map.get(id - 1).getOwner());
        html.append("<p>Бывшие владельцы: "+ map.get(id - 1).getSellers().toString());
        html.append("<p>Дата покупки: "+ map.get(id - 1).getPurchase_date());
        html.append("<p>Цена: "+ map.get(id - 1).getPrice()+" "+ map.get(id - 1).getCurrency());
        html.append("<p>Статус: "+ map.get(id - 1).getStatus());
        //html.append(Map.get(id - 1).getPicture());
        html.append("<p><a href=\"/map/area/" + id + "/edit\">Редактировать</a>");
        html.append("</body>");
        return html.toString();
    }

    @GET
    @Path("/map/area/{id}/edit")
    @Produces(MediaType.TEXT_HTML + "; charset=UTF-8")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + "; charset=UTF-8")
    public String editArea(@PathParam("id") int id) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><title>Cadastral Map Area "+id+"</title><meta charset=\"UTF-8\"></head>");
        html.append("<body>");
        html.append("<h1>Участок "+id+"</h1>");

        html.append("<p>Владелец: ");
        html.append("<form method=\"post\" action=\"/map/area/" + id + "/edit\">");
        html.append("<input type=\"text\" name=\"owner\" value=\"" + map.get(id - 1).getOwner() +"\"/>");
        html.append("<input type=\"submit\"/></form>");

        html.append("<p>Бывшие владельцы: "+ map.get(id - 1).getSellers().toString());
        html.append("<form method=\"post\" action=\"/map/area/" + id + "/edit\">");
        html.append("<input type=\"text\" name=\"value\" value=\"" + map.get(id - 1).getOwner() +"\"/>");
        html.append("<input type=\"submit\"/></form>");

        html.append("<p>Дата покупки: ");
        html.append("<form method=\"post\" action=\"/map/area/" + id + "/editDate\">");
        html.append("<input type=\"text\" name=\"value\" value=\"" + map.get(id - 1).getPurchase_date() +"\"/>");
        html.append("<input type=\"submit\"/></form>");

        html.append("<p>Цена: ");
        html.append("<form method=\"post\" action=\"/map/area/" + id + "/editPrice\">");
        html.append("<input type=\"text\" name=\"value\" value=\"" + map.get(id - 1).getPrice() +"\"/>");
        html.append("<input type=\"submit\"/></form>");
        html.append("<form method=\"post\" action=\"/map/area/" + id + "/editCurrency\">");
        html.append("<input type=\"text\" name=\"value\" value=\"" + map.get(id - 1).getCurrency() +"\"/>");
        html.append("<input type=\"submit\"/></form>");

        html.append("<p>Статус: ");
        html.append("<form method=\"post\" action=\"/map/area/" + id + "/editStatus\">");
        html.append("<input type=\"text\" name=\"value\" value=\"" + map.get(id - 1).getStatus() +"\"/>");
        html.append("<input type=\"submit\"/></form>");

        html.append("<p>Картинка ");
        html.append("<form method=\"post\" action=\"/map/area/" + id + "/editPicture\">");
        html.append("<input type=\"text\" name=\"value\" value=\"" + map.get(id - 1).getPicture() +"\"/>");
        html.append("<input type=\"submit\"/></form>");

        html.append("</body>");
        return html.toString();
    }

    @POST
    @Path("/map/area/{id}/edit")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + "; charset=UTF-8")
    public Response editOwner(@PathParam("id") int id,
                              @FormParam("owner") String owner) {


        try {
            String decodedOwner = new String(owner.getBytes("ISO-8859-1"), "UTF-8");

            map.get(id - 1).setOwner(decodedOwner);
            Cadastral_Object.saveToJSON(map);

            return Response.seeOther(new URI("/map/area/" + id + "/edit")).build();
        } catch (Exception e) {
            System.err.println("Error processing owner: " + e.getMessage());
            return Response.serverError().entity("Ошибка обработки данных").build();
        }
    }

}
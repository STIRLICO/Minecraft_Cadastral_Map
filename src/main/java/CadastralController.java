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

    private String getStatusClass(String status) {
        if (status == null) return "cell-default";

        switch(status.toLowerCase()) {
            case "купленно": return "cell-sold";
            case "бронь": return "cell-reserved";
            case "стройка": return "cell-construction";
            case "продаётся": return "cell-available";
            default: return "cell-default";
        }
    }
    @GET
    @Path("/map/")
    @Produces(MediaType.TEXT_HTML + "; charset=UTF-8")
    public String showGrid() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><title>Cadastral Map</title>");
        html.append("<style>");
        html.append(".grid { display: grid; grid-template-columns: repeat(10, 50px); gap: 0px; }");
        html.append(".cell { width: 50px; height: 50px; display: flex; justify-content: center; align-items: center; }");
        html.append(".cell:hover { opacity: 0.8; }");
        html.append(".cell { transition: all 0.2s ease; }");
        html.append(".cell:hover { transform: scale(1.05); box-shadow: 0 0 5px rgba(0,0,0,0.2)");
        html.append("</style></head><body>");
        html.append("<h1>Cadastral Map</h1>");
        html.append("<div class='grid'>");

        try {
            map = Cadastral_Object.loadFromJSON();

            for (int i = 1; i <= 100; i++) {
                Cadastral_Object obj = map.get(i - 1);
                String color = obj.getCell_color() != null ? obj.getCell_color() : "#eee";

                html.append("<a href='/map/area/").append(i).append("' class='cell' ")
                        .append("style='background:").append(color).append("'")
                        .append(" title='Участок ").append(i).append("'></a>");
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки данных", e);
        }

        html.append("</div></body></html>");
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
        html.append("<p><a href=\"/map/" + "\">Назад</a>");
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
        html.append("<input type=\"hidden\" name=\"field\" value=\"owner\">");
        html.append("<input type=\"text\" name=\"value\" value=\"" + map.get(id - 1).getOwner() +"\"/>");
        html.append("<input type=\"submit\"/></form>");

        html.append("<p>Бывшие владельцы: "+ map.get(id - 1).getSellers().toString());
        html.append("<form method=\"post\" action=\"/map/area/" + id + "/edit\">");
        html.append("<input type=\"hidden\" name=\"field\" value=\"seller\">");
        html.append("<input type=\"text\" name=\"value\" value=\"" + map.get(id - 1).getOwner() +"\"/>");
        html.append("<input type=\"submit\"/></form>");

        html.append("<p>Дата покупки: ");
        html.append("<form method=\"post\" action=\"/map/area/" + id + "/edit\">");
        html.append("<input type=\"hidden\" name=\"field\" value=\"date\">");
        html.append("<input type=\"text\" name=\"value\" value=\"" + map.get(id - 1).getPurchase_date() +"\"/>");
        html.append("<input type=\"submit\"/></form>");

        html.append("<p>Цена: ");
        html.append("<form method=\"post\" action=\"/map/area/" + id + "/edit\">");
        html.append("<input type=\"hidden\" name=\"field\" value=\"price\">");
        html.append("<input type=\"text\" name=\"value\" value=\"" + map.get(id - 1).getPrice() +"\"/>");
        html.append("<input type=\"submit\"/></form>");
        html.append("<form method=\"post\" action=\"/map/area/" + id + "/edit\">");
        html.append("<input type=\"hidden\" name=\"field\" value=\"currency\">");
        html.append("<input type=\"text\" name=\"value\" value=\"" + map.get(id - 1).getCurrency() +"\"/>");
        html.append("<input type=\"submit\"/></form>");

        html.append("<p>Статус: ");
        html.append("<form method=\"post\" action=\"/map/area/" + id + "/edit\">");
        html.append("<input type=\"hidden\" name=\"field\" value=\"status\">");
        html.append("<input type=\"text\" name=\"value\" value=\"" + map.get(id - 1).getStatus() +"\"/>");
        html.append("<input type=\"submit\"/></form>");

        html.append("<p>Картинка ");
        html.append("<form method=\"post\" action=\"/map/area/" + id + "/edit\">");
        html.append("<input type=\"hidden\" name=\"field\" value=\"picture\">");
        html.append("<input type=\"text\" name=\"value\" value=\"" + map.get(id - 1).getPicture() +"\"/>");
        html.append("<input type=\"submit\"/></form>");

        html.append("<p>Цвет клетки ");
        html.append("<form method=\"post\" action=\"/map/area/" + id + "/edit\">");
        html.append("<input type=\"hidden\" name=\"field\" value=\"cell_color\">");
        html.append("<input type=\"text\" name=\"value\" value=\"" + map.get(id - 1).getCell_color() +"\"/>");
        html.append("<input type=\"submit\"/></form>");
        html.append("<p><a href=\"/map/area/" + id + "\">Назад</a>");

        html.append("</body>");
        return html.toString();
    }

    @POST
    @Path("/map/area/{id}/edit")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + "; charset=UTF-8")
    public Response handleEdit(
            @PathParam("id") int id,
            @FormParam("field") String field,
            @FormParam("value") String value) {

        if (field == null || field.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Не указано поле для обновления").build();
        }

        if (value == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Значение не может быть null").build();
        }

        try {
            String decodedValue = new String(value.getBytes("ISO-8859-1"), "UTF-8");

            Cadastral_Object obj = map.get(id - 1);
            if (obj == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Участок не найден").build();
            }

            switch(field.toLowerCase()) {
                case "owner":
                    obj.setOwner(decodedValue);
                    break;
                case "seller":
                    obj.addSeller(decodedValue);
                    break;
                case "date":
                    obj.setPurchase_date(decodedValue);
                    break;
                case "price":
                    try {
                        obj.setPrice(Integer.parseInt(decodedValue));
                    } catch (NumberFormatException e) {
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity("Цена должна быть числом").build();
                    }
                    break;
                case "currency":
                    obj.setCurrency(decodedValue);
                    break;
                case "status":
                    obj.setStatus(decodedValue);
                    break;
                case "picture":
                    obj.setPicture(decodedValue);
                    break;
                case "cell_color":
                    obj.setCell_color(decodedValue);
                    break;
                default:
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Неизвестное поле: " + field).build();
            }

            // Сохранение изменений
            Cadastral_Object.saveToJSON(map);

            // Перенаправление обратно на страницу редактирования
            return Response.seeOther(new URI("/map/area/" + id + "/edit")).build();

        } catch (URISyntaxException e) {
            return Response.serverError()
                    .entity("Ошибка создания URI для перенаправления").build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("Ошибка при обновлении: " + e.getMessage()).build();
        }
    }

}
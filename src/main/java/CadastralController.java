import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
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

    private Response checkAuth(@CookieParam("authToken") String authToken) {
        if (authToken == null || !LoginService.isValidToken(authToken)) {
            return Response.seeOther(URI.create("/login")).build();
        }
        return null;
    }
    @GET
    @Path("/")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + "; charset=UTF-8")
    public Response toMap() {


        try{
            return Response.seeOther(new URI("/map/")).build();

        } catch (URISyntaxException e) {
            return Response.serverError()
                    .entity("Ошибка создания URI для перенаправления").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity("Ошибка " + e.toString()).build();
        }
    }


    @GET
    @Path("/map.webp")
    @Produces("image/webp")
    public Response getMapImage() {
        File file = new File("map.webp");
        if (!file.exists()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(file).build();
    }

    @GET
    @Path("/map/")
    @Produces(MediaType.TEXT_HTML + "; charset=UTF-8")
    public String showGrid() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang='ru'>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<title>Кадастровая карта</title>");
        html.append("<style>");
        html.append("body {");
        html.append("  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;");
        html.append("  background-color: #f8f9fa;");
        html.append("  margin: 0;");
        html.append("  padding: 20px;");
        html.append("  color: #333;");
        html.append("}");
        html.append(".header {");
        html.append("  text-align: center;");
        html.append("  margin-bottom: 30px;");
        html.append("}");
        html.append("h1 {");
        html.append("  color: #2c3e50;");
        html.append("  margin-bottom: 10px;");
        html.append("}");
        html.append(".map-container {");
        html.append("  display: flex;");
        html.append("  flex-direction: column;");
        html.append("  align-items: center;");
        html.append("  margin-bottom: 30px;");
        html.append("}");
        html.append(".grid-container {");
        html.append("  position: relative;");
        html.append("  background-image: url('/map.webp');");
        html.append("  background-size: contain;");
        html.append("  background-position: 2px 2px;");
        html.append("  background-repeat: no-repeat;");
        html.append("  width: 100%;");
        html.append("  max-width: 896px;");
        html.append("  height: 896px;");
        html.append("  margin: 0  auto;");
        html.append("  padding: 0  auto;");
        html.append("  border-radius: 0px;");
        html.append("  box-shadow: 0 4px 15px rgba(0,0,0,0.1);");
        html.append("  overflow: hidden;");
        html.append("}");
        html.append(".grid {");
        html.append("  display: grid;");
        html.append("  grid-template-columns: repeat(14, 64px);");
        html.append("  gap: 0px;");
        html.append("  height: 100%;");
        html.append("}");
        html.append(".cell {");
        html.append("  width: 64px;");
        html.append("  height: 64px;");
        html.append("  display: flex;");
        html.append("  justify-content: center;");
        html.append("  align-items: center;");
        html.append("  opacity: 0.7;");
        html.append("  transition: all 0.2s ease;");
        html.append("  border-radius: 0px;");
        html.append("  position: relative;");
        html.append("}");
        html.append(".cell:hover {");
        html.append("  opacity: 0.9;");
        html.append("  transform: scale(1.05);");
        html.append("  box-shadow: 0 0 8px rgba(0,0,0,0.3);");
        html.append("  z-index: 10;");
        html.append("}");
        html.append(".actions {");
        html.append("  text-align: center;");
        html.append("  margin-top: 20px;");
        html.append("}");
        html.append(".btn {");
        html.append("  display: inline-block;");
        html.append("  padding: 10px 20px;");
        html.append("  background-color: #3498db;");
        html.append("  color: white;");
        html.append("  text-decoration: none;");
        html.append("  border-radius: 5px;");
        html.append("  transition: background-color 0.3s;");
        html.append("  margin: 0 10px;");
        html.append("}");
        html.append(".btn:hover {");
        html.append("  background-color: #2980b9;");
        html.append("}");
        html.append(".btn-applications {");
        html.append("  background-color: #2ecc71;");
        html.append("}");
        html.append(".btn-applications:hover {");
        html.append("  background-color: #27ae60;");
        html.append("}");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='header'>");
        html.append("<h1>Кадастровая карта</h1>");
        html.append("</div>");

        html.append("<div class='map-container'>");
        html.append("<div class='grid-container'>");
        html.append("<div class='grid'>");

        try {
            map = Cadastral_Object.loadFromJSON();

            if (map.size() != 196) {
                throw new RuntimeException("Ожидается 196 участков, получено: " + map.size());
            }

            for (int i = 1; i <= 196; i++) {
                Cadastral_Object obj = map.get(i - 1);
                String color = obj.getCell_color() != null ? obj.getCell_color() : "rgba(238, 238, 238, 0.7)";

                html.append("<a href='/map/area/").append(i).append("' class='cell' ")
                        .append("style='background:").append(color).append("' ")
                        .append("data-id='").append(i).append("' ")
                        .append("title='Участок ").append(i).append("'></a>");
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки данных", e);
        }

        html.append("</div></div>");
        html.append("<div class='actions'>");
        html.append("<a href='/applications/' class='btn btn-applications'>Заявки игроков</a>");
        html.append("</div>");
        html.append("</div>");
        html.append("</body></html>");
        return html.toString();
    }

    @GET
    @Path("/map/area/{id}")
    @Produces(MediaType.TEXT_HTML + "; charset=UTF-8")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + "; charset=UTF-8")
    public String handleClick(@PathParam("id") int id) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang='ru'>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<title>Участок " + id + "</title>");
        html.append("<style>");
        html.append("body {");
        html.append("    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;");
        html.append("    background-color: #f8f9fa;");
        html.append("    margin: 0;");
        html.append("    padding: 20px;");
        html.append("    color: #333;");
        html.append("}");
        html.append(".container {");
        html.append("    max-width: 800px;");
        html.append("    margin: 0 auto;");
        html.append("    background: white;");
        html.append("    padding: 25px;");
        html.append("    border-radius: 10px;");
        html.append("    box-shadow: 0 2px 10px rgba(0,0,0,0.1);");
        html.append("}");
        html.append("h1 {");
        html.append("    color: #2c3e50;");
        html.append("    border-bottom: 2px solid #eee;");
        html.append("    padding-bottom: 10px;");
        html.append("}");
        html.append(".property {");
        html.append("    margin-bottom: 15px;");
        html.append("    padding-bottom: 15px;");
        html.append("    border-bottom: 1px solid #eee;");
        html.append("}");
        html.append(".property-title {");
        html.append("    font-weight: bold;");
        html.append("    color: #7f8c8d;");
        html.append("    margin-bottom: 5px;");
        html.append("}");
        html.append(".property-value {");
        html.append("    font-size: 16px;");
        html.append("}");
        html.append(".btn {");
        html.append("    display: inline-block;");
        html.append("    padding: 8px 16px;");
        html.append("    background-color: #3498db;");
        html.append("    color: white;");
        html.append("    text-decoration: none;");
        html.append("    border-radius: 4px;");
        html.append("    transition: all 0.3s;");
        html.append("    border: none;");
        html.append("    cursor: pointer;");
        html.append("    font-size: 14px;");
        html.append("}");
        html.append(".btn:hover {");
        html.append("    background-color: #2980b9;");
        html.append("}");
        html.append(".btn-back {");
        html.append("    background-color: #95a5a6;");
        html.append("    margin-bottom: 20px;");
        html.append("}");
        html.append(".btn-back:hover {");
        html.append("    background-color: #7f8c8d;");
        html.append("}");
        html.append(".btn-apply {");
        html.append("    background-color: #2ecc71;");
        html.append("}");
        html.append(".btn-apply:hover {");
        html.append("    background-color: #27ae60;");
        html.append("}");
        html.append(".btn-edit {");
        html.append("    background-color: #f39c12;");
        html.append("}");
        html.append(".btn-edit:hover {");
        html.append("    background-color: #e67e22;");
        html.append("}");
        html.append("form {");
        html.append("    display: inline-block;");
        html.append("    margin-left: 10px;");
        html.append("}");
        html.append("input[type='text'] {");
        html.append("    padding: 8px;");
        html.append("    border: 1px solid #ddd;");
        html.append("    border-radius: 4px;");
        html.append("    margin-right: 5px;");
        html.append("}");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='container'>");

        html.append("<a href='/map/' class='btn btn-back'>← Назад к карте</a>");
        html.append("<h1>Участок " + id + "</h1>");

        html.append("<div class='property'>");
        html.append("<div class='property-title'>Владелец</div>");
        html.append("<div class='property-value'>" + map.get(id - 1).getOwner() + "</div>");
        html.append("</div>");

        html.append("<div class='property'>");
        html.append("<div class='property-title'>Бывшие владельцы</div>");
        html.append("<div class='property-value'>" + map.get(id - 1).getSellers().toString() + "</div>");
        html.append("</div>");

        html.append("<div class='property'>");
        html.append("<div class='property-title'>Дата покупки</div>");
        html.append("<div class='property-value'>" + map.get(id - 1).getPurchase_date() + "</div>");
        html.append("</div>");

        html.append("<div class='property'>");
        html.append("<div class='property-title'>Цена</div>");
        html.append("<div class='property-value'>" + map.get(id - 1).getPrice() + " " + map.get(id - 1).getCurrency() + "</div>");
        html.append("</div>");

        html.append("<div class='property'>");
        html.append("<div class='property-title'>Статус</div>");
        html.append("<div class='property-value'>" + map.get(id - 1).getStatus() + "</div>");
        html.append("</div>");

        html.append("<div class='property'>");
        html.append("<a href='/map/area/" + id + "/send_application' class='btn btn-apply'>Оставить заявку</a>");
        html.append("<a href='/map/area/" + id + "/edit' class='btn btn-edit'>Редактировать</a>");
        html.append("</div>");

        html.append("</div>");
        html.append("</body></html>");
        return html.toString();


    }

    @GET
    @Path("/map/area/{id}/edit")
    @Produces(MediaType.TEXT_HTML + "; charset=UTF-8")
    public Response editArea(@PathParam("id") int id, @CookieParam("authToken") String authToken) {
        Response authResponse = checkAuth(authToken);
        if (authResponse != null) return authResponse;


        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang='ru'>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<title>Участок " + id + "</title>");
        html.append("<style>");
        html.append("body {");
        html.append("    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;");
        html.append("    background-color: #f8f9fa;");
        html.append("    margin: 0;");
        html.append("    padding: 20px;");
        html.append("    color: #333;");
        html.append("}");
        html.append(".container {");
        html.append("    max-width: 800px;");
        html.append("    margin: 0 auto;");
        html.append("    background: white;");
        html.append("    padding: 25px;");
        html.append("    border-radius: 10px;");
        html.append("    box-shadow: 0 2px 10px rgba(0,0,0,0.1);");
        html.append("}");
        html.append("h1 {");
        html.append("    color: #2c3e50;");
        html.append("    border-bottom: 2px solid #eee;");
        html.append("    padding-bottom: 10px;");
        html.append("}");
        html.append(".property {");
        html.append("    margin-bottom: 15px;");
        html.append("    padding-bottom: 15px;");
        html.append("    border-bottom: 1px solid #eee;");
        html.append("}");
        html.append(".property-title {");
        html.append("    font-weight: bold;");
        html.append("    color: #7f8c8d;");
        html.append("    margin-bottom: 5px;");
        html.append("}");
        html.append(".property-value {");
        html.append("    font-size: 16px;");
        html.append("}");
        html.append(".btn {");
        html.append("    display: inline-block;");
        html.append("    padding: 8px 16px;");
        html.append("    background-color: #3498db;");
        html.append("    color: white;");
        html.append("    text-decoration: none;");
        html.append("    border-radius: 4px;");
        html.append("    transition: all 0.3s;");
        html.append("    border: none;");
        html.append("    cursor: pointer;");
        html.append("    font-size: 14px;");
        html.append("}");
        html.append(".btn:hover {");
        html.append("    background-color: #2980b9;");
        html.append("}");
        html.append(".btn-back {");
        html.append("    background-color: #95a5a6;");
        html.append("    margin-bottom: 20px;");
        html.append("}");
        html.append(".btn-back:hover {");
        html.append("    background-color: #7f8c8d;");
        html.append("}");
        html.append(".btn-apply {");
        html.append("    background-color: #2ecc71;");
        html.append("}");
        html.append(".btn-apply:hover {");
        html.append("    background-color: #27ae60;");
        html.append("}");
        html.append(".btn-edit {");
        html.append("    background-color: #f39c12;");
        html.append("}");
        html.append(".btn-edit:hover {");
        html.append("    background-color: #e67e22;");
        html.append("}");
        html.append("form {");
        html.append("    display: inline-block;");
        html.append("    margin-left: 10px;");
        html.append("}");
        html.append("input[type='text'] {");
        html.append("    padding: 8px;");
        html.append("    border: 1px solid #ddd;");
        html.append("    border-radius: 4px;");
        html.append("    margin-right: 5px;");
        html.append("}");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='container'>");
        html.append("<h1>Участок "+id+"</h1>");

        html.append("<a href='/map/area/" + id + "' class='btn btn-back'>← Назад к участку</a>");
        html.append("<h1>Редактирование участка " + id + "</h1>");

        html.append("<div class='property'>");
        html.append("<div class='property-title'>Владелец</div>");
        html.append("<form method='post' action='/map/area/" + id + "/edit'>");
        html.append("<input type='hidden' name='field' value='owner'>");
        html.append("<input type='text' name='value' value='" + map.get(id - 1).getOwner() + "'>");
        html.append("<button type='submit' class='btn'>Сохранить</button>");
        html.append("</form>");
        html.append("</div>");

        html.append("<div class='property'>");
        html.append("<div class='property-title'>Бывшие владельцы</div>");
        html.append("<div class='property-value'>" + map.get(id - 1).getSellers().toString() + "</div>");
        html.append("<form method='post' action='/map/area/" + id + "/edit'>");
        html.append("<input type='hidden' name='field' value='seller'>");
        html.append("<input type='text' name='value' placeholder='Добавить бывшего владельца'>");
        html.append("<button type='submit' class='btn'>Добавить</button>");
        html.append("</form>");
        html.append("</div>");

        html.append("<div class='property'>");
        html.append("<div class='property-title'>Дата покупки</div>");
        html.append("<form method='post' action='/map/area/" + id + "/edit'>");
        html.append("<input type='hidden' name='field' value='date'>");
        html.append("<input type='text' name='value' value='" + map.get(id - 1).getPurchase_date() + "'>");
        html.append("<button type='submit' class='btn'>Сохранить</button>");
        html.append("</form>");
        html.append("</div>");

        html.append("<div class='property'>");
        html.append("<div class='property-title'>Цена</div>");
        html.append("<form method='post' action='/map/area/" + id + "/edit'>");
        html.append("<input type='hidden' name='field' value='price'>");
        html.append("<input type='text' name='value' value='" + map.get(id - 1).getPrice() + "'>");
        html.append("<button type='submit' class='btn'>Сохранить</button>");
        html.append("</form>");
        html.append("<form method='post' action='/map/area/" + id + "/edit'>");
        html.append("<input type='hidden' name='field' value='currency'>");
        html.append("<input type='text' name='value' value='" + map.get(id - 1).getCurrency() + "'>");
        html.append("<button type='submit' class='btn'>Сохранить</button>");
        html.append("</form>");
        html.append("</div>");

        html.append("<div class='property'>");
        html.append("<div class='property-title'>Статус</div>");
        html.append("<form method='post' action='/map/area/" + id + "/edit'>");
        html.append("<input type='hidden' name='field' value='status'>");
        html.append("<input type='text' name='value' value='" + map.get(id - 1).getStatus() + "'>");
        html.append("<button type='submit' class='btn'>Сохранить</button>");
        html.append("</form>");
        html.append("</div>");

        html.append("<div class='property'>");
        html.append("<div class='property-title'>Картинка</div>");
        html.append("<form method='post' action='/map/area/" + id + "/edit'>");
        html.append("<input type='hidden' name='field' value='picture'>");
        html.append("<input type='text' name='value' value='" + map.get(id - 1).getPicture() + "'>");
        html.append("<button type='submit' class='btn'>Сохранить</button>");
        html.append("</form>");
        html.append("</div>");

        html.append("<div class='property'>");
        html.append("<div class='property-title'>Цвет клетки</div>");
        html.append("<form method='post' action='/map/area/" + id + "/edit'>");
        html.append("<input type='hidden' name='field' value='cell_color'>");
        html.append("<input type='text' name='value' value='" + map.get(id - 1).getCell_color() + "'>");
        html.append("<button type='submit' class='btn'>Сохранить</button>");
        html.append("</form>");
        html.append("</div>");

        html.append("</div>");
        html.append("</body></html>");
        return Response.ok(html.toString()).build();
    }

    @POST
    @Path("/map/area/{id}/edit")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + "; charset=UTF-8")
    public Response handleEdit(
            @PathParam("id") int id,
            @FormParam("field") String field,
            @CookieParam("authToken") String authToken,
            @FormParam("value") String value) {
        checkAuth(authToken);
        Response authResponse = checkAuth(authToken);
        if (authResponse != null) return authResponse;

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

            Cadastral_Object.saveToJSON(map);

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
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
        html.append("  padding: 15px;");
        html.append("  color: #333;");
        html.append("}");
        html.append(".header {");
        html.append("  text-align: center;");
        html.append("  margin-bottom: 20px;");
        html.append("}");
        html.append("h1 {");
        html.append("  color: #2c3e50;");
        html.append("  margin-bottom: 10px;");
        html.append("  font-size: 1.8rem;");
        html.append("}");
        html.append(".map-container {");
        html.append("  display: flex;");
        html.append("  flex-direction: column;");
        html.append("  align-items: center;");
        html.append("  margin-bottom: 20px;");
        html.append("  overflow-x: auto;");
        html.append("  width: 100%;");
        html.append("}");
        html.append(".grid-container {");
        html.append("  position: relative;");
        html.append("  background-image: url('/map.webp');");
        html.append("  background-size: contain;");
        html.append("  background-position: 0px 0px;");
        html.append("  background-repeat: no-repeat;");
        html.append("  width: 100%;");
        html.append("  max-width: 896px;");
        html.append("  height: auto;");
        html.append("  aspect-ratio: 1/1;");
        html.append("  margin: 0 auto;");
        html.append("  border-radius: 0px;");
        html.append("  box-shadow: 0 4px 15px rgba(0,0,0,0.1);");
        html.append("  overflow: hidden;");
        html.append("}");
        html.append(".grid {");
        html.append("  display: grid;");
        html.append("  grid-template-columns: repeat(14, 1fr);");
        html.append("  gap: 0px;");
        html.append("  height: 100%;");
        html.append("  width: 100%;");
        html.append("}");
        html.append(".cell {");
        html.append("  width: 100%;");
        html.append("  padding-bottom: 100%;"); // Создает квадратные ячейки
        html.append("  display: block;");
        html.append("  opacity: 0.6;");
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
        html.append("  padding: 12px 24px;");
        html.append("  background-color: #3498db;");
        html.append("  color: white;");
        html.append("  text-decoration: none;");
        html.append("  border-radius: 5px;");
        html.append("  transition: background-color 0.3s;");
        html.append("  margin: 0 10px 10px 10px;");
        html.append("  font-size: 16px;");
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
        html.append(".btn-colors {");
        html.append("  background-color: #2ecc71;");
        html.append("}");
        html.append(".btn-colors:hover {");
        html.append("  background-color: #27ae60;");
        html.append("}");
        html.append("@media (max-width: 768px) {");
        html.append("  body { padding: 10px; }");
        html.append("  h1 { font-size: 1.5rem; }");
        html.append("  .btn {");
        html.append("    padding: 10px 20px;");
        html.append("    margin: 5px;");
        html.append("  }");
        html.append("}");
        html.append("@media (max-width: 480px) {");
        html.append("  body { padding: 8px; }");
        html.append("  h1 { font-size: 1.3rem; }");
        html.append("  .btn {");
        html.append("    display: block;");
        html.append("    width: 100%;");
        html.append("    margin: 5px 0;");
        html.append("    box-sizing: border-box;");
        html.append("  }");
        html.append("  .actions {");
        html.append("    width: 100%;");
        html.append("  }");
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
        html.append("<a href='/map/colors' class='btn btn-colors'>Цвета игроков</a>");
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
        html.append("    padding: 15px;");
        html.append("    color: #333;");
        html.append("}");
        html.append(".container {");
        html.append("    max-width: 800px;");
        html.append("    margin: 0 auto;");
        html.append("    background: white;");
        html.append("    padding: 20px;");
        html.append("    border-radius: 10px;");
        html.append("    box-shadow: 0 2px 10px rgba(0,0,0,0.1);");
        html.append("}");
        html.append("h1 {");
        html.append("    color: #2c3e50;");
        html.append("    border-bottom: 2px solid #eee;");
        html.append("    padding-bottom: 10px;");
        html.append("    margin-top: 0;");
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
        html.append("    padding: 10px 20px;");
        html.append("    background-color: #3498db;");
        html.append("    color: white;");
        html.append("    text-decoration: none;");
        html.append("    border-radius: 4px;");
        html.append("    transition: all 0.3s;");
        html.append("    border: none;");
        html.append("    cursor: pointer;");
        html.append("    font-size: 16px;");
        html.append("    margin-right: 10px;");
        html.append("    margin-bottom: 10px;");
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
        html.append(".property-image {");
        html.append("    width: 100%;");
        html.append("    max-height: 400px;");
        html.append("    object-fit: cover;");
        html.append("    border-radius: 8px;");
        html.append("    margin-bottom: 20px;");
        html.append("    box-shadow: 0 2px 5px rgba(0,0,0,0.1);");
        html.append("}");
        html.append("@media (max-width: 768px) {");
        html.append("    body { padding: 10px; }");
        html.append("    .container { padding: 15px; }");
        html.append("    .btn {");
        html.append("        display: block;");
        html.append("        width: 100%;");
        html.append("        margin-right: 0;");
        html.append("        box-sizing: border-box;");
        html.append("    }");
        html.append("}");
        html.append("@media (max-width: 480px) {");
        html.append("    body { padding: 8px; }");
        html.append("    .container { padding: 12px; }");
        html.append("    h1 { font-size: 1.4rem; }");
        html.append("    .property-title, .property-value {");
        html.append("        font-size: 14px;");
        html.append("    }");
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
        if (!map.get(id-1).getPicture().equals("Картинка") && !map.get(id-1).getPicture().equals("-")) {
            html.append("<div class='property'>");
            html.append("<img src='" + map.get(id-1).getPicture() + "' alt='Изображение участка " + id + "' class='property-image'>");
            html.append("</div>");
        }

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
        html.append("<title>Редактирование участка " + id + "</title>");
        html.append("<style>");
        html.append("body {");
        html.append("    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;");
        html.append("    background-color: #f8f9fa;");
        html.append("    margin: 0;");
        html.append("    padding: 15px;");
        html.append("    color: #333;");
        html.append("}");
        html.append(".container {");
        html.append("    max-width: 800px;");
        html.append("    margin: 0 auto;");
        html.append("    background: white;");
        html.append("    padding: 20px;");
        html.append("    border-radius: 10px;");
        html.append("    box-shadow: 0 2px 10px rgba(0,0,0,0.1);");
        html.append("}");
        html.append("h1 {");
        html.append("    color: #2c3e50;");
        html.append("    border-bottom: 2px solid #eee;");
        html.append("    padding-bottom: 10px;");
        html.append("    margin-top: 0;");
        html.append("}");
        html.append(".property {");
        html.append("    margin-bottom: 20px;");
        html.append("    padding-bottom: 20px;");
        html.append("    border-bottom: 1px solid #eee;");
        html.append("}");
        html.append(".property-title {");
        html.append("    font-weight: bold;");
        html.append("    color: #7f8c8d;");
        html.append("    margin-bottom: 8px;");
        html.append("    display: block;");
        html.append("}");
        html.append(".property-value {");
        html.append("    font-size: 16px;");
        html.append("    margin-bottom: 10px;");
        html.append("}");
        html.append(".btn {");
        html.append("    display: inline-block;");
        html.append("    padding: 10px 20px;");
        html.append("    background-color: #3498db;");
        html.append("    color: white;");
        html.append("    text-decoration: none;");
        html.append("    border-radius: 4px;");
        html.append("    transition: all 0.3s;");
        html.append("    border: none;");
        html.append("    cursor: pointer;");
        html.append("    font-size: 16px;");
        html.append("}");
        html.append(".btn:hover {");
        html.append("    background-color: #2980b9;");
        html.append("}");
        html.append(".btn-back {");
        html.append("    background-color: #95a5a6;");
        html.append("    margin-bottom: 20px;");
        html.append("}");
        html.append(".btn-colors {");
        html.append("    background-color: #94a8a6;");
        html.append("    margin-bottom: 20px;");
        html.append("}");
        html.append(".btn-back:hover {");
        html.append("    background-color: #7f8c8d;");
        html.append("}");
        html.append(".btn-save {");
        html.append("    background-color: #2ecc71;");
        html.append("}");
        html.append(".btn-save:hover {");
        html.append("    background-color: #27ae60;");
        html.append("}");
        html.append(".form-group {");
        html.append("    margin-bottom: 15px;");
        html.append("}");
        html.append("input[type='text'] {");
        html.append("    width: 100%;");
        html.append("    padding: 12px;");
        html.append("    border: 1px solid #ddd;");
        html.append("    border-radius: 4px;");
        html.append("    font-size: 16px;");
        html.append("    box-sizing: border-box;");
        html.append("    margin-bottom: 10px;");
        html.append("}");
        html.append(".form-row {");
        html.append("    display: flex;");
        html.append("    gap: 10px;");
        html.append("    margin-bottom: 10px;");
        html.append("}");
        html.append(".form-row input {");
        html.append("    flex: 1;");
        html.append("    margin-bottom: 0;");
        html.append("}");
        html.append("@media (max-width: 768px) {");
        html.append("    body { padding: 10px; }");
        html.append("    .container { padding: 15px; }");
        html.append("    .form-row {");
        html.append("        flex-direction: column;");
        html.append("        gap: 0;");
        html.append("    }");
        html.append("    .btn {");
        html.append("        display: block;");
        html.append("        width: 100%;");
        html.append("        margin-bottom: 10px;");
        html.append("        box-sizing: border-box;");
        html.append("    }");
        html.append("}");
        html.append("@media (max-width: 480px) {");
        html.append("    body { padding: 8px; }");
        html.append("    .container { padding: 12px; }");
        html.append("    h1 { font-size: 1.4rem; }");
        html.append("    .property-title, .property-value {");
        html.append("        font-size: 14px;");
        html.append("    }");
        html.append("    input[type='text'] {");
        html.append("        padding: 14px;");
        html.append("    }");
        html.append("}");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='container'>");

        html.append("<a href='/map/area/" + id + "' class='btn btn-back'>← Назад к участку</a>");
        html.append("<a href='/map/colors/" + "' class='btn btn-colors'>Цвета участков</a>");
        html.append("<h1>Редактирование участка " + id + "</h1>");

        html.append("<div class='property'>");
        html.append("<span class='property-title'>Владелец</span>");
        html.append("<form method='post' action='/map/area/" + id + "/edit' class='form-group'>");
        html.append("<input type='hidden' name='field' value='owner'>");
        html.append("<input type='text' name='value' value='" + map.get(id - 1).getOwner() + "'>");
        html.append("<button type='submit' class='btn btn-save'>Сохранить</button>");
        html.append("</form>");
        html.append("</div>");

        html.append("<div class='property'>");
        html.append("<span class='property-title'>Бывшие владельцы</span>");
        html.append("<div class='property-value'>" + map.get(id - 1).getSellers().toString() + "</div>");
        html.append("<form method='post' action='/map/area/" + id + "/edit' class='form-group'>");
        html.append("<input type='hidden' name='field' value='seller'>");
        html.append("<input type='text' name='value' placeholder='Добавить бывшего владельца'>");
        html.append("<button type='submit' class='btn'>Добавить</button>");
        html.append("</form>");
        html.append("</div>");

        html.append("<div class='property'>");
        html.append("<span class='property-title'>Дата покупки</span>");
        html.append("<form method='post' action='/map/area/" + id + "/edit' class='form-group'>");
        html.append("<input type='hidden' name='field' value='date'>");
        html.append("<input type='text' name='value' value='" + map.get(id - 1).getPurchase_date() + "'>");
        html.append("<button type='submit' class='btn btn-save'>Сохранить</button>");
        html.append("</form>");
        html.append("</div>");

        html.append("<div class='property'>");
        html.append("<span class='property-title'>Цена и валюта</span>");
        html.append("<form method='post' action='/map/area/" + id + "/edit' class='form-group'>");
        html.append("<input type='hidden' name='field' value='price'>");
        html.append("<div class='form-row'>");
        html.append("<input type='text' name='value' value='" + map.get(id - 1).getPrice() + "' placeholder='Цена'>");
        html.append("<input type='text' name='currency' value='" + map.get(id - 1).getCurrency() + "' placeholder='Валюта'>");
        html.append("</div>");
        html.append("<button type='submit' class='btn btn-save'>Сохранить</button>");
        html.append("</form>");
        html.append("</div>");

        html.append("<div class='property'>");
        html.append("<span class='property-title'>Статус</span>");
        html.append("<form method='post' action='/map/area/" + id + "/edit' class='form-group'>");
        html.append("<input type='hidden' name='field' value='status'>");
        html.append("<input type='text' name='value' value='" + map.get(id - 1).getStatus() + "'>");
        html.append("<button type='submit' class='btn btn-save'>Сохранить</button>");
        html.append("</form>");
        html.append("</div>");

        html.append("<div class='property'>");
        html.append("<span class='property-title'>Картинка (URL)</span>");
        html.append("<form method='post' action='/map/area/" + id + "/edit' class='form-group'>");
        html.append("<input type='hidden' name='field' value='picture'>");
        html.append("<input type='text' name='value' value='" + map.get(id - 1).getPicture() + "'>");
        html.append("<button type='submit' class='btn btn-save'>Сохранить</button>");
        html.append("</form>");
        html.append("</div>");

        html.append("<div class='property'>");
        html.append("<span class='property-title'>Цвет клетки (HEX или RGB)</span>");
        html.append("<form method='post' action='/map/area/" + id + "/edit' class='form-group'>");
        html.append("<input type='hidden' name='field' value='cell_color'>");
        html.append("<input type='text' name='value' value='" + map.get(id - 1).getCell_color() + "'>");
        html.append("<button type='submit' class='btn btn-save'>Сохранить</button>");
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


    @GET
    @Path("/map/colors")
    @Produces(MediaType.TEXT_HTML + "; charset=UTF-8")
    public Response mapColors() {


        List<Color_Object> ownerColors = new ArrayList<>();
        //Color_Object dummy = new Color_Object("STIRLICO","#ffc369");
        //ownerColors.add(dummy);
        //Color_Object.saveToJSON(ownerColors);
        try {
            ownerColors = Color_Object.loadFromJSON();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang='ru'>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<title>Цвета владельцев</title>");
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
        html.append("    margin-bottom: 25px;");
        html.append("    text-align: center;");
        html.append("}");
        html.append(".back-button {");
        html.append("    display: inline-block;");
        html.append("    padding: 10px 20px;");
        html.append("    background-color: #4CAF50;");
        html.append("    color: white;");
        html.append("    text-decoration: none;");
        html.append("    border-radius: 5px;");
        html.append("    transition: background-color 0.3s;");
        html.append("    margin-bottom: 20px;");
        html.append("}");
        html.append(".back-button:hover {");
        html.append("    background-color: #45a049;");
        html.append("}");
        html.append(".owner-list {");
        html.append("    display: grid;");
        html.append("    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));");
        html.append("    gap: 15px;");
        html.append("}");
        html.append(".owner-item {");
        html.append("    display: flex;");
        html.append("    align-items: center;");
        html.append("    padding: 15px;");
        html.append("    background-color: #f9f9f9;");
        html.append("    border-radius: 8px;");
        html.append("    border-left: 4px solid #ddd;");
        html.append("}");
        html.append(".color-box {");
        html.append("    width: 30px;");
        html.append("    height: 30px;");
        html.append("    border-radius: 4px;");
        html.append("    margin-right: 15px;");
        html.append("    border: 1px solid #ddd;");
        html.append("}");
        html.append(".owner-info {");
        html.append("    flex-grow: 1;");
        html.append("}");
        html.append(".owner-name {");
        html.append("    font-weight: bold;");
        html.append("    margin-bottom: 5px;");
        html.append("}");
        html.append(".color-code {");
        html.append("    font-size: 12px;");
        html.append("    color: #777;");
        html.append("}");
        html.append("@media (max-width: 768px) {");
        html.append("    .container {");
        html.append("        padding: 15px;");
        html.append("    }");
        html.append("    .owner-list {");
        html.append("        grid-template-columns: 1fr;");
        html.append("    }");
        html.append("}");
        html.append("@media (max-width: 480px) {");
        html.append("    body {");
        html.append("        padding: 10px;");
        html.append("    }");
        html.append("    .container {");
        html.append("        padding: 12px;");
        html.append("    }");
        html.append("    .back-button {");
        html.append("        display: block;");
        html.append("        text-align: center;");
        html.append("        margin-bottom: 15px;");
        html.append("    }");
        html.append("}");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='container'>");
        html.append("<a href='/map/' class='back-button'>← Назад к карте</a>");
        html.append("<h1>Цвета владельцев участков</h1>");

        html.append("<div class='owner-list'>");


        for (Color_Object entry : ownerColors) {
            String owner = entry.getName();
            String color = entry.getColor();

            html.append("<div class='owner-item'>");
            html.append("<div class='color-box' style='background-color: ").append(color).append("'></div>");
            html.append("<div class='owner-info'>");
            html.append("<div class='owner-name'>").append(owner).append("</div>");
            html.append("<div class='color-code'>").append(color).append("</div>");
            html.append("</div>");
            html.append("</div>");
        }

        html.append("</div>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return Response.ok(html.toString()).build();
    }


}
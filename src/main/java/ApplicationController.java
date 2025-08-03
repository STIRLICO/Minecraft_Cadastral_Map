import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Path("/")//ПЕРЕИМЕНОВАТЬ ВСЁ В РЕКВЕСТ
public class ApplicationController {

    private List<ApplicationService> applications = new ArrayList<>();

    {
        try {
            //ApplicationService test = new ApplicationService(0,"Сервер","Тест");
            //applications.add(test);
            //ApplicationService.saveToJSON(applications);
            applications = ApplicationService.loadFromJSON();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @GET
    @Path("map/area/{id}/send_application")
    @Produces(MediaType.TEXT_HTML + "; charset=UTF-8")
    public Response applicationArea(@PathParam("id") int id) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><title>Cadastral Map Area " + id + "</title><meta charset=\"UTF-8\">");

        html.append("<style>");
        html.append("body {");
        html.append("    font-family:  sans-serif;");
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
        html.append(".back-button {");
        html.append("    display: inline-block;");
        html.append("    padding: 10px 20px;");
        html.append("    background-color: #4CAF50;");
        html.append("    color: white;");
        html.append("    text-decoration: none;");
        html.append("    border-radius: 5px;");
        html.append("    transition: background-color 0.3s;");
        html.append("}");
        html.append("form { display: flex; flex-direction: column; gap: 15px; }");
        html.append("input, select, textarea { padding: 8px; border: 1px solid #ddd; border-radius: 4px; }");
        html.append("textarea { height: 150px; resize: vertical; }");
        html.append("button { padding: 10px; background: #4CAF50; color: white; border: none; cursor: pointer; }");
        html.append("button:hover { background: #45a049; }");
        html.append("</style>");

        html.append("</head><body>");
        html.append("<p><a class='back-button'; href=\"/map/" + "\">Назад</a>");
        html.append("<div class ='container'>");
        html.append("<h1>Заявка для участка " + id + "</h1>");

        html.append("<form method=\"post\" action=\"/map/area/" + id + "/send_application/\">");

        html.append("<label for=\"applicant\"><b>Ваш никнейм</b></label>");
        html.append("<input type=\"text\" placeholder=\"Введите ваш никнейм\" name=\"applicant\" required>");

        html.append("<label for=\"request_type\"><b>Тип заявки</b></label>");
        html.append("<select name=\"request_type\" required>");
        html.append("<option value=\"\" disabled selected>Выберите тип заявки</option>");
        html.append("<option value=\"Покупка\">Покупка</option>");
        html.append("<option value=\"Продажа\">Продажа</option>");
        html.append("<option value=\"Жалоба\">Жалоба</option>");
        html.append("<option value=\"Предложение\">Предложение</option>");
        html.append("<option value=\"Другое\">Другое</option>");
        html.append("</select>");


        html.append("<label for=\"text\"><b>Текст заявки</b></label>");
        html.append("<textarea placeholder=\"Напишите вашу заявку...\" name=\"text\" required></textarea>");


        html.append("<button type=\"submit\">Отправить</button>");
        html.append("</form>");
        html.append("</div>");

        html.append("</body></html>");
        return Response.ok(html.toString()).build();
    }

    @POST
    @Path("map/area/{id}/send_application")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + "; charset=UTF-8")
    public Response handleApplication(
            @PathParam("id") int id,
            @FormParam("applicant") String applicant,
            @FormParam("text") String text,
            @FormParam("request_type") String request) {

        if (applicant == null || text == null || applicant.isEmpty() || text.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Все поля должны быть заполнены").build();
        }

        try {
            String decoded_applicant = new String(applicant.getBytes("ISO-8859-1"), "UTF-8");
            String decoded_text = new String(text.getBytes("ISO-8859-1"), "UTF-8");
            String decoded_type = new String(request.getBytes("ISO-8859-1"), "UTF-8");

            if (applications.size() > 0) {
                int maxId = applications.stream().mapToInt(ApplicationService::getId).max().orElse(0);
                ApplicationService.setAll_id(maxId + 1);
            }

            ApplicationService application = new ApplicationService(id, decoded_applicant, decoded_text,decoded_type);
            applications.add(application);

            ApplicationService.saveToJSON(applications);
            TelegramIntegration notice = new TelegramIntegration();
            notice.sendNotification();
            return Response.seeOther(new URI("map/send_application_success")).build();

        } catch (URISyntaxException e) {
            return Response.serverError()
                    .entity("Ошибка создания URI для перенаправления").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity("Ошибка при сохранении заявки: " + e.toString()).build();
        }
    }
    @GET
    @Path("/map/send_application_success")
    @Produces(MediaType.TEXT_HTML + "; charset=UTF-8")
    public String send_application_success() {


        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang='ru'>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<title>Заявка отправлена</title>");
        html.append("<style>");
        html.append("body {");
        html.append("    font-family: 'Arial', sans-serif;");
        html.append("    background-color: #f5f5f5;");
        html.append("    margin: 0;");
        html.append("    padding: 0;");
        html.append("    display: flex;");
        html.append("    justify-content: center;");
        html.append("    align-items: center;");
        html.append("    height: 100vh;");
        html.append("    text-align: center;");
        html.append("}");
        html.append(".success-container {");
        html.append("    background-color: white;");
        html.append("    border-radius: 10px;");
        html.append("    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);");
        html.append("    padding: 30px;");
        html.append("    max-width: 500px;");
        html.append("    width: 90%;");
        html.append("}");
        html.append(".success-icon {");
        html.append("    color: #4CAF50;");
        html.append("    font-size: 50px;");
        html.append("    margin-bottom: 20px;");
        html.append("}");
        html.append(".success-title {");
        html.append("    font-size: 24px;");
        html.append("    margin-bottom: 15px;");
        html.append("    color: #333;");
        html.append("}");
        html.append(".success-message {");
        html.append("    font-size: 16px;");
        html.append("    color: #666;");
        html.append("    margin-bottom: 25px;");
        html.append("}");
        html.append(".back-button {");
        html.append("    display: inline-block;");
        html.append("    padding: 10px 20px;");
        html.append("    background-color: #4CAF50;");
        html.append("    color: white;");
        html.append("    text-decoration: none;");
        html.append("    border-radius: 5px;");
        html.append("    transition: background-color 0.3s;");
        html.append("}");
        html.append(".back-button:hover {");
        html.append("    background-color: #45a049;");
        html.append("}");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='success-container'>");
        html.append("    <div class='success-icon'>✓</div>");
        html.append("    <h1 class='success-title'>Заявка успешно отправлена!</h1>");
        html.append("    <p class='success-message'>Спасибо! Ваша заявка принята в обработку.</p>");
        html.append("    <a href='/map/' class='back-button'>Вернуться на карту</a>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    @GET
    @Path("/applications")
    @Produces(MediaType.TEXT_HTML + "; charset=UTF-8")
    public Response applicationsPage() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><title>Cadastral Map Area</title><meta charset=\"UTF-8\">");
        html.append("<style>");
        html.append("body {");
        html.append("    font-family: 'Arial', sans-serif;");
        html.append("    background-color: #f5f5f5;");
        html.append("}");
        html.append(".application-block {");
        html.append("    border: 1px solid #ddd;");
        html.append("    padding: 15px;");
        html.append("    margin: 10px 0;");
        html.append("   border-radius: 5px;");
        html.append("   }");
        html.append(".back-button {");
        html.append("    display: inline-block;");
        html.append("    padding: 10px 20px;");
        html.append("    background-color: #4CAF50;");
        html.append("    color: white;");
        html.append("    text-decoration: none;");
        html.append("    border-radius: 5px;");
        html.append("    transition: background-color 0.3s;");
        html.append("}");
        html.append(".back-button:hover {");
        html.append("    background-color: #4CA049;");
        html.append("}");


        html.append(".type-buy { background-color: #b3ffbf; }");
        html.append(".type-sell { background-color: #ffd88a; }");
        html.append(".type-complaint  { background-color: #ffdee3; }");
        html.append(".type-suggestion { background-color: #b3daff; }");
        html.append(".type-default { background-color: #f9f9f9; }");

        html.append("</style>");
        html.append("</head><body>");
        html.append("<p><a class='back-button'; href=\"/map/" + "\">Назад</a>");
        try {
            applications = ApplicationService.loadFromJSON();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for(ApplicationService application : applications) {
            if(application.isPublished()) {
                String typeClass = "type-default";
                if (application.getType() != null) {
                    switch (application.getType()) {
                        case "Покупка": typeClass = "type-buy"; break;
                        case "Продажа": typeClass = "type-sell"; break;
                        case "Жалоба": typeClass = "type-complaint"; break;
                        case "Предложение": typeClass = "type-suggestion"; break;
                        default: typeClass = "type-default";
                    }
                }

                html.append("<div class=\"application-block " + typeClass + "\">");
                html.append("<p>Заявка №"+application.getId()+" Тип: "+application.getType()+"</p>");
                html.append("<p>Участок №"+application.getArea_id()+"</p>");
                html.append("<p>Ник игрока: "+application.getApplicant()+"</p>");
                html.append("<p>"+application.getText()+"</p>");
                html.append("</div>");
            }
        }

        html.append("</body></html>");
        return Response.ok(html.toString()).build();
    }
}

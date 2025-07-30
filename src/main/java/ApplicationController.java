import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Path("/map")
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
    @Path("/area/{id}/send_application")
    @Produces(MediaType.TEXT_HTML + "; charset=UTF-8")
    public Response applicationArea(@PathParam("id") int id) {



        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><title>Cadastral Map Area "+id+"</title><meta charset=\"UTF-8\"></head>");
        html.append("<body>");
        html.append("<h1>Заявка для участка "+id+"</h1>");

        html.append("    <form method=\"post\" action=\"/map/area/" + id + "/send_application/\">" +
                "      <label for=\"applicant\"><b>Ваш никнейм</b></label>\n" +
                "      <input type=\"text\" placeholder=\"Ваш никнейм\" name=\"applicant\" required>\n" +
                "      <p><label for=\"text\"><b>Текст заявки</b></label>\n" +
                "      <input type=\"textbox\" placeholder=\"Текст заявки\" name=\"text\" required>\n" +
                "      <p><button type=\"submit\">Отправить</button>" +
                "    </form>" );




        html.append("</body>");
        return Response.ok(html.toString()).build();
    }

    @POST
    @Path("/area/{id}/send_application")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + "; charset=UTF-8")
    public Response handleApplication(
            @PathParam("id") int id,
            @FormParam("applicant") String applicant,
            @FormParam("text") String text) {

        if (applicant == null || text == null || applicant.isEmpty() || text.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Все поля должны быть заполнены").build();
        }

        try {
            String decoded_applicant = new String(applicant.getBytes("ISO-8859-1"), "UTF-8");
            String decoded_text = new String(text.getBytes("ISO-8859-1"), "UTF-8");

            if (applications.size() > 0) {
                int maxId = applications.stream().mapToInt(ApplicationService::getId).max().orElse(0);
                ApplicationService.setAll_id(maxId + 1);
            }

            ApplicationService application = new ApplicationService(id, decoded_applicant, decoded_text);
            applications.add(application);

            ApplicationService.saveToJSON(applications);

            return Response.seeOther(new URI("/map/area/" + id + "/send_application_success")).build();

        } catch (URISyntaxException e) {
            return Response.serverError()
                    .entity("Ошибка создания URI для перенаправления").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity("Ошибка при сохранении заявки: " + e.toString()).build();
        }
    }
}

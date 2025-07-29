

import javax.ws.rs.*;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Контроллер входа: выдает и обрабатывает форму входа.
 */
@Path("/login")
public class LoginController {

    private LoginService loginService = new LoginService();

    @GET
    @Produces("text/html")
    public String getForm() {
        String result =
                "<html>" +
                        "  <head>" +
                        "    <title>Вход</title>" +
                        "  </head>" +
                        "  <body>" +
                        "    <h1>Вход</h1>" +
                        "    <form method=\"post\" action=\"/login/\">" +
                        "      <label for=\"uname\"><b>Username</b></label>\n" +
                        "      <input type=\"text\" placeholder=\"Enter Username\" name=\"username\" required>\n" +
                        "      <label for=\"psw\"><b>Password</b></label>\n" +
                        "      <input type=\"password\" placeholder=\"Enter Password\" name=\"password\" required>\n" +
                        "      <button type=\"submit\">Login</button>" +
                        "    </form>" +
                        "  </body>" +
                        "</html>";
        return result;
    }

    @POST
    @Produces("text/html")
    public Response login(@FormParam("username") String username,
                          @FormParam("password") String password) {
        try {
            if (loginService.login(username, password)) {
                String token = loginService.generateToken(username);
                NewCookie authCookie = new NewCookie(
                        "authToken",
                        token,
                        "/",
                        null,
                        null,
                        NewCookie.DEFAULT_MAX_AGE,
                        false
                );
                return Response.seeOther(new URI("/map/"))
                        .cookie(authCookie)
                        .build();
            } else {
                return Response.seeOther(new URI("/login/failure")).build();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GET
    @Path("/success")
    @Produces("text/html")
    public String getSuccessPage() {
        String result =
                "<html>" +
                        "  <head>" +
                        "    <title>Успешный вход</title>" +
                        "  </head>" +
                        "  <body>" +
                        "    <h1>Успешный вход</h1>" +
                        "    <a href=\"/login\">Назад</a>" +
                        "  </body>" +
                        "</html>";
        return result;
    }

    @GET
    @Path("/failure")
    @Produces("text/html")
    public String getFailurePage() {
        String result =
                "<html>" +
                        "  <head>" +
                        "    <title>Ошибка входа</title>" +
                        "  </head>" +
                        "  <body>" +
                        "    <h1>Ошибка входа</h1>" +
                        "    <a href=\"/login\">Назад</a>" +
                        "  </body>" +
                        "</html>";
        return result;
    }
}
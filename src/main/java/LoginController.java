

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
        return "<!DOCTYPE html>" +
                "<html lang='ru'>" +
                "<head>" +
                "  <meta charset='UTF-8'>" +
                "  <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "  <title>Вход в систему</title>" +
                "  <style>" +
                "    body {" +
                "      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;" +
                "      background-color: #f5f7fa;" +
                "      display: flex;" +
                "      justify-content: center;" +
                "      align-items: center;" +
                "      height: 100vh;" +
                "      margin: 0;" +
                "    }" +
                "    .login-container {" +
                "      background: white;" +
                "      padding: 2rem;" +
                "      border-radius: 10px;" +
                "      box-shadow: 0 4px 12px rgba(0,0,0,0.1);" +
                "      width: 100%;" +
                "      max-width: 400px;" +
                "    }" +
                "    h1 {" +
                "      color: #2c3e50;" +
                "      text-align: center;" +
                "      margin-bottom: 1.5rem;" +
                "    }" +
                "    .form-group {" +
                "      margin-bottom: 1.5rem;" +
                "    }" +
                "    label {" +
                "      display: block;" +
                "      margin-bottom: 0.5rem;" +
                "      color: #7f8c8d;" +
                "      font-weight: 500;" +
                "    }" +
                "    input {" +
                "      width: 100%;" +
                "      padding: 0.75rem;" +
                "      border: 1px solid #ddd;" +
                "      border-radius: 5px;" +
                "      font-size: 1rem;" +
                "      box-sizing: border-box;" +
                "    }" +
                "    input:focus {" +
                "      outline: none;" +
                "      border-color: #3498db;" +
                "    }" +
                "    button {" +
                "      width: 100%;" +
                "      padding: 0.75rem;" +
                "      background-color: #3498db;" +
                "      color: white;" +
                "      border: none;" +
                "      border-radius: 5px;" +
                "      font-size: 1rem;" +
                "      cursor: pointer;" +
                "      transition: background-color 0.3s;" +
                "    }" +
                "    button:hover {" +
                "      background-color: #2980b9;" +
                "    }" +
                "    .footer {" +
                "      text-align: center;" +
                "      margin-top: 1.5rem;" +
                "      color: #95a5a6;" +
                "    }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <div class='login-container'>" +
                "    <h1>Вход в систему</h1>" +
                "    <form method='post' action='/login/'>" +
                "      <div class='form-group'>" +
                "        <label for='username'>Имя пользователя</label>" +
                "        <input type='text' id='username' name='username' placeholder='Введите имя пользователя' required>" +
                "      </div>" +
                "      <div class='form-group'>" +
                "        <label for='password'>Пароль</label>" +
                "        <input type='password' id='password' name='password' placeholder='Введите пароль' required>" +
                "      </div>" +
                "      <button type='submit'>Войти</button>" +
                "    </form>" +
                "    <div class='footer'>Cadastral Map System</div>" +
                "  </div>" +
                "</body>" +
                "</html>";
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
    @Path("/failure")
    @Produces("text/html")
    public String getFailurePage() {
        return "<!DOCTYPE html>" +
                "<html lang='ru'>" +
                "<head>" +
                "  <meta charset='UTF-8'>" +
                "  <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "  <title>Ошибка входа</title>" +
                "  <style>" +
                "    body {" +
                "      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;" +
                "      background-color: #f5f7fa;" +
                "      display: flex;" +
                "      justify-content: center;" +
                "      align-items: center;" +
                "      height: 100vh;" +
                "      margin: 0;" +
                "    }" +
                "    .message-container {" +
                "      background: white;" +
                "      padding: 2rem;" +
                "      border-radius: 10px;" +
                "      box-shadow: 0 4px 12px rgba(0,0,0,0.1);" +
                "      width: 100%;" +
                "      max-width: 400px;" +
                "      text-align: center;" +
                "    }" +
                "    h1 {" +
                "      color: #e74c3c;" +
                "      margin-bottom: 1.5rem;" +
                "    }" +
                "    .error-icon {" +
                "      color: #e74c3c;" +
                "      font-size: 3rem;" +
                "      margin-bottom: 1rem;" +
                "    }" +
                "    .btn {" +
                "      display: inline-block;" +
                "      padding: 0.75rem 1.5rem;" +
                "      background-color: #3498db;" +
                "      color: white;" +
                "      text-decoration: none;" +
                "      border-radius: 5px;" +
                "      transition: background-color 0.3s;" +
                "    }" +
                "    .btn:hover {" +
                "      background-color: #2980b9;" +
                "    }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <div class='message-container'>" +
                "    <div class='error-icon'>✗</div>" +
                "    <h1>Ошибка входа</h1>" +
                "    <p>Неверное имя пользователя или пароль</p>" +
                "    <a href='/login' class='btn'>Попробовать снова</a>" +
                "  </div>" +
                "</body>" +
                "</html>";
    }
}
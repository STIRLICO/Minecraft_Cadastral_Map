import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.util.Headers;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;

public class WebServer {
    public static void main(String[] args) {
        UndertowJaxrsServer server = new UndertowJaxrsServer()
                .start(Undertow.builder()
                        .addHttpListener(8081, "0.0.0.0")
                        .setHandler(new HttpHandler() {
                            @Override
                            public void handleRequest(io.undertow.server.HttpServerExchange exchange) throws Exception {
                                exchange.getResponseHeaders().put(
                                        Headers.CONTENT_TYPE,
                                        "text/html; charset=UTF-8"
                                );
                                exchange.getResponseSender().send("Сервер запущен: http://localhost:8081/");
                            }
                        }));

        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("org.jboss.resteasy.character-encoding", "UTF-8");

        server.deploy(RestApplication.class);
        System.out.println("Сервер запущен: http://localhost:8081/");
    }
}
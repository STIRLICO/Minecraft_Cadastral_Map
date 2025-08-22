import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.util.Headers;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public class WebServer {
    public static void main(String[] args) throws TelegramApiException {
        ServerSettings settings;
        try {
            settings = ServerSettings.loadFromJSON();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load server settings", e);
        }

        UndertowJaxrsServer server = new UndertowJaxrsServer();
        boolean useHttps = true;

        try {
            SSLContext sslContext = createSSLContext(settings);
            server.start(Undertow.builder()
                    .addHttpsListener(settings.getPort(), settings.getIp(), sslContext)
                    .setHandler(createRootHandler()));
        } catch (Exception e) {
            System.err.println("HTTPS failed, falling back to HTTP: " + e.getMessage());
            useHttps = false;
            server.start(Undertow.builder()
                    .addHttpListener(settings.getPort(), settings.getIp())
                    .setHandler(createRootHandler()));
        }

        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("org.jboss.resteasy.character-encoding", "UTF-8");

        server.deploy(RestApplication.class);
        String protocol = useHttps ? "https" : "http";
        System.out.println("Сервер запущен: " + protocol + "://" + settings.getIp() + ":" + settings.getPort() + "/");

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new TelegramIntegration());
    }

    private static SSLContext createSSLContext(ServerSettings settings) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (FileInputStream fis = new FileInputStream(settings.getFileName())) {
            keyStore.load(fis, settings.getKey().toCharArray());
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, settings.getKey().toCharArray());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, null);
        return sslContext;
    }

    private static HttpHandler createRootHandler() {
        return exchange -> {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html; charset=UTF-8");
            exchange.getResponseSender().send("Сервер запущен");
        };
    }
}
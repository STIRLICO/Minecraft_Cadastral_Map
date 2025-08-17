import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.util.Headers;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public class WebServer {
    public static void main(String[] args) throws TelegramApiException {

        //ServerSettings dummy = new ServerSettings("0.0.0.0", 8081);
        //ServerSettings.saveToJSON(dummy);
        SSLContext sslContext = null;
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("JKS");

        keyStore.load(new FileInputStream("keystore.jks"), "123".toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, "123".toCharArray());

        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, null);
        }
        catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        }

        ServerSettings settings = new ServerSettings();
        try {
            settings = ServerSettings.loadFromJSON();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        UndertowJaxrsServer server = new UndertowJaxrsServer()
                .start(Undertow.builder()
                        .addHttpsListener(settings.getPort(), settings.getIp(),sslContext)
                        .setHandler(new HttpHandler() {
                            @Override
                            public void handleRequest(io.undertow.server.HttpServerExchange exchange) throws Exception {
                                exchange.getResponseHeaders().put(
                                        Headers.CONTENT_TYPE,
                                        "text/html; charset=UTF-8"
                                );
                                exchange.getResponseSender().send("Сервер запущен: https://localhost:8081/");
                            }
                        }));

        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("org.jboss.resteasy.character-encoding", "UTF-8");

        server.deploy(RestApplication.class);
        System.out.println("Сервер запущен: https://"+settings.getIp()+":"+settings.getPort()+"/");
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new TelegramIntegration());
    }
}
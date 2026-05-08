package org.smilecz.mdharvester;

import static org.assertj.core.api.Assertions.assertThat;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import io.micronaut.context.ApplicationContext;
import org.smilecz.mdharvester.commands.CommandBus;
import org.smilecz.mdharvester.commands.download.DownloadMarkdownPageCommand;
import org.smilecz.mdharvester.commands.download.DownloadedMarkdownPage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

final class CommandBusWiringTest {

    @Test
    void dispatchesDownloadCommandToDownloaderHandler() throws IOException {
        try (
                LocalMarkdownServer server = LocalMarkdownServer.start("/docs/page.md", "# Page");
                ApplicationContext context = ApplicationContext.run()
        ) {
            CommandBus commandBus = context.getBean(CommandBus.class);

            DownloadedMarkdownPage page = commandBus.dispatch(
                    DownloadMarkdownPageCommand.from(server.uri().toString(), null, null)
            );

            assertThat(page.fileName()).isEqualTo("page.md");
            assertThat(page.content()).isEqualTo("# Page");
        }
    }

    private static final class LocalMarkdownServer implements AutoCloseable {

        private final HttpServer server;
        private final String path;

        private LocalMarkdownServer(HttpServer server, String path) {
            this.server = server;
            this.path = path;
        }

        private static LocalMarkdownServer start(String path, String content) throws IOException {
            HttpServer server = HttpServer.create(
                    new InetSocketAddress(InetAddress.getLoopbackAddress(), 0),
                    0
            );
            server.createContext(path, exchange -> respond(exchange, content));
            server.start();
            return new LocalMarkdownServer(server, path);
        }

        private URI uri() {
            return URI.create("http://127.0.0.1:" + server.getAddress().getPort() + path);
        }

        @Override
        public void close() {
            server.stop(0);
        }

        private static void respond(HttpExchange exchange, String content) throws IOException {
            byte[] responseBody = content.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/markdown; charset=utf-8");
            exchange.sendResponseHeaders(200, responseBody.length);
            try (OutputStream response = exchange.getResponseBody()) {
                response.write(responseBody);
            }
        }
    }
}

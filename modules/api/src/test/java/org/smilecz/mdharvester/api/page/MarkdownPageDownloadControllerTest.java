package org.smilecz.mdharvester.api.page;

import static org.assertj.core.api.Assertions.assertThat;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import org.smilecz.mdharvester.commands.Command;
import org.smilecz.mdharvester.commands.CommandBus;
import org.smilecz.mdharvester.commands.NoCommandHandlerException;
import org.smilecz.mdharvester.commands.download.DownloadMarkdownPageCommand;
import org.smilecz.mdharvester.commands.download.DownloadMarkdownPageException;
import org.smilecz.mdharvester.commands.download.DownloadedMarkdownPage;
import org.smilecz.mdharvester.commands.download.InvalidDownloadMarkdownPageCommandException;
import org.junit.jupiter.api.Test;

final class MarkdownPageDownloadControllerTest {

    private final CapturingCommandBus commandBus = new CapturingCommandBus(
            new DownloadedMarkdownPage("api-guide.md", "# Api Guide")
    );
    private final MarkdownPageDownloadController controller = new MarkdownPageDownloadController(
            new MarkdownPageDownloadRequestMapper(),
            commandBus,
            new MarkdownPageDownloadResponseFactory()
    );

    @Test
    void dispatchesDownloadCommandAndReturnsMarkdownFileAttachment() {
        MarkdownPageDownloadRequest request =
                new MarkdownPageDownloadRequest("https://example.com/docs/api-guide.md", "Api Guide", null);

        HttpResponse<String> response = controller.download(request);

        assertThat(response.getStatus().getCode()).isEqualTo(HttpStatus.OK.getCode());
        assertThat(response.getBody()).hasValue("# Api Guide");
        assertThat(response.getContentType()).contains(MediaType.of(MarkdownMediaTypes.TEXT_MARKDOWN));
        assertThat(response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION))
                .isEqualTo("attachment; filename=\"api-guide.md\"");
        assertThat(response.getHeaders().get(HttpHeaders.CACHE_CONTROL)).isEqualTo("no-store");
        assertThat(commandBus.dispatchedCommand).isInstanceOfSatisfying(
                DownloadMarkdownPageCommand.class,
                command -> assertThat(command.sourceUri().toString()).isEqualTo("https://example.com/docs/api-guide.md")
        );
    }

    @Test
    void mapsInvalidDownloadRequestToBadRequest() {
        InvalidDownloadMarkdownPageCommandException exception =
                new InvalidDownloadMarkdownPageCommandException("Source URI is required.");

        HttpResponse<ApiErrorResponse> response = controller.invalidRequest(exception);

        assertThat(response.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        assertThat(response.getBody()).hasValue(new ApiErrorResponse("Source URI is required."));
        assertThat(response.getContentType()).contains(MediaType.of(MediaType.APPLICATION_JSON));
    }

    @Test
    void mapsDownloaderFailureToBadGateway() {
        DownloadMarkdownPageException exception =
                new DownloadMarkdownPageException("Markdown page download failed.");

        HttpResponse<ApiErrorResponse> response = controller.downloadFailed(exception);

        assertThat(response.getStatus().getCode()).isEqualTo(HttpStatus.BAD_GATEWAY.getCode());
        assertThat(response.getBody()).hasValue(new ApiErrorResponse("Markdown page download failed."));
        assertThat(response.getContentType()).contains(MediaType.of(MediaType.APPLICATION_JSON));
    }

    @Test
    void mapsMissingCommandHandlerToServerError() {
        NoCommandHandlerException exception = new NoCommandHandlerException(DownloadMarkdownPageCommand.class);

        HttpResponse<ApiErrorResponse> response = controller.missingHandler(exception);

        assertThat(response.getStatus().getCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.getCode());
        assertThat(response.getContentType()).contains(MediaType.of(MediaType.APPLICATION_JSON));
    }

    private static final class CapturingCommandBus implements CommandBus {

        private final DownloadedMarkdownPage result;
        private Command<?> dispatchedCommand;

        private CapturingCommandBus(DownloadedMarkdownPage result) {
            this.result = result;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <R> R dispatch(Command<R> command) {
            dispatchedCommand = command;
            return (R) result;
        }
    }
}

package org.smilecz.mdharvester.api.page;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Post;
import org.smilecz.mdharvester.commands.CommandBus;
import org.smilecz.mdharvester.commands.NoCommandHandlerException;
import org.smilecz.mdharvester.commands.download.DownloadMarkdownPageCommand;
import org.smilecz.mdharvester.commands.download.DownloadMarkdownPageException;
import org.smilecz.mdharvester.commands.download.DownloadedMarkdownPage;
import org.smilecz.mdharvester.commands.download.InvalidDownloadMarkdownPageCommandException;

@Controller("/api/pages")
public final class MarkdownPageDownloadController {

    private final MarkdownPageDownloadRequestMapper requestMapper;
    private final CommandBus commandBus;
    private final MarkdownPageDownloadResponseFactory responseFactory;

    public MarkdownPageDownloadController(
            MarkdownPageDownloadRequestMapper requestMapper,
            CommandBus commandBus,
            MarkdownPageDownloadResponseFactory responseFactory
    ) {
        this.requestMapper = requestMapper;
        this.commandBus = commandBus;
        this.responseFactory = responseFactory;
    }

    @Post(uri = "/download", consumes = MediaType.APPLICATION_JSON, produces = MarkdownMediaTypes.TEXT_MARKDOWN)
    public HttpResponse<String> download(@Body MarkdownPageDownloadRequest request) {
        DownloadMarkdownPageCommand command = requestMapper.toCommand(request);
        DownloadedMarkdownPage file = commandBus.dispatch(command);
        return responseFactory.attachment(file);
    }

    @Error(exception = InvalidDownloadMarkdownPageCommandException.class)
    public HttpResponse<ApiErrorResponse> invalidRequest(InvalidDownloadMarkdownPageCommandException exception) {
        return HttpResponse.badRequest(new ApiErrorResponse(exception.getMessage()))
                .contentType(MediaType.of(MediaType.APPLICATION_JSON));
    }

    @Error(exception = DownloadMarkdownPageException.class)
    public HttpResponse<ApiErrorResponse> downloadFailed(DownloadMarkdownPageException exception) {
        return HttpResponse.status(HttpStatus.BAD_GATEWAY)
                .body(new ApiErrorResponse(exception.getMessage()))
                .contentType(MediaType.of(MediaType.APPLICATION_JSON));
    }

    @Error(exception = NoCommandHandlerException.class)
    public HttpResponse<ApiErrorResponse> missingHandler(NoCommandHandlerException exception) {
        return HttpResponse.serverError(new ApiErrorResponse(exception.getMessage()))
                .contentType(MediaType.of(MediaType.APPLICATION_JSON));
    }
}

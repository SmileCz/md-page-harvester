package org.smilecz.mdharvester.api.page;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Post;
import org.smilecz.mdharvester.cqrs.download.DownloadMarkdownPageException;
import org.smilecz.mdharvester.cqrs.download.DownloadMarkdownPageQuery;
import org.smilecz.mdharvester.cqrs.download.DownloadedMarkdownPage;
import org.smilecz.mdharvester.cqrs.download.InvalidDownloadMarkdownPageQueryException;
import org.smilecz.mdharvester.cqrs.query.NoQueryHandlerException;
import org.smilecz.mdharvester.cqrs.query.QueryBus;

@Controller("/api/pages")
public final class MarkdownPageDownloadController {

    private final MarkdownPageDownloadRequestMapper requestMapper;
    private final QueryBus queryBus;
    private final MarkdownPageDownloadResponseFactory responseFactory;

    public MarkdownPageDownloadController(
            MarkdownPageDownloadRequestMapper requestMapper,
            QueryBus queryBus,
            MarkdownPageDownloadResponseFactory responseFactory
    ) {
        this.requestMapper = requestMapper;
        this.queryBus = queryBus;
        this.responseFactory = responseFactory;
    }

    @Post(uri = "/download", consumes = MediaType.APPLICATION_JSON, produces = MarkdownMediaTypes.TEXT_MARKDOWN)
    public HttpResponse<String> download(@Body MarkdownPageDownloadRequest request) {
        DownloadMarkdownPageQuery query = requestMapper.toQuery(request);
        DownloadedMarkdownPage file = queryBus.ask(query);
        return responseFactory.attachment(file);
    }

    @Error(exception = InvalidDownloadMarkdownPageQueryException.class)
    public HttpResponse<ApiErrorResponse> invalidRequest(InvalidDownloadMarkdownPageQueryException exception) {
        return HttpResponse.badRequest(new ApiErrorResponse(exception.getMessage()))
                .contentType(MediaType.of(MediaType.APPLICATION_JSON));
    }

    @Error(exception = DownloadMarkdownPageException.class)
    public HttpResponse<ApiErrorResponse> downloadFailed(DownloadMarkdownPageException exception) {
        return HttpResponse.status(HttpStatus.BAD_GATEWAY)
                .body(new ApiErrorResponse(exception.getMessage()))
                .contentType(MediaType.of(MediaType.APPLICATION_JSON));
    }

    @Error(exception = NoQueryHandlerException.class)
    public HttpResponse<ApiErrorResponse> missingHandler(NoQueryHandlerException exception) {
        return HttpResponse.serverError(new ApiErrorResponse(exception.getMessage()))
                .contentType(MediaType.of(MediaType.APPLICATION_JSON));
    }
}

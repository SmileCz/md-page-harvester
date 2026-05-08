package org.smilecz.mdharvester;

import static org.assertj.core.api.Assertions.assertThat;

import io.micronaut.context.ApplicationContext;
import org.junit.jupiter.api.Test;
import org.smilecz.mdharvester.cqrs.download.DownloadMarkdownPageQuery;
import org.smilecz.mdharvester.cqrs.download.DownloadedMarkdownPage;
import org.smilecz.mdharvester.cqrs.query.QueryBus;

final class DownloadMarkdownPageIntegrationTest {

    private static final String CHINESE_COSMOGRAPHY_URL =
            "https://publish.obsidian.md/spaceaudits/99_Old/Cosmography/China/Chinese-Cosmography-CZ";

    @Test
    void downloadsObsidianPublishPageAsMarkdownFile() {
        try (ApplicationContext context = ApplicationContext.run()) {
            QueryBus queryBus = context.getBean(QueryBus.class);

            DownloadedMarkdownPage page = queryBus.ask(
                    DownloadMarkdownPageQuery.from(CHINESE_COSMOGRAPHY_URL, null, null)
            );

            assertThat(page.fileName()).isEqualTo("chinese-cosmography-cz.md");
            assertThat(page.content()).startsWith("# **Od 2. stolet");
            assertThat(page.content()).contains("Zheng He");
            assertThat(page.content()).doesNotContain("<!doctype html>");
        }
    }
}

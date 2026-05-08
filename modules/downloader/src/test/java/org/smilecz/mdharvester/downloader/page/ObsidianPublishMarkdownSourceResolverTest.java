package org.smilecz.mdharvester.downloader.page;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import org.junit.jupiter.api.Test;

final class ObsidianPublishMarkdownSourceResolverTest {

    private final ObsidianPublishMarkdownSourceResolver resolver = new ObsidianPublishMarkdownSourceResolver();

    @Test
    void resolvesPreloadedMarkdownSourceFromObsidianPublishShell() {
        URI originalUri = URI.create(
                "https://publish.obsidian.md/spaceaudits/99_Old/Cosmography/China/Chinese-Cosmography-CZ"
        );
        String responseBody = """
                <script type="text/javascript">
                window.preloadPage=f("https://publish-01.obsidian.md/access/site/page.md");
                </script>
                """;

        assertThat(resolver.resolve(originalUri, responseBody))
                .contains(URI.create("https://publish-01.obsidian.md/access/site/page.md"));
    }

    @Test
    void ignoresNonObsidianPublishResponses() {
        URI originalUri = URI.create("https://example.com/page");

        assertThat(resolver.resolve(originalUri, "window.preloadPage=f(\"https://example.com/page.md\");"))
                .isEmpty();
    }
}

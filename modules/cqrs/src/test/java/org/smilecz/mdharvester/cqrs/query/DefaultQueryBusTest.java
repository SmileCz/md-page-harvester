package org.smilecz.mdharvester.cqrs.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

final class DefaultQueryBusTest {

    @Test
    void dispatchesQueryToRegisteredHandlerAndReturnsData() {
        DefaultQueryBus queryBus = new DefaultQueryBus(List.of(new TestQueryHandler("handled")));

        String result = queryBus.ask(new TestQuery("payload"));

        assertThat(result).isEqualTo("handled: payload");
    }

    @Test
    void rejectsMissingHandler() {
        DefaultQueryBus queryBus = new DefaultQueryBus(List.of());

        assertThatThrownBy(() -> queryBus.ask(new TestQuery("payload")))
                .isInstanceOf(NoQueryHandlerException.class)
                .hasMessage("No query handler registered for "
                        + TestQuery.class.getName()
                        + ".");
    }

    @Test
    void rejectsDuplicateHandlers() {
        assertThatThrownBy(() -> new DefaultQueryBus(List.of(
                new TestQueryHandler("first"),
                new TestQueryHandler("second")
        )))
                .isInstanceOf(DuplicateQueryHandlerException.class)
                .hasMessage("Duplicate query handler for "
                        + TestQuery.class.getName()
                        + ".");
    }

    private record TestQuery(String value) implements Query<String> {
    }

    private record TestQueryHandler(String prefix) implements QueryHandler<TestQuery, String> {

        @Override
        public Class<TestQuery> queryType() {
            return TestQuery.class;
        }

        @Override
        public String handle(TestQuery query) {
            return prefix + ": " + query.value();
        }
    }
}

package dev.nathanlively.embabeltests;

import com.embabel.agent.test.unit.FakeOperationContext;
import com.embabel.agent.test.unit.LlmInvocation;
import com.embabel.common.ai.prompt.PromptContributor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static dev.nathanlively.embabeltests.ClassificationAgent.RequestFragment.RequestType.QUERY;
import static org.assertj.core.api.Assertions.assertThat;

class ClassificationAgentTest {

    @Test
    void givenUserInput_whenClassifying_thenBuildsLlmInteractionCorrectly() {
        // Given
        String userInput = "What is the capital of Portugal?";
        ClassificationAgent.ClassifiedIntents dummyResponse = new ClassificationAgent.ClassifiedIntents(
                Set.of(new ClassificationAgent.RequestFragment("any", QUERY)));
        FakeOperationContext context = FakeOperationContext.create();
        context.expectResponse(dummyResponse);
        ClassificationAgent agent = new ClassificationAgent();

        // When
        agent.classify(userInput, context.ai());

        // Then
        List<LlmInvocation> invocations = context.getLlmInvocations();
        assertThat(invocations)
                .as("Should call LLM exactly once")
                .hasSize(1);

        LlmInvocation invocation = invocations.getFirst();

        List<PromptContributor> examples = invocation.getInteraction().getPromptContributors();
        assertThat(examples)
                .as("Should provide four classification examples (query, command, other, multiple requests)")
                .hasSize(4);

        assertThat(invocation.getInteraction().getToolGroups())
                .as("Should not require any tools")
                .isEmpty();

        assertThat(invocation.getInteraction().getLlm().getTemperature())
                .as("Should use low temperature for consistent classification")
                .isEqualTo(0.1);

        String expectedPrompt = """
                Analyze the user input. Split it into individual requests. Determine if each request \
                is a command, a query, or other (not related to mixer control). Each fragment must be \
                self-contained with all necessary context. Avoid ambiguity like pronouns like "their" or "them".

                ## User input
                
                %s""".formatted(userInput);

        assertThat(invocation.getPrompt())
                .as("Should generate correct classification prompt")
                .isEqualTo(expectedPrompt);

        assertThat(invocation.getInteraction().getId())
                .as("Should use consistent interaction ID for tracing")
                .isEqualTo("classify-intent");
    }
}

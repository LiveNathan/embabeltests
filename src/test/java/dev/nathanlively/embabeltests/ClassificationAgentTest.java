package dev.nathanlively.embabeltests;

import com.embabel.agent.test.unit.FakeOperationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static dev.nathanlively.embabeltests.RequestFragment.RequestType.QUERY;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ClassificationAgent")
class ClassificationAgentTest {

    private static final String USER_INPUT = "What is the capital of Portugal?";
    private static final int EXPECTED_EXAMPLE_COUNT = 3;
    private static final double EXPECTED_TEMPERATURE = 0.1;
    private static final String EXPECTED_INTERACTION_ID = "classify-intent";

    private ClassificationAgent agent;
    private FakeOperationContext context;

    @BeforeEach
    void setUp() {
        agent = new ClassificationAgent();
        context = FakeOperationContext.create();

        var dummyResponse = new ClassifiedIntents(
                Set.of(new RequestFragment("any", QUERY)));
        context.expectResponse(dummyResponse);
    }

    @Test
    @DisplayName("should call LLM exactly once")
    void shouldCallLlmOnce() {
        agent.classify(USER_INPUT, context.ai());

        var invocations = context.getLlmInvocations();
        assertThat(invocations)
                .as("LLM should be called exactly once")
                .hasSize(1);
    }

    @Test
    @DisplayName("should provide three classification examples")
    void shouldProvideClassificationExamples() {
        agent.classify(USER_INPUT, context.ai());

        var invocation = context.getLlmInvocations().getFirst();
        var examples = invocation.getInteraction().getPromptContributors();

        assertThat(examples)
                .as("Should provide query, command, and other examples")
                .hasSize(EXPECTED_EXAMPLE_COUNT);
    }

    @Test
    @DisplayName("should not use any tools")
    void shouldNotUseToos() {
        agent.classify(USER_INPUT, context.ai());

        var invocation = context.getLlmInvocations().getFirst();
        var toolGroups = invocation.getInteraction().getToolGroups();

        assertThat(toolGroups)
                .as("Classification should not require tools")
                .isEmpty();
    }

    @Test
    @DisplayName("should use low temperature for consistent classification")
    void shouldUseLowTemperature() {
        agent.classify(USER_INPUT, context.ai());

        var invocation = context.getLlmInvocations().getFirst();
        var temperature = invocation.getInteraction().getLlm().getTemperature();

        assertThat(temperature)
                .as("Should use low temperature for deterministic results")
                .isEqualTo(EXPECTED_TEMPERATURE);
    }

    @Test
    @DisplayName("should generate correct classification prompt")
    void shouldGenerateCorrectPrompt() {
        agent.classify(USER_INPUT, context.ai());

        var invocation = context.getLlmInvocations().getFirst();
        var prompt = invocation.getPrompt();

        var expectedPrompt = """
                Analyze the user input. Split it into individual requests. Determine if each request \
                is a command, a query, or other (not related to mixer control). Each fragment must be \
                self-contained with all necessary context. Avoid ambiguity like pronouns like "their" or "them".
                
                ## User input
                
                %s""".formatted(USER_INPUT);

        assertThat(prompt)
                .as("Should generate properly formatted classification prompt")
                .isEqualTo(expectedPrompt);
    }

    @Test
    @DisplayName("should use correct interaction ID")
    void shouldUseCorrectInteractionId() {
        agent.classify(USER_INPUT, context.ai());

        var invocation = context.getLlmInvocations().getFirst();
        var interactionId = invocation.getInteraction().getId();

        assertThat(interactionId)
                .as("Should identify the interaction for tracing")
                .isEqualTo(EXPECTED_INTERACTION_ID);
    }
}

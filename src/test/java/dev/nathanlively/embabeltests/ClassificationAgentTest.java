package dev.nathanlively.embabeltests;

import com.embabel.agent.test.unit.FakeOperationContext;
import com.embabel.agent.test.unit.LlmInvocation;
import com.embabel.common.ai.prompt.PromptContributor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ClassificationAgentTest {

    @Test
    void classifyIntent_assemblesCorrectPromptAndExamples() {
        ClassifiedIntents dummyResponse = new ClassifiedIntents(Set.of(new RequestFragment("any", RequestFragment.RequestType.QUERY)));
        final FakeOperationContext context = FakeOperationContext.create();
        context.expectResponse(dummyResponse);
        ClassificationAgent agent = new ClassificationAgent();
        String userInput = "What is the capital of Portugal?";

        agent.with(userInput, context.ai());

        final List<LlmInvocation> llmInvocations = context.getLlmInvocations();
        final LlmInvocation llmInvocation = llmInvocations.getFirst();
        assertThat(llmInvocations.size())
                .as("The LLM should only be called once.")
                .isEqualTo(1);

        List<PromptContributor> promptContributors = llmInvocation.getInteraction().getPromptContributors();
        assertThat(promptContributors)
                .as("The agent should provide classification examples to the LLM")
                .isNotEmpty();

        assertThat(llmInvocation.getInteraction().getToolGroups())
                .as("No tools are necessary.")
                .isEmpty();
        assertThat(llmInvocation.getInteraction().getLlm().getTemperature())
                .isEqualTo(0.1);

        String prompt = llmInvocation.getPrompt();
        assertThat(prompt).isEqualTo("""
                Analyze the user input. Split it into individual requests. Determine if each request is a command, a query, or other..
                
                ## User input
                
                %s""".formatted(userInput));

        assertThat(llmInvocation.getInteraction().getId())
                .isEqualTo("classify-intent");
    }
}

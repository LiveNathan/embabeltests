package dev.nathanlively.embabeltests;

import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.Ai;
import com.embabel.common.ai.model.LlmOptions;

import java.util.Set;

@Agent(description = "Classifies user requests into intents")
public class ClassificationAgent {
    public ClassifiedIntents with(String userInput, Ai ai) {
        return ai.withLlm(LlmOptions.withAutoLlm().withTemperature(0.1))
                .withId("classify-intent")
                .creating(ClassifiedIntents.class)
                .withExample("Request example: Where's the lead vocal? Send it to the vocal buss.", createIndependentRequestsExample())
                .withExample("Request example: Rename channels 1-4 to RF 1-4 then change the color to red and then disable routes to main.", createCompoundRequestExample())
                .withExample("Request example: Hello there!", createOtherRequestExample())
                .fromPrompt(createClassifyIntentPrompt(userInput));
    }

    private ClassifiedIntents createIndependentRequestsExample() {
        return new ClassifiedIntents(Set.of(
                new RequestFragment("Where is the lead vocal channel?", RequestFragment.RequestType.QUERY),
                new RequestFragment("Send the lead vocal channel to the vocal buss.", RequestFragment.RequestType.COMMAND)));
    }

    private ClassifiedIntents createCompoundRequestExample() {
        return new ClassifiedIntents(Set.of(
                new RequestFragment("Rename channels 1 through 4 to RF 1 through 4.", RequestFragment.RequestType.COMMAND),
                new RequestFragment("Change the color of channels 1 through 4 to red.", RequestFragment.RequestType.COMMAND),
                new RequestFragment("Disable all routes to main for channels 1 through 4.", RequestFragment.RequestType.COMMAND)));
    }

    private ClassifiedIntents createOtherRequestExample() {
        return new ClassifiedIntents(Set.of(
                new RequestFragment("Hello there!", RequestFragment.RequestType.OTHER)));
    }

    String createClassifyIntentPrompt(String userInput) {
        return """
                Analyze the user input. Split it into individual requests. Determine if each request is a command, a query, or other (not related to mixer control). Each fragment must be self-contained with all necessary context. Avoid ambiguity like pronouns like "their" or "them".
                
                ## User input
                
                %s""".formatted(userInput.trim());
    }
}

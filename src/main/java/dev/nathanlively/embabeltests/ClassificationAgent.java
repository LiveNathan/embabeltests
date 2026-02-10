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
                .withExample("Query example: Where's the lead vocal?", createQueryExample())
                .withExample("Command example: Rename channels 1-4 to RF 1-4", createCommandExample())
                .withExample("Other example: Hello there robot!", createOtherExample())
                .fromPrompt(createClassifyIntentPrompt(userInput));
    }

    private ClassifiedIntents createQueryExample() {
        return new ClassifiedIntents(Set.of(
                new RequestFragment("Which channel is named lead vocal?", RequestFragment.RequestType.QUERY)));
    }

    private ClassifiedIntents createCommandExample() {
        return new ClassifiedIntents(Set.of(
                new RequestFragment("Rename channels 1 through 4 to RF 1 through RF 4.", RequestFragment.RequestType.COMMAND)));
    }

    private ClassifiedIntents createOtherExample() {
        return new ClassifiedIntents(Set.of(
                new RequestFragment("Hello there robot!", RequestFragment.RequestType.OTHER)));
    }

    String createClassifyIntentPrompt(String userInput) {
        return """
                Analyze the user input. Split it into individual requests. Determine if each request is a command, a query, or other (not related to mixer control). Each fragment must be self-contained with all necessary context. Avoid ambiguity like pronouns like "their" or "them".
                
                ## User input
                
                %s""".formatted(userInput.trim());
    }
}

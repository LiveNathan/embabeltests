package dev.nathanlively.embabeltests;

import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.Ai;
import com.embabel.common.ai.model.LlmOptions;

import java.util.Set;

import static dev.nathanlively.embabeltests.RequestFragment.RequestType.*;

@Agent(description = "Classifies user requests into intents")
public class ClassificationAgent {

    private static final double LOW_TEMPERATURE = 0.1;
    private static final String INTERACTION_ID = "classify-intent";

    private static ClassifiedIntents queryExample() {
        return new ClassifiedIntents(Set.of(
                new RequestFragment("Which channel is named lead vocal?", QUERY)));
    }

    private static ClassifiedIntents commandExample() {
        return new ClassifiedIntents(Set.of(
                new RequestFragment("Rename channels 1 through 4 to RF 1 through RF 4.", COMMAND)));
    }

    private static ClassifiedIntents otherExample() {
        return new ClassifiedIntents(Set.of(
                new RequestFragment("Hello there robot!", OTHER)));
    }

    private static String buildPrompt(String userInput) {
        return """
                Analyze the user input. Split it into individual requests. Determine if each request \
                is a command, a query, or other (not related to mixer control). Each fragment must be \
                self-contained with all necessary context. Avoid ambiguity like pronouns like "their" or "them".
                
                ## User input
                
                %s""".formatted(userInput.trim());
    }

    public ClassifiedIntents classify(String userInput, Ai ai) {
        return ai.withLlm(LlmOptions.withAutoLlm().withTemperature(LOW_TEMPERATURE))
                .withId(INTERACTION_ID)
                .creating(ClassifiedIntents.class)
                .withExample("Query example: Where's the lead vocal?", queryExample())
                .withExample("Command example: Rename channels 1-4 to RF 1-4", commandExample())
                .withExample("Other example: Hello there robot!", otherExample())
                .fromPrompt(buildPrompt(userInput));
    }
}

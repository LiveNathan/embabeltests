package dev.nathanlively.embabeltests;

import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.Ai;
import com.embabel.common.ai.model.LlmOptions;

import java.util.Set;

import static dev.nathanlively.embabeltests.ClassificationAgent.RequestFragment.RequestType.*;

@Agent(description = "Classifies user requests into intents")
public class ClassificationAgent {

    private static final double TEMPERATURE = 0.1;
    private static final String INTERACTION_ID = "classify-intent";

    private ClassifiedIntents queryExample() {
        return new ClassifiedIntents(Set.of(
                new RequestFragment("Which channel is named lead vocal?", QUERY)));
    }

    private ClassifiedIntents commandExample() {
        return new ClassifiedIntents(Set.of(
                new RequestFragment("Rename channels 1 through 4 to RF 1 through RF 4.", COMMAND)));
    }

    private ClassifiedIntents otherExample() {
        return new ClassifiedIntents(Set.of(
                new RequestFragment("Hello there robot!", OTHER)));
    }

    private ClassifiedIntents independentRequestsExample() {
        return new ClassifiedIntents(Set.of(
                new RequestFragment("Which channel is named lead vocal?", QUERY),
                new RequestFragment("Send the channel named lead vocal to the mix buss named vocal.", COMMAND)));
    }

    private String buildPrompt(String userInput) {
        return """
                Analyze the user input. Split it into individual requests. Determine if each request \
                is a command, a query, or other (not related to mixer control). Each fragment must be \
                self-contained with all necessary context. Avoid ambiguity like pronouns like "their" or "them".
                
                ## User input
                
                %s""".formatted(userInput.trim());
    }

    ClassifiedIntents classify(String userInput, Ai ai) {
        return ai.withLlm(LlmOptions.withAutoLlm().withTemperature(TEMPERATURE))
                .withId(INTERACTION_ID)
                .creating(ClassifiedIntents.class)
                .withExample("Query example: Where's the lead vocal?", queryExample())
                .withExample("Command example: Rename channels 1-4 to RF 1-4", commandExample())
                .withExample("Other example: Hello there robot!", otherExample())
                .withExample("Multiple requests example: Where's the lead vocal? Send it to the vocal buss.", independentRequestsExample())
                .fromPrompt(buildPrompt(userInput));
    }

    record ClassifiedIntents(Set<RequestFragment> fragments) {
    }

    record RequestFragment(String description, RequestType type) {
        public enum RequestType {COMMAND, QUERY, OTHER}
    }
}

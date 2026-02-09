package dev.nathanlively.embabeltests;

import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.Ai;

import java.util.Collections;

@Agent(description = "Classifies user requests into intents")
public class ClassificationAgent {
    public ClassifiedIntents with(String userInput, Ai ai) {
        return new ClassifiedIntents(Collections.emptySet());
    }
}

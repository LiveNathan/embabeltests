package dev.nathanlively.embabeltests;

import com.embabel.agent.api.common.Ai;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ClassificationAgentIntegrationTest {
    @Autowired
    private Ai ai;

    private ClassificationAgent agent;

    @BeforeEach
    void setUp() {
        agent = new ClassificationAgent();
    }

    @Test
    void givenOtherUserInput_whenClassifying_thenReturnsSetOfSizeOneWithTypeOther() {
        String userInput = "Bom dia amigo!";

        ClassificationAgent.ClassifiedIntents actual = agent.classify(userInput, ai);

        IO.println("Classified intents:" + actual);
        assertThat(actual).isNotNull();
        assertThat(actual.fragments())
                .as("Should identify a single intent of type OTHER")
                .hasSize(1)
                .extracting(ClassificationAgent.RequestFragment::type)
                .contains(ClassificationAgent.RequestFragment.RequestType.OTHER);
    }

    @Test
    void givenUserInputQuery_whenClassify_thenReturnsSetOfSizeOneWithTypeQuery() {
        String userInput = "What channel is the snare on?";

        ClassificationAgent.ClassifiedIntents actual = agent.classify(userInput, ai);

        IO.println("Classified intents:" + actual);
        assertThat(actual).isNotNull();
        assertThat(actual.fragments())
                .as("Should identify a single intent of type QUERY")
                .hasSize(1)
                .extracting(ClassificationAgent.RequestFragment::type)
                .contains(ClassificationAgent.RequestFragment.RequestType.QUERY);
    }

}
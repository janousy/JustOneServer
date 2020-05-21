package ch.uzh.ifi.seal.soprafs20.helper;

import ch.uzh.ifi.seal.soprafs20.constant.ActionTypeStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Round;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Hint;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Term;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

class HintValidatorTest {

    private Term testTerm;
    private Round testRound;
    private Hint testHint1;
    private Hint testHint2;
    private Hint testHint3;
    private List<Hint> hintList;

    private HintValidator hintValidator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        hintValidator = new HintValidator();

        testHint1 = new Hint();
        testHint2 = new Hint();
        testHint3 = new Hint();
        testHint1.setToken("testToken1");
        testHint2.setToken("testToken2");
        testHint3.setToken("testToken3");
        hintList = new ArrayList<>();

        testTerm = new Term();
        testTerm.setContent("testTerm");
        testTerm.setToken("testToken");

        testRound = new Round();
        testRound.setTerm(testTerm);
    }

    @Test
    void givenListOfHints_validateSimilarity() {
        ArrayList<Integer> similarities1 = new ArrayList<>();
        similarities1.add(2); //hint1 is similar to hint3
        similarities1.add(2); //hint1 is similar to hint3

        testHint1.setMarked(ActionTypeStatus.VALID);
        testHint2.setMarked(ActionTypeStatus.VALID);
        testHint3.setMarked(ActionTypeStatus.VALID);
        testHint1.setSimilarity(similarities1);
        hintList.add(testHint1);
        hintList.add(testHint2);
        hintList.add(testHint3);

        List<Hint> updatedHints = hintValidator.validateSimilarityAndMarking(hintList);

        assertEquals(ActionTypeStatus.INVALID, updatedHints.get(0).getStatus());
    }

    @Test
    void givenHintAndTerm_validateEquality_success() {
        Hint hint = new Hint();
        hint.setContent("test");
        Term term = new Term();
        term.setContent("test");

        HintValidator hintValidatorSpy = Mockito.spy(HintValidator.class);
        Mockito.when(hintValidatorSpy.processWithNLP(hint.getContent(), "wordnet")).thenReturn(hint.getContent());
        Mockito.when(hintValidatorSpy.processWithNLP(term.getContent(), "wordnet")).thenReturn(term.getContent());

        hintValidatorSpy.validateWithExernalResources(hint, term, "wordnet");

        Mockito.verify(hintValidatorSpy, Mockito.times(2)).processWithNLP(Mockito.any(), Mockito.anyString());

        assertEquals(ActionTypeStatus.INVALID, hint.getStatus());
    }

    @Test
    void givenHints_validateDuplicates() {
        testHint1.setContent("test");
        testHint2.setContent("test1");
        testHint3.setContent("test");

        List<Hint> currentHints = new ArrayList<>();
        currentHints.add(testHint1);
        currentHints.add(testHint2);

        hintValidator.checkDuplicates(testHint3, currentHints);

        assertEquals(ActionTypeStatus.INVALID, testHint1.getStatus());
        assertEquals(ActionTypeStatus.INVALID, testHint3.getStatus());
    }

    @Test
    void givenHint_compareHintToTerm() {
        testHint1.setContent("SomeMoreStringtestSomeMoreString");
        testTerm.setContent("test");

        hintValidator.checkTermIncluded(testHint1, testTerm);

        assertEquals(ActionTypeStatus.INVALID, testHint1.getStatus());
    }

    @Test
    void givenHint_checkSingleWord() {
        testHint1.setContent("SomeMore MoreString");

        hintValidator.checkSingleWord(testHint1);

        assertEquals(ActionTypeStatus.INVALID, testHint1.getStatus());
    }
}

package ch.uzh.ifi.seal.soprafs20.helper;

import ch.uzh.ifi.seal.soprafs20.constant.ActionTypeStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Round;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Hint;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Term;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.stylesheets.LinkStyle;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class HintValidatorTest {

    private HintValidator hintValidator = new HintValidator();
    private Term testTerm;
    private Round testRound;
    private Hint inputHint1;
    private Hint testHint1;
    private Hint testHint2;
    private Hint testHint3;
    private List<Hint> hintList;


    @BeforeEach
    public void setup() {
        inputHint1 = new Hint();
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
    public void givenListOfHints_validateMarking() {
        testHint1.setMarked(ActionTypeStatus.INVALID);
        testHint2.setMarked(ActionTypeStatus.VALID);
        hintList.add(testHint1);
        hintList.add(testHint2);

        List<Hint> validatedHints = hintValidator.validateSimilarityAndMarking(hintList);

        assertEquals(hintList.size(), validatedHints.size());
        assertEquals(testHint1.getMarked(), validatedHints.get(0).getStatus());
        assertEquals(testHint2.getMarked(), validatedHints.get(1).getStatus());
    }

    @Test
    public void givenListOfHints_validateSimilarity() {
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

        List<Hint> validatedHints = hintValidator.validateSimilarityAndMarking(hintList);

        assertEquals(hintList.size(), validatedHints.size());
        assertEquals(ActionTypeStatus.INVALID, validatedHints.get(0).getStatus());
        assertEquals(ActionTypeStatus.VALID, validatedHints.get(1).getStatus());
        assertEquals(ActionTypeStatus.INVALID, validatedHints.get(2).getStatus());
    }

    @Test
    public void givenHint_returnWordLemma_success() {

    }
}

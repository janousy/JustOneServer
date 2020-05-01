package ch.uzh.ifi.seal.soprafs20.helper;

import ch.uzh.ifi.seal.soprafs20.constant.ActionTypeStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Round;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Hint;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Term;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.w3c.dom.stylesheets.LinkStyle;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.ArrayList;
import java.util.List;

public class HintValidatorTest {

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

        List<Hint> updatedHints = hintValidator.validateSimilarityAndMarking(hintList);

        assertEquals(ActionTypeStatus.INVALID, updatedHints.get(0).getStatus());
    }

    @Test
    public void givenHintAndTerm_validateEquality_success() {
        Hint hint = new Hint();
        hint.setContent("test");
        Term term = new Term();
        term.setContent("test");

        HintValidator hintValidatorSpy = Mockito.spy(HintValidator.class);
        Mockito.when(hintValidatorSpy.getWordLemma(hint.getContent())).thenReturn(hint.getContent());
        Mockito.when(hintValidatorSpy.getWordLemma(term.getContent())).thenReturn(term.getContent());

        hintValidatorSpy.validateWithExernalResources(hint, term);

        Mockito.verify(hintValidatorSpy, Mockito.times(2)).getWordLemma(Mockito.any());

        assertEquals(ActionTypeStatus.INVALID, hint.getStatus());
    }
}

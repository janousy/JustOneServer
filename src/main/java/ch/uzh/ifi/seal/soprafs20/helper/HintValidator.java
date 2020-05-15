package ch.uzh.ifi.seal.soprafs20.helper;

import ch.uzh.ifi.seal.soprafs20.constant.ActionTypeStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Round;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Hint;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Term;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.repository.RoundRepository;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@Transactional
public class HintValidator {
    private final Logger log = LoggerFactory.getLogger(HintValidator.class);

    private static final String USER_AGENT = "Mozilla/5.0";


    @Autowired
    public HintValidator() {
    }

    /* External APIs:
     *   http://text-processing.com/, stemming and lemmatization, throttled at 1000 calls per day per IP, returns 503 if exceeded
     * */
    public Hint validateWithExernalResources(Hint inputHint, Term currentTerm, String inputStemmer) {
        String termContentProcessed = processWithNLP(currentTerm.getContent(), inputStemmer);
        String hintContentProcessed = processWithNLP(inputHint.getContent(), inputStemmer);
        return compareHintToTerm(inputHint, hintContentProcessed, termContentProcessed);
    }

    public String processWithNLP(String word, String stemmer) {
        try {
            final String POST_URL = "http://text-processing.com/api/stem/"; //api for word stem lemmatization processing

            //either wordnet stemmer or empty stemmer possible
            if (!stemmer.equals("wordnet")) {
                stemmer = "";
            }
            //stemmer must be wordnet in order to get lemmatization
            // UPPERCASE NOT WORKING BY DEFINITION OF API SPECS!!!!
            String requestParameter = word.toLowerCase();
            final String POST_PARAMS = String.format("text=%s&stemmer=%s", requestParameter, stemmer);

            log.info(String.format("connecting to %s", POST_URL));
            log.info(String.format("post form data: %s", POST_PARAMS));

            URL obj = new URL(POST_URL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);

            // For POST only - START
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write(POST_PARAMS.getBytes());
            os.flush();
            os.close();
            // For POST only - END

            int responseCode = con.getResponseCode();
            log.info("POST Response Code :: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {//success

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                //decompose response message
                JSONObject JSON_response = new JSONObject(response.toString()); //gradlew dependency required! should be in
                String wordLemma = JSON_response.getString("text");

                log.info(((stemmer.equals("wordnet")) ? "lemmatized " : "stemmed ") + "word: " + wordLemma + " || current word: " + word);
                return wordLemma;

            }
            else {
                log.info("POST request invalid");
                return word;
            }
        }
        catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "api not available"); //TODO what
        }
    }

    //only when all hints are reported, hints can get validated by marking and similarity
    public List<Hint> validateSimilarityAndMarking(List<Hint> currentHints) {
        int nrOfClueGivers = currentHints.size();

        //such that a hint becomes invalid by marking, half of the players must have it marked as invalid (invalidCounter)
        //same goes with similarity, half of the players must mark another hint as similar
        for (Hint hint : currentHints) {
            if (hint.getInvalidCounter() >= nrOfClueGivers / 2) {
                currentHints.get(currentHints.indexOf(hint)).setStatus(ActionTypeStatus.INVALID);
            }
            else {
                hint.setStatus(ActionTypeStatus.VALID);
            }

            List<Integer> similarites = hint.getSimilarity();
            for (int similarity : similarites) {
                if (Collections.frequency(similarites, similarity) >= nrOfClueGivers / 2) {
                    currentHints.get(similarity).setStatus(ActionTypeStatus.INVALID);
                    currentHints.get(currentHints.indexOf(hint)).setStatus(ActionTypeStatus.INVALID);
                }
            }
        }
        for (Hint hint : currentHints) {
            if (hint.getStatus() != (ActionTypeStatus.INVALID)) {
                hint.setStatus(ActionTypeStatus.VALID);
            }
        }
        return currentHints;
    }

    public Hint compareHintToTerm(Hint hint, String hintContent, String termContent) {

        if (termContent.toLowerCase().equals(hintContent.toLowerCase())) {
            hint.setStatus(ActionTypeStatus.INVALID);
        }
        return hint;
    }

    public Hint checkDuplicates(Hint inputHint, List<Hint> currentHints) {
        if (currentHints.stream().anyMatch(hint
                -> hint.getContent().equals(inputHint.getContent()))) {
            inputHint.setStatus(ActionTypeStatus.INVALID);
        }
        return inputHint;
    }

    public Hint checkSingleWord(Hint inputHint) {
        if (inputHint.getContent().contains(" ")) {
            inputHint.setStatus(ActionTypeStatus.INVALID);
        }
        return inputHint;
    }

    public Hint checkTermIncluded(Hint inputHint, Term currentTerm) {
        if (inputHint.getContent().toLowerCase().contains(currentTerm.getContent().toLowerCase())) {
            inputHint.setStatus(ActionTypeStatus.INVALID);
        }
        return inputHint;
    }
}

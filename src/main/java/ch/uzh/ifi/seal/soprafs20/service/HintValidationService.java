package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.ActionTypeStatus;
import ch.uzh.ifi.seal.soprafs20.constant.CONSTANTS;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.entity.Round;
import ch.uzh.ifi.seal.soprafs20.entity.actions.ActionType;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Guess;
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

import javax.swing.*;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@Transactional
public class HintValidationService {
    private final Logger log = LoggerFactory.getLogger(HintValidationService.class);

    private final RoundRepository roundRepository;
    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;

    private static final String USER_AGENT = "Mozilla/5.0";


    @Autowired
    public HintValidationService(@Qualifier("roundRepository") RoundRepository roundRepository,
                                 @Qualifier("gameRepository") GameRepository gameRepository,
                                 @Qualifier("playerRepository") PlayerRepository playerRepository) {
        this.roundRepository = roundRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    public Hint validateWithExernalResources(Hint inputHint, Round currentRound) {
        Term term = currentRound.getTerm();
        Hint hint = validateWordStem(inputHint, term);
        return hint;
    }

    public Hint validateWordStem(Hint inputHint, Term term) {
        try {
            final String POST_URL = "http://text-processing.com/api/stem/"; //api for word stem processing
            final String POST_PARAMS = String.format("text=%s", inputHint.getContent());

            log.info(String.format("connecting to %s", POST_URL));
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
                JSONObject JSON_response = new JSONObject(response.toString());
                String stemmedWord = JSON_response.getString("text");
                log.info("stemmed word: " + JSON_response.getString("text") + " || current term: " + term.getContent());
                //checked if the stemmed hint content is equal to the current term
                if (term.getContent().toLowerCase().equals(stemmedWord.toLowerCase())) {
                    inputHint.setStatus(ActionTypeStatus.INVALID);
                    log.info("setting hint to invalid");
                }
                else {
                    inputHint.setStatus(ActionTypeStatus.UNKNOWN);
                }
            }
            else {
                log.info("POST request invalid");
            }
        }
        catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "api not available"); //TODO what
        }
        return inputHint;
    }

    public List<Hint> validateSimilarityAndMarking(List<Hint> currentHints) {
        int nrOfClueGivers = currentHints.size();
        List<Hint> copyHints = currentHints;

        for (Hint hint : currentHints) {
            if (hint.getMarked().equals(ActionTypeStatus.INVALID)) {
                copyHints.get(currentHints.indexOf(hint)).setStatus(ActionTypeStatus.INVALID);
            }

            List<Integer> similarites = hint.getSimilarity();
            for (int similarity : similarites) {
                if (Collections.frequency(similarites, similarity) > nrOfClueGivers / 2) {
                    copyHints.get(similarity).setStatus(ActionTypeStatus.INVALID);
                    copyHints.get(currentHints.indexOf(hint)).setStatus(ActionTypeStatus.INVALID);
                }
            }
        }
        for (Hint hint : copyHints) {
            if (!hint.getStatus().equals(ActionTypeStatus.INVALID)) {
                hint.setStatus(ActionTypeStatus.VALID);
            }
        }
        return copyHints;
    }

    private static final String GET_URL = "https://localhost:9090/SpringMVCExample";

    public void sendGET() throws IOException {
        log.debug("sent GET");
        URL obj = new URL(GET_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
        }
        else {
            System.out.println("GET request not worked");
        }
    }
}

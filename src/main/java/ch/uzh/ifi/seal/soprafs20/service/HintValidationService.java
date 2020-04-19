package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.ActionTypeStatus;
import ch.uzh.ifi.seal.soprafs20.constant.CONSTANTS;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Player;
import ch.uzh.ifi.seal.soprafs20.entity.Round;
import ch.uzh.ifi.seal.soprafs20.entity.actions.ActionType;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Guess;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Hint;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.repository.RoundRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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


    @Autowired
    public HintValidationService(@Qualifier("roundRepository") RoundRepository roundRepository,
                                 @Qualifier("gameRepository") GameRepository gameRepository,
                                 @Qualifier("playerRepository") PlayerRepository playerRepository) {
        this.roundRepository = roundRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
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

    private static final String USER_AGENT = "Mozilla/5.0";

    private static final String GET_URL = "https://localhost:9090/SpringMVCExample";

    public void sendGetRequest() throws IOException {
        sendGET();
        sendPOST();
    }

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

    public void sendPOST() throws IOException {
        final String POST_URL = "http://text-processing.com/api/stem/";
        String requestTerm = "processes";
        final String POST_PARAMS = "text=" + requestTerm + "\"";

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
            log.info(response.toString());
        }
        else {
            log.info("POST request not worked");
        }
    }
}

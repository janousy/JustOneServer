package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.entity.Round;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Guess;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.repository.RoundRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class ValidationService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final RoundRepository roundRepository;
    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;


    @Autowired
    public ValidationService(@Qualifier("roundRepository") RoundRepository roundRepository,
                             @Qualifier("gameRepository") GameRepository gameRepository,
                             @Qualifier("playerRepository") PlayerRepository playerRepository) {
        this.roundRepository = roundRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }


    public boolean guessValidation(Guess guess, Round currentRound) {

        String termContent = currentRound.getTerm().getContent();
        String guessContent = guess.getContent();

        termContent = termContent.toLowerCase();
        guessContent = guessContent.toLowerCase();

        return termContent.equals(guessContent);
    }


}

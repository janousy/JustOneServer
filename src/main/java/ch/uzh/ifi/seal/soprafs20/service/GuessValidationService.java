package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.ActionTypeStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Round;
import ch.uzh.ifi.seal.soprafs20.entity.actions.Guess;
import ch.uzh.ifi.seal.soprafs20.repository.RoundRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class GuessValidationService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public GuessValidationService() {
    }

    public Guess guessValidation(Guess guess, Round currentRound) {

        String termContent = currentRound.getTerm().getContent();
        String guessContent = guess.getContent();

        termContent = termContent.toLowerCase();
        guessContent = guessContent.toLowerCase();

        if (termContent.equals(guessContent)) {
            guess.setStatus(ActionTypeStatus.VALID);
        }
        else {
            guess.setStatus(ActionTypeStatus.INVALID);
        }

        return guess;
    }
}

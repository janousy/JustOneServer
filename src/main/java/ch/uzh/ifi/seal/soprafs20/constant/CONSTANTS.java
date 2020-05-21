package ch.uzh.ifi.seal.soprafs20.constant;

public final class CONSTANTS {

    //private constructor ensures no object is instantiated
    private CONSTANTS() {
        throw new IllegalStateException("CONSTANTS class");
    }

    public static final int MAX_WORDS_PER_CARD = 5;

    //constants for the game
    public static final int NUMBER_OF_ROUNDS = 13;
    public static final int MINIMAL_NR_OF_PLAYERS = 3;

    //constants for the scoring system
    public static final int MAX_POINTS_PER_ROUND_GUESS = 30;
    public static final int MAX_POINTS_PER_ROUND_HINT = 20;
    public static final int MAX_POINTS_PER_ROUND_GUESS_DEDUCTION = 15;
    public static final int MAX_POINTS_PER_ROUND_HINT_DEDUCTION = 10;
    public static final double POINT_DEDUCTION_PER_SECOND = 0.5;
    public static final int MIN_POINT_DEDUCTION_WRONG_GUESS = 5;
    public static final int MIN_POINT_DEDUCTION_WRONG_HINT = 5;
    public static final int ADDITIONAL_POINTS_REWARD_HINT = 10;
    public static final int ADDITIONAL_POINTS_REWARD_GUESS = 15;
    public static final int MIN_NR_OF_CORRECT_HINTS_FOR_REWARD = 7;
    public static final int MIN_NR_OF_CORRECT_GUESS_FOR_REWARD = 2;


}

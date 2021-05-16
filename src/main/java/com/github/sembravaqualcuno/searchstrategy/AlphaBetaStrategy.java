package com.github.sembravaqualcuno.searchstrategy;

import com.github.sembravaqualcuno.domain.Action;
import com.github.sembravaqualcuno.domain.Game;
import com.github.sembravaqualcuno.domain.State;
import com.github.sembravaqualcuno.exceptions.*;

import java.io.IOException;
import java.util.List;

/**
 * This class implements the {@see com.github.sembravaqualcuno.searchstrategy.SearchStrategy} interface with the alpha-beta algorithm
 *
 * @author Luca Bongiovanni
 */
public class AlphaBetaStrategy implements SearchStrategy {
    private static final int POSITIVE_INFINITY = 100000000;
    private static final int NEGATIVE_INFINITY = -POSITIVE_INFINITY;

    private Game game;
    private int maxDepth;
    private long maxTime;

    /**
     * @param game     the actual game
     * @param maxDepth the max depth of the search tree
     * @param maxTime
     */
    public AlphaBetaStrategy(Game game, int maxDepth, long maxTime) {
        this.game = game;
        this.maxDepth = maxDepth;
        this.maxTime = maxTime;
    }

    @Override
    public Action choseMove(State state) throws IOException, ActionException {
        Action result = null;
        int resultValue = NEGATIVE_INFINITY;

        for (Action action : state.getActions()) {
            try {
                int value = alphabeta(game.checkMove(state.clone(), action), maxDepth - 1, NEGATIVE_INFINITY, POSITIVE_INFINITY, false);
                if (value > resultValue) {
                    result = action;
                    resultValue = value;
                }
            } catch (PawnException | DiagonalException | ClimbingException | CitadelException | StopException | OccupiedException | BoardException | ThroneException | ClimbingCitadelException e) {
                //e.printStackTrace();
            }
        }
        return result;
    }

    private int alphabeta(State state, int depth, int alfa, int beta, boolean maximizingPlayer) throws IOException, ActionException {
        if (depth == 0 || state.isTerminal())
            return state.heuristicsFunction(depth);
        int value;
        if (maximizingPlayer) {
            value = NEGATIVE_INFINITY;
            for (Action action : state.getActions()) {
                try {
                    value = Math.max(value, alphabeta(game.checkMove(state.clone(), action), depth - 1, alfa, beta, false));
                    alfa = Math.max(alfa, value);
                    if (alfa >= beta)
                        break;
                } catch (PawnException | DiagonalException | ClimbingException | CitadelException | StopException | OccupiedException | BoardException | ThroneException | ClimbingCitadelException e) {
                    //e.printStackTrace();
                }
            }
        } else {
            value = POSITIVE_INFINITY;
            List<Action> actions = state.getActions();
            for (Action action : actions) {
                try {
                    value = Math.min(value, alphabeta(game.checkMove(state.clone(), action), depth - 1, alfa, beta, true));
                    beta = Math.min(beta, value);
                    if (beta <= alfa)
                        break;
                } catch (PawnException | DiagonalException | ClimbingException | CitadelException | StopException | OccupiedException | BoardException | ThroneException | ClimbingCitadelException e) {
                    //e.printStackTrace();
                }
            }
        }
        return value;
    }
}

package com.github.sembravaqualcuno.searchstrategy;

import com.github.sembravaqualcuno.domain.Action;
import com.github.sembravaqualcuno.domain.Game;
import com.github.sembravaqualcuno.domain.State;
import com.github.sembravaqualcuno.exceptions.*;

import java.io.IOException;

/**
 * This class implements the {@see com.github.sembravaqualcuno.searchstrategy.SearchStrategy} interface with the alpha-beta algorithm
 *
 * @author Luca Bongiovanni
 */
public class AlphaBetaStrategy implements SearchStrategy{
    private Game game;
    private int maxDepth;
    private long maxTime;

    /**
     *
     * @param game
     *          the actual game
     * @param maxDepth
     *          the max depth of the search tree
     * @param maxTime
     *
     */
    public AlphaBetaStrategy(Game game, int maxDepth, long maxTime) {
        this.game = game;
        this.maxDepth = maxDepth;
        this.maxTime = maxTime;
    }

    @Override
    public Action choseMove(State state) throws IOException, PawnException, DiagonalException, ClimbingException, ActionException, CitadelException, StopException, OccupiedException, BoardException, ClimbingCitadelException, ThroneException {
        Action result = null;
        int resultValue = -10000; //NEGATIVE INFINITY

        for (Action action : state.getActions()) {
            int value = alphabeta(game.checkMove(state, action), maxDepth - 1, -10000, 10000, true);
            if(value > resultValue) {
                result = action;
                resultValue = value;
            }
        }

        return result;
    }

    //TODO Si può evitare di invertire l'euristica grazie al booleano maximizingPlayer
    private int alphabeta(State state, int depth, int alfa, int beta, boolean maximizingPlayer) throws IOException, PawnException, DiagonalException, ClimbingException, ActionException, CitadelException, StopException, OccupiedException, BoardException, ClimbingCitadelException, ThroneException {
        if(depth == 0 || state.isTerminal())
            return state.heuristicsFunction();
        int value;
        if(maximizingPlayer) {
            value = -10000; //NEGATIVE INFINITY
            for(Action action : state.getActions()) {
                value = Math.max(value, alphabeta(game.checkMove(state, action), depth - 1, alfa, beta, false));
                alfa = Math.max(alfa, value);
                if(alfa >= beta)
                    break;
            }
        }
        else {
            value = 10000; //POSITIVE INFINITY
            for(Action action : state.getActions()) {
                value = Math.min(value, alphabeta(game.checkMove(state, action), depth - 1, alfa, beta, true));
                beta = Math.min(beta, value);
                if(beta <= alfa)
                    break;
            }
        }
        return value;
    }
}

package com.github.sembravaqualcuno.searchstrategy;

import com.github.sembravaqualcuno.domain.Action;
import com.github.sembravaqualcuno.domain.Game;
import com.github.sembravaqualcuno.domain.State;
import com.github.sembravaqualcuno.exceptions.*;
import javafx.scene.paint.Stop;

import java.io.IOException;
import java.util.List;

/**
 * This class implements the {@see com.github.sembravaqualcuno.searchstrategy.SearchStrategy} interface with the alpha-beta algorithm
 *
 * @author Luca Bongiovanni
 */
public class AlphaBetaStrategy implements SearchStrategy {
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
        int resultValue = -10000; //NEGATIVE INFINITY

        for (Action action : state.getActions()) {
            try {
                int value = alphabeta(game.checkMove(state.clone(), action), maxDepth - 1, -10000, 10000, false);
                System.out.println("Value: " + value);
                if (value > resultValue) {
                    result = action;
                    resultValue = value;
                }
            } catch (PawnException | DiagonalException | ClimbingException | CitadelException | StopException | OccupiedException | BoardException | ThroneException | ClimbingCitadelException e) {
                //e.printStackTrace();
            }
        }
        System.out.println("Value finale: " + resultValue);
        return result;
    }

    private int alphabeta(State state, int depth, int alfa, int beta, boolean maximizingPlayer) throws IOException, ActionException {
        if (depth == 0 || state.isTerminal())
            return state.heuristicsFunction();
        int value;
        if (maximizingPlayer) {
            value = -10000; //NEGATIVE INFINITY
            for (Action action : state.getActions()) {
                try {
                    value = Math.max(value, alphabeta(game.checkMove(state.clone(), action), depth - 1, alfa, beta, false));
                    System.out.println(value);
                    alfa = Math.max(alfa, value);
                    if (alfa >= beta)
                        break;
                } catch (PawnException | DiagonalException | ClimbingException | CitadelException | StopException | OccupiedException | BoardException | ThroneException | ClimbingCitadelException e) {
                    //e.printStackTrace();
                }
            }
        } else {
            value = 10000; //POSITIVE INFINITY
            List<Action> actions = state.getActions();
            for (Action action : actions) {
                try {
                    value = Math.min(value, alphabeta(game.checkMove(state.clone(), action), depth - 1, alfa, beta, true));
                    System.out.println(value);
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

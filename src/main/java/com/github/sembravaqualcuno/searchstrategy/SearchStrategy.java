package com.github.sembravaqualcuno.searchstrategy;

import com.github.sembravaqualcuno.domain.Action;
import com.github.sembravaqualcuno.domain.State;
import com.github.sembravaqualcuno.exceptions.*;

import java.io.IOException;

/**
 * Interface that represent a search strategy
 *
 * @author Luca Bongiovanni
 */
public interface SearchStrategy {

    /**
     * This method chose the best action from a initial state
     *
     * @param state the initial state
     * @return the chosen action
     * @throws IOException
     * @throws PawnException
     * @throws DiagonalException
     * @throws ClimbingException
     * @throws ActionException
     * @throws CitadelException
     * @throws StopException
     * @throws OccupiedException
     * @throws BoardException
     * @throws ClimbingCitadelException
     * @throws ThroneException
     */
    public Action choseMove(State state) throws IOException, PawnException, DiagonalException, ClimbingException, ActionException, CitadelException, StopException, OccupiedException, BoardException, ClimbingCitadelException, ThroneException;
}

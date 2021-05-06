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
     * @throws ActionException
     */
    public Action choseMove(State state) throws IOException, ActionException;
}

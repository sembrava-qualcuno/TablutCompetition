package com.github.sembravaqualcuno.exceptions;

import com.github.sembravaqualcuno.domain.Action;

/**
 * This exception represent an action that is moving to an occupied box
 *
 * @author A.Piretti
 */
public class OccupiedException extends Exception {
    private static final long serialVersionUID = 1L;

    public OccupiedException(Action a) {
        super("Move into a box occupied form another pawn: " + a);
    }
}

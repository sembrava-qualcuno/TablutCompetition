package com.github.sembravaqualcuno.exceptions;

import com.github.sembravaqualcuno.domain.Action;

/**
 * This exception represent an action that is climbing over a pawn
 *
 * @author A.Piretti
 */
public class ClimbingException extends Exception {
    private static final long serialVersionUID = 1L;

    public ClimbingException(Action a) {
        super("A pawn is trying to climb over another pawn: " + a);
    }
}

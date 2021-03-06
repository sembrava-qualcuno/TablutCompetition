package com.github.sembravaqualcuno.exceptions;

import com.github.sembravaqualcuno.domain.Action;

/**
 * This exception represent an action with the wrong format
 *
 * @author A.Piretti
 */
public class ActionException extends Exception {
    private static final long serialVersionUID = 1L;

    public ActionException(Action a) {
        super("The format of the action is not correct: " + a);
    }
}

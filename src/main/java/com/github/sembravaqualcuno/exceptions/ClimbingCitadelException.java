package com.github.sembravaqualcuno.exceptions;

import com.github.sembravaqualcuno.domain.Action;

public class ClimbingCitadelException extends Exception {
    private static final long serialVersionUID = 1L;

    public ClimbingCitadelException(Action a) {
        super("A pawn is trying to climb over a citadel: " + a);
    }
}

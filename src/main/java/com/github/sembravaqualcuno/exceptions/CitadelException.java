package com.github.sembravaqualcuno.exceptions;

import com.github.sembravaqualcuno.domain.Action;

public class CitadelException extends Exception {
    private static final long serialVersionUID = 1L;

    public CitadelException(Action a) {
        super("Move into a citadel: " + a);
    }
}

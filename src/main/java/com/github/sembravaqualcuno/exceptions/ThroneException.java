package com.github.sembravaqualcuno.exceptions;

import com.github.sembravaqualcuno.domain.Action;

/**
 * This exception represent an action that is trying to move a pawn into the throne
 * @author A.Piretti
 *
 */
public class ThroneException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public ThroneException(Action a) {
		super("Player " + a.getTurn() + " is trying to go into the castle: " + a);
	}
}

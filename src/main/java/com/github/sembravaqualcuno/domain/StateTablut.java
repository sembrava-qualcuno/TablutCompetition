package com.github.sembravaqualcuno.domain;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * This class represents a state of a match of Tablut (classical or second
 * version)
 * 
 * @author A.Piretti
 * 
 */
public class StateTablut extends State implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final int initialPawnsBlack = 16;
	private static final int initialPawnsWhite = 9;

	public StateTablut() {
		super();
		this.board = new Pawn[9][9];

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				this.board[i][j] = Pawn.EMPTY;
			}
		}

		this.board[4][4] = Pawn.THRONE;

		this.turn = Turn.BLACK;

		this.board[4][4] = Pawn.KING;

		this.board[0][1] = Pawn.ESCAPE;
		this.board[0][2] = Pawn.ESCAPE;
		this.board[0][6] = Pawn.ESCAPE;
		this.board[0][7] = Pawn.ESCAPE;
		this.board[1][0] = Pawn.ESCAPE;
		this.board[2][0] = Pawn.ESCAPE;
		this.board[6][0] = Pawn.ESCAPE;
		this.board[7][0] = Pawn.ESCAPE;
		this.board[8][1] = Pawn.ESCAPE;
		this.board[8][2] = Pawn.ESCAPE;
		this.board[8][6] = Pawn.ESCAPE;
		this.board[8][7] = Pawn.ESCAPE;
		this.board[1][8] = Pawn.ESCAPE;
		this.board[2][8] = Pawn.ESCAPE;
		this.board[6][8] = Pawn.ESCAPE;
		this.board[7][8] = Pawn.ESCAPE;

		this.board[0][3] = Pawn.CAMP;
		this.board[0][4] = Pawn.CAMP;
		this.board[0][5] = Pawn.CAMP;
		this.board[3][0] = Pawn.CAMP;
		this.board[4][0] = Pawn.CAMP;
		this.board[5][0] = Pawn.CAMP;
		this.board[8][3] = Pawn.CAMP;
		this.board[8][4] = Pawn.CAMP;
		this.board[8][5] = Pawn.CAMP;
		this.board[3][8] = Pawn.CAMP;
		this.board[4][8] = Pawn.CAMP;
		this.board[5][8] = Pawn.CAMP;
		this.board[1][4] = Pawn.CAMP;
		this.board[4][1] = Pawn.CAMP;
		this.board[7][4] = Pawn.CAMP;
		this.board[4][7] = Pawn.CAMP;

		this.board[2][4] = Pawn.WHITE;
		this.board[3][4] = Pawn.WHITE;
		this.board[5][4] = Pawn.WHITE;
		this.board[6][4] = Pawn.WHITE;
		this.board[4][2] = Pawn.WHITE;
		this.board[4][3] = Pawn.WHITE;
		this.board[4][5] = Pawn.WHITE;
		this.board[4][6] = Pawn.WHITE;

		this.board[0][3] = Pawn.BLACK;
		this.board[0][4] = Pawn.BLACK;
		this.board[0][5] = Pawn.BLACK;
		this.board[1][4] = Pawn.BLACK;
		this.board[8][3] = Pawn.BLACK;
		this.board[8][4] = Pawn.BLACK;
		this.board[8][5] = Pawn.BLACK;
		this.board[7][4] = Pawn.BLACK;
		this.board[3][0] = Pawn.BLACK;
		this.board[4][0] = Pawn.BLACK;
		this.board[5][0] = Pawn.BLACK;
		this.board[4][1] = Pawn.BLACK;
		this.board[3][8] = Pawn.BLACK;
		this.board[4][8] = Pawn.BLACK;
		this.board[5][8] = Pawn.BLACK;
		this.board[4][7] = Pawn.BLACK;

	}

	public StateTablut clone() {
		StateTablut result = new StateTablut();

		Pawn oldboard[][] = this.getBoard();
		Pawn newboard[][] = result.getBoard();

		for (int i = 0; i < this.board.length; i++) {
			for (int j = 0; j < this.board[i].length; j++) {
				newboard[i][j] = oldboard[i][j];
			}
		}

		result.setBoard(newboard);
		result.setTurn(this.turn);
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		StateTablut other = (StateTablut) obj;
		if (this.board == null) {
			if (other.board != null)
				return false;
		} else {
			if (other.board == null)
				return false;
			if (this.board.length != other.board.length)
				return false;
			if (this.board[0].length != other.board[0].length)
				return false;
			for (int i = 0; i < other.board.length; i++)
				for (int j = 0; j < other.board[i].length; j++)
					if (!this.board[i][j].equals(other.board[i][j]))
						return false;
		}
		if (this.turn != other.turn)
			return false;
		return true;
	}

	/**
	 * @return True if we've reached a terminal state, false otherwise
	 */
	@Override
	public boolean terminalCheck() {
		return getTurn().equals(Turn.WHITEWIN) ||
				getTurn().equals(Turn.BLACKWIN) ||
				getTurn().equals(Turn.DRAW);
	}

	/**
	 * @return The heuristics value related to this state
	 */
	@Override //TODO implement the actual euristicsFunction
	public int heuristicsFunction() {
		/*
		 * This heuristics function initializes the values for white pawns, then it converts some values
		 * by multiplying them by -1 if incoherent with black pawns behaviour
		 */

		//Positive heuristics initialization
		int kingEscaped = 400;
		int kingCouldEscape = 40;
		int pawnsEaten = 2 * (initialPawnsBlack - this.getNumberOf(Pawn.BLACK));

		//Negative heuristics initialization
		int kingEaten = -400;
		int kingEatablePosition =  -400;
		int eatablePosition =  -20;
		int blackBlockingEscape = 0;
		int pawnsLost = -3 * (initialPawnsWhite - this.getNumberOf(Pawn.WHITE));
		int nearObstacle = -3;

		//Check for terminal states and exit (convert value if needed)
		//Check if the king has reached an escape
		if(this.getTurn().equals(Turn.WHITEWIN))
			return (GameAshtonTablut.player.equals(Turn.WHITE) ? kingEscaped: -kingEscaped);
		//Check if the king has been eaten
		else if (this.getTurn().equals(Turn.BLACKWIN))
			return (GameAshtonTablut.player.equals(Turn.WHITE) ? kingEaten: -kingEaten);

		int totalHeuristicsValue = 0;

		for(int row = 0; row < this.getBoard().length; row++){
			for(int column = 0; column < this.getBoard().length; column++){
				//Find the king
				if (board[row][column].equals(Pawn.KING)){
					//Check if the king is in an escapable position and return th heuristic value accordingly
					int escapableHeuristicsValue = getKingCouldEscapeHeuristic(kingCouldEscape, row, column);
					totalHeuristicsValue += (GameAshtonTablut.player.equals(Turn.WHITE) ?
							escapableHeuristicsValue : -escapableHeuristicsValue);

					//Check if the king is in an eatable position and return the heuristic value accordingly
					int eatableKingHeuristicsValue = getKingEatableHeuristic(kingEatablePosition, row, column);
					totalHeuristicsValue += (GameAshtonTablut.player.equals(Turn.WHITE) ?
							eatableKingHeuristicsValue : -eatableKingHeuristicsValue);
				}
			}
		}

		return totalHeuristicsValue;
	}

	private int getKingEatableHeuristic(int kingEatablePosition, int row, int column) {
		int i;
		int heuristicsValue = 0;

		//Check special king eating cases

		//Check if the king has a black pawn close to the right

		//Check if the king has a black pawn close to the left

		//Check if the king has a black pawn close to the top

		//Check if the king has a black pawn close to the bottom
		return heuristicsValue;
	}

	private int getKingCouldEscapeHeuristic(int kingCouldEscape, int row, int column) {
		int i;
		int heuristicsValue = 0;
		//Check if the escape is on the top and the road towards it is clear
		if(board[0][column].equals(Pawn.ESCAPE)) {
			for (i = row - 1; i >= 0; i--) {
				if (!board[i][column].equals(Pawn.EMPTY))
					break;
			}
			heuristicsValue += (i == 0 ? kingCouldEscape : 0);
		}
		//Check if the escape is on the bottom and the road towards it is clear
		if(board[8][column].equals(Pawn.ESCAPE)) {
			for (i = row + 1; i < board.length; i++) {
				if (!board[i][column].equals(Pawn.EMPTY))
					break;
			}
			heuristicsValue += (i == 8 ? kingCouldEscape : 0);
		}
		//Check if the escape is on the left and the road towards it is clear
		if(board[row][0].equals(Pawn.ESCAPE)) {
			for (i = column - 1; i >= 0; i--) {
				if (!board[row][i].equals(Pawn.EMPTY))
					break;
			}
			heuristicsValue += (i == 0 ? kingCouldEscape : 0);
		}
		//Check if the escape is on the right and the road towards it is clear
		if(board[row][8].equals(Pawn.ESCAPE)) {
			for (i = column + 1; i < board.length; i++) {
				if (!board[row][i].equals(Pawn.EMPTY))
					break;
			}
			heuristicsValue += (i == 8 ? kingCouldEscape : 0);
		}
		return heuristicsValue;
	}

	/**
	 * @return true if the king is in the castle, false otherwise
	 */
	private boolean isKingInCastle() {	return this.getPawn(4,4).equals(Pawn.KING); }

	/**
	 * @return The list of actions available for the current state.
	 */
	@Override
	public List<Action> getActions() throws IOException {
		List<Action> result = new ArrayList<>();

		for(int i = 0; i < board.length; i++) {
			for(int j= 0; j < board.length; j++) {
				if(board[i][j].toString().equals(turn.toString())) {
					result.addAll(getPawnActions(i, j));
				}
			}
		}

		return result;
	}

	private List<Action> getPawnActions(int row, int column) throws IOException {
		List<Action> result = new ArrayList<>();

		//Check at the bottom
		for(int i = row + 1; i < board.length && board[i][column].equalsPawn("EMPTY"); i++) {
			result.add(new Action(Action.getStringFromIndex(row, column), Action.getStringFromIndex(i, column), turn));
		}

		//Check at the top
		for(int i = row - 1; i >= 0 && board[i][column].equalsPawn("EMPTY"); i--) {
			result.add(new Action(Action.getStringFromIndex(row, column), Action.getStringFromIndex(i, column), turn));
		}

		//Check at the right
		for(int j = column + 1; j < board[row].length && board[row][j].equalsPawn("EMPTY"); j++) {
			result.add(new Action(Action.getStringFromIndex(row, column), Action.getStringFromIndex(row, j), turn));
		}

		//Check at the left
		for(int j = column - 1; j >= 0 && board[row][j].equalsPawn("EMPTY"); j--) {
			result.add(new Action(Action.getStringFromIndex(row, column), Action.getStringFromIndex(row, j), turn));
		}

		return result;
	}
}

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
 */
public class StateTablut extends State implements Serializable {
	private  final long serialVersionUID = 1L;
	private  final int initialPawnsBlack = 16;
	private  final int initialPawnsWhite = 9;

	public StateTablut() {
		super();
		this.board = new Pawn[9][9];

		for (int row = 0; row < board.length; row++) {
			for (int column = 0; column < board[row].length; column++) {
				if(isEscape(row, column))
					this.board[row][column] = Pawn.ESCAPE;
				else
					this.board[row][column] = Pawn.EMPTY;
			}
		}

		this.board[4][4] = Pawn.THRONE;

		this.turn = Turn.BLACK;

		this.board[4][4] = Pawn.KING;

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
	public boolean isTerminal() {
		return getTurn().equals(Turn.WHITEWIN) ||
				getTurn().equals(Turn.BLACKWIN) ||
				getTurn().equals(Turn.DRAW);
	}

	/**
	 * This heuristics function initializes the values for white pawns, then it converts some values
	 * by multiplying them by -1 if incoherent with black pawns behaviour
	 * @return The heuristics value related to this state
	 */
	public int heuristicsFunction() {
		//Positive heuristics initialization
		final int kingEscapedValue = 4000;
		final int kingCouldEscapeValue = 40;
		final int pawnsEatenValue = 10 + 20 * (initialPawnsBlack - this.getNumberOf(Pawn.BLACK));

		//Negative heuristics initialization
		final int kingEatenValue = -4000;
		final int kingEatablePositionValue =  -100;
		final int eatablePositionValue =  -20;
		final int pawnsLostValue = -20 + 30 * (initialPawnsWhite - this.getNumberOf(Pawn.WHITE));
		final int nearObstacleValue = -3;
		final int blackBlockingValue = -4;
		final int drawValue = -30;

		//Check for terminal states and exit (convert value if needed)
		//Check if the king has reached an escape
		if(this.getTurn().equals(Turn.WHITEWIN))
			return (GameAshtonTablut.player.equals(Turn.WHITE) ? kingEscapedValue: -kingEscapedValue);
		//Check if the king has been eaten
		else if (this.getTurn().equals(Turn.BLACKWIN))
			return (GameAshtonTablut.player.equals(Turn.WHITE) ? kingEatenValue: -kingEatenValue);
		else if(this.getTurn().equals(Turn.DRAW))
			return drawValue;

		int swappableHeuristicsValue = pawnsEatenValue + pawnsLostValue;
		int fixedHeuristicsValue = 0;

		for(int row = 0; row < this.getBoard().length; row++){
			for(int column = 0; column < this.getBoard().length; column++){
				//Find the king
				if (board[row][column].equals(Pawn.KING)){
					//Check if the king is in an escapable position and return th heuristic value accordingly
					swappableHeuristicsValue += getKingCouldEscapeHeuristic(kingCouldEscapeValue, row, column);

					//Check if the king is in an eatable position and return the heuristic value accordingly
					swappableHeuristicsValue += getKingEatableHeuristic(kingEatablePositionValue, row, column);

					//Check if the king is in the castle's row or column as there's no point for this heuristics in these cases
					if(row != 4 || column != 4){
						swappableHeuristicsValue += getBlackBlockingKingHeuristic(row, column, blackBlockingValue);
					}

				}
				fixedHeuristicsValue += getEatableOrObstacleHeuristic(board[row][column].equals(Pawn.KING), kingEatablePositionValue,
						eatablePositionValue, nearObstacleValue, row, column);

				//If it's the black's turn, calculate black-only strategy and add it to fixedHeuristics
				if(this.getTurn().equals(Turn.BLACK) && board[row][column].equals(Pawn.BLACK)){
					fixedHeuristicsValue += blackStrategy(row, column);
				}
				//If it's the white's turn, calculate white-only strategy and add it to fixedHeuristics
				if(this.getTurn().equals(Turn.WHITE) && (board[row][column].equals(Pawn.KING) ||
						board[row][column].equals(Pawn.WHITE))){
					fixedHeuristicsValue += whiteStrategy(board[row][column].equals(Pawn.KING), row, column);
				}
			}
		}

		return fixedHeuristicsValue + (GameAshtonTablut.player.equals(Turn.WHITE) ?
				swappableHeuristicsValue : -swappableHeuristicsValue);
	}

	//TODO Implement the actual white strategy
	private int whiteStrategy(boolean isKing, int row, int column) {
		int heuristicsValue = 0;

		return heuristicsValue;
	}

	//TODO Implement the actual black strategy
	private int blackStrategy(int row, int column) {
		int heuristicsValue = 0;

		return heuristicsValue;
	}

	/*
	 * This function checks for every occurrence of a pawn to be eaten and if a pawn is near an obstacle
	 * It discriminates between the king's case and any other pawn's case
	 */
	private int getEatableOrObstacleHeuristic(boolean isKing, int kingEatablePositionValue, int eatablePositionValue,
											  int nearObstacleValue, int row, int column) {
		int heuristicsValue = 0;

		//Treat the fortress as an obstacle if the pawn is not the king
		if (!isKing) {
			//Check if the pawn is on the left side of the fortress
			if (row == 4 && column == 3) {
				heuristicsValue += nearObstacleValue +
						isEatableFromLeft(eatablePositionValue, row, column);
			}
			//Check if the pawn is on the right side of the fortress
			else if (row == 4 && column == 5) {
				heuristicsValue += nearObstacleValue +
						isEatableFromRight(eatablePositionValue, row, column);

			}
			//Check if the pawn is on the top side of the fortress
			else if (row == 3 && column == 4) {
				heuristicsValue += nearObstacleValue +
					isEatableFromTop(eatablePositionValue, row, column);
			}
			//Check if the pawn is on the bottom side of the fortress
			else if (row == 5 && column == 4) {
				heuristicsValue += nearObstacleValue +
				isEatableFromBottom(eatablePositionValue, row, column);
			}
		}//Pawn is not the king

		//Check if the pawn is near a camp
		if(!(row == 0 || row == 8 || column == 0 || column == 8)) {
			if (board[row + 1][column].equals(Pawn.CAMP))
				heuristicsValue += nearObstacleValue;
			if (board[row - 1][column].equals(Pawn.CAMP))
				heuristicsValue += nearObstacleValue;
			if (board[row][column + 1].equals(Pawn.CAMP))
				heuristicsValue += nearObstacleValue;
			if (board[row][column - 1].equals(Pawn.CAMP))
				heuristicsValue += nearObstacleValue;

			//Check if there is an enemy/obstacle on the right
			if (this.getTurn().equals(Turn.WHITE) && board[row][column + 1].equals(Pawn.BLACK) ||
					this.getTurn().equals(Turn.BLACK) && board[row][column + 1].equals(Pawn.WHITE) ||
					board[row][column + 1].equals(Pawn.CAMP)) {
				heuristicsValue += isEatableFromLeft(isKing ? kingEatablePositionValue : eatablePositionValue,
						row, column);
			}

			//Check if there is an enemy/obstacle on the left
			if (this.getTurn().equals(Turn.WHITE) && board[row][column - 1].equals(Pawn.BLACK) ||
					this.getTurn().equals(Turn.BLACK) && board[row][column - 1].equals(Pawn.WHITE) ||
					board[row][column - 1].equals(Pawn.CAMP)) {
				heuristicsValue += isEatableFromRight(isKing ? kingEatablePositionValue : eatablePositionValue,
						row, column);
			}

			//Check if there is an enemy/obstacle on the bottom
			if (this.getTurn().equals(Turn.WHITE) && board[row + 1][column].equals(Pawn.BLACK) ||
					this.getTurn().equals(Turn.BLACK) && board[row + 1][column].equals(Pawn.WHITE) ||
					board[row + 1][column].equals(Pawn.CAMP)) {
				heuristicsValue += isEatableFromTop(isKing ? kingEatablePositionValue : eatablePositionValue,
						row, column);
			}

			//Check if there is an enemy/obstacle on the top
			if (this.getTurn().equals(Turn.WHITE) && board[row - 1][column].equals(Pawn.BLACK) ||
					this.getTurn().equals(Turn.BLACK) && board[row - 1][column].equals(Pawn.WHITE) ||
					board[row - 1][column].equals(Pawn.CAMP)) {
				heuristicsValue += isEatableFromBottom(isKing ? kingEatablePositionValue : eatablePositionValue,
						row, column);
			}
		}

		return heuristicsValue;
	}

	/*
	 * This function checks if some enemy is clear to eat from the left of the current pawn
	 * or if some enemy can reach the left of the current pawn from the top or the bottom
	 */
	private int isEatableFromLeft(int eatablePositionValue, int row, int column){
		int heuristicsValue = 0;
		//Check if the first position to my left is empty as it will be pointless to go on checking otherwise
		if (board[row][column - 1].equals(Pawn.EMPTY)) {
			//Check if there is an enemy that has a clear path on the left of the pawn
			if (isClearToEatLeft(row, column - 2))
				heuristicsValue += eatablePositionValue;

			//Check if there are other pawns that could get to my left from the top
			if (isClearToEatTop(row - 1, column - 1))
				heuristicsValue += eatablePositionValue;

			//Check if there are other pawns that could get to my left from the bottom
			if (isClearToEatBottom(row + 1, column - 1))
				heuristicsValue += eatablePositionValue;
		}
		return heuristicsValue;
	}

	/*
	 * This function checks if some enemy is clear to eat from the right of the current pawn
	 * or if some enemy can reach the right of the current pawn from the top or the bottom
	 */
	private int isEatableFromRight(int eatablePositionValue, int row, int column){
		int heuristicsValue = 0;
		//Check if the first position to my right is empty as it will be pointless to go on checking otherwise
		if (board[row][column + 1].equals(Pawn.EMPTY)) {
			//Check if there is an enemy that has a clear path on the right of the pawn
			if (isClearToEatRight(row, column + 2))
				heuristicsValue += eatablePositionValue;

			//Check if there are other pawns that could get to my right from the top
			if (isClearToEatTop(row - 1, column + 1))
				heuristicsValue += eatablePositionValue;

			//Check if there are other pawns that could get to my right from the bottom
			if (isClearToEatBottom(row + 1, column + 1))
				heuristicsValue += eatablePositionValue;
		}
		return heuristicsValue;
	}

	/*
	 * This function checks if some enemy is clear to eat from the top of the current pawn
	 * or if some enemy can reach the top of the current pawn from the left or the right
	 */
	private int isEatableFromTop(int eatablePositionValue, int row, int column){
		int heuristicsValue = 0;
		//Check if the first position to my top side is empty as it will be pointless to go on checking otherwise
		if (board[row - 1][column].equals(Pawn.EMPTY)) {
			//Check if there is an enemy that has a clear path on the top of the pawn
			if(isClearToEatTop(row - 2, column))
				heuristicsValue += eatablePositionValue;

			//Check if there are other pawns that could get to my top side from the left
			if (isClearToEatLeft(row - 1, column - 1))
				heuristicsValue += eatablePositionValue;

			//Check if there are other pawns that could get to my top side from the right
			if(isClearToEatRight(row - 1, column + 1))
				heuristicsValue += eatablePositionValue;
		}
		return heuristicsValue;
	}

	/*
	 * This function checks if some enemy is clear to eat from the bottom of the current pawn
	 * or if some enemy can reach the bottom of the current pawn from the left or the right
	 */
	private int isEatableFromBottom(int eatablePositionValue, int row, int column){
		int heuristicsValue = 0;
		//Check if the first position to my bottom side is empty as it will be pointless to go on checking otherwise
		if (board[row + 1][column].equals(Pawn.EMPTY)) {
			//Check if there is an enemy that has a clear path on the bottom of the pawn
			if(isClearToEatBottom(row + 2, column))
				heuristicsValue += eatablePositionValue;

			//Check if there are other pawns that could get to my bottom side from the left
			if(isClearToEatLeft(row + 1, column - 1))
				heuristicsValue += eatablePositionValue;

			//Check if there are other pawns that could get to my bottom side from the right
			if(isClearToEatRight(row + 1, column + 1))
				heuristicsValue += eatablePositionValue;
		}
		return heuristicsValue;
	}

	/*
	 * This function checks if some enemy is clear to eat from the left of the current pawn
	 * This is possible if he's on the left of the pawn and neither an ally nor an obstacle is present
	 * between the current pawn and the enemy
	 */
	private boolean isClearToEatLeft(int row, int column){
		for (int i = column; i >= 0; i--) {
			if (this.getTurn().equals(Turn.WHITE) && board[row][i].equals(Pawn.WHITE) ||
					this.getTurn().equals(Turn.BLACK) && board[row][i].equals(Pawn.BLACK) ||
					board[row][i].equals(Pawn.THRONE) || board[row][i].equals(Pawn.CAMP))
				return false;
			if (this.getTurn().equals(Turn.WHITE) && board[row][i].equals(Pawn.BLACK) ||
					this.getTurn().equals(Turn.BLACK) && board[row][i].equals(Pawn.WHITE)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * This function checks if some enemy is clear to eat from the right of the current pawn
	 * This is possible if he's on the right of the pawn and neither an ally nor an obstacle is present
	 * between the current pawn and the enemy
	 */
	private boolean isClearToEatRight(int row, int column) {
		for (int i = column; i < board[row].length; i++) {
			if (this.getTurn().equals(Turn.WHITE) && board[row][i].equals(Pawn.WHITE) ||
					this.getTurn().equals(Turn.BLACK) && board[row][i].equals(Pawn.BLACK) ||
					board[row][i].equals(Pawn.THRONE) || board[row][i].equals(Pawn.CAMP))
				return false;
			if (this.getTurn().equals(Turn.WHITE) && board[row][i].equals(Pawn.BLACK) ||
					this.getTurn().equals(Turn.BLACK) && board[row][i].equals(Pawn.WHITE)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * This function checks if some enemy is clear to eat from the top of the current pawn
	 * This is possible if he's on the top of the pawn and neither an ally nor an obstacle is present
	 * between the current pawn and the enemy
	 */
	private boolean isClearToEatTop(int row, int column) {
		for (int i = row; i >= 0; i--) {
			if (this.getTurn().equals(Turn.WHITE) && board[i][column].equals(Pawn.WHITE) ||
					this.getTurn().equals(Turn.BLACK) && board[i][column].equals(Pawn.BLACK) ||
					board[i][column].equals(Pawn.THRONE) || board[i][column].equals(Pawn.CAMP))
				return false;
			if (this.getTurn().equals(Turn.WHITE) && board[i][column].equals(Pawn.BLACK) ||
					this.getTurn().equals(Turn.BLACK) && board[i][column].equals(Pawn.WHITE)) {
				return true;
			}
		}
	return false;
	}

	/*
	 * This function checks if some enemy is clear to eat from the bottom of the current pawn
	 * This is possible if he's on the bottom of the pawn and neither an ally nor an obstacle is present
	 * between the current pawn and the enemy
	 */
	private boolean isClearToEatBottom(int row, int column) {
		for (int i = row; i < board.length; i++) {
			if (this.getTurn().equals(Turn.WHITE) && board[i][column].equals(Pawn.WHITE) ||
					this.getTurn().equals(Turn.BLACK) && board[i][column].equals(Pawn.BLACK) ||
					board[i][column].equals(Pawn.THRONE) || board[i][column].equals(Pawn.CAMP))
				return false;
			if (this.getTurn().equals(Turn.WHITE) && board[i][column].equals(Pawn.BLACK) ||
					this.getTurn().equals(Turn.BLACK) && board[i][column].equals(Pawn.WHITE)) {
				return true;
			}
		}
	return false;
	}

	/*
	 * This function checks for the special positions for the king to be eaten
	 * If the king is in the castle, checks if the king is surrounded
	 * If the king is near the castle, checks if he's surrounded on the remaining sides
	 * If neither of the conditions are met, returns 0
	 */
	private int getKingEatableHeuristic(int kingEatablePositionValue, int row, int column) {
		//Check if the king is in the castle
		if(isKingInCastle()){
			//Check if there are three enemies to the left, top and right of the castle
			if(this.getBoard()[row][column - 1].equals(Pawn.BLACK) &&
					this.getBoard()[row - 1][column].equals(Pawn.BLACK) &&
					this.getBoard()[row][column + 1].equals(Pawn.BLACK))
				return isEatableFromBottom(kingEatablePositionValue, row, column);

			//Check if there are three enemies to the top, right and bottom of the castle
			else if(this.getBoard()[row - 1][column].equals(Pawn.BLACK) &&
					this.getBoard()[row][column + 1].equals(Pawn.BLACK) &&
					this.getBoard()[row + 1][column].equals(Pawn.BLACK))
				return isEatableFromLeft(kingEatablePositionValue, row, column);

			//Check if there are three enemies to the right, bottom and left of the castle
			else if(this.getBoard()[row][column + 1].equals(Pawn.BLACK) &&
					this.getBoard()[row + 1][column].equals(Pawn.BLACK) &&
					this.getBoard()[row][column - 1].equals(Pawn.BLACK))
				return isEatableFromTop(kingEatablePositionValue, row, column);

			//Check if there are three enemies to the bottom, left and top of the castle
			else if(this.getBoard()[row + 1][column].equals(Pawn.BLACK) &&
					this.getBoard()[row][column - 1].equals(Pawn.BLACK) &&
					this.getBoard()[row - 1][column].equals(Pawn.BLACK))
				return isEatableFromRight(kingEatablePositionValue, row, column);
		}//King in castle

		//Check if the king is on the left of the castle
		else if(row == 4 && column == 3){
			//Check if there are two enemies on the left and on the top
			if(this.getBoard()[row][column - 1].equals(Pawn.BLACK) &&
					this.getBoard()[row - 1][column].equals(Pawn.BLACK))
			return isEatableFromBottom(kingEatablePositionValue, row, column);

			//Check if there are two enemies on the bottom and on the left
			else if(this.getBoard()[row + 1][column].equals(Pawn.BLACK) &&
					this.getBoard()[row][column - 1].equals(Pawn.BLACK))
				return isEatableFromTop(kingEatablePositionValue, row, column);

			//Check if there are two enemies on the top and on the bottom
			else if(this.getBoard()[row - 1][column].equals(Pawn.BLACK) &&
					this.getBoard()[row + 1][column].equals(Pawn.BLACK))
				return isEatableFromLeft(kingEatablePositionValue, row, column);
		}//King left of the castle

		//Check if the king is on top of the castle
		else if(row == 3 && column == 4){
			//Check if there are two enemies on the left and on the top
			if(this.getBoard()[row][column - 1].equals(Pawn.BLACK) &&
					this.getBoard()[row - 1][column].equals(Pawn.BLACK))
				return isEatableFromRight(kingEatablePositionValue, row, column);

			//Check if there are two enemies on the top and on the right
			else if(this.getBoard()[row - 1][column].equals(Pawn.BLACK) &&
					this.getBoard()[row][column + 1].equals(Pawn.BLACK))
				return isEatableFromLeft(kingEatablePositionValue, row, column);

			//Check if there are two enemies on the right and on the left
			else if(this.getBoard()[row][column + 1].equals(Pawn.BLACK) &&
					this.getBoard()[row][column - 1].equals(Pawn.BLACK))
				return isEatableFromTop(kingEatablePositionValue, row, column);
		}//King in top of the castle

		//Check if the king is on the right of the castle
		else if(row == 4 && column == 5){
			//Check if there are two enemies on the top and on the right
			if(this.getBoard()[row - 1][column].equals(Pawn.BLACK) &&
					this.getBoard()[row][column + 1].equals(Pawn.BLACK))
				return isEatableFromBottom(kingEatablePositionValue, row, column);

			//Check if there are two enemies on the right and on the bottom
			else if(this.getBoard()[row][column + 1].equals(Pawn.BLACK) &&
					this.getBoard()[row + 1][column].equals(Pawn.BLACK))
				return isEatableFromTop(kingEatablePositionValue, row, column);

			//Check if there are two enemies on the bottom and on the top
			else if(this.getBoard()[row + 1][column].equals(Pawn.BLACK) &&
					this.getBoard()[row - 1][column].equals(Pawn.BLACK))
				return isEatableFromRight(kingEatablePositionValue, row, column);
		}//King right of the castle

		//Check if the king is at the bottom of the castle
		else if(row == 5 && column == 4){
			//Check if there are two enemies on the bottom and on the left
			if(this.getBoard()[row + 1][column].equals(Pawn.BLACK) &&
					this.getBoard()[row][column - 1].equals(Pawn.BLACK))
				return isEatableFromRight(kingEatablePositionValue, row, column);

			//Check if there are two enemies on the right and on the bottom
			else if(this.getBoard()[row][column + 1].equals(Pawn.BLACK) &&
					this.getBoard()[row + 1][column].equals(Pawn.BLACK))
				return isEatableFromLeft(kingEatablePositionValue, row, column);
			//Check if there are two enemies on the left and on the right
			else if(this.getBoard()[row][column - 1].equals(Pawn.BLACK) &&
					this.getBoard()[row][column + 1].equals(Pawn.BLACK))
				return isEatableFromBottom(kingEatablePositionValue, row, column);
		}//King bottom of the castle

		return 0;
	}

	private int getBlackBlockingKingHeuristic(int row, int column, int blackBlockingValue) {
		int nBlacks = 0;
		int kingPosValue = 0;

		//Check if the king is in the upper right square of the board
		if(row < 4 && column > 4){
			//Check how many blacks are near the upper right escapes
			if(this.getBoard()[1][6].equals(Pawn.BLACK))
				nBlacks++;
			if(this.getBoard()[0][6].equals(Pawn.BLACK))
				nBlacks++;
			if(this.getBoard()[0][7].equals(Pawn.BLACK))
				nBlacks++;
			if(this.getBoard()[1][7].equals(Pawn.BLACK))
				nBlacks++;
			if(this.getBoard()[2][7].equals(Pawn.BLACK))
				nBlacks++;
			if(this.getBoard()[1][8].equals(Pawn.BLACK))
				nBlacks++;
			if(this.getBoard()[2][8].equals(Pawn.BLACK))
				nBlacks++;

			//Check if black are blocking the upper right escapes
			if(nBlacks > 0) {
				kingPosValue = -1;

				//Check if the king is closer to the escapes and worsen the heuristic
				if(nBlacks > 1 && (row == 2 || column == 6))
					kingPosValue = -2;
				else if (nBlacks >= 3 && (row == 1 || column == 7))
					kingPosValue = -3;
			}
		}//King is in the upper right square

		//Check if the king is in the lower right square of the board
		else if(row > 4 && column > 4){
			//Check how many blacks are near the lower right escapes
			if(this.getBoard()[6][7].equals(Pawn.BLACK))
				nBlacks++;
			if(this.getBoard()[6][8].equals(Pawn.BLACK))
				nBlacks++;
			if(this.getBoard()[7][6].equals(Pawn.BLACK))
				nBlacks++;
			if(this.getBoard()[7][7].equals(Pawn.BLACK))
				nBlacks++;
			if(this.getBoard()[7][8].equals(Pawn.BLACK))
				nBlacks++;
			if(this.getBoard()[8][6].equals(Pawn.BLACK))
				nBlacks++;
			if(this.getBoard()[8][7].equals(Pawn.BLACK))
				nBlacks++;

			//Check if black are blocking the lower right escapes
			if(nBlacks > 0) {
				kingPosValue = -1;

				//Check if the king is closer to the escapes and worsen the heuristic
				if(nBlacks > 1 && (row == 6 || column == 6))
					kingPosValue = -2;
				else if (nBlacks >= 3 && (row == 7 || column == 7))
					kingPosValue = -3;
			}
		}//King is in the lower right square

		//Check if the king is in the lower left square of the board
		else if(row > 4 && column < 4){
			//Check how many blacks are near the lower left escapes
			if(this.getBoard()[6][0].equals(Pawn.BLACK))
				nBlacks++;
			if(this.getBoard()[6][1].equals(Pawn.BLACK))
				nBlacks++;
			if(this.getBoard()[7][0].equals(Pawn.BLACK))
				nBlacks++;
			if(this.getBoard()[7][1].equals(Pawn.BLACK))
				nBlacks++;
			if(this.getBoard()[7][2].equals(Pawn.BLACK))
				nBlacks++;
			if(this.getBoard()[8][1].equals(Pawn.BLACK))
				nBlacks++;
			if(this.getBoard()[8][2].equals(Pawn.BLACK))
				nBlacks++;

			//Check if black are blocking the lower left escapes
			if(nBlacks > 0) {
				kingPosValue = -1;

				//Check if the king is closer to the escapes and worsen the heuristic
				if(nBlacks > 1 && (row == 6 || column == 2))
					kingPosValue = -2;
				else if (nBlacks >= 3 && (row == 7 || column == 1))
					kingPosValue = -3;
			}
		}//King is in the lower left square

		//Check if the king is in the upper left square of the board
		else{
			//Check how many blacks are near the upper left escapes
			if(this.getBoard()[0][1].equals(Pawn.BLACK))
				nBlacks++;
			if(this.getBoard()[1][1].equals(Pawn.BLACK))
				nBlacks++;
			if(this.getBoard()[0][2].equals(Pawn.BLACK))
				nBlacks++;
			if(this.getBoard()[1][2].equals(Pawn.BLACK))
				nBlacks++;
			if(this.getBoard()[2][1].equals(Pawn.BLACK))
				nBlacks++;
			if(this.getBoard()[1][0].equals(Pawn.BLACK))
				nBlacks++;
			if(this.getBoard()[2][0].equals(Pawn.BLACK))
				nBlacks++;

			//Check if black are blocking the upper left escapes
			if(nBlacks > 0) {
				kingPosValue = -1;

				//Check if the king is closer to the escapes and worsen the heuristic
				if(nBlacks > 1 && (row == 2 || column == 2))
					kingPosValue = -2;
				else if (nBlacks >= 3 && (row == 1 || column == 1))
					kingPosValue = -3;
			}
		}//King is in the upper left

		return nBlacks * blackBlockingValue + kingPosValue;
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

		for(int row = 0; row < board.length; row++) {
			for(int column = 0; column < board[row].length; column++) {
				if(board[row][column].toString().equals(turn.toString())) {
					result.addAll(getPawnActions(row, column));
				}
			}
		}

		return result;
	}

	@Override
	public void initialize() {
		for(int row = 0; row < board.length; row++) {
			for(int column = 0; column < board[row].length; column++) {
				if(board[row][column].equals(Pawn.EMPTY)) {
					if(isEscape(row, column))
						board[row][column] = Pawn.ESCAPE;
					else if(isCamp(row, column))
						board[row][column] = Pawn.CAMP;
				}
			}
		}
	}

	private boolean isEscape(int row, int column) {
		return ((row == 0 || row == 8) && column != 3 && column != 4 && column != 5) ||
				((column == 0 || column == 8) && row != 3 && row != 4 && row != 5);
	}

	private boolean isCamp(int row, int column) {
		return ((row == 0 || row == 8) && (column == 3 || column == 4 || column == 5)) ||
				((column == 0 || column == 8) && (row == 3 || row == 4 || row == 5)) ||
				(column == 4 && (row == 1 || row == 7)) ||
				(row == 4 && (column == 1 || column == 7));
	}

	private List<Action> getPawnActions(int row, int column) throws IOException {
		List<Action> result = new ArrayList<>();

		//Check at the bottom
		for(int i = row + 1; i < board.length && (!board[i][column].equals(Pawn.WHITE) && !board[i][column].equals(Pawn.BLACK) && !board[i][column].equals(Pawn.KING)); i++) {
			result.add(new Action(Action.getStringFromIndex(row, column), Action.getStringFromIndex(i, column), turn));
		}

		//Check at the top
		for(int i = row - 1; i >= 0 && (!board[i][column].equals(Pawn.WHITE) && !board[i][column].equals(Pawn.BLACK) && !board[i][column].equals(Pawn.KING)); i--) {
			result.add(new Action(Action.getStringFromIndex(row, column), Action.getStringFromIndex(i, column), turn));
		}

		//Check at the right
		for(int j = column + 1; j < board[row].length && (!board[row][j].equals(Pawn.WHITE) && !board[row][j].equals(Pawn.BLACK) && !board[row][j].equals(Pawn.KING)); j++) {
			result.add(new Action(Action.getStringFromIndex(row, column), Action.getStringFromIndex(row, j), turn));
		}

		//Check at the left
		for(int j = column - 1; j >= 0 && (!board[row][j].equals(Pawn.WHITE) && !board[row][j].equals(Pawn.BLACK) && !board[row][j].equals(Pawn.KING)); j--) {
			result.add(new Action(Action.getStringFromIndex(row, column), Action.getStringFromIndex(row, j), turn));
		}

		return result;
	}
}

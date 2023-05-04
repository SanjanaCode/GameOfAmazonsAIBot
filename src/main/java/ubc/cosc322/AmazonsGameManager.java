package ubc.cosc322;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;
import ubc.cosc322.Graph.MovesGenerator;
import ubc.cosc322.Algorithm.MinimaxSearch;
import ubc.cosc322.Graph.Graph;
import java.util.*;
import java.util.logging.Logger;


public class AmazonsGameManager{

	private static final int THRESHOLD = 200000;	//Threshold for the number of nodes to be expanded by the minimax algorithm.
	private static final int DIM = 10;
	private static final int[][] BOARD_STATE_BEGINNING = {

			{0, 0, 0, 2, 0, 0, 2, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{2, 0, 0, 0, 0, 0, 0, 0, 0, 2},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 1, 0, 0, 1, 0, 0, 0},
	};

	private Square ourPlayer;
	private Square opponentPlayer;
	private final Logger logger;
	private Graph currentBoardState;
	private Map<MovesGenerator.Move, Graph> legalMovesMap;

	// This method returns the beginning state of the board as a 2D integer array.
	public static int[][] getBoardSetup(){
		return BOARD_STATE_BEGINNING;
	}

	// This is a constructor of GameStateManager class which initializes the logger,
	// creates the currentBoardState object from the initial state, and initializes legalMovesMap.
	public AmazonsGameManager(){
		logger = Logger.getLogger(AmazonsGameManager.class.toString());
		int[][] initialState = getBoardSetup();
		currentBoardState = new Graph(initialState);
		legalMovesMap = new HashMap<>();
	}

	/**
	Sets the players for the game.
	Determines the current player and their opponent based on the provided player.
	@param player the player to set
	*/
	public void setPlayers(Square player){
		if(player.isPlayer()){
			ourPlayer = player;
			if(player.isWhite())
				opponentPlayer = Square.BLACK;
			else
				opponentPlayer = Square.WHITE;
		}
	}

	/**
	This function takes an index of a square on the game board and returns its corresponding coordinates as a List of two integers.
	@param index an integer representing the index of a square on the game board.
	@return a List of two integers representing the coordinates of the square on the game board.
	*/
	public static List<Integer> getCoordinates(int index){
		ArrayList<Integer> result = new ArrayList<>(2);
		result.add((int) Math.ceil(DIM - ((float) index/DIM)));
		result.add((index % DIM) + 1);
		return result;
	}

	/**
	This function takes in a map containing the details of a move made by a player, extracts the arrow position from the map
	and returns the corresponding index of the arrow in the game board.
	@param msgDetails A map containing the details of the move made by a player.
	@return The index of the arrow in the game board.
	*/
	public static int getArrowIndexFromCoord (Map<String, Object> msgDetails) {
		ArrayList<Integer> arrowPosition = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.ARROW_POS);
		int row = DIM-arrowPosition.get(0);
		int col = arrowPosition.get(1)-1;
		int index = row*DIM + col;
		return index;
	}
	
	/**
	Converts the queen's initial position from its (row, column) format to its corresponding index in the game board.
	@param msgDetails a Map object containing the message details of the opponent's move
	@return the index of the queen's initial position
	*/
	public static int getQueenInitialIndexFromCoord (Map<String, Object> msgDetails) {
		ArrayList<Integer> initialPosition = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR);
		int row = DIM-initialPosition.get(0);
		int col = initialPosition.get(1)-1;
		int index = row*DIM + col;
		return index;
	}

	/**
	Get the new index of the queen based on the coordinates provided in the message details.
	@param msgDetails a map containing details of the opponent's move, including the new position of the queen
	@return the new index of the queen on the game board
	*/
	public static int getQueenNewIndexFromCoord (Map<String, Object> msgDetails) {
		ArrayList<Integer> newPosition =  (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_NEXT);
		int row = DIM-newPosition.get(0);
		int col = newPosition.get(1)-1;
		int index = row*DIM + col;
		return index;
	}

	/**
	This method is called when the opponent makes a move. 
	@param opponentMove a Map containing the details of the opponent's move
	*/
	public void opponentPlayerMove(Map<String, Object> opponentMove){
		// Extract the indices of the queen's initial position, new position and arrow position from the opponent's move.
		int initialIndex = getQueenInitialIndexFromCoord(opponentMove);
		int newIndex = getQueenNewIndexFromCoord(opponentMove);
		int arrowIndex = getArrowIndexFromCoord(opponentMove);
		// Create a new move object.
		MovesGenerator.Move turn = new MovesGenerator.Move(initialIndex, newIndex, arrowIndex);
		
		
		// Get all the possible legal moves for the current player.
		legalMovesMap = MovesGenerator.possibleMoves(currentBoardState, opponentPlayer);
		// If the opponent's move is not in the legal moves map, log an error message.
		if(!legalMovesMap.containsKey(turn)){
			String opponentColor = ""; 
			if(opponentPlayer.isBlack()){
				opponentColor = "black";
			}else{
				opponentColor = "white";
			}
			String msg = "Illegal move committed by "+opponentColor ;
			logger.warning(msg);
		}

		logger.info("Opponent has played their turn. Planning our next move...");
		// Determine the opponent's color and update the current board state with the opponent's move.
		Square opponentSquare = ourPlayer.isWhite() ? Square.BLACK : Square.WHITE;
		currentBoardState.updateGraphWithNewMove(turn, opponentSquare);
	}
	
	/**

	Updates the current board state and legalMovesMap with the given move.
	@param move The move to be made on the current board state.
	@return A map containing details of the move made, including the current and next positions of the queen,
	    the position of the arrow, and the updated legal moves for the opponent.
	**/
	public Map<String, Object> updateBoardState(MovesGenerator.Move move){
		Map<String, Object> moveDetails = new HashMap<>();
		moveDetails.put(AmazonsGameMessage.QUEEN_POS_CURR, getCoordinates(move.currentIndex()));
		moveDetails.put(AmazonsGameMessage.QUEEN_POS_NEXT, getCoordinates(move.nextIndex()));
		moveDetails.put(AmazonsGameMessage.ARROW_POS, getCoordinates(move.arrowIndex()));
		// Update the current board state with the move.
		currentBoardState = legalMovesMap.get(move);
		// Update the legalMovesMap with all possible moves for the opponent.
		legalMovesMap = MovesGenerator.possibleMoves(currentBoardState, opponentPlayer);
		if(legalMovesMap.isEmpty()){
			logger.info("Opponent is left with no moves. Victory is ours, let it sink in!");
		}

		return moveDetails;
	}
	
	/**
	Finds the best move for our player using alpha-beta pruning.
	Searches through all the possible moves and selects the best move for our player.
	@return a map containing the updated board state after the best move is made
	*/
	public Map<String, Object> findOurBestMove() {

		legalMovesMap = MovesGenerator.possibleMoves(currentBoardState, ourPlayer);

		// Determine the search depth based on the size of the legalMovesMap.
		int searchDepth = 1;
		for(int i = 5; i > 0; i--){
			if(Math.pow(legalMovesMap.size(), i) < THRESHOLD){
				searchDepth = i;
				break;
			}
		}
		
		// Clone the current board state and find the best move using alpha-beta pruning.
		Graph clonedGraph = Graph.cloneGraph(currentBoardState);
		MovesGenerator.Move optimalMove = MinimaxSearch.findBestMoveUsingAlphaBeta(clonedGraph, ourPlayer, searchDepth);
		if(optimalMove == null) {
			optimalMove = MinimaxSearch.findBestMoveUsingAlphaBeta(Graph.cloneGraph(currentBoardState), ourPlayer, 1);
			return Collections.emptyMap();
		}

		return updateBoardState(optimalMove);
	}

	
	/*
	 This code defines a class Square with four static members and several methods. 
	 Each static member represents the type of square, namely EMPTY, WHITE, BLACK, and ARROW.
	 */
	public static class Square {

		public static final Square EMPTY = new Square(0);
		public static final Square WHITE = new Square(1);
		public static final Square BLACK = new Square(2);
		public static final Square ARROW = new Square(3);
	
		private final int value;
	
		private Square(int value) {
			this.value = value;
		}
	
		public int getValue() {
			return value;
		}

		public static Square valueOf(int id) {
			switch (id) {
				case 0:
					return EMPTY;
				case 1:
					return WHITE;
				case 2:
					return BLACK;
				case 3:
					return ARROW;
				default:
					throw new IllegalArgumentException("Invalid id: " + id);
			}
		}
	
		public boolean isEmpty() {
			if(this == EMPTY){
				return true;
			} else {
				return false;
			}
		}
	
		public boolean isPlayer() {
			if((isBlack() || isWhite())){
				return true;
			} else {
				return false;
			}
		}
	
		public boolean isWhite() {
			if(this == WHITE){
				return true;
			} else {
				return false;
			}
		}
	
		public boolean isBlack() {
			if(this == BLACK){
				return true;
			} else {
				return false;
			}
		}
	
		public boolean isArrow() {
			if(this == ARROW){
				return true;
			} else {
				return false;
			}
		}
	}

}

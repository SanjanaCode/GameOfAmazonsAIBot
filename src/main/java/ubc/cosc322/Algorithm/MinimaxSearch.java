package ubc.cosc322.Algorithm;

import java.util.Map;
import ubc.cosc322.Graph.*;
import ubc.cosc322.AmazonsGameManager.Square;

public class MinimaxSearch {

 // This class is used to store the result of alpha-beta pruning
    public record AlphaBetaResult(MovesGenerator.Move move, float heuristic) { }
    
    /**
    This method uses the alpha-beta pruning algorithm to find the best possible move for the given player
    at the given depth in the game tree.
    @param graph the graph representing the game state
    @param currentPlayer the player for whom the best move is being searched
    @param depth the depth of the game tree to search
    @return the best move found using alpha-beta pruning algorithm
    */
    public static MovesGenerator.Move findBestMoveUsingAlphaBeta(Graph graph, Square currentPlayer, int depth) {
    	AlphaBetaResult bestAlphaBetaMove = alphaBetaPruning(graph, depth, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, currentPlayer, null);
        return bestAlphaBetaMove.move;
    }

    /**
    Perform alpha-beta pruning to find the best move for a given player at a specific search depth.
    @param graph the graph representing the current state of the game
    @param searchLevel the depth of the search tree
    @param alpha the alpha value for alpha-beta pruning
    @param beta the beta value for alpha-beta pruning
    @param currentPlayer the player whose turn it is to make a move
    @param prevMove the previous move made in the game
    @return an AlphaBetaResult object containing the optimal move and its corresponding score
    */
    private static AlphaBetaResult alphaBetaPruning(Graph graph, int searchLevel, float alpha, float beta, Square currentPlayer, MovesGenerator.Move prevMove) {
    	// If the search depth has reached zero, return the heuristic value of the current state
    	if (searchLevel == 0)
            return new AlphaBetaResult(prevMove, AmazonsDistanceHeuristic.Heuristic.calculateHeuristicValue(graph, currentPlayer));

        float optimalValue = (currentPlayer.isWhite() ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY);
        MovesGenerator.Move optimalMove = null;

     // Iterate over all possible moves for the current player and calculate their scores
        	for (Map.Entry<MovesGenerator.Move, Graph> action : MovesGenerator.possibleMoves(graph, currentPlayer).entrySet()) {
                float optimalScore = alphaBetaPruning(action.getValue(), searchLevel - 1, alpha, beta, Square.BLACK, action.getKey()).heuristic;
             // If the current score is better than the current optimal score, update the optimal score and move
                if ((currentPlayer.isWhite() && optimalScore > optimalValue) ||
                        (!currentPlayer.isWhite() && optimalScore < optimalValue)) {
                	optimalValue = optimalScore;
                	optimalMove = action.getKey();
                    }
                
             // Update alpha and beta values based on the player
                if (currentPlayer.isWhite())
                    alpha = Math.max(alpha, optimalValue);
                else
                    beta = Math.max(alpha, optimalValue);

             // If beta is less than or equal to alpha, stop searching further
                if (beta <= alpha)
                    break;
                }
        	// Return the optimal move and score at the current search level
        return new AlphaBetaResult(optimalMove, optimalValue);
    }
}
package ubc.cosc322.Graph;

import ubc.cosc322.AmazonsGameManager;
import java.util.*;

public class MovesGenerator {
		
    public record Move(int currentIndex, int nextIndex, int arrowIndex){
        
    }	  
	  /**
	  Returns a map of all possible moves a player can make on a given graph, based on the current state of the game.
	  @param graph the graph representing the current state of the game
	  @param player the player whose moves are being generated
	  @return a map of all possible moves the player can make, with each move corresponding to a new cloned graph
	  reflecting the state of the game after the move is made
	  **/
    public static Map<Move, Graph> possibleMoves(Graph graph, AmazonsGameManager.Square playerToMove){
        List<GraphNode> currentPlayer = new LinkedList<>();
        Iterator<GraphNode> iter1 = graph.getAllGraphNodes().iterator();
        while(iter1.hasNext()){
            GraphNode currentNode = iter1.next();
            if(currentNode.getNodeValue() == playerToMove)
            	currentPlayer.add(currentNode);
        }

     // Iterates over all the nodes in the location list and for each node, iterates over all its edges and
      // for each edge, iterates over all the edges it points to.
      // Generates a Move for each combination of nodes and adds it to the playerMoves map.
        Map<Move, Graph> playerMoves = new HashMap<>();
        Iterator<GraphNode> iter = currentPlayer.iterator();
        GraphEdge.Direction[] path = GraphEdge.Direction.getAllDirections();
        while (iter.hasNext()) {
            GraphNode node = iter.next();
            for(GraphEdge.Direction next : path){
            	GraphEdge currentEdge = node.getExistingEdge(next);
                while(currentEdge!=null){
                    for(GraphEdge.Direction directionOfArrow : path){
                    	GraphEdge EdgeInDir = currentEdge.getTargetNode().getAvailableOrStartEdge( node,directionOfArrow);
                    	while(EdgeInDir!=null){
                    		 // Clones the current graph
                            Graph clone = Graph.cloneGraph(graph);
                             // creating a new move based on the edges available
                            Move currentMove = new Move(node.getNodeId(), currentEdge.getTargetNode().getNodeId(), EdgeInDir.getTargetNode().getNodeId());
                            clone.updateGraphWithNewMove(currentMove, playerToMove);
                            // adding the move and the cloned graph to the Map
                            playerMoves.put(currentMove, clone);
                            EdgeInDir = EdgeInDir.getTargetNode().getAvailableOrStartEdge(node,directionOfArrow);
                        }
                    }
                    currentEdge = currentEdge.getTargetNode().getExistingEdge(next);
                }
            }
        }
        return playerMoves;
    }
	
	}
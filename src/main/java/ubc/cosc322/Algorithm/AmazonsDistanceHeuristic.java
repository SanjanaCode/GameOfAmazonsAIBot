package ubc.cosc322.Algorithm;

import ubc.cosc322.Graph.*;
import ubc.cosc322.AmazonsGameManager.Square;

import java.util.*;

public class AmazonsDistanceHeuristic {

    public class Heuristic {

        /***
         * @param board The current board state
         * @param player The current player's square on the board
         * @return Computes and returns the overall heuristic
         */
        public static float calculateHeuristicValue(Graph board, Square player){
        

            float territoryScore = territoryWeight(board);
            float outsideInfluenceScore = outsideInfluenceWeight(board);
           // float transitionScore1 = transistionHeuristic1(board);
            float reachableSquareScore = reachableSquareWeight(board);
            float mobilityScore = mobilityWeight(board);
            float clusteringScore = clusteringWeight(board);
            float attackingPotentialScore = attackingPotentialHeuristic(board);
            float attackingPotential1Score = attackingPotential1Heuristic(board);


            double weight = Math.sqrt(Math.pow(territoryScore,2) + Math.pow(outsideInfluenceScore,2)
            +reachableSquareScore*reachableSquareScore + Math.pow(mobilityScore,2) + Math.pow(clusteringScore,2));

            float t1 = 0;
            float t2 = 0;

            for(GraphNode n : board.getAllGraphNodes()){
                
                    if(!n.getNodeValue().isEmpty()) {
                        continue;
                    }
                    t1 += Ti_value(player, n.getQueenDistanceWhite(), n.getQueenDistanceBlack());
                    t2 += Ti_value(player, n.getKingDistanceWhite(), n.getKingDistanceBlack());
                    
            }

            float c1 = C1_value(board);
            float c2 = C2_value(board);

            double heuristic1 = (territoryScore/weight) * t1;
            double heuristic2 = (outsideInfluenceScore/weight) * c1;
            double heuristic3 = (reachableSquareScore/weight) * c2;
            double heuristic4 = (mobilityScore/weight) * t2;
            double heuristic5 = (clusteringScore/weight) * t2;
            // double heuristic6 = (attackingPotential1Score/weight) * t1;
      
            return (float) (heuristic1 + heuristic2 + heuristic4 + heuristic3 + heuristic5);
        }

        /***
         * @param player The current player's square on the board
         * @param dist1 Queen or King ditance for the current square
         * @param dist2 Corresponding distane to dist1
         * @return Computes and returns a heuristic that represents number of controlled squares between two players or the number of legal moves available
         */
        private static float Ti_value(Square player, int dist1, int dist2){
            float k = 1/5f;
    
            if(dist1 == Integer.MAX_VALUE && dist2 == Integer.MAX_VALUE) 
                return 0;
            else if(dist1 == dist2) {
                if (player ==  Square.BLACK)
                    return -k;
                else
                    return k;
            }
            else 
                return dist1 < dist2 ? 1 : -1;
        }

        /***
         * @param board The current state of the board
         * @return Computes and returns a heuristic that represents the influence a player's Amazons have on the empty squares
         */
        private static float C1_value(Graph board){
            float sum = 0;
            for (GraphNode n : board.getAllGraphNodes()) {
                float term1 = (float) Math.pow(2, -n.getQueenDistanceWhite());
                float term2 = (float) Math.pow(2, -n.getQueenDistanceBlack());
                sum += term1 - term2;
            }
            return 2 * sum;
        }

        /***
         * @param board The current state of the board
         * @return Computes and returns a heuristic that represents the difference in the number of reachable squares between the two players
         */
        private static float C2_value(Graph board){
            float sum = 0;
            for (GraphNode n : board.getAllGraphNodes()) {
                sum += Math.min(1, Math.max(-1, ((n.getKingDistanceBlack() - n.getKingDistanceWhite()) / 6f)));
            }
    
            return sum;
        }


        /***
         * @param board The current board state
         * @return Returns a heursitic weight for t1 based on the number of empty nodes in board 
         */
        private static float territoryWeight(Graph board){

            float weight = 0;                             //calculates the number of filled tiles
            for(GraphNode n : board.getAllGraphNodes()) {
                if(!n.getNodeValue().isEmpty()) {
                    weight++;
                }
            }
            weight = 100 - weight;
            return weight;
        }
  
        /***
         * @param board The current board state
         * @return Returns a heursitic weight for c1 based on the number of empty nodes in the board and giving them a desired weight
         */
        private static float outsideInfluenceWeight(Graph board) {

            float weight = 100;
                
            for(GraphNode n : board.getAllGraphNodes()){
                if(!n.getNodeValue().isEmpty()) {
                    weight--;
                
                }   
            }

            weight = 100 - weight;

            return ((((weight-40)/10)*((weight-40)/10))*(-1))+30;
        }
        

        /***
         * @param board The current board state
         * @return Returns a heursitic weight weight for c2 based on the number of empty nodes in the board and giving them a desired weight
         */
        private static float reachableSquareWeight (Graph board){
            
            float weight = 100;
               
            for(GraphNode n : board.getAllGraphNodes()){
               if(!n.getNodeValue().isEmpty()) {
                   weight--;
                
               }
            }

            weight = 100 - weight;

            return ((((weight-60)/10)*((weight-30)/10))*(-1))+30;

        }

        /***
         * @param board The current board state
         * @return Returns a heursitic weight weight for t2 based on the number of empty nodes in the board and giving them a desired weight that is inversed compared to territoryWeight(Graph board)
         */
        private static float mobilityWeight (Graph board){
              
            float w = 100; 
            for(GraphNode n : board.getAllGraphNodes()){
               if(!n.getNodeValue().isEmpty()) w--;
            }
                return (100 - w);
    
        }

        /***
         * Calculates a heuristic weight that penalizes clustering of pieces
         * @param board The current board state
         * @return Returns a heursitic weight for t2 based on the difference between queen distance values and king distance values.
         */
        private static float clusteringWeight(Graph board) {
            float score = 0;
        
            for (GraphNode node : board.getAllGraphNodes()) {
                if (!node.getNodeValue().isEmpty()) {
                    continue;
                }
        
                int qDistWhite = node.getQueenDistanceWhite();
                int qDistBlack = node.getQueenDistanceBlack();
                int kDistWhite = node.getKingDistanceWhite();
                int kDistBlack = node.getKingDistanceBlack();
        
                // Encourage larger kDist for both players to reduce clustering
                score += (kDistWhite - kDistBlack) * (qDistWhite - qDistBlack);
            }
        
            return score;
        }

        /***
         * Calculates a heuristic weight that favors attacking potential of pieces
         * @param board The current board state
         * @return Returns a heursitic weight for t1 based on the difference between queen and king distance values.
         */
        private static float attackingPotentialHeuristic(Graph board) {
            float score = 0;

            for (GraphNode node : board.getAllGraphNodes()) {
                if (!node.getNodeValue().isEmpty()) {
                    continue;
                }

                int qDistWhite = node.getQueenDistanceWhite();
                int qDistBlack = node.getQueenDistanceBlack();
                int kDistWhite = node.getKingDistanceWhite();
                int kDistBlack = node.getKingDistanceBlack();

                // Encourage smaller qDist values for both players to increase attacking potential
                score += (qDistBlack - qDistWhite) * (kDistWhite - kDistBlack);
            }

            return score;
        }

        /***
         * Calculates a heuristic weight that favors attacking potential with respect to mobility of pieces
         * @param board The current board state
         * @return Returns a heursitic weight for t1 based on only queen distance values.
         */
        private static float attackingPotential1Heuristic(Graph board) {
            float score = 0;

            for (GraphNode node : board.getAllGraphNodes()) {
                if (!node.getNodeValue().isEmpty()) {
                    continue;
                }

                int qDistWhite = node.getQueenDistanceWhite();
                int qDistBlack = node.getQueenDistanceBlack();
                // Encourage smaller qDist values for both players to increase mobility
                score += (1.0 / qDistWhite) - (1.0 / qDistBlack);
            }

            return score;
        }


    }

    public static class GraphDistanceCalculator {

    	/**

    	Calculates the distances between a given player node and all the other nodes in a graph.
    	Uses a Breadth First Search algorithm to explore the graph and set the distances.
    	@param graph The graph containing all the nodes to explore.
    	@param playerNode The node representing the player.
    	*/
        public static void calculatePlayerDistances(Graph graph, Square playerNode){
        	// Create a set of unvisited nodes and a list of nodes to search, initialize the queen's distance to 1.
            Set<GraphNode> unvisitedNodes = new HashSet<>();
            List<DistanceNode> nodeSearch = new LinkedList<>();
            List<GraphNode> allNodes = graph.getAllGraphNodes();
            int queenDistance = 1;
            
            for (int i = 0; i < allNodes.size(); i++) {
                GraphNode node = allNodes.get(i);
                Square tile = node.getNodeValue();
             // If the tile is an arrow, skip it.
                if (tile.isArrow()) {
                    continue;
                }
             // If the tile is not the player, add it to the set of unvisited nodes.
                if (!tile.isPlayer()) 
                {
                    unvisitedNodes.add(node);
                }
             // If the node is the player node, set its distance to zero and add it to the node search list.
                if (node.getNodeValue() == playerNode) {
                    node.setPlayerDistancesZero(node.getNodeValue());
                    GraphDistanceCalculator.DistanceNode distanceNode = new DistanceNode(node, node.getNodeValue());
                    nodeSearch.add(distanceNode);
                }
    
            }
         // Continue to explore the graph until all nodes have been visited.
            while (!unvisitedNodes.isEmpty()) {
                nodeSearch = exploreAndSetDistances(nodeSearch, unvisitedNodes, queenDistance);
                queenDistance++;
             // If there are no more nodes to search, exit the loop.
                if (nodeSearch.isEmpty()) {
                    break;
                }
            }
        }
    
    
        /**

        Explores and sets the distances between the initial nodes and their neighbors in the graph.
        @param initialNodes list of distance nodes from which to start exploring
        @param unvisitedNodes set of nodes in the graph that have not yet been visited
        @param queenDistance the queen distance
        @return a list of new distance nodes that were visited in the graph during the exploration
        */
        private static List<DistanceNode> exploreAndSetDistances(List<DistanceNode> initiaNodes,  Set<GraphNode> unvisitedNodes, int queenDistance){
    
            List<DistanceNode> newVistedNodes = new LinkedList<>();
    
            for(int i = 0; i < initiaNodes.size(); i++){
    
                GraphNode initNode = initiaNodes.get(i).node;
                unvisitedNodes.remove(initNode);
    
                for (int j = 0; j < GraphEdge.Direction.getAllDirections().length; j++) {
                	// get the current edge from the initial node in the current direction
                    GraphEdge currEdge = initNode.getExistingEdge(GraphEdge.Direction.getAllDirections()[j]);
    
                    int kingDistance = 1;
                    // set distances for all nodes in the current edge
                    while(currEdge!=null){

                        GraphNode currNode = currEdge.getTargetNode();
                        
                        if(initiaNodes.get(i).currentPlayer().isWhite()){
                        	//If player is white, get the king distance of the white piece
                            kingDistance = initNode.getKingDistanceWhite();
                            if(currNode.getQueenDistanceWhite() > queenDistance) {
                                currNode.setQueenDistanceWhite(queenDistance);
                            }
                            if(currNode.getKingDistanceWhite() > kingDistance) {
                                ++kingDistance;
                                currNode.setKingDistanceWhite(kingDistance);
                            }
                        }
                        
                        else if(initiaNodes.get(i).currentPlayer().isBlack())
                        {
                        	 //If the player is black, get the king distance of the black piece
                            kingDistance = initNode.getKingDistanceBlack();
                            if(currNode.getQueenDistanceBlack() > queenDistance){
                                 currNode.setQueenDistanceBlack(queenDistance);
                            }
                            if(currNode.getKingDistanceBlack() > kingDistance) {
                                ++kingDistance;
                                currNode.setKingDistanceBlack(kingDistance);
                            }
                        }
    
                        if (unvisitedNodes.remove(currNode)) {
                        	 // create a new distance node for the current node and add it to the list of new visited nodes
                            DistanceNode distanceNode = new DistanceNode(currNode, initiaNodes.get(i).currentPlayer);
                            newVistedNodes.add(distanceNode);
                        }
                     // get the next edge in the current direction from the current node
                        currEdge = currNode.getExistingEdge(GraphEdge.Direction.getAllDirections()[j]);
                    }
    
    
                }
            }
    
            return newVistedNodes;
        }

        private record DistanceNode(GraphNode node, Square currentPlayer){

            @Override
            public boolean equals(Object o) { return o instanceof DistanceNode d && d.node.equals(node); }
    
        }
    
    
    
    }
    
   

}
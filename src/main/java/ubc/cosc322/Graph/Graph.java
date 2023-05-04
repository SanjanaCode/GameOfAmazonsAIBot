package ubc.cosc322.Graph;

import ubc.cosc322.AmazonsGameManager.Square;
import ubc.cosc322.Algorithm.AmazonsDistanceHeuristic.GraphDistanceCalculator;

import java.util.ArrayList;
import java.util.List;


public class Graph {

    private List<GraphNode> nodeList;
    private int h;
    private int w;

    
     /**
    Constructor for the Graph class.
    @param gameBoard a 2D integer array representing the game board
    @param dim an array of integers representing the width and height of the graph (only used if gameBoard is null)
    */
    public Graph(int[][] gameBoard, int ...dim) {
    	//If gameBoard is null, creates an empty graph with width and height specified in dim.
        if(gameBoard == null){
            w = dim[0];
            h = dim[1];
            nodeList = new ArrayList<>();
            return;
        }
        //If gameBoard is not null, initializes the graph with the provided gameBoard array.
        else{
            w = gameBoard[0].length;
            h = gameBoard.length;
            nodeList = new ArrayList<>(w * h);
            initializeGraph(gameBoard);
        }
       
    }

    
    /**
    This function creates a deep copy of the given Graph object.
    @param original the original graph to be cloned
    @return a cloned graph with copied nodes and edges
    */
    public static Graph cloneGraph(Graph original){
        Graph clone = new Graph(null, original.w, original.h);
        for (GraphNode node: original.getAllGraphNodes()) {
            clone.nodeList.add(GraphNode.cloneNode(node));
        }

        for (GraphNode node: original.getAllGraphNodes()) {
            int id = node.getNodeId();
            GraphNode copyNode = clone.nodeList.get(id);
            for(GraphEdge edge: node.getAllEdges()){
                int endPointId = edge.getTargetNode().getNodeId();
                copyNode.getAllEdges().add(GraphEdge.cloneEdge(edge, clone.nodeList.get(endPointId)));
            }
        }
        return clone;
    }

   
    
    public List<GraphNode> getAllGraphNodes(){
        return nodeList;
    }

    /**
    Updates the graph with the new move made by the player.
    @param currentMove - the move that was made by the player.
    @param currentPlayer - the player who made the move.
    */
    public void updateGraphWithNewMove(MovesGenerator.Move currentMove, Square currentPlayer){
    	// Get the initial, new, and arrow nodes from the graph
        GraphNode initialNode = nodeList.get(currentMove.currentIndex());
        GraphNode newNode = nodeList.get(currentMove.nextIndex());
        GraphNode arrowNode = nodeList.get(currentMove.arrowIndex());

     // Set the initial node's value to empty and disconnect its edges
        initialNode.setNodeValue(Square.EMPTY);
        
        connectOrDisconnectEdges(initialNode, true);

     // Set the new node's value to the current player and connect its edges
        newNode.setNodeValue(currentPlayer);
       
        connectOrDisconnectEdges(newNode, false);

     // Set the arrow node's value to ARROW and connect its edges
        arrowNode.setNodeValue(Square.ARROW);

        
        connectOrDisconnectEdges(arrowNode, false);

     // Initialize all distances for each node and calculate all distances for both players
        for(GraphNode nodes : nodeList){
            nodes.initializeAllDistances();
        } 
        GraphDistanceCalculator.calculatePlayerDistances(this, Square.WHITE);
        GraphDistanceCalculator.calculatePlayerDistances(this, Square.BLACK);
    }

    /**
    Initializes the graph based on the given game board, adding nodes for each square on the board and edges to connect
    each node to its adjacent neighbors. Also sets the initial values of each node based on the game board.
    @param board the game board represented as a 2D array of integers
    */
    private void initializeGraph(int[][] board){
    	// For each square on the board, create a new graph node and add it to the node list
        for(int j = 0; j < h; j++){
            for(int i = 0; i < w; i++){
                int id = j * w + i;
                GraphNode n = new GraphNode(id, Square.valueOf(board[j][i]));
                nodeList.add(n);
            }
        }
        
     // For each node in the graph, add edges connecting it to its adjacent neighbors
      for(int row = 0; row < h; row++){
        for(int col = 0; col < w; col++){
            int id = row * w + col;
            GraphNode node = nodeList.get(id);

         // Add edge to top neighbor
            if(row - 1 >= 0){
                int top = id - w;
                GraphNode topNode = nodeList.get(top);
                addEdgeToNeighbour(node, topNode,GraphEdge.Direction.TOP, topNode.getNodeValue().isEmpty());
            }
         // Add edge to bottom neighbor
            if(row + 1 < h){
                int bottom = id + h;
                GraphNode bottomNode = nodeList.get(bottom);
                addEdgeToNeighbour(node, bottomNode, GraphEdge.Direction.BOTTOM, bottomNode.getNodeValue().isEmpty());
            }
         // Add edge to right neighbor
            if(col + 1 < w){
                int right = id + 1;
                GraphNode rightNode = nodeList.get(right);
                addEdgeToNeighbour(node, rightNode, GraphEdge.Direction.RIGHT, rightNode.getNodeValue().isEmpty());
            }
         // Add edge to left neighbor
            if(col - 1 >= 0){
                int left = id - 1;
                GraphNode leftNode = nodeList.get(left);
                addEdgeToNeighbour(node, leftNode, GraphEdge.Direction.LEFT, leftNode.getNodeValue().isEmpty());
            }
         // Add edge to bottom right neighbor
            if(row + 1 < h && col + 1 < w){
                int bottomRight = id + w + 1;
                GraphNode bottomRightNode = nodeList.get(bottomRight);
                addEdgeToNeighbour(node, bottomRightNode, GraphEdge.Direction.BOTTOM_RIGHT, bottomRightNode.getNodeValue().isEmpty());
            }
         // Add edge to bottom left neighbor
            if(row + 1 < h && col - 1 >= 0){
                int bottomLeft = id + w - 1;
                GraphNode bottomLeftNode = nodeList.get(bottomLeft);
                addEdgeToNeighbour(node, bottomLeftNode, GraphEdge.Direction.BOTTOM_LEFT, bottomLeftNode.getNodeValue().isEmpty());
            }
         // Add edge to top right neighbor
            if(row - 1 >= 0 && col + 1 < w){
                int topRight = id - w + 1;
                GraphNode topRightNode = nodeList.get(topRight);
                addEdgeToNeighbour(node, topRightNode, GraphEdge.Direction.TOP_RIGHT, topRightNode.getNodeValue().isEmpty());
            }
         // Add edge to top left neighbor
            if(row - 1 >= 0 && col - 1 >= 0){
                int topLeft = id - w - 1;
                GraphNode topLeftNode = nodeList.get(topLeft);
                addEdgeToNeighbour(node, topLeftNode, GraphEdge.Direction.TOP_LEFT, topLeftNode.getNodeValue().isEmpty());
            }
            
        }
    }
    }

    /**
    This method connects or disconnects edges of the given node based on the given toggle value.
    @param node the node whose edges will be connected or disconnected
    @param toggle a boolean value that indicates whether to connect or disconnect edges
    */
    private void connectOrDisconnectEdges(GraphNode node, boolean toggle) {
        for (GraphEdge forwardEdge : node.getAllEdges())
            for (GraphEdge backwardEdge : forwardEdge.getTargetNode().getAllEdges())
                if (backwardEdge.getTargetNode() == node){
                    backwardEdge.setEdgeExists(toggle);
                }
                    
    }

    /**
    Adds an edge from a start node to a neighbor node in the given direction
    @param startNode the starting node to add the edge from
    @param neighbourNode the neighboring node to connect the edge to
    @param direction the direction of the edge
    @param exists a boolean value indicating whether the edge already exists or not
    */
    private void addEdgeToNeighbour(GraphNode startNode, GraphNode neighbourNode, GraphEdge.Direction direction, boolean exists){
        if(startNode.getAllEdges().size() == 8){
            return;
        }
        GraphEdge newEdge = new GraphEdge(neighbourNode, direction, exists);
        startNode.getAllEdges().add(newEdge);
    }

    /**
    Checks if this Graph object is equal to another object.
    @param o the object to compare to
    @return true if the two objects are equal, false otherwise
    */
    @Override
    public boolean equals(Object o){
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
      //Two Graph objects are equal if their nodeList have the same size and their corresponding nodes are equal.
        Graph graph = (Graph) o;
        if(nodeList.size() != graph.nodeList.size()) {
            return false;
        }
        for(int i = 0; i < nodeList.size(); i++){
            if(!nodeList.get(i).equals(graph.getAllGraphNodes().get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode(){
        return super.hashCode();
    }

   }
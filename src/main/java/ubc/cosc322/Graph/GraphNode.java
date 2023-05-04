package ubc.cosc322.Graph;

import java.util.ArrayList;
import java.util.List;


import ubc.cosc322.AmazonsGameManager;

public class GraphNode {

    private int id;
    private  List<GraphEdge> edgeList;
    private int kingDistanceWhite;
    private int kingDistanceBlack;
    private int queenDistanceWhite;
    private int queenDistanceBlack;
    private AmazonsGameManager.Square squareValue;


    /**
    Initializes a new instance of the GraphNode class with the specified id and value.
    @param id the unique identifier of the graph node
    @param value the value of the graph node
    */
    public GraphNode(int id, AmazonsGameManager.Square value){
        this.id = id;
        edgeList = new ArrayList<>();
        setNodeValue(value);
        initializeAllDistances();
    }

    //This function initializes all distances of a GraphNode to infinity (maximum value of int). 
    //It sets the queen and king distances to infinity for both black and white players.
     public void initializeAllDistances(){
        setQueenDistanceWhite(Integer.MAX_VALUE);
        setQueenDistanceBlack(Integer.MAX_VALUE);
        setKingDistanceWhite(Integer.MAX_VALUE);
        setKingDistanceBlack(Integer.MAX_VALUE);
    }


     /**
     Creates a deep copy of the provided GraphNode object.
     @param original the GraphNode object to be copied
     @return a new GraphNode object with the same attributes as the original object
     */
    public static GraphNode cloneNode(GraphNode original){
        GraphNode clone = new GraphNode(original.id, original.squareValue);
        clone.setKingDistanceWhite(original.getKingDistanceWhite());
        clone.setKingDistanceBlack(original.getKingDistanceBlack());
        clone.setQueenDistanceWhite(original.getQueenDistanceWhite());
        clone.setQueenDistanceBlack(original.getQueenDistanceBlack());
        return clone;
    }

    /**
    Sets the king and queen distances of a player to zero.
    @param player the player whose distances are being set to zero
    */
    public void setPlayerDistancesZero(AmazonsGameManager.Square player){
    	//If the player is black, the king and queen distances of the black player are set to zero.
        if(player.isBlack()){
            setQueenDistanceBlack(0);
            setKingDistanceBlack(0);  
        }
      //If the player is white, the king and queen distances of the white player are set to zero.
        else {        
            setQueenDistanceWhite(0);
            setKingDistanceWhite(0);
        }
    }

    /**
    Returns an existing edge with the given direction from the edge list, or null if it does not exist.
    @param direction the direction of the desired edge
    @return the existing edge with the given direction, or null if it does not exist
    */
    public GraphEdge getExistingEdge(GraphEdge.Direction direction){
        for(GraphEdge edge : edgeList){

            if(!edge.getEdgeExists()){
                continue;
            }
            if (edge.getEdgeDirection() == direction) {
                return edge;
            }
        }
        return null;
    }

    /**
    Sets the node value to the given square value. 
    @param value the square value to set
    */
    public void setNodeValue(AmazonsGameManager.Square value) {
    	//If the square value is an arrow,
        //then sets the king and queen distances for both black and white players to 0.
        if(value.isArrow()) {
            setQueenDistanceBlack(0);
            setKingDistanceBlack(0);  
            setQueenDistanceWhite(0);
            setKingDistanceWhite(0);
        }
        this.squareValue = value;
    }

    /**
    Returns the available edge in the given direction or the edge that leads back to the starting node if it exists.
    @param start the starting node
    @param direction the direction of the desired edge
    @return the available edge in the given direction or the edge that leads back to the starting node if it exists, or null if no such edge exists
    */
    public GraphEdge getAvailableOrStartEdge(GraphNode start, GraphEdge.Direction direction){
        for(GraphEdge edge : edgeList){
            if(edge.getEdgeDirection() != direction) {
                continue;
            }
            if ((edge.getEdgeExists() || edge.getTargetNode().equals(start))) {
                return edge;
            }
        }
        return null;
    }

    public int getNodeId(){ 
        return id; 
    }

    public AmazonsGameManager.Square getNodeValue() {
        return squareValue;
    }

    public List<GraphEdge> getAllEdges(){
        return edgeList;
    }


    public int getQueenDistanceWhite() {
        return queenDistanceWhite;
    }

    public void setQueenDistanceWhite(int queenDistanceWhite) {
        this.queenDistanceWhite = queenDistanceWhite;
    }

    public int getQueenDistanceBlack() {
        return queenDistanceBlack;
    }

    public void setQueenDistanceBlack(int queenDistanceBlack) {
        this.queenDistanceBlack = queenDistanceBlack;
    }

    public int getKingDistanceWhite() {
        return kingDistanceWhite;
    }

    public void setKingDistanceWhite(int kingDistanceWhite) {
        this.kingDistanceWhite = kingDistanceWhite;
    }

    public int getKingDistanceBlack() {
        return kingDistanceBlack;
    }

    public void setKingDistanceBlack(int kingDistanceBlack) {
        this.kingDistanceBlack = kingDistanceBlack;
    }

   
    /**
    Checks if this GraphNode is equal to another object.
    @param o the object to compare
    @return true if the object is equal to this GraphNode, false otherwise
    */
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GraphNode node = (GraphNode) o;
        if(id==node.id && squareValue==node.squareValue) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
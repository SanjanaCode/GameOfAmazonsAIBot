package ubc.cosc322.Graph;


public class GraphEdge {

        private final Direction edgeDirection;
        private boolean edgeExists;
        private final GraphNode targetNode;

        /**
         * Constructor for GraphEdge object
         * @param targetNode the GraphNode object that this edge points to
         * @param direction the direction of the edge
         * @param edgeExists whether or not the edge exists
         */
        public GraphEdge(GraphNode targetNode, Direction direction, boolean edgeExists){
        	this.edgeDirection = direction;
            this.edgeExists = edgeExists;
        	this.targetNode = targetNode; 
        }

        /**
         * Clones an existing GraphEdge object and returns a new GraphEdge with the same properties
         * @param edge the GraphEdge object to be cloned
         * @param connectedNode the GraphNode object that the cloned edge will point to
         * @return a new GraphEdge object with the same direction and existence value as the input edge
         */
        public static GraphEdge cloneEdge(GraphEdge edge, GraphNode connectedNode){
            return new GraphEdge(connectedNode, edge.getEdgeDirection(), edge.edgeExists);
        }

        /**
         * This nested static class represents the possible directions an edge can be oriented in.
         * It defines eight static final instances, each with a direction label.
         * The getAllDirections() method returns an array containing all eight Direction objects.
         */
        public static class Direction {
        	public static final Direction TOP = new Direction("Top");
            public static final Direction TOP_RIGHT = new Direction("Top-Right");
            public static final Direction TOP_LEFT = new Direction("Top-Left");
            public static final Direction RIGHT = new Direction("Right");
            public static final Direction LEFT = new Direction("Left");
            public static final Direction BOTTOM_RIGHT = new Direction("Bottom-Right");
            public static final Direction BOTTOM = new Direction("Bottom");
            public static final Direction BOTTOM_LEFT = new Direction("Bottom-Left");

            public  String directionLabel;

            /**
        	 * Constructor for Direction object
        	 * @param directionLabel the label for the direction
        	 */
            Direction(String directionLabel) {
                this.directionLabel = directionLabel;
            }

            /**
        	 * Returns the direction label for this Direction object
        	 * @return the direction label
        	 */
            public String getDirectionLabel() {
                return directionLabel;
            }
            
            //An array containing all eight Direction objects
            public static GraphEdge.Direction[] directions = {
            	    GraphEdge.Direction.TOP,
            	    GraphEdge.Direction.TOP_RIGHT,
            	    GraphEdge.Direction.RIGHT,
            	    GraphEdge.Direction.BOTTOM_RIGHT,
            	    GraphEdge.Direction.BOTTOM,
            	    GraphEdge.Direction.BOTTOM_LEFT,
            	    GraphEdge.Direction.LEFT,
            	    GraphEdge.Direction.TOP_LEFT
            	};
            
            /**
        	 * Returns an array containing all eight Direction objects
        	 * @return an array of Direction objects
        	 */
            public static GraphEdge.Direction[] getAllDirections(){
            	return directions;
            }

        } 

        public Direction getEdgeDirection() {
            return edgeDirection;
        }
        
        public GraphNode getTargetNode(){
            return targetNode;
        }

        public void setEdgeExists(boolean edgeExists) {
            this.edgeExists = edgeExists;
        }

        public boolean getEdgeExists() { 
        	return edgeExists; 
        }

       
        @Override
        public String toString(){
        	 return String.format("Index: %d, Value: %s, Direction: %s", targetNode.getNodeId(), targetNode.getNodeValue(), edgeDirection.getDirectionLabel());
        }
}
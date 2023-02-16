import java.util.List;
import java.util.ArrayList;

public class GraphNode{
	private GraphNode previous;
	private List<GraphNode> next;
	private int direction;
	private int xDistance;
	private int yDistance;
	private String name;
	
	//for root node
	public GraphNode(){
		next = new ArrayList<GraphNode>();
		this.xDistance = 0;
		this.yDistance = 0;
		this.direction = 1;
	}
	
	//named root node
	public GraphNode(String name){
		next = new ArrayList<GraphNode>();
		this.name = name;
		this.xDistance = 0;
		this.yDistance = 0;
		this.direction = 1;
	}
	
	//for all other nodes
	//requires robot movement code / other part of program to determine x and y distance
	//x and y distance can be negative
	public GraphNode(GraphNode previous, int xDistMoved, int yDistMoved, int direction){
		next = new ArrayList<GraphNode>();
		this.previous = previous;
		this.direction = direction;
		this.xDistance = xDistMoved;
		this.yDistance = yDistMoved;
	}
	
	//named standard node
	public GraphNode(GraphNode previous, int xDistMoved, int yDistMoved, int direction, String name){
		next = new ArrayList<GraphNode>();
		this.previous = previous;
		this.direction = direction;
		this.xDistance = xDistMoved;
		this.yDistance = yDistMoved;
		
		this.name = name;
	}
	
	public int getxDist(){
		return xDistance;
	}
	
	public int getyDist(){
		return yDistance;
	}
	
	public GraphNode getPrevious(){
		return previous;
	}
	
	public int getDirection(){
		return direction;
	}
	
	public String getName(){
		if(name == null){
			return "This node has not been named";
		} else {
			return this.name;
		}
	}
	
	public GraphNode getNext(int index){
		if(this.next.isEmpty()){
			return null;
		} else{
			GraphNode next = this.next.get(index);
			return next;
		}
	}
	
	public void addNext(GraphNode nextNode){
		this.next.add(nextNode);
	}
	
	public int getNumChild(){
		return this.next.size();
	}
}
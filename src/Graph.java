import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

public class Graph{
	private GraphNode root;
	private GraphNode top;
	private List<GraphNode> nodeList;
	private GraphNode last;
	private List<GraphNode> depthFirstList;

	
	//calling graph creates the root node
	public Graph(String name){
		this.depthFirstList = new ArrayList<GraphNode>();
		
		this.nodeList = new ArrayList<GraphNode>();
		this.root = new GraphNode(name);
		this.nodeList.add(this.root);
		this.top = this.root;
	}
	
	public Graph(){
		this.depthFirstList = new ArrayList<GraphNode>();
		
		this.nodeList = new ArrayList<GraphNode>();
		this.root = new GraphNode();
		this.nodeList.add(this.root);
		this.top = this.root;
	}
	
	
	//options for creating a new node:
	
	// 0) Calling the constructor creates the root node
	
	// 1) create a new node giving a previous GraphNode object, x distance, y distance and direction 
	//addNode(GraphNode,int,int,int)
	//USE THIS ONE
	
	// 2) create a new node giving x distance, y distance and direction (the previous node will automatically be assigned to the last created node)
	//addNode(int,int,int)
	
	// 3) create a new named node giving a name, previous GraphNode object, x distance, y distance and direction 
	//addNamedNode(String,GraphNode,int,int,int)
	
	// 4) create a new named node giving a name, (String) name of previous GraphNode object, x distance, y distance and direction 
	//addNamedNode(String,String,int,int,int)
	
	// 5) create a new named node giving a name, x distance, y distance and direction (the previous node will automatically be assigned to the last created node)
	//addNamedNode(String,int,int,int)
	
	//IMPORTANT!!!
	//THE ADDNODE FUNCTIONS WERE ADDED FOR TESTING EXCEPT NUMBER 1 AND 2, HOWEVER NUMBER 2 IS BEST SUITED TO THE PROJECT SO USE NUMBER 2
	
	// 1) normal node addNode()
	public void addNode(GraphNode previous, int xDistMoved, int yDistMoved, int direction){
		if(checkForExisting(xDistMoved,yDistMoved)){
			tryConnection(xDistMoved,yDistMoved);
			return;
		}
		
		GraphNode graphnode = new GraphNode(previous, xDistMoved, yDistMoved, direction);
		GraphNode previousNode = graphnode.getPrevious();
		previousNode.addNext(graphnode);
		
		this.last = this.top;
		this.top = graphnode;
		this.nodeList.add(graphnode);
	}
	
	
	// 2) adding a node without a previous node specified will use the most recently visited node as the previous
	//USE THIS FOR ADDING NODES
	public void addNode(int xDistMoved, int yDistMoved, int direction){
		if(checkForExisting(xDistMoved,yDistMoved)){
			tryConnection(xDistMoved,yDistMoved);
			return;
		}
		GraphNode previousNode = this.top;
		GraphNode graphnode = new GraphNode(previousNode, xDistMoved, yDistMoved, direction);
		previousNode.addNext(graphnode);
		
		this.last = this.top;
		this.top = graphnode;
		this.nodeList.add(graphnode);
	}
	
	// 3)named node
	public void addNamedNode(String name, GraphNode previous, int xDistMoved, int yDistMoved, int direction){
		if(checkForExisting(xDistMoved,yDistMoved)){
			tryConnection(xDistMoved,yDistMoved);
			return;
		}
		if(getNode(name)!=null){
			System.out.println("Error: you cannot use the same name as another existing node in this graph");
			return;
		}
		GraphNode graphnode = new GraphNode(previous, xDistMoved, yDistMoved, direction, name);
		GraphNode previousNode = graphnode.getPrevious();
		previousNode.addNext(graphnode);
		
		this.last = this.top;
		this.top = graphnode;
		this.nodeList.add(graphnode);
	}
	
	// 4) named node with previous given by string name
	public void addNamedNode(String name, String previousName, int xDistMoved, int yDistMoved, int direction){
		if(checkForExisting(xDistMoved,yDistMoved)){
			tryConnection(xDistMoved,yDistMoved);
			return;
		}
		/*
		if(getNode(name)!=null){
			System.out.println("Error: you cannot use the same name as another existing node in this graph");
			return;
		}
		*/
		GraphNode previousNode = getNode(previousName);
		if (getNode(previousName)==null){
			return;
		}
		GraphNode graphnode = new GraphNode(previousNode, xDistMoved, yDistMoved, direction, name);
		previousNode.addNext(graphnode);
		
		this.last = this.top;
		this.top = graphnode;
		this.nodeList.add(graphnode);
	}
	
	// 5) named node added on top (of last created node)
	public void addNamedNode(String name, int xDistMoved, int yDistMoved, int direction){
		if(checkForExisting(xDistMoved,yDistMoved)){
			tryConnection(xDistMoved,yDistMoved);
			return;
		}
		if(getNode(name)!=null){
			System.out.println("Error: you cannot use the same name as another existing node in this graph");
			return;
		}
		
		GraphNode previousNode = this.top;
		GraphNode graphnode = new GraphNode(previousNode, xDistMoved, yDistMoved, direction, name);
		previousNode.addNext(graphnode);
		
		this.last = this.top;
		this.top = graphnode;
		this.nodeList.add(graphnode);
	}
	
	public GraphNode getRoot(){
		return this.root;
	}
	
	
	public void traverse(GraphNode node){
		if(node == null){
			return;
		}
		this.depthFirstList.add(node);
		int numChildNode = node.getNumChild();
		for(int i = 0; i < numChildNode; i++){
			GraphNode nextNode = node.getNext(i);
			traverse(nextNode);
		}
		return;
	}
	
	//for testing
	public void traversalString(){
		depthFirstList.clear();
		traverse(this.root);
		for(GraphNode item : depthFirstList){
			System.out.print(item.getName());
			System.out.print(", ");
		}
		System.out.println("");
	}
	
	public GraphNode getNode(String name){
		for(GraphNode item : this.nodeList){
			if(name.equals(item.getName())){
				return item;
			}
		}
		return null;
	}
	
	//used for testing
	public void getGraphList(){
		System.out.println("Graph List:");
		for(GraphNode item : this.nodeList){
			System.out.println(item.getName());
		}
		System.out.println("-----------");
	}
	
	//used for testing
	public void getListCons(){
		for(GraphNode item : this.nodeList){
			System.out.println(item.getName()+": ");
			if(!(item.equals(this.root))){
				System.out.println("previous: "+item.getPrevious().getName());
			}
			int numChildNode = item.getNumChild();
			for(int i = 0; i < numChildNode; i++){
				GraphNode nextNode = item.getNext(i);
				System.out.println("next: "+nextNode.getName());
			}
		}
	}
	
	public GraphNode getprevious(GraphNode node){
		GraphNode old = node.getPrevious();
		return old;
	}
	
	public GraphNode getprevious(String name){
		GraphNode node = getNode(name);
		if(node == null){
			return null;
		}
		return node.getPrevious();
	}
	
	public ArrayList<Integer> getCoords(GraphNode node){
		int x = node.getxDist();
		int y = node.getyDist();
		ArrayList<Integer> coords = new ArrayList<Integer>();
		coords.add(x);
		coords.add(y);
		return coords;
	}
	
	public boolean checkForExisting(int x, int y){
		ArrayList<Integer> nodeCoords = new ArrayList<Integer>();
		nodeCoords.add(x);
		nodeCoords.add(y);
		for(GraphNode item : nodeList){
			if(nodeCoords.equals(getCoords(item))){
				this.last = this.top;
				this.top = item;
				return true;
			}
		}
		return false;
	}
	
	public void tryConnection(int x, int y){
		if(this.last == null){
			return;
		}
		ArrayList<Integer> coords = new ArrayList<Integer>();
		coords.add(x);
		coords.add(y);
		if(getCoords(this.last).equals(coords)){
			this.last = null;
			return;
		} else{
			GraphNode node = this.last;
			node.addNext(this.top);
			this.last = null;
		}
	}
}
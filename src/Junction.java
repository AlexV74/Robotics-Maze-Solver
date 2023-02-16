import java.util.*;
public class Junction {
	private HashMap<Integer, Path> existingPaths = new HashMap<Integer, Path>();
	private int xCoord;
	private int yCoord;
	
	/*
	 * constructors
	 * sets the coordinates of the junction or just leaves them ull to be set within the atJunction method within the SolveMaze class
	 */
	public Junction(int currentDirection, Junction prevJunction) {
		setCoord(currentDirection, prevJunction);
	}
	public Junction() {}

	public Junction(int x, int y) {
		this.xCoord = x;
		this.yCoord = y;
	}
	//direction relative to starting direction, 1 = forward, 2 = right, 3 = backwards, 4 = left
	public int getXCoord() {return xCoord;}
	public int getYCoord() {return yCoord;}
	public Path getLeft() {return existingPaths.get(4);}
	public Path getFront() {return existingPaths.get(1);}
	public Path getRight() {return existingPaths.get(2);}
	public Path getBack() {return existingPaths.get(3);}
	
	/*
	 * checks if paths exist and updates left, right and front accordingly
	 * param seenPaths: all of the previously visited paths
	 * param seenJunctions: all  of the previously visited junctions
	 * param: currentDirection: current direction in which the robot is facing
	 * param left: if a left path exists
	 * param right: if a right path exists
	 * param front: if a front path exists
	 * return: list containing all the existing paths off the current junction
	 */
	public ArrayList<Path> updatePaths(ArrayList<Path> seenPaths, ArrayList<Junction> seenJunctions, int currentDirection, boolean left, boolean right, boolean front) {
		ArrayList<Path> paths = new ArrayList<Path>();
		if (left) {
			paths = addPath(seenPaths, seenJunctions, currentDirection, 4, paths);
		}
		if (right) {
			paths = addPath(seenPaths, seenJunctions, currentDirection, 2, paths);
		}
		if (front) {
			paths = addPath(seenPaths, seenJunctions,currentDirection, 1, paths);
		}
		
		return paths;
	}
	
	/*
	 * adds a path to the paths list using the current direction of the robot and the direction of the new path
	 * param seenPaths: all of the previously visited paths
	 * param seenJunctions: all of the previously visited junctions
	 * param currentDirection: direction in which the robot is facing
	 * param directionToFace: direction in which the robot needs to face to take said path
	 * param paths: list in which the new path will be added to
	 * return: list paths with the new path added, if it wasn't already in the list
	 */
	private ArrayList<Path> addPath (ArrayList<Path> seenPaths, ArrayList<Junction> seenJunctions, int currentDirection, int directionToFace, ArrayList<Path> paths) {
		int direction;
		direction = updateDirection(currentDirection, directionToFace);
		Junction newJunction = new Junction(direction, this);
		for (Junction junction : seenJunctions) {
			if (junction.getXCoord() == newJunction.getXCoord() && junction.getYCoord() == newJunction.getYCoord()) newJunction = junction;
		}
		Path newPath = new Path(this, newJunction);
		for (Path path : seenPaths) {
			if (newPath.getStart().equals(path.getStart()) && newPath.getEnd().equals(path.getEnd())) newPath = path;
		}
		boolean seen = false;
		for (Path path : paths) {
			if (newPath.getStart().equals(path.getStart()) && newPath.getEnd().equals(path.getEnd())) seen = true;
		}
		existingPaths.put(direction, newPath);
		if (!seen) paths.add(newPath);
		return paths;
	}
	
	/*
	 * updates direction of robot 
	 * param currentDirection: direction in which the robot is currently facing
	 * param newDirection: direction in which the robot is going to turn, relative to the robot
	 * return: the new direction
	 */
	public int updateDirection(int currentDirection, int newDirection) {
		// 1 = forward, 2 = right, 3 = backwards, 4 = left
		switch (newDirection) {
		case 1:
			return currentDirection;
		case 2:
			if (currentDirection == 4) return 1;
			return currentDirection + 1;
		case 3:
			if (currentDirection > 2) return currentDirection - 2;
			return currentDirection + 2;
			
		case 4:
			if (currentDirection == 1) return 4;
			return currentDirection - 1;
			
		default:
			return currentDirection;
			
		}
	}
	
	/*
	 * sets the coordinates of the current junction using the direction travelled from the last junction
	 * param currentDirection: current direction in which the robot is facing
	 * param prevJunction: the previous junction the robot visited
	 */
	public void setCoord(int currentDirection, Junction prevJunction) {
		//1 = forward, 2 = right, 3 = backwards, 4 = left
		switch(currentDirection) {
		case 1: //travelled forward
			this.yCoord = prevJunction.getYCoord() + 1;
			this.xCoord = prevJunction.getXCoord();
			break;
		case 2: //travelled right
			this.xCoord = prevJunction.getXCoord() + 1;
			this.yCoord = prevJunction.getYCoord();
			break;
		case 3: //travelled back
			this.yCoord = prevJunction.getYCoord() - 1;
			this.xCoord = prevJunction.getXCoord();
			break;
		case 4: //travelled left
			this.xCoord = prevJunction.getXCoord() - 1;
			this.yCoord = prevJunction.getYCoord();
			break;
			
		}	
	}
	
	/*
	 * checks if the if the coordinates of the current junction is the same as the passed in junction
	 * param o: object in which the current junction is being compared to
	 * return: true if they are equal, false if they are not
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Junction)) return false;
		Junction junction = (Junction)o;
		return junction == this;
	}
	
	/*
	 * overrides the hashCode so it is the concatenation of the x and y coordinate (will be unique)
	 * returns: hashCode of the coordinates
	 */
	@Override
	public int hashCode() {
		return (Integer.parseInt(Integer.toString(this.xCoord) + Integer.toString(this.yCoord)));
	}
}
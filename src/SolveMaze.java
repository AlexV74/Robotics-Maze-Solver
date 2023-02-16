import java.util.*;
public class SolveMaze {
	private ArrayList<Path> seenPaths = new ArrayList<Path>(); //visited paths
	private ArrayList<Junction> junctions = new ArrayList<Junction>(); //visited junctions (junctions are any turns within maze)
	Junction junction;
	Junction prevJunction;
	int direction; // 1 = forward, 2 = right, 3 = backwards, 4 = left (relative to starting direction)
	Path previousPath;
	
	/*
	 * Constructor - initialises the maze so path through can be reused without having to re - solve the maze
	 * creates the first 2 junctions and the path between them - adds info to relevant lists and updates variables 
	 */
	public SolveMaze() {
		Junction startJunction = new Junction(0, 0);
		addJunction(startJunction);
		Path initialPath = new Path(startJunction, new Junction());
		initialPath.updateVisits(seenPaths);
		addPath(initialPath);
		previousPath = initialPath;
		direction = 1;
		this.prevJunction = startJunction;
	}
	
	/*
	 * adds path to seenPaths list - checks if it is already been added before
	 * adds mirrored version of the path - starting from the other end of the said path
	 * param path: path to be added to list
	 */
	private void addPath(Path path) {
		boolean seen = false;
		for (Path listPath : seenPaths) {
			if (listPath.equals(path)) {
				seen = true;
			}
		}
		if (!seen) {
			this.seenPaths.add(path);
			Path mirrorPath = new Path(path.getEnd(), path.getStart());
			this.seenPaths.add(mirrorPath);
		}
	}
	
	/*
	 * adds junction to junctions list - checks if it has already been added before
	 * param junction - junction to be added to the list
	 */
	private void addJunction(Junction junction) {
		boolean seen = false;
		for (Junction listJunction : junctions) {
			if (listJunction.equals(junction)) {
				seen = true;
			}
		}
		if (!seen) {this.junctions.add(junction);}
		
	}
	
	/*
	 * calculates the direction the robot needs to turn (relative to the robot NOT the starting direction
	 * calculates which direction the path to take is facing - passes info into turn method
	 * param path: path in which the robot is to follow off of the current junction
	 * return: direction robot needs to turn at this junction
	 */
	private int directionToTurn(Path path) {
		int turnDirection = 0;
		if (this.junction.getFront() == path) {
			this.junction = previousPath.getEnd();
			
			turnDirection = turn(this.direction, 1);
		}
		if (this.junction.getLeft() ==  path) {
			this.junction = previousPath.getEnd();
			
			turnDirection = turn(this.direction, 4);
		}
		if (this.junction.getRight() == path) {
			this.junction = previousPath.getEnd();
			
			turnDirection = turn(this.direction, 2);
		}
		if (this.junction.getBack() == path) {
			this.junction = previousPath.getEnd();
			
			turnDirection = turn(this.direction, 3);
		}
		
		return turnDirection;
	}
	
	/*
	 * calculates the direction the robot needs to face, given the current direction and direction to face (which are relative to the start)
	 * param currentDirection: direction the robot is currently facing relative to the start
	 * param directionToFace: direction in which the robot needs to face relative to the start
	 * return: the direction the robot needs to turn relative to the robot
	 */
	private int turn(int currentDirection, int directionToFace) {
		this.direction = directionToFace;
		switch(currentDirection) {
		
		case(1): return directionToFace;
		case(2):
			if (directionToFace == 4) return 3;
			if (directionToFace == 1) return 4;
			if (directionToFace == 3) return 2;
			if (directionToFace == 2) return 1;
		case(3):
			if (directionToFace == 4) return 2;
			if (directionToFace == 1) return 3;
			if (directionToFace == 3) return 1;
			if (directionToFace == 2) return 4;
		case(4):
			if (directionToFace == 4) return 1;
			if (directionToFace == 1) return 2;
			if (directionToFace == 3) return 4;
			if (directionToFace == 2) return 3;
			
		}
		
		return 5;
	}
	
	/*
	 * finds the mirrored version of a given path (where the start of the current pathy = the end of the mirrored and vice versa
	 * param path: path to find the mirrored version of
	 * return: mirrored version of the given path
	 */
	private Path getMirroredPath(Path path) {
		for (Path seenPath : seenPaths) {
			if ( seenPath.getStart().equals(path.getEnd()) && seenPath.getEnd().equals(path.getStart())) {
				return seenPath;
			}
		}
		return path;
	}
	
	/*
	 * called from robot
	 * uses Tremauxe's Algorithm to determine best path to take - updates variables and lists accordingly
	 * param left: if a path to the left of the rbot exists
	 * param front: if a path to the front of the robot exists
	 * param right: if a path to the right of the robot exists
	 * return: direction the robot needs to turn to take the decided path
	 */
	public int atjunction(boolean left, boolean front, boolean right) {
		ArrayList<Path> paths = new ArrayList<Path>(); //existing paths off this junction
		this.junction = previousPath.getEnd();
		this.junction.setCoord(direction, this.prevJunction);
		previousPath.updateVisits(seenPaths); //adds a visit to the path taken to get to this junction
		paths = this.junction.updatePaths(seenPaths, junctions, direction, left, right, front); //finds existing paths off junction
		
		//checks if any of the paths have been visited before - if the current junction has already been visited
		boolean visited = false;
		for (Path path : paths) {
			for (Path listPath : seenPaths) {
				if (listPath.equals(path)) {
					visited = true;
				}
			}
			
		}
		
		if(visited) {
			
			//tryBactrack
			if (previousPath.canVisit()) {
				//backtrack
				this.direction = junction.updateDirection(this.direction, 3);
				previousPath = getMirroredPath(previousPath);
				this.prevJunction = this.junction;
				
				return 3;
			}
			else {
				//take path with least visits
				Path leastVisited = paths.get(0);
				for (Path path : paths) {
					if (path.getVisits() < leastVisited.getVisits()) leastVisited = path;
				}
				
				if (leastVisited.getVisits() >= 2) return 0;
				this.previousPath = leastVisited;
				addJunction(this.junction);
				addPath(previousPath);
				this.prevJunction = this.junction;
				int turnDirection = directionToTurn(leastVisited);
				return turnDirection;
			}
			
		} //if not visited before:
		else {	
			//take any path (in this case the path at index 0 of the paths list)
			this.previousPath = paths.get(0);
			addJunction(this.junction);
			addPath(previousPath);
			this.prevJunction = this.junction;
			int turnDirection = directionToTurn(paths.get(0));
			return turnDirection;
		}
	}
	
	/*
	 * called by robot when at a dead end
	 * adds dead end as a junction and update the path visits
	 * sets relevant info so the robot is in the correct state after turning around
	 */
	public void atDeadEnd() {
		this.junction = previousPath.getEnd();
		this.junction.setCoord(direction, this.prevJunction);
		previousPath.updateVisits(seenPaths);
		
		this.direction = junction.updateDirection(this.direction, 3);
		previousPath = getMirroredPath(previousPath);
		this.prevJunction = this.junction;
	}
	
	/*
	 * called by robot once at the finish
	 * adds the finish as a junction and updates path visits and other relevant info
	 */
	public void atFinish() {
		this.junction = previousPath.getEnd();
		this.junction.setCoord(direction, this.prevJunction);
		previousPath.updateVisits(seenPaths);
	}
	//all paths that have been visited once = solution route through maze from that start point to that finish
}
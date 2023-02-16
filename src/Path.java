import java.util.*;
public class Path {
	private Junction start;
	private Junction end;
	private int visits;
	
	/*
	 * constructor - initialises the start and end junctions of the new path
	 * param start: start junction of the path
	 * param end: end junction of the path
	 */
	public Path(Junction start, Junction end) {
		this.start = start;
		this.end = end;
		visits = 0;
	}
	
	/*
	 * getter methods
	 * return: desired attribute of an instance of Path (start junction, end junction or number of visits
	 */
	public Junction getStart() {
		return start;
	}
	public Junction getEnd() {
		return end;
	}
	public int getVisits() {
		return visits;
	}
	
	/*
	 * setter methods 
	 * sets the desired attribute to the one provided as a parameter
	 * param visits: 
	 */
	public void setVisits(int visits) {
		this.visits = visits;
	}
	public void setStart(Junction start) {
		this.start = start;
	}
	public void setEnd(Junction end) {
		this.end = end;
	}
	public void updateVisits(ArrayList<Path> paths) {
		this.visits = this.visits + 1;
		for (Path path : paths) {
			if (this.equals(path)) path.setVisits(this.visits);
		}
	}
	public boolean canVisit() {
		return (visits < 2);
	}
	@Override 
	public boolean equals(Object o) {
		if (!(o instanceof Path)) return false;
		Path path = (Path)o;
		return (path.getStart().equals(this.end) && path.getEnd().equals(this.start));
	}
	@Override
	public int hashCode() {
		return Integer.parseInt(Integer.toString(this.start.hashCode()) + Integer.toString(this.end.hashCode()));
	}
}

package roadgraph;

import java.util.Objects;

import geography.GeographicPoint;

/**
 * Represents a directed edge in a graph.<br>
 * Consists of start and end {@link GeographicPoint}s, road name (e.g. "Main street"),
 *  type (e.g. "residential") and length of road.
 * 
 * @author Miri Yehezkel
 *
 */
class DirectedEdge {
	/** Name of road in map	 */
	private String roadName;
	
	/** Type of road in map (residential, motorway etc.) */
	private String roadType;
	
	/** Length of road */
	private double length;
	
	/** Start location on map */
	private GeographicPoint start;
	
	/** End location on map */
	private GeographicPoint end;
	
	/**
	 * Constructs a directed edge with data
	 * @param roadName Name of road
	 * @param roadType Type of road (residential, motorway etc.)
	 * @param length Length of road
	 * @param start Start location on map
	 * @param end End location on map
	 */
	DirectedEdge(String roadName, String roadType, double length, 
			GeographicPoint start, GeographicPoint end) {
		setRoadName(roadName);
		setRoadType(roadType);
		setLength(length);
		setStart(start);
		setEnd(end);
	}
	
	String getRoadName() { return roadName; }
	void setRoadName(String roadName) { this.roadName = roadName; }
	
	String getRoadType() { return roadType; }
	void setRoadType(String roadType) { this.roadType = roadType; }
	
	double getLength() { return length; }
	void setLength(double length) { this.length = length; }
	
	GeographicPoint getStart() { return start; }
	void setStart(GeographicPoint start) { this.start = start; }
	
	GeographicPoint getEnd() { return end; }
	void setEnd(GeographicPoint end) { this.end = end; }
	
	/**
	 * Determines object hashCode by its attributes.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(roadName, roadType, length, start, end);
	}
	
	/**
	 * Checks if Objects are equal by checking all attributes. 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj != null && obj instanceof DirectedEdge) {
			DirectedEdge other = (DirectedEdge) obj;
			if (this.roadName.equals(other.roadName)
				&& this.roadType.equals(other.roadType)
				&& this.length == other.length
				&& this.start.equals(other.start)
				&& this.end.equals(other.end))
					return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "[roadName=" + roadName + ", roadType=" + roadType + ", length="
				+ length + ", start=(" + start.getX() + ", " + start.getY() 
				+ "), end=(" + end.getX() + ", " + end.getY()  + ") ]";
	}
	
	
	
}

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
	private String roadName;
	private String roadType;
	private double length;
	private GeographicPoint start;
	private GeographicPoint end;
	
	DirectedEdge(String roadName, String roadType, double length, 
			GeographicPoint start, GeographicPoint end) {
		setRoadName(roadName);
		setRoadType(roadType);
		setLength(length);
		setStart(start);
		setEnd(end);
	}
	
	public String getRoadName() { return roadName; }
	public void setRoadName(String roadName) { this.roadName = roadName; }
	
	public String getRoadType() { return roadType; }
	public void setRoadType(String roadType) { this.roadType = roadType; }
	
	public double getLength() { return length; }
	public void setLength(double length) { this.length = length; }
	
	public GeographicPoint getStart() { return start; }
	public void setStart(GeographicPoint start) { this.start = start; }
	
	public GeographicPoint getEnd() { return end; }
	public void setEnd(GeographicPoint end) { this.end = end; }
	
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

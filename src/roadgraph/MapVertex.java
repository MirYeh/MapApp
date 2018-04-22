package roadgraph;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import geography.GeographicPoint;

/**
 * Represents an intersection on a map.
 * 
 * @author Miri Yehezkel
 *
 */
class MapVertex {
	/**
	 * A GeographicPoint (latitude and longitude) on a map
	 */
	private GeographicPoint geoPoint;
	/**
	 * A {@link Set} of DirectedEdges from this vertex to another
	 */
	private Set<DirectedEdge> edges;
	
	/**
	 * No-args Constructor
	 */
	MapVertex() {
		edges = new HashSet<>();
	}
	
	/**
	 * Constructs a MapVertex with a GeographicPoint
	 * @param geographicPoint latitude and longitude on a map
	 */
	MapVertex(GeographicPoint geographicPoint) {
		this();
		geoPoint = geographicPoint;
	}

	GeographicPoint getGeoPoint() { return geoPoint; }
	void setGeoPoint(GeographicPoint geoPoint) { this.geoPoint = geoPoint; }

	Set<DirectedEdge> getEdges() { return edges; }
	void setEdges(Set<DirectedEdge> edges) { this.edges = edges; }
	
	boolean addEdge(DirectedEdge directedEdge) {
		if(directedEdge != null) {
			return edges.add(directedEdge);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(geoPoint, edges);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj != null && obj instanceof MapVertex) {
			MapVertex other = (MapVertex) obj;
			return geoPoint.equals(other.geoPoint);
		}
		return false;
	}

	@Override
	public String toString() {
		return "MapVertex [geoPoint=(" + geoPoint.getX() + "," 
				+ geoPoint.getY() + "), edges size=" + edges.size() + "]";
	}

	
	
}

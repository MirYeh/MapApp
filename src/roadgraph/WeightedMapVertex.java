package roadgraph;

/**
 * Represents a weighted intersection on a map.
 * 
 * @author Miri Yehezkel
 *
 */
class WeightedMapVertex extends MapVertex implements Comparable<WeightedMapVertex> {
	/**
	 * Weight of intersection (distance from start vertex)
	 */
	private double weight;
	
	
	/**
	 * Constructs a WeightedMapVertex from an existing MapVertex.
	 * @param v An existing MapVertex
	 */
	public WeightedMapVertex(MapVertex v) {
		super();
		setGeoPoint(v.getGeoPoint());
		setEdges(v.getEdges());
	}
	
	/**
	 * Constructs a WeightedMapVertex from an existing MapVertex and a given weight.
	 * @param v An existing MapVertex
	 * @param weight Weight of vertex
	 */
	public WeightedMapVertex(MapVertex v, double weight) {
		this(v);
		setWeight(weight);
	}
	
	double getWeight() { return weight; }
	void setWeight(double weight) { this.weight = weight; }
	
	
	@Override
	public int compareTo(WeightedMapVertex vertex) {
		return Double.compare(weight, vertex.weight);
	}
	
	
	@Override
	public String toString() {
		return "Weighted " + super.toString() + ", weight=" + weight;
		
	}
	
	
}

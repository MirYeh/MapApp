package roadgraph;

/**
 * Represents a weighted intersection on a map.
 * 
 * @author Miri Yehezkel
 *
 */
class WeightedMapVertex extends MapVertex implements Comparable<WeightedMapVertex> {
	/** Weight of intersection (distance from start vertex) */
	private double weight;

	/** Predicted distance from goal vertex */
	private double predictedDistance;
	
	/**
	 * Constructs a WeightedMapVertex from an existing MapVertex.
	 * @param v An existing MapVertex
	 */
	WeightedMapVertex(MapVertex v) {
		super();
		setGeoPoint(v.getGeoPoint());
		setEdges(v.getEdges());
	}
	
	/**
	 * Constructs a WeightedMapVertex from an existing MapVertex and a given weight.
	 * @param v An existing MapVertex
	 * @param weight Weight of vertex
	 */
	WeightedMapVertex(MapVertex v, double weight) {
		this(v);
		setWeight(weight);
	}
	
	WeightedMapVertex(MapVertex v, double weight, double predictedDistance) {
		this(v);
		setWeight(weight);
		setPredictedDistance(predictedDistance);
	}
	
	double getWeight() { return weight; }
	void setWeight(double weight) { this.weight = weight; }
	
	double getPredictedDistance() { return predictedDistance; }
	void setPredictedDistance(double predictedDistance) { this.predictedDistance = predictedDistance; }

	/**
	 * Gets the total weight of the vertex, which includes the sum of the
	 *  distance from start vertex and the distance from the goal vertex.
	 * @return the total weight of the vertex.
	 */
	Double getTotalWeight() {
		return weight + predictedDistance;
	}
	
	@Override
	public int compareTo(WeightedMapVertex vertex) {
		return Double.compare(getTotalWeight(), vertex.getTotalWeight());
	}
	
	
	@Override
	public String toString() {
		return "Weighted " + super.toString() + ", weight=" + weight;
		
	}
	
	
}

package roadgraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;

import geography.GeographicPoint;
import util.GraphLoader;

/**
 * A class which represents a directed graph of geographic locations.<br>
 * Nodes in the graph are intersections between.
 * 
 * @author UCSD MOOC development team
 * @author Miri Yehezkel
 * 
 */
public class MapGraph {
	/**
	 * A Map of geographic locations and their corresponding Vertices.
	 *///Using a map to reduce search-time of a location
	private Map<GeographicPoint, MapVertex> vertices;
	
	/** Number of edges on Map */
	private int numEdges;
	
	
	/** 
	 * Create a new empty MapGraph 
	 */
	public MapGraph() {
		vertices = new HashMap<>();
	}
	
	/**
	 * Get the number of vertices (road intersections) in the graph
	 * @return The number of vertices in the graph.
	 */
	public int getNumVertices() {
		return vertices.size();
	}
	
	/**
	 * Get the intersections, which are the vertices in this graph.
	 * @return The vertices in this graph as GeographicPoints
	 *///returning a copy in order to protect map data
	public Set<GeographicPoint> getVertices() {
		Set<GeographicPoint> copyOfVertices = new HashSet<>();
		Iterator<GeographicPoint> it = vertices.keySet().iterator();
		while(it.hasNext()) {
			GeographicPoint pt = it.next();
			copyOfVertices.add(new GeographicPoint(pt.getX(), pt.getY()));
		}
		return copyOfVertices;
	}
	
	/** Add a node corresponding to an intersection at a Geographic Point
	 * If the location is already in the graph or null, this method does 
	 * not change the graph.
	 * @param location  The location of the intersection
	 * @return true if a node was added, false if it was not (the node
	 * was already in the graph, or the parameter is null).
	 */
	public boolean addVertex(GeographicPoint location) {
		if (location == null || vertices.containsKey(location))
			return false;
		vertices.put(location, new MapVertex(location));
		return true;
	}

	
	/**
	 * Get the number of road segments in the graph
	 * @return The number of edges in the graph.
	 */
	public int getNumEdges() {
		return numEdges;
	}

	/**
	 * Adds a directed edge to the graph from pt1 to pt2.  
	 * Precondition: Both GeographicPoints have already been added to the graph
	 * @param from The starting point of the edge
	 * @param to The ending point of the edge
	 * @param roadName The name of the road
	 * @param roadType The type of the road
	 * @param length The length of the road, in km
	 * @throws IllegalArgumentException If the points have not already been
	 *   added as nodes to the graph, if any of the arguments is null,
	 *   or if the length is less than 0.
	 */
	public void addEdge(GeographicPoint from, GeographicPoint to, String roadName,
					String roadType, double length) throws IllegalArgumentException {
		//throws IllegalArgumentException with corresponding message
		verifyEdgeFields(from, to, roadName, roadType, length);
		vertices.get(from).addEdge(new DirectedEdge(roadName, roadType, length, from, to));
		numEdges++;
	}
	
	/**
	 * Checks if fields are valid, throws an exception with corresponding message if needed
	 * @param from The starting point of the edge
	 * @param to The ending point of the edge
	 * @param roadName The name of the road
	 * @param roadType The type of the road
	 * @param length The length of the road
	 * @throws IllegalArgumentException If the points have not already been
	 *   added as nodes to the graph, if any of the arguments is null,
	 *   or if the length is less than 0.
	 */
	private void verifyEdgeFields(GeographicPoint from, GeographicPoint to, String roadName,
			String roadType, double length) throws IllegalArgumentException {
		if (! notNull(from, to))
			throw new IllegalArgumentException("One or more GeographicPoint is null",
					new Throwable("from=" + from + ", to=" + to));
		if (! notNull(roadName, roadType) )
			throw new IllegalArgumentException("Road must contain values of name and type",
					new Throwable("roadName=" + roadName + ", roadType=" + roadType));
		if (length < 0)
			throw new IllegalArgumentException("Length must be greater than or equal to 0");
		if (! vertices.containsKey(from))
			throw new IllegalArgumentException("Start GeographicPoint doesn't exist in Map",
					new Throwable("from=" + from));
		if (! vertices.containsKey(to))
			throw new IllegalArgumentException("Goal GeographicPoint doesn't exist in Map",
					new Throwable("to=" + to));
	}
	
	/**
	 * Helper method, checks if objects are not null.
	 * @param objs Objects to check
	 * @return {@code true} if the objects are not null, {@code false} otherwise.
	 */
	private boolean notNull(Object... objs) {
		for (Object obj : objs)
			if (obj == null)
				return false;
		return true;
				
	}
	
	
	
	/* Search MapGraph Methods: */
	
	
	
	/** 
	 * Find the path from start to goal using breadth first search
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest (unweighted)
	 *   path from start to goal (including both start and goal).
	 */
	public List<GeographicPoint> bfs(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
        Consumer<GeographicPoint> temp = (x) -> {};
        return bfs(start, goal, temp);
	}
	
	/** 
	 * Find the path from start to goal using breadth first search
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization
	 * @return The list of intersections that form the shortest (unweighted)
	 *   path from start to goal (including both start and goal), or {@code null} if path doesn't exist.
	 */
	public List<GeographicPoint> bfs(GeographicPoint start, GeographicPoint goal,
					Consumer<GeographicPoint> nodeSearched) {
		if (isValidGeographicPoints(start, goal)) {
			//maps points to their "parent" point to find path taken
			Map<GeographicPoint, GeographicPoint> parentMap = new HashMap<>();
			if (hasBfsPath(start, goal, parentMap, nodeSearched))
				return reconstructPath(start, goal, parentMap);
		}
		return null;
	}
	
	
	/**
	 * Performs a breadth-first search on Map and finds the shortest (unweighted)
	 *  path from start to goal.
	 * @param start {@link GeographicPoint} start on Map
	 * @param goal {@link GeographicPoint} goal on Map
	 * @param parentMap A Map to reconstruct the path taken
	 * @param nodeSearched A hook for visualization
	 * @return {@code true} if found path, {@code false} otherwise.
	 */
	private boolean hasBfsPath(GeographicPoint start, GeographicPoint goal, 
			Map<GeographicPoint, GeographicPoint> parentMap,
			Consumer<GeographicPoint> nodeSearched) {
		Queue<GeographicPoint> toExplore = new LinkedList<>();
		Set<GeographicPoint> visited = new HashSet<>();
		
		visited.add(start);
		toExplore.add(start);
		
		while(!toExplore.isEmpty()) {
			GeographicPoint curr = toExplore.poll();
			nodeSearched.accept(curr); //Visualization of search
			if (curr.equals(goal))
				return true;
			
			Iterator<DirectedEdge> it = vertices.get(curr).getEdges().iterator();
			while(it.hasNext()) {
				GeographicPoint next = it.next().getEnd();
				boolean notVisited = visited.add(next);
				if (notVisited) {
					parentMap.put(next, curr);
					toExplore.add(next);
				}
			}//inner while
		}//outer while
		return false;
	}
	
	/**
	 * Reconstructs path from start {@link GeographicPoint} to goal.
	 * @param start {@link GeographicPoint} start on Map
	 * @param goal {@link GeographicPoint} goal on Map
	 * @param parentMap A Map to reconstruct the path taken
	 * @return A {@link List} representing the path taken (including both start and goal). 
	 */
	private List<GeographicPoint> reconstructPath(GeographicPoint start, GeographicPoint goal, 
			Map<GeographicPoint, GeographicPoint> parentMap) {
		LinkedList<GeographicPoint> path = new LinkedList<>();
		GeographicPoint curr = goal;
		while(!curr.equals(start)) {
			path.addFirst(curr);
			curr = parentMap.get(curr);
		}
		path.addFirst(start);
		return path;
	}
	
	
	
	/** 
	 * Find the path from start to goal using Dijkstra's algorithm.
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> dijkstra(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
        Consumer<GeographicPoint> temp = (x) -> {};
        return dijkstra(start, goal, temp);
	}
	
	
	/** 
	 * Find the path from start to goal using Dijkstra's algorithm.
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal), or {@code null} if path doesn't exist.
	 */
	public List<GeographicPoint> dijkstra(GeographicPoint start, GeographicPoint goal, 
			Consumer<GeographicPoint> nodeSearched) {
		if (isValidGeographicPoints(start, goal)) {
			//maps points to their "parent" point to find path taken
			Map<GeographicPoint, GeographicPoint> parentMap = new HashMap<>();
			if (hasDijkstraPath(start, goal, parentMap, nodeSearched))
				return reconstructPath(start, goal, parentMap);
		}
		return null;
	}
	
	/**
	 * Performs Dijkstra's search on Map and finds the shortest weighted
	 *  path from start to goal.
	 * @param start The starting location
	 * @param goal The goal location
	 * @param parentMap A Map to reconstruct the path taken
	 * @param nodeSearched A hook for visualization
	 * @return {@code true} if found path, {@code false} otherwise.
	 */
	private boolean hasDijkstraPath(GeographicPoint start, GeographicPoint goal, 
			Map<GeographicPoint, GeographicPoint> parentMap,
			Consumer<GeographicPoint> nodeSearched) {
		PriorityQueue<WeightedMapVertex> toExplore = new PriorityQueue<>();
		//maps points to their weight
		Map<GeographicPoint, Double> visited = new HashMap<>();
		visited.put(start, 0.0);
		toExplore.add(new WeightedMapVertex(vertices.get(start), 0.0));
		
		while (! toExplore.isEmpty()) {
			WeightedMapVertex currVertex = toExplore.poll();
			GeographicPoint currPt = currVertex.getGeoPoint();
			nodeSearched.accept(currPt); //visualization for search
			
			if (currPt.equals(goal))
				return true;
			
			double currWeight = currVertex.getWeight();
			Iterator<DirectedEdge> it = currVertex.getEdges().iterator();
			while(it.hasNext()) {
				DirectedEdge edge = it.next();
				GeographicPoint next = edge.getEnd();
				Double nextWeight = currWeight + edge.getLength();
				if (hasLowerPriority(edge, nextWeight, visited)) {
					visited.put(next, nextWeight);
					parentMap.put(next, currVertex.getGeoPoint());
					toExplore.add(new WeightedMapVertex(vertices.get(next), nextWeight));
				}
			}//inner while
		}//outer while
		return false;
	}
	
	
	/**
	 * Checks if end-point of edge has lower priority than found beforehand.
	 * @param edge DirectedEdge connected to end-point
	 * @param nextWeight Sum of weight of the end-point by route taken
	 * @param visited A Map of visited points and their weights
	 * @return {@code true} if end-point has lower weight than found before, {@code false} otherwise.
	 */
	private boolean hasLowerPriority(DirectedEdge edge, double nextWeight, 
			Map<GeographicPoint, Double> visited) {
		GeographicPoint next = edge.getEnd();
		Double visitedWeight = visited.get(next);
		return visitedWeight == null || nextWeight < visitedWeight;
	}
	
	/** 
	 * Find the path from start to goal using A-Star search
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> aStarSearch(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
        Consumer<GeographicPoint> temp = (x) -> {};
        return aStarSearch(start, goal, temp);
	}
	
	
	/** 
	 * Find the path from start to goal using A-Star search
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal), or {@code null} if path doesn't exist.
	 */
	public List<GeographicPoint> aStarSearch(GeographicPoint start, GeographicPoint goal, 
			Consumer<GeographicPoint> nodeSearched) {
		if (isValidGeographicPoints(start, goal)) {
			Map<GeographicPoint, GeographicPoint> parentMap = new HashMap<>();
			if (hasAStarPath(start, goal, parentMap, nodeSearched))
				return reconstructPath(start, goal, parentMap);
		}
		return null;
	}

	/**
	 * 
	 * Performs A* search on Map and finds the shortest weighted
	 *  path from start to goal.
	 * @param start The starting location
	 * @param goal The goal location
	 * @param parentMap A Map to reconstruct the path taken
	 * @param nodeSearched A hook for visualization
	 * @return {@code true} if found path, {@code false} otherwise.
	 */
	private boolean hasAStarPath(GeographicPoint start, GeographicPoint goal, 
			Map<GeographicPoint, GeographicPoint> parentMap,
			Consumer<GeographicPoint> nodeSearched) {
		PriorityQueue<WeightedMapVertex> toExplore = new PriorityQueue<>();
		//maps points to their total weight
		Map<GeographicPoint, Double> visited = new HashMap<>();
		
		visited.put(start, 0.0);
		toExplore.add(new WeightedMapVertex(vertices.get(start), 0.0, 0.0));
		
		while(! toExplore.isEmpty()) {
			WeightedMapVertex currVertex = toExplore.poll();
			GeographicPoint currPt = currVertex.getGeoPoint();
			nodeSearched.accept(currPt); //visualization for search
			
			if (currPt.equals(goal))
				return true;
			
			Double currWeight = currVertex.getWeight();
			Iterator<DirectedEdge> it = currVertex.getEdges().iterator();
			while(it.hasNext()) {
				DirectedEdge edge = it.next();
				GeographicPoint next = edge.getEnd();
				//if total weight less than found before
				Double visitedTotal = visited.get(next);
				double predictedDistance = next.distance(goal);
				double nextWeight = edge.getLength() + currWeight;
				double totalWeight = predictedDistance + nextWeight;
				if (visitedTotal == null || totalWeight < visitedTotal ) {
					visited.put(next, totalWeight);
					parentMap.put(next, currVertex.getGeoPoint());
					toExplore.add(new WeightedMapVertex(
							vertices.get(next), nextWeight, predictedDistance));
				}
			}//inner while
		}//outer while
		return false;
	}
	

	/**
	 * Checks if GeographicPoints are valid.
	 * @param start start The starting location
	 * @param goal The goal location
	 * @return {@code true} if GeographicPoints are valid, {@code false} otherwise.
	 */
	private boolean isValidGeographicPoints(GeographicPoint start, GeographicPoint goal) {
		return start != null && goal != null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		final String separator = "\n=============================================\n";
		System.out.print("Making a new map...");
		MapGraph firstMap = new MapGraph();
		System.out.println("DONE. \nLoading the map...");
		GraphLoader.loadRoadMap("data/testdata/simpletest.map", firstMap);
		
		System.out.println(separator);
		
		GeographicPoint startfirstMap = new GeographicPoint(4, 1);
		GeographicPoint goalfirstMap = new GeographicPoint(8, -1);
		
		//BFS TEST
		/*
		System.out.printf("\nBFS search on data/testdata/simpletest.map \nFrom (%s) To (%s): \nResult: %s \n",
				startfirstMap, goalfirstMap, firstMap.bfs(startfirstMap, goalfirstMap));
		
		System.out.println(separator);
		*/
		
		//Dijkstra's search
		/*
		System.out.printf("\nDijkstra search on data/testdata/simpletest.map \nFrom (%s) To (%s): \nResult: %s \n",
				startfirstMap, goalfirstMap, firstMap.dijkstra(startfirstMap, goalfirstMap));
		
		System.out.println(separator);
		*/
		//A* search
		/*System.out.printf("\nA* search on data/testdata/simpletest.map \nFrom (%s) To (%s): \nResult: %s \n",
				startfirstMap, goalfirstMap, firstMap.aStarSearch(startfirstMap, goalfirstMap));
		
		System.out.println(separator);
		*/
		
		
		
		
		
		
		System.out.println("DONE.");
		
		
		//test UCSD map:
		/*
		System.out.println(separator);
		MapGraph ucsdTest = new MapGraph();
		GraphLoader.loadRoadMap("data/maps/ucsd.map", ucsdTest);
		
		GeographicPoint start = new GeographicPoint(32.872964, -117.2433911);
		GeographicPoint goal = new GeographicPoint(32.8756071, -117.2437778);
		
		System.out.printf("Searching ucsd.map from (%s) to (%s): \nResult: %s \n",
				start, goal, ucsdTest.bfs(start, goal));
		*/
		
		// You can use this method for testing.  
		
		/* Here are some test cases you should try before you attempt 
		 * the Week 3 End of Week Quiz, EVEN IF you score 100% on the 
		 * programming assignment.
		 */
		
		/*
		MapGraph simpleTestMap = new MapGraph();
		GraphLoader.loadRoadMap("data/testdata/simpletest.map", simpleTestMap);
		
		GeographicPoint testStart = new GeographicPoint(1.0, 1.0);
		GeographicPoint testEnd = new GeographicPoint(8.0, -1.0);
		
		System.out.println("Test 1 using simpletest: Dijkstra should be 9 and AStar should be 5");
		List<GeographicPoint> testroute = simpleTestMap.dijkstra(testStart,testEnd);
		List<GeographicPoint> testroute2 = simpleTestMap.aStarSearch(testStart,testEnd);
		
		
		MapGraph testMap = new MapGraph();
		GraphLoader.loadRoadMap("data/maps/utc.map", testMap);
		
		// A very simple test using real data
		testStart = new GeographicPoint(32.869423, -117.220917);
		testEnd = new GeographicPoint(32.869255, -117.216927);
		System.out.println("Test 2 using utc: Dijkstra should be 13 and AStar should be 5");
		testroute = testMap.dijkstra(testStart,testEnd);
		testroute2 = testMap.aStarSearch(testStart,testEnd);
		
		
		// A slightly more complex test using real data
		testStart = new GeographicPoint(32.8674388, -117.2190213);
		testEnd = new GeographicPoint(32.8697828, -117.2244506);
		System.out.println("Test 3 using utc: Dijkstra should be 37 and AStar should be 10");
		testroute = testMap.dijkstra(testStart,testEnd);
		testroute2 = testMap.aStarSearch(testStart,testEnd);
		*/
		
		
		/* Use this code in Week 3 End of Week Quiz */
		/*MapGraph theMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadRoadMap("data/maps/utc.map", theMap);
		System.out.println("DONE.");

		GeographicPoint start = new GeographicPoint(32.8648772, -117.2254046);
		GeographicPoint end = new GeographicPoint(32.8660691, -117.217393);
		
		
		List<GeographicPoint> route = theMap.dijkstra(start,end);
		List<GeographicPoint> route2 = theMap.aStarSearch(start,end);

		*/
		
	}
	
}

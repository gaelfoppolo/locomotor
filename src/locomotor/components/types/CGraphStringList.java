package locomotor.components.types;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import locomotor.components.Pair;

import org.jgrapht.alg.FloydWarshallShortestPaths;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

/**
 * @todo .
 */
public class CGraphStringList implements CUniverseType {

	/**
	 * The list of string (unique index).
	 */
	private TreeMap<Integer, String> _values;

	/**
	 * The graph of relations.
	 */
	private SimpleGraph<Integer, DefaultEdge> _graph;

	/**
	 * Floyd-Warshall shortest paths.
	 */
	private FloydWarshallShortestPaths<Integer, DefaultEdge> _fwsp;

	/**
	 * Constructs the object.
	 *
	 * @param      values     The values
	 * @param      relations  The relations
	 */
	public CGraphStringList(TreeMap<Integer, String> values, ArrayList<Pair<Integer, Integer>> relations) {
		_values = values;
		
		// create vertices
		_graph = new SimpleGraph(DefaultEdge.class);
		for(Integer key : values.keySet()) {
			_graph.addVertex(key);
		}

		// create edges
		for(Pair<Integer, Integer> value : relations) {
			_graph.addEdge(value.getLeft(), value.getRight());
		}

		// shortest paths, no computations are performed yet
		_fwsp = new FloydWarshallShortestPaths(_graph);
	}

	/**
	 * Returns a string representation of the object.
	 *
	 * @return     String representation of the object.
	 */
	public String toString() {
		String str = "Set:\n";
		for(Map.Entry<Integer, String> value : _values.entrySet()) {
			
			Integer key = value.getKey();
			String val = value.getValue();

			str += key + "(" + val + ")\n";
		}
		str += "Relations:\n" + _graph.toString();
		return str;
	}

	/**
	 * Gets the map.
	 *
	 * @return     The map.
	 */
	public TreeMap<Integer, String> getMap() {
		return _values;
	}

	/**
	 * Gets the diameter.
	 *
	 * @return     The diameter.
	 */
	protected double getDiameter() {
		return _fwsp.getDiameter();
	}

	/**
	 * Get the distance, the length of a shortest path.
	 *
	 * @param      start  The start
	 * @param      end    The end
	 *
	 * @return     the shortest distance between start and end
	 */
	protected double distance(Integer start, Integer end) {
		return _fwsp.shortestDistance(start, end);
	}

	
}
import java.util.HashMap;

public class MarkovChain {
	private static HashMap<Integer, Vertex> chain;
	
	static {
		chain = new HashMap<>();
	}
	
	public static void addVertex(int key) {
		chain.put(key, new Vertex(key));
	}
	
	public static void addEdge(int key, Edge e) {
		chain.get(key).getEdges().put(e.getKeyNote(), e);
	}
	
	public static Vertex getVertex(int key) {
		return chain.get(key);
	}

	/**
	 *
	 * @param key unique to each two note sequence
	 * @param e the edge that contains all of the note data
	 */
	public static void updateWeight(int key, Edge e) {
		// if there is not a vertex mapped to this key
		if(!chain.containsKey(key)) {
			addVertex(key);			
			addEdge(key, e);
			// if none of the edges have this key, then add an edge with that key
		} else if(!chain.get(key).getEdges().containsKey(e.getKeyNote())) {
			addEdge(key, e);
		} else {
			// we have an edge with this key so update its weight
			// to make this note more likely to show up in the generated playback
			Edge ex = chain.get(key).getEdges().get(e.getKeyNote());
			ex.incChance();
			
			ex.addVelocity(e.getVelocity());
			ex.addDuration(e.getDuration());
		}
	}
	
	public static int sumEdges(HashMap<Integer, Edge> edges) {
		int sum = 0;
		
		for(Edge e : edges.values()) {
			sum += e.getChance();
		}
		
		return sum;
	}

	/**
	 * The chain up until this method is called only has counted
	 * the times particular two-note sequences have popped up in the
	 * training data so now we need to make it into a transitional
	 * probability matrix by getting the average duration for each note
	 * and the average chance of that note popping up after a particular
	 * two-note sequence
	 */
	public static void setAsProbabilityMatrix() {
		for(Vertex v : chain.values()) {
			int sum = sumEdges(chain.get(v.getKey()).getEdges());
			
			for(Edge e : chain.get(v.getKey()).getEdges().values()) {
				// call averageDuration and averageVelocity first
				// b/c they depend on the total times that
				// the edge was updated (i.e. the chance)
				// which is changed in the probability method
				e.averageDuration();
				e.averageVelocity();
				e.probability(sum);
			}
		}
	}
	
	public static Edge nextEdge(int key) {
		// determines how the next note is generated
		// if there is a vertex mapped to this key
		if(chain.containsKey(key)) {
			double rnd = Math.random();
			double sum = 0.0;
			// if some of note sequence is > random value
			// use that note, sum up the weight of the edges of the vertex
			for(Edge e : chain.get(key).getEdges().values()) {
				sum += e.getChance();
				
				if(sum >= rnd)
					return e;
			}
			
			int t = (int) (rnd*127);

			// get the note corresponding to the key passed if available
			if(chain.get(key).getEdges().containsKey(t)) 
				return chain.get(key).getEdges().get(t);
			else 
				return new Edge(0.0, Defaults.VELOCITY, Defaults.PAUSE, t,1);
		} else {
			int t = (int) (Math.random()*127);
			return new Edge(0.0, Defaults.VELOCITY, Defaults.PAUSE, t,1);
		}
	}
}

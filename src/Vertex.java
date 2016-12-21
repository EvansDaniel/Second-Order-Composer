import java.util.HashMap;

public class Vertex {
	public HashMap<Integer, Edge> edges;
	public int key;
	
	public Vertex(int key) {
		this.key = key;
		
		edges = new HashMap<>();
	}
	
	public HashMap<Integer, Edge> getEdges() {
		return edges;
	}
	
	public int getKey() {
		return key;
	}
}

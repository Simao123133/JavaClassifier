//This class stores important node information
class Vertex{
	boolean checked;
	double weight;
	int link;
}

public class Grafoo implements java.io.Serializable{

	private static final long serialVersionUID = 3L;
	int dim;
	double[][] ma;

	// Initialize Graph with adjacency matrix
	public Grafoo(int d) {

		dim = d;
		ma = new double[d][d];

	}

	// Add new edge with weight if the nodes are present in the graph
	public void addEdge(int o, int d,  double w) {

		if (o >= 0 && o < dim && d >= 0 && d < dim)
			ma[o][d] = w;
		else
			throw new AssertionError("node not in graph");

	}

	// Get the maximum spanning tree
	public Floresta max_spanning_tree() {
		//List of vertices to check
		Vertex[] vertex = new Vertex[dim];

		//Initialize all vertices unchecked
		for (int i = 0; i < dim; i++) {
			vertex[i] = new Vertex();
			vertex[i].checked = false;
			vertex[i].weight = Double.MIN_VALUE;
		}

		//Assign maximum weight to last vertex to start with it
		//Also make it the root (it is the class)
		vertex[dim - 1].weight = Double.MAX_VALUE;
		vertex[dim - 1].link = -1;

		for (int i = 0; i < dim - 1; i++) {

			//auxiliary variables to find the maximum weight vertex currently
			int biggest_weight_vertex = -1;
			double max_weight = Double.MIN_VALUE;

			//Find maximum weight vertex unchecked
			for (int j = 0; j < dim; j++) {

				if (!vertex[j].checked && vertex[j].weight > max_weight) {

					max_weight = vertex[j].weight;
					biggest_weight_vertex = j;
					
				}
			}

			//Check the vertex with the biggest weight
			vertex[biggest_weight_vertex].checked = true;

			//Find the connection with the biggest weight (unchecked)
			//and set the current vertex j this weight and its parent
			//is the previous biggest weight vertex
			for (int j = 0; j < dim; j++) {

				if (ma[j][biggest_weight_vertex] != 0 && !vertex[j].checked) {

					if (ma[j][biggest_weight_vertex] > vertex[j].weight) {

						vertex[j].weight = ma[j][biggest_weight_vertex];
						vertex[j].link = biggest_weight_vertex;
						
					}
				
				}
			
			}
			
		}
		
		//Create the forest with the vertices found
		Floresta floresta = new Floresta(vertex.length);
		
		for (int i = 0; i < vertex.length; i++)
			floresta.set_parent(i, vertex[i].link);
		
		return floresta;
		
	}
	
}
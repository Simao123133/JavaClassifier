import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;

public class BN implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	//Store mutual information
	double[][] It;
	//Store Nx, Ny of It (mutual information)
	ArrayList<Hashtable<Integer, Double>> ni = new ArrayList<Hashtable<Integer, Double>>();
	//Store theta -- could have used a 3D tensor, but this way the fields can be accessed easier
	ArrayList<Hashtable<String, Double>> theta = new ArrayList<Hashtable<String, Double>>();
	//Convert between non 1 domain variables and amostra variables
	ArrayList<Integer> Xi_conv;
	//Store the tree graph
	Floresta floresta;
	//First line will be used when trying an array in the 
	//Reading Gui
	int[] first_line;
	Grafoo grafoo;
	
	public BN(Amostra amostra, double S) {
		
		// This loop fills a list with the conversion between the indices in the amostra 
		// to the to be used indices in the BN network. The goal is to not consider the variables
		// with domain = 1
		Xi_conv = new ArrayList<Integer>(); 
		
		first_line = amostra.list.get(0).clone();
		
		for(int Xi = 0; Xi < amostra.list.get(0).length; Xi++) {
			
			if(amostra.domain(Xi) > 1) {
				
				Xi_conv.add(Xi);
				
			}
			
		}
		
		//Calculate Nx, Ny
		for (int i = 0; i < Xi_conv.size(); i++) {
			
			int Xi = Xi_conv.get(i);
			ni.add(new Hashtable<Integer, Double>());
			//Select all lines along the scv file
			for(int line = 0; line < amostra.list.size(); line++) {
				
					int[] vector = amostra.list.get(line);
					int element = vector[Xi];
					ni.get(i).put(element, (double) amostra.count(new int[] {Xi}, new int[] {element})/amostra.list.size());
					
			}
			
		}
		
		It = new double[ni.size()][ni.size()];
		int[] variables;
		int curr_xi;
		int curr_xj;
		int[] values;
		double xixj;
		
		//Select Xi variable up until last - 1 variable (we want combinations, not permutations)
		for (int i = 0; i < ni.size() - 1; i++) {
			//Select Xj variable, starting with the Xi variable + 1, so that same variables 
			//are not selected. 
			for(int j = i + 1; j <  ni.size(); j++) {
				
				Enumeration<Integer> xi = ni.get(i).keys();
				Enumeration<Integer> xj = ni.get(j).keys();
				
				//Select element xi of the domain of Xi 
				while (xi.hasMoreElements()) {
					
					curr_xi = xi.nextElement();
					//Select element xj of the domain of Xj
					while (xj.hasMoreElements()) {
						
						variables = new int[] {Xi_conv.get(i), Xi_conv.get(j)};
						curr_xj = xj.nextElement();
						values = new int[] {curr_xi, curr_xj};
						xixj = (double) amostra.count(variables, values)/amostra.list.size();
						
						if (xixj != 0) {
							//Expression for the mutual information
							It[i][j] = It[i][j] + xixj*Math.log(xixj/ni.get(i).get(curr_xi)/ni.get(j).get(curr_xj))/Math.log(2);
						
						}
						
					}
					
				}
				
			}
			
		}
		

		//Build the graph to be used in the Bayesian Network
		grafoo = new Grafoo(ni.size());
		
		for (int i = 0; i < ni.size() - 1; i++) {
		
			for(int j = i + 1; j < ni.size(); j++) {
					
					//Connect the edges with the calculated weights in both directions
					grafoo.addEdge(i, j, It[i][j]);
					grafoo.addEdge(j, i, It[i][j]);
			
			}
			
		}
		
		//Get the maximum spanning tree
		floresta = grafoo.max_spanning_tree();
		
		//Build the Bayesian Network
		//Go through all the nodes in the forest
		for(int Xi = 0; Xi < floresta.floresta.length; Xi++) {
			
			//Increase theta length to account for new variables
			// Theta has the tuple (xi,wi) implemented as a string
			theta.add(new Hashtable<String, Double>());
			// Get the parent Wi
			int Wi = floresta.floresta[Xi];
			//Enumerate the xi elements (ni list has them)
			Enumeration<Integer> xi = ni.get(Xi).keys();
			//Iterate through the ni list
			for (int i = 0; i < ni.get(Xi).size(); i++) {
				//Get current xi
				curr_xi = xi.nextElement();
				//If it is not the root
				if(Wi != -1) {
					//Iterate through the possible wi elements of the parent Wi
					Enumeration<Integer> wi = ni.get(Wi).keys();
					for (int j = 0; j < ni.get(Wi).size(); j++) {
						// Get the count for the condition Xi = xi and Wi = wi
						variables = new int[] {Xi_conv.get(Xi), Xi_conv.get(Wi)};
						values = new int[] {curr_xi, wi.nextElement()};
						double num = (double) amostra.count(variables, values) + S;
						//DFO with pseudo S
						double den = ni.get(Wi).get(values[1])*(double) amostra.list.size() + S*amostra.domain(Xi_conv.get(Xi));
						//Add DFO to list 
						theta.get(Xi).put(curr_xi + "," + values[1], num/den);
						
					}
				
				}
				else {
					// The root has no parents 
					// and as such the formula is a bit different
					variables = new int[] {Xi_conv.get(Xi)};
					values = new int[] {curr_xi};
					double num = (double) amostra.count(variables, values);
					double den = (double) amostra.list.size() + (double) S*amostra.domain(Xi_conv.get(Xi));
					// The second element of the tuple is -1 because the parent of the root 
					// is -1 in the forest
					theta.get(Xi).put(curr_xi + "," + "-1", num/den);
				
				}
			
			}
		
		}
		
	}
	
	public double prob(BN new_BN, int[] vector) {
		
		double Pr = 1.0;
		
		try {
			for(int Xi = 0; Xi < new_BN.Xi_conv.size(); Xi++) {
				
				//Formula 2.4.1
				if(Xi < new_BN.Xi_conv.size() - 1)
					Pr = Pr*new_BN.theta.get(Xi).get(String.valueOf(vector[new_BN.Xi_conv.get(Xi)]) + "," + String.valueOf(vector[new_BN.Xi_conv.get(new_BN.floresta.floresta[Xi])]));
				else
					Pr = Pr*new_BN.theta.get(Xi).get(String.valueOf(vector[new_BN.Xi_conv.get(Xi)]) + ",-1");
			}
		}
		catch(Exception e) {
			return -1;
		}
		
		return Pr;
		
	}
	
}
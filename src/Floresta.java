import java.util.Arrays;

public class Floresta implements java.io.Serializable{
	
	private static final long serialVersionUID = 4L;
	int[] floresta;
	
	public Floresta(int size) {
		
		floresta = new int[size]; 
		Arrays.fill(floresta, -1);
	}
	
	public void set_parent(int child, int parent) {
		
		floresta[child] = parent;
		
	}
	
	public boolean treeQ() {
		
		int root = 0;
		
		for (int i = 0; i < floresta.length; i++) {
			
			if (floresta[i] == -1){
				
				root++;
				if (root > 1) {
					
					return false;
					
				}
				
			}
			
		}
		
		if (root == 1) {
			
			return true;
		
		}
		
		return false;
			
		
	}
	
	
}

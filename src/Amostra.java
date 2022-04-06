import java.util.LinkedHashSet;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;

public class Amostra implements java.io.Serializable{

	private static final long serialVersionUID = 2L;
	ArrayList<int[]> list = new ArrayList<int[]>();
	
	public Amostra(String csvFile) {

		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		//Read the file
		try {br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {

				String[] country     = line.split(cvsSplitBy);
				int[] stringToIntVec = new int[country.length];
				for (int i = 0; i < country.length; i++)
					stringToIntVec[i] = Integer.parseInt(country[i]);	
				add(stringToIntVec);
			}

		//Catch errors
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public int length() {
		return list.size();
	}

	public void add(int[] vector) {
		list.add(vector);
	} 
	
	public int[] element(int position) {
		return list.get(position);
	}

	public int domain(int variable) {
		
		//HashSet only adds if not already present, so one can tell
		// the number of different elements in the amostra
		LinkedHashSet<Integer> hashSet = new LinkedHashSet<Integer>();
		int[] vector;
		
		for (int i=0; i < list.size(); i++) {
			vector = list.get(i);
			hashSet.add(vector[variable]);
		}
		
		return hashSet.size(); 
	}
	
	public int count(int[] variables, int[] values) {
		
		//Checking if everything is ok
		if (variables.length != values.length) {
			System.out.println("Variables and values vectors have different sizes, aborting");
			System.exit(1);
		}
		else if (variables.length > list.get(0).length) {
			System.out.println("Variables and values have longer size than the vectors of amostra, aborting");
			System.exit(-1);
		}
		
	
		int count = 0;
		
		// Iterate through amostra line by line
		for (int i = 0; i < list.size(); i++) {
			
			//Get amostra vector at line i
			int[] vector = list.get(i);
			
			//Iterate through the vector at line i
			for (int j = 0; j < variables.length; j++) {
				
				// If value of the variable in the vector is different than the
				// given values vector, go to next line because the current one 
				// does not count verify the condition
				if (vector[variables[j]] != values[j]) {
					
					break;
					
				}				
				
				// if the last iteration of the vector was reached, the condition
				// is true and the count increases
				if (j == variables.length - 1) {
					
					count++;
				}
				
			}
			
		}
		
		return count;
		
	}
	
	//Amostra print function
	@Override
	public String toString() {
		
		String s="Amostra:" + System.lineSeparator();

		if (list.size()>0);
			for (int i=0; i<list.size();i++) {
				s+= Arrays.toString(list.get(i));
				s+= System.lineSeparator();
			}
		
		return s;
	}

}



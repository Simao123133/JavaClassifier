import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.io.*;

public class CreateBNGui implements ActionListener {
    private JFrame frame = new JFrame();
    private JLabel Frequency = new JLabel("Frequencies");
    private JLabel Graph = new JLabel("Graph");
    private JLabel MaxSpanningTree = new JLabel("Max Spanning Tree");
    private JLabel Thetas = new JLabel("Thetas");
    private Amostra amostra;
    private BN bn;
    private JPanel panel = new JPanel();
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private JButton save;
    // This string "file" will save the data with the correct filename
    private String file;
    
    public CreateBNGui() {
     
        JButton button = new JButton("Open file with data");
        
        button.addActionListener(this);
        
        //Add button to save network
        save = new JButton("Save bayesian network");
        save.addActionListener(new ActionListener()
            {
              public void actionPerformed(ActionEvent e)
              {
            	if(!bn.floresta.treeQ()) {
            		
            		JOptionPane.showMessageDialog(frame, "Forest is not a tree! Don't save this",
     		               "What now", JOptionPane.ERROR_MESSAGE);
            		return;
            		
            	}
            		
            	JFileChooser jfc1 = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            	File workingDirectory = new File(System.getProperty("user.dir"));
            	jfc1.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            	jfc1.setAcceptAllFileFilterUsed(false);
            	jfc1.setCurrentDirectory(workingDirectory);
            	jfc1.setDialogTitle("Choose the directory to save the file");

           		int returnValue = jfc1.showOpenDialog(null);

           		if (returnValue == JFileChooser.APPROVE_OPTION) {
           			
           			  File selectedFile = jfc1.getSelectedFile();
           		
           			  //Get the file name when opened ex: "bcancer.csv" and remove the csv part
           			  String fileNameWithOutExt = file.replaceFirst("[.][^.]+$", "");
           			  
           			  //Save the file with .BN ending
	            	  try (FileOutputStream fos = new FileOutputStream(selectedFile.getAbsolutePath() + "\\" + fileNameWithOutExt + ".BN");
	         			     ObjectOutputStream oos = new ObjectOutputStream(fos)) {
	
	         			    // write object to file
	         			    oos.writeObject(bn);
	         			    oos.close();
	
	            	  } catch (IOException ex) {
	         				ex.printStackTrace();
	            	  }
           		}
              }
            });
        // Set border
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        
        //Add everything to layout
        panel.setLayout(new GridLayout(2, 3));
        panel.add(Frequency);
        panel.add(Graph);
        panel.add(MaxSpanningTree);
        panel.add(Thetas);
        panel.add(button);
        panel.add(save);
        
        // set up the frame and display it
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("GUI");
        frame.pack();
        frame.setVisible(true);
        frame.setExtendedState( frame.getExtendedState()|JFrame.MAXIMIZED_BOTH);
        
    }
    
    //Auxiliary function to print an Hashtable correctly
    public String hashtable_to_string(Hashtable<String, Double> hashtable) {
    	
    	Enumeration<Double> values = hashtable.elements();
    	Enumeration<String> keys = hashtable.keys();
    	Enumeration<String> keys2 = hashtable.keys();
    	keys2.nextElement();
    	
    	String S = "{";
    	// Iterate through last element -1 to correctly finish with a "}" without ","
    	while( keys2.hasMoreElements() ){
    	    S = S + "{" + keys.nextElement() + " : " + df.format(values.nextElement()) + "}, ";
    	    keys2.nextElement();
    	}
    	
    	 S = S + "{" + keys.nextElement() + " : " + df.format(values.nextElement()) + " }";
    	
    	return S + "}";
    	
    }
    
    //Callback to get amostra.csv file
    public void actionPerformed(ActionEvent e) {
    	
    
    	 JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
    	 jfc.setCurrentDirectory(new java.io.File("."));
    	 jfc.setDialogTitle("Choose file");

 		int returnValue = jfc.showOpenDialog(null);

 		if (returnValue == JFileChooser.APPROVE_OPTION) {
 			File selectedFile = jfc.getSelectedFile();
 			//THis file will be used to save the bayesian network with this name
 			file = selectedFile.getName();
 			try{
 				amostra = new Amostra(selectedFile.getAbsolutePath());
	 		}catch (Exception ex1) {
				 JOptionPane.showMessageDialog(frame, "Wrong file!",
			               "Wrong file", JOptionPane.ERROR_MESSAGE);
				 return;
		    }
 		}
 		
 		//Try bn; if something does not work,
 		//Report error
 		
 			
		//Get the bayesian network	
 		bn = new BN(amostra, 50.0);
 		String S = "<html> Frequencies <br>";
 		
 		//Get the Frequency of appearance of the elements
 		ArrayList<Hashtable<Integer, Double>> ni = new ArrayList<Hashtable<Integer, Double>>();
		for (int i = 0; i < amostra.list.get(0).length; i++) {
					
			ni.add(new Hashtable<Integer, Double>());
			//Select all lines along the scv file
			for(int line = 0; line < amostra.list.size(); line++) {
				
					int[] vector = amostra.list.get(line);
					int element = vector[i];
					ni.get(i).put(element, (double) amostra.count(new int[] {i}, new int[] {element})/amostra.list.size());
					
			}
			
		}
 		
		//Print the Frequency of appearance of the elements
		for (int i = 0; i < ni.size(); i++) {
			
			S = S + "Xi = " + i + ": " + ni.get(i) + "<br>";
			
		}
		Frequency.setText(S + "</html>");
		
		S = "<html> Adjacency matrix of the weighted graph with mutual information <br>";
		S = S + "The variables X with domain 1 were discarded<br>--";
		
		for(int i = 0; i < bn.ni.size(); i++) {
			
			S = S + "---X" + bn.Xi_conv.get(i);
			
		}
		
		S = S + "<br>";
		
		//Print the Graph
		for(int i = 0; i < bn.ni.size(); i++)
		{
			
			S = S + "X" + bn.Xi_conv.get(i) + " ";
		    for(int j = 0; j < bn.ni.size(); j++)
		    {
		       S = S +  df.format(bn.grafoo.ma[i][j]) + " ";
		    }
		    S = S + "<br>";
		}
		
		Graph.setText(S + "<html>");
		
		S = "<html> Max Spanning Tree: <br>";
		S = S + "Each index is a node, the position v[i] represents the parent and -1 is the root (class) <br>";
		
		MaxSpanningTree.setText(S + Arrays.toString(bn.floresta.floresta) + "<html>");
		
		if(!bn.floresta.treeQ())
			JOptionPane.showMessageDialog(frame, "Forest is not a tree!",
		               "What now", JOptionPane.ERROR_MESSAGE);
		
		S = "<html> Thetas of the Bayesian Network <br> Structure: {xi,wi : theta(Xi,xi,wi)} <br>";
		
		for(int i = 0; i < bn.ni.size(); i++)
		{
			
			S = S + "X" + bn.Xi_conv.get(i) + ": " + hashtable_to_string(bn.theta.get(i)) + "<br>";
			
		}
		
		Thetas.setText(S + "<html>");
 		
 			
    }
}
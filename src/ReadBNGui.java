import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Enumeration;
import java.io.*;

public class ReadBNGui implements ActionListener {
    private JFrame frame = new JFrame();
    private JLabel Probability = new JLabel("Probability");
    private JLabel Variables = new JLabel("Variables");
    private BN bn;
    private JPanel panel = new JPanel();
    JTextField textField;
    
    public ReadBNGui() {
     
    	//Create new open file button
        JButton button = new JButton("Open file with Bayesian Network");
        textField = new JTextField(20);
        
        //Add callback
        button.addActionListener(this);
        
        textField.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
        	  //Get array
        	  String text = textField.getText();
        	  String[] parts = text.split(",");
 
        	  int[] array = new int[parts.length + 1];
        	  
        	  for (int i = 0; i < parts.length; i++) {
                  array[i] = Integer.parseInt(parts[i]);
              }
        	  //Calculate probability
        	  double prob = 0;
        	  double max_prob = 0; 
        	  int max_prob_wi = 0;
        	  int curr_wi;
        	  
        	  Enumeration<Integer> wi = bn.ni.get(bn.ni.size()-1).keys();
        	  while(wi.hasMoreElements()) {
        		  curr_wi = wi.nextElement();
        		  array[array.length-1] = curr_wi;
        		  prob = bn.prob(bn, array);
        		  if(prob > max_prob) {
        			  
        			   max_prob = prob;
        			   max_prob_wi = curr_wi;
        		  }
        			   
        	  }
        	  
        	  if (prob != -1) {
        		  Probability.setText("Classification = " + Integer.toString(max_prob_wi));
        	  }
        	  else //In case the inserted number in any variable does not exist
        		  Probability.setText("Variable not found"); 
        	  	
        	  	
          }
        });
        //Create border
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        
        //Add grid layout, labels and buttons
        panel.setLayout(new GridLayout(2, 2));
        panel.add(Variables);
        Variables.setText("X1, ..., Xn (After loading data, press enter)");
        panel.add(Probability);
        panel.add(textField);
        
        panel.add(button);
        
        // set up the frame and display it
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("GUI");
        frame.pack();
        frame.setVisible(true);
        
    }
      
    //Open the file with the .csv data
    public void actionPerformed(ActionEvent e) {
    	
    	 JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
    	 jfc.setCurrentDirectory(new java.io.File("."));
    	 jfc.setDialogTitle("Choose file");

 		int returnValue = jfc.showOpenDialog(null);	

 		if (returnValue == JFileChooser.APPROVE_OPTION) {
 			File selectedFile = jfc.getSelectedFile();
 			
 			 try {
 				 FileInputStream streamIn = new FileInputStream(selectedFile.getAbsolutePath());
 			     ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);
 			     //Get the bayesian network
 			     bn = (BN) objectinputstream.readObject();
 			     
 			     String S = "";
 		 		 //Initialize the input field of the amostra vector
 			     //with the first line in the .csv filet
 			     //to let the user know the syntax	     			     
 		 		 for(int i = 0; i < bn.first_line.length - 2; i++)
 		 			 
 		 		 	 S = S + bn.first_line[i] + ",";
 		 		 
 		 		 textField.setText(S + bn.first_line[bn.first_line.length - 2]);
 		 		 //Calculate probability
 	        	  double prob = 0;
 	        	  double max_prob = 0; 
 	        	  int max_prob_wi = 0;
 	        	  int curr_wi;
 	        	  
 	        	  //Iterate through possible classes
 	        	  Enumeration<Integer> wi = bn.ni.get(bn.ni.size()-1).keys();
 	        	  //The biggest probability is the correct classification
 	        	  while(wi.hasMoreElements()) {
 	        		  curr_wi = wi.nextElement();
 	        		  bn.first_line[bn.first_line.length-1] = curr_wi;
 	        		  prob = bn.prob(bn, bn.first_line);
 	        		  if(prob > max_prob) {
 	        			   max_prob = prob;
 	        			   max_prob_wi = curr_wi;
 	        			   
 	        			  
 	        		  }
 	        			   
 	        	  }
 		 		 
 		 		 Probability.setText("Classification = " + Integer.toString(max_prob_wi));
 			 //Catch any error
 			 } catch (Exception e1) {
 			     e1.printStackTrace();
 			    JOptionPane.showMessageDialog(frame, "Wrong file!",
			               "Wrong file", JOptionPane.ERROR_MESSAGE);
				 return;
 			 }
 			
 		}
 	
 		

    }
}
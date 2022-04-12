import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

public class ImageProcessor {
	private static ArrayList<ArrayList<Pixel>> graph; //graph representation, an arraylist of an arraylist of columns
	private static ArrayList<ArrayList<Integer>> I; //importance matrix for graph
	private static int height;    // the height of the image
	private static int width;     // the width of the image
	private File file;            // store the name of the file	
			
	public ImageProcessor(String FName) throws IOException
	{
		file = new File(FName);
		graph = new ArrayList<ArrayList<Pixel>>();			
		I = new ArrayList<ArrayList<Integer>>();	
		createGraph(file);
	}
				
	// as reading the file, put the node into the graph and make sure to mark b,g,r and position i,j and calculate the importance and set the node importance 
	// calculate the importance after all of the nodes are done 
	private void createGraph(File file) throws IOException
	{
		Scanner s = new Scanner(file);    
		height = s.nextInt(); //first line contains the height H of the image     
		width = s.nextInt();  //second line contains the width W of the image
		int row = 0;          // the counter increased by one after each while loop is done to keep track of the rows
		
		while(s.hasNextLine()){	
			ArrayList<Pixel> columns = new ArrayList<Pixel>();
			for(int column = 0; column < width; column++){					
				int r = s.nextInt(); 
				int g = s.nextInt();
				int b = s.nextInt();				
				Pixel temp = new Pixel(r, g, b, row, column);  //create a new pixel					
				columns.add(column, temp);				
			}			
			graph.add(columns);

			row++; //current row is filled, go to next row
		}
		s.close(); //close scanner								
		
		this.updateImportance(); //set the importance for all the pixels
	}	
		
	// method loops through the entire graph and 
	// updates all of the importance values for each node and sets the new importance value 	
	private static void updateImportance()
	{				
		for(int i = 0; i < height; i++){          //loops through all rows		
			for(int j = 0; j < width; j++){       //loops through all columns			
				Pixel temp = graph.get(i).get(j); //temporary pixel				
				temp.setImportance(importance(i, j));	
				
				//set edges for each pixel
				if(i != height - 1) {
					temp.addEdge(graph.get(i + 1).get(j), graph.get(i + 1).get(j).getImportance()); //straight down
					if(j != 0) {
						temp.addEdge(graph.get(i + 1).get(j - 1), graph.get(i + 1).get(j - 1).getImportance()); //down to the left
					}				
					if(j != width - 1) {
						temp.addEdge(graph.get(i + 1).get(j + 1), graph.get(i + 1).get(j + 1).getImportance()); //down to the right
					}
				}				
			}
		}				
	}			
			
	// Compute Importance matrix: The matrix I capturing the importance values for each element in M
	// returns the 2-D matrix I as per its definition
	public static ArrayList<ArrayList<Integer>> getImportance()
	{			
		for(int i = 0; i < height; i++) {         //loops through all the rows	
			ArrayList<Integer> columnsForI = new ArrayList<Integer>();
			for(int j = 0; j < width; j++) {      //loops through all the columns			
				Pixel temp = graph.get(i).get(j); //temporary pixel
				columnsForI.add(j, (int) temp.getImportance()); //add pixel to the current column 
				I.add(i, columnsForI); //add all the importance values for the first row					
			}
		}
		return I;
	}			
		
	// this helper method helps to calculate the total importance of a node 
	// input: M[i, j] 
	private static int importance(int i, int j){
		return xImportance(i, j) + yImportance(i, j);
	}
	
	// this helper method helps to calculate YImportance of a node 
	// input: M[i, j]
	private static int yImportance(int row, int col)
	{
		int i = row;
		int j = col;
		if(i == 0){
			Pixel up = graph.get(height - 1).get(j);
			Pixel down = graph.get(i + 1).get(j);
			return pDist(up,down);
		}
		if(i == height - 1){
			Pixel up = graph.get(i - 1).get(j);
			Pixel down = graph.get(0).get(j);
			return pDist(up,down);
		}
		else{
			Pixel up = graph.get(i - 1).get(j);
			Pixel down = graph.get(i + 1).get(j);
			return pDist(up,down);
		}
	}
	
	// this helper method helps to calculate XImportance of a node 
	// input: M[i, j]
	private static int xImportance(int row, int col)
	{
		int i = row;
		int j = col;
		if(j == 0){
			Pixel left = graph.get(i).get(width - 1);
			Pixel right = graph.get(i).get(j + 1);
			return pDist(left,right);
		}
		if(j == width - 1){
			Pixel left = graph.get(i).get(j - 1);
			Pixel right = graph.get(i).get(0);
			return pDist(left,right);
		}
		else{
			Pixel left = graph.get(i).get(j-1);
			Pixel right = graph.get(i).get(j+1);
			return pDist(left,right);
		}
	}
		
	private static int pDist(Pixel neighbour_1, Pixel neighbour_2)
	{
		int r1 = neighbour_1.getR();
		int r2 = neighbour_2.getR();
		int g1 = neighbour_1.getG();
		int g2 = neighbour_2.getG();
		int b1 = neighbour_1.getB();
		int b2 = neighbour_2.getB();
		return (int) (Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2));
	}	
	
	// Computes the new image matrix after reducing the width by k
	// read the file
	private static void writeReduced(int k, String FName) throws IOException
	{		
		PrintStream ps = null;
		File file = new File(FName); //output file
		
		try {
   	 		ps = new PrintStream(file);
	 	} 
	 	catch (IOException e1) {	 		
	 		e1.printStackTrace();
	 	}
		
		for(int i = 0; i < k; i++) {  // make k cuts					
			MinVC(I);
		}
		ps.println(height);
		ps.println(width);
		for(int i = 0; i < height; i++) {         //loops through all the rows					
			for(int j = 0; j < width; j++) {      //loops through all the columns																			
				ps.print(graph.get(i).get(j).getR() +" "+ graph.get(i).get(j).getG() +" "+ graph.get(i).get(j).getB() +" ");
			}	
			ps.println(" ");
		}
		ps.close();					
	}
		
	private static void MinVC(ArrayList<ArrayList<Integer>> i2) //takes in I which is Type Integer
	{						
		ArrayList<Pixel> s1 = new ArrayList<Pixel>();
		ArrayList<Pixel> s2 = new ArrayList<Pixel>();
		
		for(int i = 0; i < width; i++){ //nodes in S1
			Pixel source = graph.get(0).get(i);
			s1.add(source);
		}
		for(int i = 0; i < width; i++){ //nodes in S2
			Pixel destination = graph.get(height - 1).get(i);
			s2.add(destination);			
		}		
		
		//make path needing to be removed: j value of pixel needing to be removed
		ArrayList<Integer> path = S2S(s1, s2); //run S2S from the first to last rows					
		
		//remove the pixels from graph in path
		for(int i = height - 1, j = 0; i >= 0; i--, j++) {
			System.out.print(path.get(i) +" " );
			graph.get(j).remove(graph.get(j).get(path.get(i)));
		}
		
		width--; //a column of least importance has been taken out
		updateImportance(); //update the new graph
	}
	
	//Computes the Min-Cost vertical cut using the S2S method from Q1
	//Given two sets of vertices S1 and S2, 
	//finds the shortest path from some vertex in S1 to some vertex in S2
	public static ArrayList<Integer> S2S(ArrayList<Pixel> s1, ArrayList<Pixel> s2)
	{				
		Pixel source = s1.get(0);			 
		
		ArrayList<Integer> shortestPath = new ArrayList<Integer>(); //list of column coordinate		
		Set<Pixel> visited = new HashSet<Pixel>();    //set contains nodes included in shortest path
		Queue<Pixel> pq = new PriorityQueue<Pixel>(); //use queue to keep track of pixels still needing visited  
		pq.add(source);	
		ArrayList<Integer> Path = new ArrayList<Integer>();
		int cost = Integer.MAX_VALUE;
		int tempCost = Integer.MAX_VALUE;
	    
	    while(!pq.isEmpty()) {  //while visited doesn't include all pixels
	    	Pixel u = pq.poll(); // u = node in priority queue with min importance
	    	visited.add(u);     // mark u as visited

	    	if(s1.contains(u)) { s1.remove(u); } //keeps track of if all pixel in S1 has been visited	 	    	
	    	
	    	if(s2.contains(u)){ //all pixels in S1 have been visited and u is in S2    		
    			tempCost = 0;
    			tempCost = (int) u.getImportance();
    			while(u != null){	  
    				shortestPath.add(u.getCol());
    				u = u.getParent();	
    			}
    			System.out.println(shortestPath.toString()+"\n");
	    		if(tempCost < cost) {
	    			cost = tempCost;
	    			Path = shortestPath;
	    		}
	    		shortestPath = new ArrayList<Integer>();
	    	}
	    	else {	   
	    		//for all u -> v.....update importance of the neighbors(v) of the picked node(u)
    			for(Entry<Pixel, Integer> v : u.getEdges().entrySet()) {	    	
    				Pixel neighbor = v.getKey();	   	    		
    				int alt = u.getImportance() + v.getValue(); //importance of u + edge weight  
    			
    				if(!visited.contains(neighbor) && alt < neighbor.getImportance()) { //A shorter path to v has been found ???
    					neighbor.setImportance(alt);   				
    					neighbor.setParent(u); //u is the parent of neighbor(v)
    					pq.add(neighbor); 
    				}
    			}
    		}
    			    	
    		//if there are no edges for u and there is still a pixel in S1 that hasn't been visited
    		if(pq.isEmpty() && !s1.isEmpty()){
    			Pixel temp = s1.iterator().next();
    			pq.add(temp); //visit the next unvisited pixel in set S1
    		}    		
	    }
	    
	    System.out.println(Path.toString());
	    return Path;	
	}		
	
	private class Pixel implements Comparable {
		private int r;              //red
		final private int g;        //green
		final private int b;        //blue
		private int row;
		private int column;			//column 
		private int importance;  //the importance of the pixel 				
		private Map<Pixel, Integer> edges = new HashMap<>(); //used to keep track of this nodes edges
		private Pixel parent;
		
		public Pixel(int R, int G, int B, int Row, int C){
			r = R;
			g = G;
			b = B;
			row = Row;
			column = C;
			importance = 0;  // Initially the importance of a node is 0, can be set with the set method 
		}
		
		// adds edge between source and destination with a weight
	    public void addEdge(Pixel destination, int weight){
	    	edges.put(destination, weight);  
	    }	    
	    public Map<Pixel, Integer> getEdges(){
	    	return edges;
	    }
		
	    //get row/column of this pixel
	    public int getCol() {
	    	return column;
	    }
	    public int getRow() {
	    	return row;
	    }
	    
	    //Get and set parent
	    public Pixel getParent() { return parent; }
	    public void setParent(Pixel parent){ this.parent = parent; }
	    
		private int getR() { return r; }		
		private int getG() { return g; }		
		private int getB() { return b; }				
		
		private int getImportance() { return importance;}		
		private void setImportance(int importanceNum) { importance = importanceNum; }

		@Override
		public int compareTo(Object arg0) {
			return Integer.compare(this.getImportance(), ((Pixel) arg0).getImportance());
		}					
	}
	
	
	
	public static void main(String[] args) throws IOException{
		String file = "10x10Input.txt";		
		ImageProcessor ip = new ImageProcessor(file);	
				
		//getImportance();		

		writeReduced(1, "10x10Output.txt");
	}						
}

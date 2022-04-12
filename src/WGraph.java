import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

public class WGraph {
//1.	
	private File file;
	private int vertices;
	private int edges;
	public Map<String, Node> graph = new HashMap<>(); 	
	
	//read the file with the name FName from the same directory 
	//and create a graph representation from the file data	
	public WGraph(String FName) throws FileNotFoundException
	{
		file = new File(FName);		
		createGraph(file);		
	}
	
	// helper method to make the graph's adjacency list
	private void createGraph(File file) throws FileNotFoundException 
	{    
		Scanner s = new Scanner(file); //used to parse through the given file				
		vertices = s.nextInt();        // first line number indicates number of vertices
		edges = s.nextInt();           // second line number indicated number of edges
		
		while(s.hasNextLine()) {    
			//source vertex coordinates
			int ux = s.nextInt();
			int uy = s.nextInt();
			//destination vertex coordinates
			int vx = s.nextInt();
			int vy = s.nextInt();
			//weight of the edge connecting the source to destination
			int weight = s.nextInt();
			//create the nodes on this line
			Node source = new Node(ux, uy);
			Node destination  = new Node(vx, vy);			
			//add source node to the graph if it isn't already in
			if(!graph.containsKey(source.getName())){
				graph.put(source.getName(), source);				
			}
			//add destination node to graph if it isn't already in
			if(!graph.containsKey(destination.getName())){
				graph.put(destination.getName(), destination);				
			}
			//give the source its new edge with weight
			graph.get(source.getName()).addEdge(destination, weight);						
		}		
		s.close(); //close scanner
	}
		
	// new custom node class 
	private class Node implements Comparable <Node>
	{
		private final String Name;     
		private final int X; 
		private final int Y;	
		private int distance = Integer.MAX_VALUE; //intitialze each node's distance as max
	    private Map<Node, Integer> edges = new HashMap<>(); //used to keep track of this nodes edges
	    private Node parent;
	    
	    public Node(int x, int y){
	    	X = x;
	    	Y = y;
	    	Name = "(" + X + ", "+ Y + ")";  
	    }
	    public String getName(){
	    	return Name;
	    }
	    
	    // adds edge between source and destination with a weight
	    public void addEdge(Node destination, int weight){
	    	edges.put(destination, weight);  
	    }	    
	    public Map<Node, Integer> getEdges(){
	    	return edges;
	    }
	    
	    //Get and set distance
	    public int getDistance(){
	    	return distance;
	    }	    	    
	    public void setDistance(int distance){
	    	this.distance = distance;
	    }
	    
	    //Get and set parent
	    public Node getParent(){
	    	return parent;
	    }
	    public void setParent(Node parent){
	    	this.parent = parent;
	    }
	    
	    @Override
		public int compareTo(Node o) {
			return Integer.compare(this.getDistance(),o.getDistance());
		}	    		    	    	    	    	    	    
	}
	
//2.____________________________________________________________________________	
	//Given valid coordinates for vertices u and v, 
	//finds the shortest path from u to v
	public ArrayList<Integer> V2V(int ux, int uy, int vx, int vy) //Dijkstra’s modifiction
	{
		Node source = graph.get("(" + ux + ", " + uy + ")"); //source	
		if(source == null) {
			return new ArrayList<Integer> ();
		}
		String destination = "(" + vx + ", " + vy + ")"; //String used to check if reached destination		
		source.setDistance(0); //assign distance value as 0 for source		 
		
		ArrayList<Integer> shortestPath = new ArrayList<Integer>(); //result		
		Set<Node> visited = new HashSet<Node>();    //set contains nodes included in shortest path
		Queue<Node> pq = new PriorityQueue<Node>(); //use queue to keep track of nodes still needng visited  
	    pq.add(source);	        
	    
	    while(!pq.isEmpty()) {  //while visited doesn't include all nodes
	    	Node u = pq.poll(); // u = node in priority queue with min distance
	    	visited.add(u);     // mark u as visited
	    	
	    	if(u.getName().equals(destination)){ //made it to destination
	    		if(source.getName().equals(destination) || u.getParent() != null){
	    			int front = 0;
	    			while(u != null){	    				
	    				shortestPath.add(front, u.Y);
	    				shortestPath.add(front, u.X);
	    				u = u.getParent();
	    			}
	    			break;
	    		}
	    	}
	    	
    		//for all u -> v.....update distance of the neighbors(v) of the picked node(u)
    		for(Map.Entry<Node, Integer> v : graph.get(u.getName()).getEdges().entrySet()) {	    	
    			Node neighbor = v.getKey();
    			int alt = u.getDistance() + v.getValue(); //distance of u + edge weight  
		
    			if (!visited.contains(neighbor) && alt < neighbor.getDistance()) {
    				neighbor.setDistance(alt);
    				neighbor.setParent(u); //u is the parent of neighbor(v)   				
    				pq.add(neighbor);   				
    			}
    		}	    		    		    	
	    }
	    return shortestPath;		
	}
	
	//Given valid coordinates for u and a set of vertices S (contatins even number of intergers),
	//finds the shortest path from u to some vertex in S
	public ArrayList<Integer> V2S(int ux, int uy, ArrayList<Integer> S)
	{
		Node source = graph.get("(" + ux + ", " + uy + ")"); //source
		if(source == null) {
			return new ArrayList<Integer> ();
		}
		source.setDistance(0); //assign distance value as 0 for source		 
		Set<Node> nodesInS = new HashSet<Node>();

		for(int i = 0; i < S.size(); i = i+2){
			int vx = S.get(i);
			int vy = S.get(i+1);
			Node destination = graph.get("(" + vx + ", " + vy + ")");
			nodesInS.add(destination);
		}
		
		ArrayList<Integer> shortestPath = new ArrayList<Integer>(); //result		
		Set<Node> visited = new HashSet<Node>();    //set contains nodes included in shortest path
		Queue<Node> pq = new PriorityQueue<Node>(); //use queue to keep track of nodes still needng visited  
	    pq.add(source);	        
	    
	    while(!pq.isEmpty()) {  //while visited doesn't include all nodes
	    	Node u = pq.poll(); // u = node in priority queue with min distance
	    	visited.add(u);     // mark u as visited	
	    	
	    	if(nodesInS.contains(graph.get(u.getName()))){ //made it to destination

	    		if(nodesInS.contains(source) || u.getParent() != null){	    			
	    			int front = 0;
	    			while(u != null){	    				
	    				shortestPath.add(front, u.Y);
	    				shortestPath.add(front, u.X);
	    				u = u.getParent();
	    			}
	    			break;
	    		}	    		
	    	}
	    	
    		//for all u -> v.....update distance of the neighbors(v) of the picked node(u)
    		for(Map.Entry<Node, Integer> v : graph.get(u.getName()).getEdges().entrySet()) {	    	
	    		Node neighbor = v.getKey();
    			int alt = u.getDistance() + v.getValue(); //distance of u + edge weight  
    		
    			if(!visited.contains(neighbor) && alt < neighbor.getDistance()) {
    				neighbor.setDistance(alt);
    				neighbor.setParent(u); //u is the parent of neighbor(v)
    				pq.add(neighbor);		                
    			}
    		}	    	
	    }
	    return shortestPath;		
	}
	
	//Given two sets of vertices S1 and S2, 
	//finds the shortest path from some vertex in S1 to some vertex in S2
	public ArrayList<Integer> S2S(ArrayList<Integer> S1, ArrayList<Integer> S2)
	{		
		Set<Node> nodesInS1 = new HashSet<Node>();
		Set<Node> nodesInS2 = new HashSet<Node>();
		
		for(int i = 0; i < S1.size(); i = i+2){ //nodes in S1
			Node source = graph.get("(" + S1.get(i) + ", " + S1.get(i+1) + ")");
			nodesInS1.add(source);
		}

		for(int i = 0; i < S2.size(); i = i+2){ //nodes in S2
			int vx = S2.get(i);
			int vy = S2.get(i+1);
			Node destination = graph.get("(" + vx + ", " + vy + ")");
			nodesInS2.add(destination);			
		}
		
		Node source = graph.get("(" + S1.get(0) + ", " + S1.get(1) + ")"); //source		
		source.setDistance(0); //assign distance value as 0 for source		 
		
		ArrayList<Integer> shortestPath = new ArrayList<Integer>(); //result		
		Set<Node> visited = new HashSet<Node>();    //set contains nodes included in shortest path
		Queue<Node> pq = new PriorityQueue<Node>(); //use queue to keep track of nodes still needng visited  
	    pq.add(source);	    
	    int count = nodesInS1.size();
	    
	    while(!pq.isEmpty()) {  //while visited doesn't include all nodes
	    	Node u = pq.poll(); // u = node in priority queue with min distance
	    	visited.add(graph.get(u.getName()));     // mark u as visited
	    	
	    	if(nodesInS1.contains(u) && nodesInS2.contains(u)){ //if node is in both S1 and S2
	    		shortestPath.add(0, u.X);
				shortestPath.add(1, u.Y);
				return shortestPath;
	    	}
	    	if(nodesInS1.contains(graph.get(u.getName()))) { count--; } //keeps track of if all nodes in S1 has been visited	 	    	
	    	
	    	if(count == 0 && nodesInS2.contains(graph.get(u.getName()))){ //all nodes in S1 have been visited and u is in S2    		
	    		if(u.getParent() != null){	
	    			int front = 0;
	    			while(u != null){	    				
	    				shortestPath.add(front, u.Y);
	    				shortestPath.add(front, u.X);
	    				u = u.getParent();
	    			}  			
	    		}
	    		break;
	    	}	    		
    		    		    	
    		//for all u -> v.....update distance of the neighbors(v) of the picked node(u)
    		for(Map.Entry<Node, Integer> v : graph.get(u.getName()).getEdges().entrySet()) {	    	
	    		Node neighbor = v.getKey();	   	    		
    			int alt = u.getDistance() + v.getValue(); //distance of u + edge weight  
    			
    			if(!visited.contains(neighbor) && alt < neighbor.getDistance()) { //A shorter path to v has been found
    				neighbor.setDistance(alt);
    				neighbor.setParent(u); //u is the parent of neighbor(v)
    				if(nodesInS1.contains(graph.get(neighbor.getName()))){ //the neighbor is also in S1, update tracked distance
    					neighbor.setParent(null);
    				}
    				pq.add(neighbor); 
    			}
    		}
    		//if there are no edges for u and there is still a node in S1 that hasn't been visited
    		if(pq.isEmpty() && count > 0){
    			Node temp = nodesInS1.iterator().next();
    			pq.add(temp); //visit the next unvisited node in set S1
    		}    		
	    }
	    return shortestPath;	
	}		
	
}

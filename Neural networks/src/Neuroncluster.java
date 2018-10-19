import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

import javax.swing.JFrame;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.ListenableGraph;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.alg.shortestpath.KShortestSimplePaths;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.generate.GnmRandomGraphGenerator;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.generate.LinearGraphGenerator;
import org.jgrapht.generate.SimpleWeightedGraphMatrixGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.jgrapht.util.SupplierUtil;

public class Neuroncluster {


	private static final Random GENERATOR = new Random(17);
	DirectedAcyclicGraph<Integer, DefaultWeightedEdge> jgraph;
	public List<Integer> tsort = new ArrayList<Integer>();


	public Neuroncluster(int n) {
		//Create network of n*n matrix
		Supplier<Integer> vSupplier = new Supplier<Integer>()
		{
			private int id = 1;

			@Override
			public Integer get()
			{
				return id++;
			}
		};


		jgraph = new DirectedAcyclicGraph<>(vSupplier,SupplierUtil.createDefaultWeightedEdgeSupplier(), true);


		double[][] weights = new double [n][n];

		for (int k=0;k<n;k++) {
			for(int l=0;l<n;l++) {
				double y = (double) GENERATOR.nextDouble();
				//				System.out.println(y);
				weights[k][l] = y;

			}
		}

		//		SimpleWeightedGraphMatrixGenerator<String, DefaultWeightedEdge> completeGenerator =
		//	            new SimpleWeightedGraphMatrixGenerator();
		GraphGenerator<Integer, DefaultWeightedEdge, Integer> gen = new GnmRandomGraphGenerator<>(n, n);



		gen.generateGraph(jgraph);
		//		completeGenerator.vertices();

		for (DefaultWeightedEdge e : jgraph.edgeSet()) {
			jgraph.setEdgeWeight(e, (double) GENERATOR.nextDouble());
		}

		Iterator<Integer> iter = new DepthFirstIterator<>(jgraph);
		while (iter.hasNext()) {
			Integer vertex = iter.next();
			Set<DefaultWeightedEdge> set = jgraph.incomingEdgesOf(vertex);
			for(DefaultWeightedEdge i : set) {

				System.out.println("Vertex " + vertex + " gets connection from: "+jgraph.getEdgeSource(i) +" with weight: "+jgraph.getEdgeWeight(i));
			}
		}

		tsort.addAll(topologicalSort());

	}


	public List<Integer> topologicalSort() {
		TopologicalOrderIterator<Integer, DefaultWeightedEdge> orderIterator;
		orderIterator = new TopologicalOrderIterator<Integer, DefaultWeightedEdge>(jgraph);

		List<Integer> output = new ArrayList<Integer>(jgraph.vertexSet().size());

		while (orderIterator.hasNext()) {
			int k = orderIterator.next();
			output.add(k);
		}

		System.out.println(output);	

		return output;
	}

	public Set<Integer> getVertices(){
		return jgraph.vertexSet();
	}

	public ArrayList<ArrayList<Integer>> getAllGraphs( ) {

		//Get all sources and destinations


		List<Integer> noIncoming = new ArrayList<Integer>();
		List<Integer> noOutgoing = new ArrayList<Integer>();
		for(int i=0;i<tsort.size();i++) {
			if(jgraph.incomingEdgesOf(tsort.get(i)).isEmpty()) {
				noIncoming.add(tsort.get(i));
			}
		}

		for(int i=0;i<tsort.size();i++) {
			if(jgraph.outgoingEdgesOf(tsort.get(i)).isEmpty()) {
				noOutgoing.add(tsort.get(i));
			}
		}
		ArrayList<ArrayList<Integer>> arr = new ArrayList<ArrayList<Integer>>();

		//find the list of all graphs
		for(int k=0;k<noIncoming.size();k++) {
			for(int m=0;m<noOutgoing.size();m++) {
				try {
					System.out.println("Finding graphs between vertices :"+noIncoming.get(k)+","+noOutgoing.get(m));
					List<GraphPath<Integer, DefaultWeightedEdge>> paths = getKshortestPaths(noIncoming.get(k),noOutgoing.get(m));
					for(GraphPath i:paths) {
						System.out.println(i.getVertexList().toString());
						arr.add((ArrayList<Integer>) i.getVertexList());
					}

				}catch(IllegalArgumentException e ) {
					System.out.println(e.getMessage());
				}
			}
		}
		return arr;

		
	}

	public double getEdgeWeight(int sourceVertex,int targetVertex) {
//		System.out.println("Get weight of edge b/w "+sourceVertex+","+targetVertex);
		DefaultWeightedEdge e = jgraph.getEdge(sourceVertex, targetVertex);
		double weight = jgraph.getEdgeWeight(e);

		return weight;
	}

	private List<GraphPath<Integer, DefaultWeightedEdge>> getKshortestPaths(Integer source, Integer destination) {


		KShortestSimplePaths<Integer, DefaultWeightedEdge> pathInspector = 
				new KShortestSimplePaths<Integer, DefaultWeightedEdge>(jgraph);


		List<GraphPath<Integer, DefaultWeightedEdge>> paths = pathInspector.getPaths(source,destination,1000);

		return paths;	

	}





	//for every input,calculate the membrane potential of each node using the weights
	//determine the membrane potential of every node in the cluster for each input,
	//get list of all activated neurons ??
	// Use topological sort to determine activation path

	public static void main(String[]args) {
		Neuroncluster n = new Neuroncluster(5);
		Neuron [] neurons = new Neuron[5];
		for(int i=0;i<neurons.length;i++) {
			neurons[i] = new Neuron();
			neurons[i].neuronId=i;
		}
		List<Integer> sorted = n.tsort;


		ArrayList<Float> input = new ArrayList<Float>();
		ArrayList<Float> output = new ArrayList<Float>();

		ArrayList<ArrayList<Integer>>graphs = n.getAllGraphs();

		//For each graph, check if last neuron is activated
		for(ArrayList<Integer> arraylist:graphs) {

			//For each graph, generate 100 bin inputs
			for (int j=0; j<100; j++)
			{
				float r;
				float x = (float) Math.random();
				//				    	System.out.println("x is:"+x );
				if(x < 0.5) {
					r=(float)0.0;
					input.add(r);


				} else {
					r=(float) 1.0;
					input.add(r);

				}
				Boolean graphActivated = false;
				//For jth input, verify that last neuron of graph is activated
				for(int i=0;i<arraylist.size();i++) {

					if(neurons[i].activation(r)) {
						graphActivated = true;
					}else {
						graphActivated = false;
						break;
					}
					//If current neuron is activated, send a signal weight*neurons[i].currVal to next neuron
					if(i<(arraylist.size()-1)) {
						double weight = n.getEdgeWeight(arraylist.get(i),arraylist.get(i+1));
						neurons[i+1].currVal= neurons[i+1].currVal+ (float) weight*neurons[i].currVal;
					}
				}
				if(graphActivated) {
					System.out.println("Graph:"+arraylist.toString()+"is activated for "+ j+"th input" );
				}

			}

		}
	}

}





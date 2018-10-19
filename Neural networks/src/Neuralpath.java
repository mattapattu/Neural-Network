import java.util.ArrayList;


public class Neuralpath {
	
	public ArrayList<Integer> pathOfNeurons;
	public Boolean pathActivated = true;
	public Integer pathId;
	
	Neuralpath(ArrayList<Integer> neuralpath) {
		
//		for (int i:neuralpath) {
//			Neuron n = new Neuron(neuralpath.get(i));
//			neuralPath.add(n);
//		}
		pathOfNeurons=new ArrayList<Integer>();
		pathOfNeurons.addAll(neuralpath);
		
		
	}
	
	
}

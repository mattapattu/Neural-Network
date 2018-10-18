import java.util.ArrayList;
import java.util.Random;

public class Neuron {
	
  public float threshold = 2; 
  public float currVal = 0; 
  public int neuronId;
//  
//  Neuron() {
//	  neuronId++;
//  }
  
  Boolean activation(float inp) {
    
    Boolean out = false;
    float act = inp + currVal;
    
    // if act is less than threshold, there will be no output from neuron
    if(act < threshold) {
      
      out  = false;   
      currVal = act;
    }
    else {
      out = true;
      currVal = 0;
    }
    
//    System.out.println("out is:"+out );
    return out;
  }
  
  /*public static void main (String[] args) {
    
    Neuron n = new Neuron();
    
    ArrayList<Float> input = new ArrayList<Float>();
    ArrayList<Float> output = new ArrayList<Float>();
    
    for (int i=0; i<100; i++)
    {
    	float r;
    	float x = (float) Math.random();
//    	System.out.println("x is:"+x );
    	if(x < 0.5) {
    		r=(float)0.0;
    		input.add(r);
    		
    		
    	} else {
    		r=(float) 1.0;
    		input.add(r);
    		
    	}
//    	System.out.println("r is:"+r );
    	float Y = n.activation(r);
    	output.add(Y);
    	
    }
    
    System.out.println(input);
    System.out.println(output);
        
  }*/
}




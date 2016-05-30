package biz.fesenmeyer;
import java.util.Random;


public class RandomGenerator {
    static Random random = new Random();
    
	public static double generateNextRepairEnd(){
		int number = generateRandomInt(10, 15);
		return (double)number/10;
	}

	public static double generateNextFailure(){
		int number = generateRandomInt((int)(Simulation.getMtbf()*10)-10, (int)(Simulation.getMtbf()*10)+10);
		return (double) number/10;
	}

	public static double generateNextArrival(){
		return exponential(0.5)/10.0;
	}
	
	public static double generateNextProcessingEnd(){
		int number = generateRandomInt(1, 2);
		return (double) number/10;
	}
	
	public static int generateRandomInt(int min, int max) {
	    int randomNum = random.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	public static double exponential(double lambda){
		return (-1/lambda) * Math.log(1-random.nextDouble());
	}
}



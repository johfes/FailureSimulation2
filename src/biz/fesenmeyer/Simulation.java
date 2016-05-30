package biz.fesenmeyer;
import java.util.List;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;


public class Simulation {
	/**
	 * Future Event List.
	 */
	private static final SortedMap<Double, List<String>> FEL = new TreeMap<>();
	private static double simulationTime = 0.0;
	/**
	 * Mean time between failure.
	 */
	private static double mtbf;
	private static final String FORMAT = "%.1f"; 
	private static double simulationDurance;
	
	public static double getSimulationTime() {
		return simulationTime;
	}

	public static double getMtbf() {
		return mtbf;
	}

	public static String getFormat() {
		return FORMAT;
	}

	public static SortedMap<Double, List<String>> getFel() {
		return FEL;
	}

	public static void main(final String[] args) {
		Scanner s = new Scanner(System.in);
		System.out.println("Wie lange ist die MTBF?");
		mtbf = s.nextDouble();
		System.out.println("Wieviele Tage soll die Simulation dauern?");
		simulationDurance = s.nextDouble();
		
		final Machine machine = new Machine();
	    addToFel(0.1, "Ankunft");
	    addToFel(RandomGenerator.generateNextFailure(), "Ausfall");

	    System.out.println("Simulationsbeginn");
	    
		while(true){
			System.out.println("*****************************************");
			
		    for(Entry<Double, List<String>> entry : FEL.entrySet()) {
		    	  final Double key = entry.getKey();
		    	  final List<String> value = entry.getValue();

		    	  System.out.println(String.format(FORMAT, key) + " => " + value);
		    }
	    	System.out.println("WS" + " => " + machine.getCountWS());
				
		    simulationTime = FEL.firstKey();
		    if(simulationTime > simulationDurance){
		    	break;
		    }
			    
			System.out.println("*****************************************");
			
			final List<String> eventTypes = FEL.get(FEL.firstKey());
		    for (String eventType: eventTypes){
		    	execute(eventType, machine);
		    	removeEvent(simulationTime, eventType);
		    }
		}
		System.out.println("*****************************************");
		System.out.println("Simulationsende");
		System.out.println("*****************************************");
		System.out.println("Statistik:");
		System.out.println("Verfügbarkeit: "+
				String.format(FORMAT, calculateAvailabiliy(machine.getDownTime()))+"%");
		System.out.println("durchschnittliche Warteschlangenlänge: "+
				String.format(FORMAT, getAverageWSLength(machine.getWSLengths())));	
	}
	
	private static void execute(final String eventType, final Machine machine) {
		switch(eventType){
			case "Ankunft":
				machine.arrival();
				break;
			case "Bearbeitungsende":
				machine.processingEnd();
				break;
			case "Reparaturende":
				machine.repairEnd();
				break;
			case "Ausfall":
				machine.failure();
				break;
			default:
				throw new IllegalStateException("Unknown eventType: "+eventType);
		}
	}
	
	public static void addToFel(Double time, String eventType){
		if(FEL.containsKey(time)){
			List<String> eventList= FEL.get(time);
			eventList.add(eventType);
		} else {
			List<String> eventList = new ArrayList<String>();
			eventList.add(eventType);
			FEL.put(time,eventList);
		}
	}
	
	public static void removeEvent(Double time, String eventType){
  	  List<String> eventTypes = FEL.get(time);
  	  if(eventTypes.size() > 1){
  		eventTypes.remove(eventType);
  		FEL.replace(time, eventTypes);
  	  } else{
		  FEL.remove(time);
  	  }
	}
	
	public static double calculateAvailabiliy(Double downTime){
		double availabilityTime = simulationDurance-downTime;
		return (availabilityTime/simulationDurance)*100;
	}
	
	public static double getAverageWSLength(SortedMap<Double, Integer> WSLengths){
		double sum = 0.0;
		SortedMap<Double, Integer> tmpMap = new TreeMap<Double, Integer>();
		
		for (Entry<Double, Integer> entry : WSLengths.entrySet()) {
			Double time = entry.getKey();
		    Integer wsCount = entry.getValue();
		    if(tmpMap.size() > 0 && !tmpMap.containsValue(wsCount)){
		    	sum+= (time-(Double)tmpMap.firstKey()) *(Integer) tmpMap.get(tmpMap.firstKey());
		    	tmpMap.clear();
		    }
		    tmpMap.put(time, wsCount);
		}

		return sum/simulationDurance;
	}
}

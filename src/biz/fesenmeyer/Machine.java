package biz.fesenmeyer;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;


public class Machine {
	
	private String status = "frei";
	private int countWS = 0;
	private double downTime = 0.0;
	private SortedMap<Double, Integer> WSLengths = new TreeMap<Double,Integer>();
	
	public int getCountWS() {
		return countWS;
	}

	public double getDownTime() {
		return downTime;
	}

	public SortedMap<Double, Integer> getWSLengths() {
		return WSLengths;
	}

	public void arrival(){
		System.out.println("Ankunft "+ String.format(Simulation.getFormat(), Simulation.getSimulationTime()));
		double arrivalTime = Simulation.getSimulationTime()+RandomGenerator.generateNextArrival();
		Simulation.addToFel(arrivalTime, "Ankunft");
		System.out.println("Ankunftsereignis wurde geplant: "+String.format(Simulation.getFormat(), arrivalTime));
		if(countWS == 0 && status.equalsIgnoreCase("frei")){
			double processingEndTime = Simulation.getSimulationTime()+RandomGenerator.generateNextProcessingEnd();
			Simulation.addToFel(processingEndTime, "Bearbeitungsende");
			System.out.println("WS leer, Bearbeitung hat begonnen \nBearbeitungsendeereignis wurde geplant: "+
								String.format(Simulation.getFormat(), processingEndTime));
			status = "aktiv";
		} else {
			countWS++;
			WSLengths.put(Simulation.getSimulationTime(), countWS);
			System.out.println("Das Teil wurde der Warteschlange hinzugefügt");
		}
	}

	public void processingEnd(){
		System.out.println("Bearbeitungsende "+ String.format(Simulation.getFormat(),Simulation.getSimulationTime()));
		if(countWS > 0){
			countWS--;
			WSLengths.put(Simulation.getSimulationTime(), countWS);
			double processingEndTime  = Simulation.getSimulationTime()+ RandomGenerator.generateNextProcessingEnd();
			Simulation.addToFel(processingEndTime , "Bearbeitungsende");
			System.out.println("Teil wurde nachgezogen \nBearbeitungsende wurde geplant: "+String.format(Simulation.getFormat(), processingEndTime ));
		} else {
			status = "frei";
		}
		
	}
	
	public void failure(){
		System.out.println("Ausfall "+ String.format(Simulation.getFormat(), Simulation.getSimulationTime()));
		double timeToRepairEnd= RandomGenerator.generateNextRepairEnd();
		downTime += timeToRepairEnd;
		double repairEndTime = Simulation.getSimulationTime()+timeToRepairEnd;
		Simulation.addToFel(repairEndTime, "Reparaturende");
		System.out.println("Reparatur wird begonnen \nReparaturende wurde geplant: "+String.format(Simulation.getFormat(), repairEndTime));
		
		if(status.equals("aktiv")){
			status = "defekt_aktiv";
			
		    Double entryToDelete = null;
		    boolean found = false;
		    for(Entry<Double, List<String>> entry : Simulation.getFel().entrySet()) {
		    	  Double key = entry.getKey();
		    	  List<String> list = entry.getValue();
		    	  for(String str : list){
		    		  if("Bearbeitungsende".equalsIgnoreCase(str)){
		    			  entryToDelete = key;
		    			  found = true;
		    			  break;
		    		  }
		    	  }    	 
		    }
		    
		    if(found){
  			  	Simulation.removeEvent(entryToDelete, "Bearbeitungsende");
  			  	double newProcessingEndTime = entryToDelete+timeToRepairEnd;
		    	Simulation.addToFel(newProcessingEndTime, "Bearbeitungsende");
		    	System.out.println("Maschine war aktiv \nBearbeitungsende wurde hinausgeschoben: "+
		    	String.format(Simulation.getFormat(), entryToDelete)+" + "+String.format(Simulation.getFormat(), timeToRepairEnd)+" = "+String.format(Simulation.getFormat(), newProcessingEndTime));
		    }

		} else {
			status = "defekt_frei";
		}
	}
	
	public void repairEnd(){
		System.out.println("Reparaturende "+ String.format(Simulation.getFormat(), Simulation.getSimulationTime()));
		double failureTime = Simulation.getSimulationTime()+RandomGenerator.generateNextFailure();;
		Simulation.addToFel(failureTime, "Ausfall");
		
		if (status.equals("defekt_aktiv")){
			status = "aktiv";
		} else {
			if(countWS >= 1){
				countWS--;
				WSLengths.put(Simulation.getSimulationTime(), countWS);
				status = "aktiv";
				double processingEndTime = Simulation.getSimulationTime()+RandomGenerator.generateNextProcessingEnd();
				Simulation.addToFel(processingEndTime, "Bearbeitungsende");
				System.out.println("Teil wurde nachgezogen \nBearbeitungsende wurde geplant: "+String.format(Simulation.getFormat(), processingEndTime));
			} else {
				status = "frei";
			}
		}
	}
}

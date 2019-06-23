package it.polito.tdp.newufosightings.model;

import java.time.Month;
import java.time.Year;
import java.util.Map;

public class TestModel {

	public void run() {
		Model model = new Model();
		model.creaGrafo(1996, "disk");
		
		Map <State, Integer> mappa =model.calcolaSommaPesi();
		for(State s : mappa.keySet()) {
			System.out.println(s.getName()+" "+mappa.get(s)+"\n");
		}
		
		
	}
	
	
	
	public static void main(String[] args) {
		TestModel main = new TestModel();
		main.run();

	}

}

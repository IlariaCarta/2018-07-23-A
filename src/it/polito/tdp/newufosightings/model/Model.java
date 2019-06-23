package it.polito.tdp.newufosightings.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.print.attribute.HashAttributeSet;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.newufosightings.NewUfoSightingsController;
import it.polito.tdp.newufosightings.db.NewUfoSightingsDAO;

public class Model {
	
	private Map<String,State> idMap;
	private NewUfoSightingsDAO dao= new NewUfoSightingsDAO();
	List<State> stati;
	private Graph<State, DefaultWeightedEdge> graph ;
	
	public Model() {
		this.idMap = new HashMap<String, State>();
		
		this.stati = new ArrayList<State>();
		
	}

	
	public void creaGrafo(int anno, String shape) {
		//NewUfoSightingsDAO dao = new NewUfoSightingsDAO();
		this.graph = new SimpleWeightedGraph<State, DefaultWeightedEdge>(DefaultWeightedEdge.class) ;

		
		this.stati=dao.loadAllStates(idMap);
		//vertici
		Graphs.addAllVertices(graph, this.idMap.values()) ;
		
		// archi
		List<Adiacenza> archi = dao.getCoppieAdiacenti() ;
		for( Adiacenza c: archi) {
			graph.addEdge(this.idMap.get(c.getState1()), 
					this.idMap.get(c.getState2())) ; 
			graph.setEdgeWeight(graph.getEdge(idMap.get(c.getState1()), idMap.get(c.getState2())), dao.calcolaAvvistamenti(shape, anno,c.getState1(), c.getState2()));
		
		}
		
			
			
		System.out.format("Grafo creato: %d vertici, %d archi\n",graph.vertexSet().size(), graph.edgeSet().size());
		
	}
	
	

	public Map <State, Integer> calcolaSommaPesi(){
		Map <State, Integer> archi = new HashMap<>();
		for(State s : this.graph.vertexSet()) {
			int somma =0;
			for(DefaultWeightedEdge edge : graph.edgesOf(s)) {
				somma += graph.getEdgeWeight(edge);
			}
			archi.put(s, somma);
		}
		
		return archi;
	}
	
	
	public List<String> getShape(int anno){
		return dao.loadShapesFromYear(anno);
	}
	
	
}

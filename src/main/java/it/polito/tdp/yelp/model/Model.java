package it.polito.tdp.yelp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	private YelpDao dao;
	private SimpleDirectedWeightedGraph<Business,DefaultWeightedEdge> grafo;
	private Map<String,Business> businesses;
	private List<Adiacenza> archi;
	private List<Business> migliore;
	
	
	public Model() {
		this.dao=new YelpDao();
		this.businesses=new HashMap<>();
		for(Business b:dao.getAllBusiness()) {
			businesses.put(b.getBusinessId(), b);
		}
	}
	
	
	public List<Business> creaGrafo(int a, String city) {
		this.grafo= new SimpleDirectedWeightedGraph<Business,DefaultWeightedEdge> (DefaultWeightedEdge.class);
		this.archi=new ArrayList<>();
		
	   Graphs.addAllVertices(grafo,dao.getVertici(a, city, businesses));
	   System.out.print(this.nVertici());
	   
	   for(Adiacenza aa:dao.getArchi(a, city, grafo.vertexSet(),businesses)) {
		   Graphs.addEdge(grafo,businesses.get(aa.getB1()),businesses.get(aa.getB2()), aa.getPeso());
		   archi.add(aa);
	   }
		List<Business> ritorno=new ArrayList<>(grafo.vertexSet());
		Collections.sort(ritorno);
		return ritorno;
	}
	
	
	
	public List<Business> cercaCammino(double x,Business partenza){
		this.migliore=new ArrayList<>();
		Business arrivo=this.migliore();
		List<Business> parziale=new ArrayList<>();
		parziale.add((partenza));
		int best=1000;
		this.cerca(x,parziale,arrivo,best);
		
		
		return migliore;
	}
	
	
	
	
	private void cerca(double x, List<Business> parziale, Business arrivo,int best) {
		if(parziale.get(parziale.size()-1).equals(arrivo)) {
			if(parziale.size()<best) {
				migliore=new ArrayList<>(parziale);
				best=parziale.size();
				return;
			}
		}
		else if(best>2){
			for(Business b:this.possibili(parziale, x)) {
				parziale.add(b);
				this.cerca(x, parziale, arrivo, best);
				if(parziale.get(parziale.size()-1).equals(arrivo))
					return;
				else 
					parziale.remove(parziale.size()-1);
			}
		}
		else return;
		
	}
	
	
	public List<Business> possibili(List<Business> parziale,double x){
		List<Business> possibili=new ArrayList<>();
		for(DefaultWeightedEdge e:grafo.outgoingEdgesOf(parziale.get(parziale.size()-1))) {
			if(grafo.getEdgeWeight(e)>=x&& !parziale.contains(grafo.getEdgeTarget(e))) {
				possibili.add(grafo.getEdgeTarget(e));
			}
		}
		return possibili;
	}


	public Business migliore() {
		Business migliore=new Business(null, null, null, null, null, 0,	null,null, 0.0, 0.0,null,0.0) ;
		double max=-1000;
		for(Business b:grafo.vertexSet()) {
			double in=0;
			double out=0;
			for(DefaultWeightedEdge e:grafo.outgoingEdgesOf(b)) {
				out+=grafo.getEdgeWeight(e);
			}
			for(DefaultWeightedEdge e: grafo.incomingEdgesOf(b)) {
				in +=grafo.getEdgeWeight(e);
			}
			if(in-out>max) {
				max=in-out;
				migliore=b;
			}
			
		}
		return migliore;
	}
	
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
		
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
}

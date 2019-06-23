package it.polito.tdp.newufosightings.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mysql.fabric.xmlrpc.base.Array;

import it.polito.tdp.newufosightings.model.Adiacenza;
import it.polito.tdp.newufosightings.model.Sighting;
import it.polito.tdp.newufosightings.model.State;

public class NewUfoSightingsDAO {

	public List<Sighting> loadAllSightings(Map<Integer,Sighting> idMap) {
		String sql = "SELECT * FROM sighting";
		List<Sighting> list = new ArrayList<>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);	
			ResultSet res = st.executeQuery();

			while (res.next()) {
				Sighting s = new Sighting(res.getInt("id"), res.getTimestamp("datetime").toLocalDateTime(),
						res.getString("city"), res.getString("state"), res.getString("country"), res.getString("shape"),
						res.getInt("duration"), res.getString("duration_hm"), res.getString("comments"),
						res.getDate("date_posted").toLocalDate(), res.getDouble("latitude"),
						res.getDouble("longitude"));
				
				if(!idMap.containsKey(res.getInt("id")))
				{
					list.add(s);
					idMap.put(s.getId(), s);
				} else 
					list.add(idMap.get(res.getInt("id")));
				
			}

			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		return list;
	}

	public List<State> loadAllStates(Map<String,State> idMap) {
		String sql = "SELECT * FROM state";
		List<State> result = new ArrayList<State>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				State state = new State(rs.getString("id"), rs.getString("Name"), rs.getString("Capital"),
						rs.getDouble("Lat"), rs.getDouble("Lng"), rs.getInt("Area"), rs.getInt("Population"),
						rs.getString("Neighbors"));
				result.add(state);
				idMap.put(state.getId(), state);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<String> loadShapesFromYear(int anno){
		String sql = "SELECT DISTINCT shape " + 
				"FROM sighting " + 
				"WHERE (NOT shape='') AND YEAR(DATETIME) = ? " + 
				"ORDER BY shape ASC ";
		
		List<String> shapes = new ArrayList<>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				String shape = rs.getString("shape");
				shapes.add(shape);
			}

			conn.close();
			return shapes;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
			
	}
	
	public List<Adiacenza> getCoppieAdiacenti(){
		String sql = "SELECT state1, state2 " + 
				"FROM neighbor " + 
				"WHERE state1<state2 ";
		
		List<Adiacenza> result = new ArrayList<>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Adiacenza adiacenza = new Adiacenza(rs.getString("state1"), rs.getString("state2"));
				result.add(adiacenza);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	
	public int calcolaAvvistamenti(String shape, int anno, String s1, String s2) {
		String sql ="SELECT s1.state, s2.state, COUNT(*) AS cnt " + 
				"FROM sighting as s1, sighting AS s2 " + 
				"WHERE s1.shape = ? " + 
				"AND s2.shape = ? " + 
				"AND YEAR (s1.DATETIME) = ? " + 
				"and YEAR (s2.DATETIME) = ? " + 
				"and s1.state = ? " + 
				"AND s2.state = ? " + 
				"GROUP BY s1.state, s2.state";
		
		int avvistamenti = 0;
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, shape);
			st.setString(2, shape);
			st.setInt(3, anno);
			st.setInt(4, anno);
			st.setString(5, s1);
			st.setString(6, s2);
			ResultSet rs = st.executeQuery();

			if(rs.next()) {
				avvistamenti = rs.getInt("cnt");
			}

			conn.close();
			return avvistamenti;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

}

package it.polito.tdp.yelp.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.polito.tdp.yelp.model.Adiacenza;
import it.polito.tdp.yelp.model.Business;
import it.polito.tdp.yelp.model.Review;
import it.polito.tdp.yelp.model.User;

public class YelpDao {

	public List<Business> getAllBusiness(){
		String sql = "SELECT * FROM Business";
		List<Business> result = new ArrayList<Business>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Business business = new Business(res.getString("business_id"), 
						res.getString("full_address"),
						res.getString("active"),
						res.getString("categories"),
						res.getString("city"),
						res.getInt("review_count"),
						res.getString("business_name"),
						res.getString("neighborhoods"),
						res.getDouble("latitude"),
						res.getDouble("longitude"),
						res.getString("state"),
						res.getDouble("stars"));
				result.add(business);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Review> getAllReviews(){
		String sql = "SELECT * FROM Reviews";
		List<Review> result = new ArrayList<Review>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Review review = new Review(res.getString("review_id"), 
						res.getString("business_id"),
						res.getString("user_id"),
						res.getDouble("stars"),
						res.getDate("review_date").toLocalDate(),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("review_text"));
				result.add(review);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<User> getAllUsers(){
		String sql = "SELECT * FROM Users";
		List<User> result = new ArrayList<User>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				User user = new User(res.getString("user_id"),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("name"),
						res.getDouble("average_stars"),
						res.getInt("review_count"));
				
				result.add(user);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> cities(){
		
		String sql = "SELECT DISTINCT(city) "
				+ "FROM business "
				+ "ORDER BY city";
		List<String> result = new ArrayList<String>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				result.add(res.getString("city"));
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Business> getVertici(int a, String city,Map<String,Business> businesses){
		String sql = "SELECT DISTINCT(b.business_id) "
				+ "FROM business b, reviews r "
				+ "WHERE YEAR(r.review_date)=? AND b.city=? "
				+ "AND r.business_id=b.business_id";
		List<Business> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, a);
			st.setString(2, city);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				result.add(businesses.get(res.getString("business_id")));
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public List<Adiacenza> getArchi(int a, String city,Set<Business> vertici,Map<String,Business> businesses){
		String sql = "SELECT t1.business_id AS b1 , t2.business_id AS b2 , t1.media AS m1 , t2.media AS m2 "
				+ "FROM (SELECT r.business_id, AVG(r.stars) AS media FROM reviews r, business b  "
				+ "WHERE YEAR(r.review_date)=? AND b.city=? AND b.business_id=r.business_id "
				+ "GROUP BY r.business_id) t1, "
				+ "(SELECT r.business_id, AVG(r.stars) AS media FROM reviews r, business b  "
				+ "WHERE YEAR(r.review_date)=? AND b.city=? AND b.business_id=r.business_id "
				+ "GROUP BY r.business_id) t2 "
				+ "WHERE t1.business_id<t2.business_id";
		List<Adiacenza> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, a);
			st.setString(2, city);
			st.setInt(3, a);
			st.setString(4, city);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				if(vertici.contains(businesses.get(res.getString("b1")))&&vertici.contains(businesses.get(res.getString("b2")))) {
				double peso=res.getDouble("m1")-res.getDouble("m2");
				if(peso>0) {
					Adiacenza aa=new Adiacenza(res.getString("b2"),res.getString("b1"),peso);
					result.add(aa);
				}
				else if(peso<0) {
					Adiacenza aa=new Adiacenza(res.getString("b1"),res.getString("b2"),-peso);
					result.add(aa);
				}
					
				}
				
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	} 
	
	
	
}

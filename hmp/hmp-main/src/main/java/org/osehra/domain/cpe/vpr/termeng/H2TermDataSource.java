package org.osehra.cpe.vpr.termeng;

import org.osehra.cpe.vpr.web.IHealthCheck;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TODO: Add more metadata to the database build part: build date, build by/machine/etc, more metadata from UMLS about source (full name, desc, version, etc)
 * TODO: Implement the fulltext search!
 * @author brian
 *
 */

public class H2TermDataSource extends AbstractTermDataSource implements ITermDataSource, IHealthCheck{
	private static ObjectMapper MAPPER = new ObjectMapper();
	private static String CREATE_TABLE1_SQL = "CREATE TABLE IF NOT EXISTS concepts (urn VARCHAR(64) PRIMARY KEY, json CLOB NOT NULL)";
	private static String CREATE_TABLE2_SQL = "CREATE TABLE IF NOT EXISTS sources (sab VARCHAR(64) PRIMARY KEY, concept_count int NOT NULL, term_count int NOT NULL)";
	private static final String PING_SQL = "SELECT * FROM INFORMATION_SCHEMA.users";
	private Connection conn;
	private PreparedStatement ps_select;
	private PreparedStatement ps_save;
	private PreparedStatement ps_search;
	private String jdbcURL;
	private Map<String,Object> sourceMap;

	/**
	 * Private constructor.  Only used for situations where we want to create a new H2 database.
	 * @param dbname
	 * @param readWrite
	 * @throws SQLException
	 */
	public H2TermDataSource(String jdbcurl) throws SQLException {
		this.conn = DriverManager.getConnection(jdbcurl, "sa", "");
		this.jdbcURL = jdbcurl;
		
		// setup the prepared statements we will use
		this.conn.prepareCall(CREATE_TABLE1_SQL).execute();
		try {
			this.conn.prepareCall(CREATE_TABLE2_SQL).execute();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		ps_select = this.conn.prepareStatement("SELECT json FROM concepts WHERE urn=?");
		ps_save = this.conn.prepareStatement("INSERT INTO concepts VALUES (?, ?)");
		ps_search = this.conn.prepareStatement("SELECT urn FROM concepts WHERE urn LIKE ? LIMIT 25");
		
		// gather the list of reference terminologies this database file contains 
		PreparedStatement src_ps = null;
		ResultSet rs;
		try {
			src_ps = this.conn.prepareStatement("SELECT * FROM sources");
			ResultSetMetaData meta = src_ps.getMetaData();
			rs = src_ps.executeQuery();
			Map<String, Object> sources = new HashMap<String,Object>();
			while (rs.next()) {
				HashMap<String, Object> row = new HashMap<String, Object>();
				for (int i=1; i <= meta.getColumnCount(); i++) {
					row.put(meta.getColumnName(i), rs.getObject(i));
				}
				sources.put(rs.getString("sab"), row);
			}
			this.sourceMap = sources;
		} catch (SQLException ex) {
			System.err.println("Error reading sources");
			ex.printStackTrace();
		} finally {
			if (src_ps != null) src_ps.close();
		}
	}
	
	public void save(Map<String, Object> data) {
		try {
			ps_save.setString(1, (String) data.get("urn"));
			Clob jsondata = this.conn.createClob();
			jsondata.setString(1, MAPPER.writeValueAsString(data));
			ps_save.setClob(2, jsondata);
			ps_save.execute();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	protected void commit() throws SQLException {
		this.conn.commit();
	}
	
	protected void close() throws SQLException {
		System.out.println("Shutdown defrag....");
		this.conn.createStatement().execute("shutdown defrag");
		this.conn.close();
	}

	@Override
	public Set<String> getCodeSystemList() {
		if (this.sourceMap == null) return null;
		return this.sourceMap.keySet();
	}
	
	@Override
	public Map<String, Object> getCodeSystemMap() {
		return this.sourceMap;
	}
	
	@Override
	public List<String> search(String text) {
		// TODO: Try one of these when the index is done building...
		//org.h2.fulltext.FullText.search(conn, text, limit, offset);
		//SELECT * FROM FT_SEARCH('Hello', 0, 0);
		
		ResultSet rs = null;
		try {
			List<String> ret = new ArrayList<String>();
			ps_search.setString(1, "%" + text + "%");
			rs = ps_search.executeQuery();
			while (rs.next()) {
				ret.add(rs.getString(1));
			}
			return ret;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			try {
				if (rs != null) rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized Map<String, Object> getConceptData(String urn) {
		ResultSet rs = null;
		try {
			ps_select.setString(1, urn);
			rs = ps_select.executeQuery();
		
			if (rs!=null && rs.next()) {
				Map<String, Object> data = MAPPER.readValue(rs.getString(1), Map.class);
				// sameas, parents, ancestors
				for (String key : new String[] {"sameas", "parents", "ancestors"}) {
					List<String> list = (List<String>) data.get(key);
					data.put(key, new HashSet<String>(list));
				}
				return data;
			}
			return null;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			try {
				if (rs != null) rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public String toString() {
		return getClass().getName() + ": " + jdbcURL;
	}
	
    /**
     * Utility function to build a full EHCache database of concepts.
     * 
     * I'm using this to build the reference terminology databases to check into nexus:
     * 
     * H2TermDataSource umls2012aa VANDF NDFRT RXNORM ===> termdb-1.20121110-drugdb
     * H2TermDataSource umls2012aa SNOMEDCT ===> termdb-1.20121110-sctdb
     * H2TermDataSource umls2012aa LNC ===> termdb-1.20121110-lncdb
     * 
     * @throws SQLException 
     */
    public static void main(String[] args) throws SQLException {
    	if (args.length < 2) {
    		System.err.println("Usage: H2TermDataSource [dbname] [sab1] [sab2]...");
    	}
    	
    	// setup JDBC connection
    	String dbname = args[0];
    	String jdbcurl = "jdbc:postgresql://localhost:5432/postgres";
		Connection conn = DriverManager.getConnection(jdbcurl, dbname, dbname);
		JSONBuilderDataSource src = new JSONBuilderDataSource(conn, dbname);
		
		// setup dest H2 DB
		jdbcurl = "jdbc:h2:split:24:data/db/termdb";
		H2TermDataSource dest = new H2TermDataSource(jdbcurl);
		
		// loop through each source system and load it into cache
    	for (int i=1; i < args.length; i++) {
    		String sab = args[i];
    		loadSAB(src, dest, sab);
    		dest.commit();
    	}
    	
    	// TODO: build the FT index
    	System.out.println("Building FT index... (this may take a while)");
    	//CREATE ALIAS IF NOT EXISTS FT_INIT FOR "org.h2.fulltext.FullText.init";
    	//CALL FT_INIT();
    	//CALL FT_CREATE_INDEX('PUBLIC', 'CONCEPTS', NULL);
    	dest.close();
    	
    	// create the zip file
    	/*
    	* TODO: Issue: this doesn't zip up all the split files, must add them manually.
    	* TODO: Also this doesn't compile correctly, so zip them up manually for now.
    	System.out.println("Create .zip file");
    	Backup.execute("data/termdb.zip", "data/db", "termdb", false);
    	*/
    	System.exit(0); // for some reason the thread doesn't exit?!?
    }
    
    private static void loadSAB(JSONBuilderDataSource src, H2TermDataSource dest, String sab) throws SQLException {
		int count = src.getConceptCount(sab.toUpperCase());
		System.out.format("Loading: %s (count: %d) \n", sab, count);
		
		int conceptcount=0;
		int termcount=0;
		String statusStr = "%2.2f%% complete. (%d/%d terms, %d concepts)\n";
		Iterator<String> itr = src.iterator(sab);
		while (itr.hasNext()) {
			String urn = itr.next();
			Map<String, Object> data = src.getConceptData(urn);
			if (data != null) {
				dest.save(data);
			}
			if (data.containsKey("terms") && data.get("terms") instanceof List) {
				termcount += ((List) data.get("terms")).size();
			}
			if (++conceptcount % 1000 == 0) {
				float pct = ((float) termcount/ (float) count) * 100.0f;
				System.out.format(statusStr, pct, termcount, count, conceptcount);
			}
		}
		System.out.format(statusStr, 100.0, termcount, count, conceptcount);
		
		// record the source and term/concept counts
		PreparedStatement ps = dest.conn.prepareStatement("INSERT INTO sources VALUES (?, ?, ?)");
		ps.setString(1, sab);
		ps.setInt(2, conceptcount);
		ps.setInt(3, termcount);
		ps.execute();
    }

	@Override
	public boolean isAlive() {
		try {
			this.conn.prepareCall(PING_SQL).execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}

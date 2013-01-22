/*
 * TODO: try running this against Oracle and MySQL, see if there is a compatible set of SQL?
 * TODO: JDBC URL/USER/PASS should be command line configurable
 * TODO: Enhance this so that it can also read/load the RxNorm and SnomedCT distributions
 * TODO: Add a check for to ensure that the generate_schema.sql script has been run (ensure that the schema exists)
    // TODO: how do I know which indexes to create?  How are the MYSQL/Oracle indexes determined?
 * 
 */



import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;

/**
 * @author bbray
 *
 */
public class UMLSLoader {

  private static Connection c;
  private static String SCHEMA;
  private static int BATCH_SIZE = 1000;

  /**
   * @param args
   * @throws SQLException 
   * @throws IOException 
   */
  public static void main(String[] args) throws SQLException, IOException {
    if (args.length < 5) {
      System.out.println("Usage: UMLSLoader <JDBC URL> <JDBC USER> <JDBC PASSWORD> <SCHEMA> <.RRF File1> <.RRF File2> <.RRF FileN>");
      return;
    }
//    c = DriverManager.getConnection("jdbc:postgresql://localhost:5444/edb", "enterprisedb", "3000gtvr4");
    //"jdbc:postgresql://localhost:5432/umls" "postgres", "paige"
    c = DriverManager.getConnection(args[0], args[1], args[2]);
    c.setAutoCommit(false);
    
    // check if the schema exists
    SCHEMA = args[3];
    ResultSet rs = runQuery("SELECT schema_name FROM information_schema.schemata WHERE schema_name='" + SCHEMA + "'", false);
    if (!rs.next()) {
      rs.close();
      System.err.format("The schema %s does not exist.", SCHEMA);
      return;
    }
    rs.close();
    
    
    // derive the base directory to read the MRFILES.RRF and MRCOLS.RRF indexes from
    File baseDir = new File(args[4]).getParentFile();
    File idxFile1 = new File(baseDir, "MRFILES.RRF");
    File idxFile2 = new File(baseDir, "MRCOLS.RRF");
    if (!baseDir.exists() || !baseDir.isDirectory()) {
      System.err.format("Base directory %s could not be determined", baseDir);
      return;
    } else if (!idxFile1.exists() || !idxFile1.canRead() || !idxFile1.isFile()) {
      System.err.format("Could not read MRFILES.RRF at %s", idxFile1);
      return;
    } else if (!idxFile2.exists() || !idxFile2.canRead() || !idxFile2.isFile()) {
      System.err.format("Could not read MRCOLS.RRF at %s", idxFile2);
      return;
    }

    // read/parse the index files
    HashMap<String,Table> tables = readMeta(baseDir, idxFile1, idxFile2);
    
    
    // loop though each specified RRF file and parse/load it.
    for (int i=4; i < args.length; i++) {
      String arg = args[i];
      File f = new File(arg);
      if (!f.exists() || !f.canRead()) {
        System.err.println("Cannot read " + f);
        continue;
      }
      
      // start a new transaction
      c.setAutoCommit(false);
      c.commit();
      
      // show error if the specified RRF file does not exist in the index files.
      Table tab = tables.get(f.getName().toUpperCase());
      if (tab == null) {
        System.err.format("No index information (in MRCOLS.RRF and MRFILES.RRF) for %s.  Skipping.", f);
        continue;
      }
      
      // see if the table already exists in the DB.
      int count = 0;
      rs = runQuery("SELECT count(*) FROM " + tab.NAME, true);
      if (rs != null && rs.next()) {
        count = rs.getInt(1);
      }
      
      // if it does, prompt the user to continue or not?
      if (count > 0) {
        System.err.format("Warning, table %s already exists with %s rows.\n", tab.NAME, count);
        System.err.print("Are you sure you want to overwrite it? [y/N] ");
        String response = readLine();
        if (!response.equalsIgnoreCase("Y")) {
          continue;
        }
      }

      // create the table in the DB
      tab.create();

      // load the values from the file
      parseFile(tab, 0);
      
      // create the indexes
      tab.index();

      // stuff that can't be run in a trascation
      c.setAutoCommit(true);
      runStatement("VACUUM ANALYZE " + tab.NAME);
    }
    
    // post process
    //computeIndex();
  }
  
  private static void computeIndex() throws SQLException {
    String idxTable = SCHEMA + ".hs_concept_index";
    runStatement("DROP TABLE IF EXISTS " + idxTable);
    runStatement("CREATE TABLE " + idxTable + " () TABLESPACE " + SCHEMA);

    // this will return all the rows to be inserted, but not all the columns
    /*
    String sql = "";
    sql += "create temporary table tmp_umls_concept_index AS ";
    sql += "select c.cui, c.sab, max(r.rank) as max_rank, count(*) as desc_count, ";
    sql += "  (select rr.tty from umls_2008aa.mrrank rr where rr.sab=c.sab and rr.rank=max(r.rank)) as max_rank_tty ";
    sql += "from umls_2008aa.mrconso c, umls_2008aa.mrrank r ";
    sql += "where c.cui < 'C0000172' and c.sab=r.sab and c.tty=r.tty "; 
    sql += "group by c.cui, c.sab ";
    sql += "order by c.cui, c.sab ";
    runStatement(sql);
    */
    
    // insert the cui, and pref_desc into the index table.
    // this is a fairly quick query if MRCONSO is indexed.
    String sql = "INSERT INTO " + idxTable + " SELECT cui, str as perf_desc FROM mrconso ";
    sql += "WHERE and ts='P' "; // filters to the preferred LUI (lexical term) of the CUI
    sql += "and stt='PF' "; // filters to the preferred form (SUI) of term (LUI)
    sql += "and ispref='Y' "; // is the preferred AUI (atom) of the SUI (String)
    runStatement(sql);

    // create PK index
    runStatement("ALTER TABLE " + idxTable + " ADD CONSTRAINT hs_concept_index_pk PRIMARY KEY (cui) USING INDEX TABLESPACE " + SCHEMA);
  }
  
  private static void computeIndexForNS(String sab) {
    
  }

  private static void parseFile(Table tab, int limit) throws IOException, SQLException {
    int rowsCount = 0;
    BufferedReader reader = new BufferedReader(new FileReader(tab.FILE));

    // create the prepared statement with the correct # of columns.
    String sql = "INSERT INTO " + tab.NAME + " VALUES (?";
    for (int i = 1; i < tab.COL_ORDER.length; i++) {
      sql += ", ?";
    }
    sql += ")";
    PreparedStatement stmt = c.prepareStatement(sql);

    // read the file line by line
    String line = reader.readLine();
    try {

      while (line != null && (limit <= 0 || rowsCount <= limit)) {
        rowsCount++;
        String[] fields = line.split("\\|");

        // build the statement column by column
        for (int i = 1; i <= tab.COL_ORDER.length; i++) {
          Column col = tab.COLS.get(tab.COL_ORDER[i - 1]);
          boolean isInt = col.isIntType();
          int jdbcType = col.getType();
          String value = null;
          if (fields.length > (i - 1)) {
            value = fields[i - 1];
          }

          if (value == null || value.length() == 0) {
            stmt.setNull(i, jdbcType);
          } else if (isInt && value.length() >= 10) {
            // we translate 10 digit ints to bigints (longs)
            long val = Long.parseLong(value);
            stmt.setLong(i, val);
          } else if (jdbcType == Types.VARCHAR) {
            stmt.setString(i, value);
          } else if (jdbcType == Types.NUMERIC) {
            stmt.setFloat(i, Float.parseFloat(value));
          } else if (isInt) {
            int val = Integer.parseInt(value);
            stmt.setInt(i, val);
          } else {
            stmt.setObject(i, value);
          }
        }
        stmt.addBatch();

        // if we reached our batch size, commit them.
        if (rowsCount % BATCH_SIZE == 0) {
          stmt.executeBatch();
          c.commit();

          showStatus(tab.FILE.getName(), rowsCount, tab.ROWS);
        }

        line = reader.readLine();
      }
      stmt.executeBatch();
    } catch (SQLException ex) {
    	ex.printStackTrace();
      throw ex.getNextException();
    }
    c.commit();
    reader.close();

    showStatus(tab.FILE.getName(), rowsCount, tab.ROWS);
    System.out.println("\nDone.");
    
  }
  
  private static void showStatus(String file, int rowsCount, int rowsTotal) {
    // display progress
    int pct = Math.round((float) rowsCount / (float) rowsTotal * 100f );
    String msg = String.format("Loading %s. (%s of %s records complete) %s%%", file, rowsCount,
        rowsTotal, pct);
    System.out.print(msg.trim());
    for (int i = 1; i <= msg.length(); i++) {
      System.out.print("\b");
    }
    // TODO: print time, ETA.    
  }
  
  private static HashMap<String, Table> readMeta(File baseDir, File tabfile, File colfile) throws IOException, SQLException {
    HashMap<String,Table> ret = new HashMap<String,Table>();

    // parse the files index
    BufferedReader reader = new BufferedReader(new FileReader(tabfile));
    String line = reader.readLine();
    while (line != null) {
      String[] fields = line.split("\\|");
      
      Table tab = new Table();
      tab.FILE = new File(baseDir, fields[0]);
      tab.NAME = SCHEMA + "." + fields[0].replace(".RRF", "").replace("CHANGE/", "");
      tab.DESC = fields[1];
      tab.ROWS = Integer.parseInt(fields[4]);
      tab.COL_ORDER = fields[2].split("\\,");
      ret.put(fields[0], tab);
      
      line = reader.readLine();
    }
    reader.close();
    
    // then parse the columns index
    reader = new BufferedReader(new FileReader(colfile));
    line = reader.readLine();
    while (line != null) {
      String[] fields = line.split("\\|");
      int min = Integer.parseInt(fields[3]);
      int max = Integer.parseInt(fields[5]);

      Column col = new Column();
      col.NAME = fields[0];
      col.DESC = fields[1];
      col.FILE = fields[6];
      col.TYPE = fields[7];
      col.NULLABLE = min == 0;
      
      // strange scenario: the MRSAT.RRF file is now larger (in bytes) than an integer will hold.
      // therefore the MRCOL should declare BIGINT instead of INT for the MRFILES.RRF file
      // but it does not.
      // We will assume that anything that is 10 digits should be translated into bigint.
      if (col.isIntType() && max >= 10) {
        col.TYPE = "bigint";
      }
      
      Table tab = ret.get(col.FILE);
      tab.COLS.put(col.NAME, col);
      
      line = reader.readLine();
    }
    reader.close();
    
    return ret;
  }
  
  
  private static void runStatement(String sql) throws SQLException {
    System.out.println("SQL=" + sql);
    CallableStatement stmt = c.prepareCall(sql);
    stmt.execute();
  }
  
  private static ResultSet runQuery(String sql, boolean suppressErrors) throws SQLException {
    // there is a known cursor leak here.
    Statement stmt = null;
    try {
      stmt = c.createStatement();
      return stmt.executeQuery(sql);
    } catch (SQLException ex) {
      if (suppressErrors) {
    	  // the transaction is corrupt, start a new one
          c.rollback();
        return null;
      }
      throw ex;
    } finally {
//      if (stmt != null)
//        stmt.close();
    }
  }
  
  private static String readLine() throws IOException {
    // first clear all existing buffered output.
    System.in.skip(System.in.available());
    
    // read the line of response
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    String response = reader.readLine();
    return response;
  }

  private static class Table {
    String NAME;
    File FILE;
    String DESC;
    int ROWS;
    String[] COL_ORDER;
    HashMap<String,Column> COLS = new HashMap<String,Column>();
    
    private void create() throws SQLException {
      // (re)create the table
      runStatement("DROP TABLE IF EXISTS " + NAME);
      runStatement("CREATE TABLE " + NAME + " () TABLESPACE " + SCHEMA);
      runStatement("ALTER TABLE " + NAME + " OWNER TO " + SCHEMA);
      runStatement("COMMENT ON TABLE " + NAME + " IS '" + DESC + "'");
      
      // add each column
      for (String col : COL_ORDER) {
        Column column = COLS.get(col);
        String flags = "";
        if (!column.NULLABLE) {
          flags += "NOT NULL";
        }
        runStatement("ALTER TABLE " + NAME + " ADD COLUMN " + col + " " + column.TYPE + " " + flags);
        runStatement("COMMENT ON COLUMN " + NAME + "." + col + " IS '" + column.DESC + "'");
      }
    }

    private void index() throws SQLException {
      // current index creation logic: index CUI and AUI columns that not null
      for (String col : COL_ORDER) {
        Column column = COLS.get(col);
        if (col.equals("CUI") || col.equals("AUI")) {
          if (!column.NULLABLE) {
            String name = "X_" + NAME.substring(NAME.indexOf('.')+1) + "_" + col;
            runStatement("CREATE INDEX " + name + " ON " + NAME + " (" + col + ") TABLESPACE " + SCHEMA);
          }
        }
      }
      
      // also, if there exists a SAB/CODE combo or CUI1/AUI1 combo, index it.
      if (COLS.containsKey("SAB") && COLS.containsKey("CODE")) {
        //String name = NAME + "_idx1";
        String name = "X_" + NAME.substring(NAME.indexOf('.')+1) + "_idx1";
        runStatement("CREATE INDEX " + name + " ON " + NAME + " (code, sab) TABLESPACE " + SCHEMA);
      } else if (COLS.containsKey("CUI1") && COLS.containsKey("AUI1")) {
        String name = "X_" + NAME.substring(NAME.indexOf('.')+1) + "_idx1";
        //String name = NAME + "_idx1";
        runStatement("CREATE INDEX " + name + " ON " + NAME + " (cui1, aui1) TABLESPACE " + SCHEMA);
      }
    }
  }
  
  private static class Column {
    String NAME;
    String FILE;
    String DESC;
    String TYPE;
    boolean NULLABLE;
    private boolean isIntType() {
      return TYPE != null && TYPE.contains("int");
    }
    private int getType() {
      if (TYPE.contains("int")) {
        return Types.INTEGER;
      } else if (TYPE.contains("numeric")) {
        return Types.NUMERIC;
      }
      return Types.VARCHAR;
    }
  }
}

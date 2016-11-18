package tikape.runko.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.net.*;

public class Database {

    private String databaseAddress;
    private boolean debug;

    public Database(String databaseAddress) throws Exception {
        this.databaseAddress = databaseAddress;

        init();
    }

    private void init() {
        List<String> lauseet = null;
        Lauseet generoiLauseet = new Lauseet();
        System.out.println("Generoidaan tietokanta osoitteella: "+this.databaseAddress);
        if (this.databaseAddress.contains("postgres")) {
            lauseet = generoiLauseet.postgreLauseet();
        } else {
            lauseet = generoiLauseet.sqliteLauseet();
        }
        
       
        // "try with resources" sulkee resurssin automaattisesti lopuksi
        try (Connection conn = getConnection()) {
            Statement st = conn.createStatement();

            // suoritetaan komennot
            for (String lause : lauseet) {
                System.out.println("Running command >> " + lause);
                try{                            //jatketaan vaikka joku lauseista failaa.
                    st.executeUpdate(lause);
                }catch(Throwable t){
                    System.out.println(t.getMessage());
                    continue;
                }
            }

        } catch (Throwable t) {
            //jos tietokantataulu on jo olemassa, ei komentoja suoriteta
            System.out.println("Error >> " + t.getMessage());
            
            
            
        }
    }

    public Connection getConnection() throws SQLException {
        if (this.databaseAddress.contains("postgres")) {
            try {
                URI dbUri = new URI(databaseAddress);

                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

                return DriverManager.getConnection(dbUrl, username, password);
            } catch (Throwable t) {
                System.out.println("Error: " + t.getMessage());
                t.printStackTrace();
            }
        }

        return DriverManager.getConnection(databaseAddress);
    }
    
    
    //kopiointia toisesta database-luokasta:
    
    public void setDebugMode(boolean d) {
        debug = d;
    }
    
    public int update(String updateQuery, Object... params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(updateQuery);

        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }

        int changes = stmt.executeUpdate();

        if (debug) {
            System.out.println("---");
            System.out.println(updateQuery);
            System.out.println("Changed rows: " + changes);
            System.out.println("---");
        }

        stmt.close();
        conn.close();

        return changes;
    }
    
    public String getDatabaseAddress(){
        return this.databaseAddress;
    }
}


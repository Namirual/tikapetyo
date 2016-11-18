package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Otsake;
import tikape.runko.domain.Viesti;
import java.sql.Timestamp;

public class ViestiDao {

    private Database database;

    public ViestiDao(Database database) {
        this.database = database;
    }

    public List<Viesti> findKeskustelu(int keskustelu) throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT "
                + "Kayttaja.nimi AS nimi, Viesti.viesti AS viesti, Viesti.luontiaika AS luontiaika, Viesti.id as id, Kayttaja.id AS kayttajaID"
                + " FROM Viesti, Kayttaja WHERE Viesti.luoja = Kayttaja.id AND Viesti.keskustelu = ? ORDER BY id ASC");

        stmt.setInt(1, keskustelu);

        ResultSet rs = stmt.executeQuery();
        List<Viesti> viestit = new ArrayList<>();
        int luku = 0;
        
        
        while (rs.next()) {
            luku++;
            String luoja = rs.getString("nimi");
            String viesti = rs.getString("viesti");
            Timestamp luontiaika = rs.getTimestamp("luontiaika");
            String id = rs.getString("id");
            int kID = rs.getInt("kayttajaID");
            viestit.add(new Viesti(Integer.toString(luku), luoja, viesti, luontiaika, id, kID));
        }
        rs.close();
        stmt.close();
        connection.close();

        return viestit;
    }
    
    public Timestamp uusinAlueella(int alue) throws SQLException{
        Connection c = database.getConnection();
        
        PreparedStatement stmt = c.prepareStatement(""
                +"SELECT Viesti.luontiaika AS aika FROM Viesti, Keskustelu, Alue "
                +"WHERE Keskustelu.alue=Alue.id " 
                +"AND Viesti.keskustelu = Keskustelu.id "
                +"AND Alue.id = ? "
                +"ORDER BY aika DESC "
                +"LIMIT 1");
        
        stmt.setInt(1, alue);
        ResultSet rs = stmt.executeQuery();
        Timestamp aika = null;
        while(rs.next()){
            aika = rs.getTimestamp("aika");
        }
        rs.close();
        stmt.close();
        c.close();
        
        return aika;
    }
    
    public Timestamp uusinKeskustelussa(int keskustelu) throws SQLException{
        Connection c = database.getConnection();
        
        PreparedStatement stmt = c.prepareStatement(""
                +"SELECT Viesti.luontiaika AS aika FROM Viesti, Keskustelu "
                +"WHERE Viesti.keskustelu = Keskustelu.id "
                +"AND Keskustelu.id = ? "
                +"ORDER BY aika DESC "
                +"LIMIT 1");
        
        stmt.setInt(1, keskustelu);
        ResultSet rs = stmt.executeQuery();
        Timestamp aika = null;
        while(rs.next()){
            aika = rs.getTimestamp("aika");
        }
        rs.close();
        stmt.close();
        c.close();
        
        return aika;
    }
    
    public String lkmKeskustelussa(int keskustelu) throws SQLException{
        Connection c = database.getConnection();
        
        PreparedStatement stmt = c.prepareStatement(""
            +"SELECT Keskustelu.otsikko, COUNT(Viesti.id) AS lkm FROM Viesti, Keskustelu "
            +"WHERE Keskustelu.id = Viesti.keskustelu "
            +"AND Keskustelu.id = ? "
            +"GROUP BY Keskustelu.otsikko ");
        
        stmt.setInt(1, keskustelu);
        ResultSet rs = stmt.executeQuery();
        String lkm = null;
        while(rs.next()){
            lkm = rs.getString("lkm");
        }
        rs.close();
        stmt.close();
        c.close();
        
        return lkm;
    }
    
    public String lkmAlueella(int alue) throws SQLException{
        Connection c = database.getConnection();
        
        PreparedStatement stmt = c.prepareStatement(""
            +"SELECT Alue.nimi, COUNT(Viesti.id) AS lkm FROM Viesti, Keskustelu, Alue "
            +"WHERE Alue.id = Keskustelu.alue "
            +"AND Keskustelu.id = Viesti.keskustelu "
            +"AND Alue.id = ? "
            +"GROUP BY Alue.nimi ");
        
        stmt.setInt(1, alue);
        ResultSet rs = stmt.executeQuery();
        String lkm = null;
        while(rs.next()){
            lkm = rs.getString("lkm");
        }
        rs.close();
        stmt.close();
        c.close();
        
        return lkm;
    }

    public void createVoid(String keskustelu, String viesti, int luoja) throws SQLException {
        KayttajaDao kaDao = new KayttajaDao(database);
        
        if(kaDao.findLuokka(luoja)==0){
            return;
        }
        
        
        
        int k = Integer.parseInt(keskustelu);
        
        if (!database.getDatabaseAddress().contains("postgres")) {
            database.update("INSERT INTO Viesti (keskustelu, luoja, viesti, luontiaika) VALUES (?, ?, ?, strftime('%Y-%m-%d %H:%M:%f', 'now'));", k,luoja, viesti);
            return;
        }
        
        
        database.update("INSERT INTO Viesti (keskustelu, luoja, viesti, luontiaika) VALUES (?, ?, ?, CURRENT_TIMESTAMP);", k,luoja, viesti);
    }
    
    public void delete(int id) throws SQLException{
        database.update("DELETE FROM Viesti WHERE Viesti.id = ?", id);
    }
    
    public String missaKeskustelussa(int id) throws SQLException{
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT Keskustelu.id AS id FROM Viesti, Keskustelu "
                +"WHERE Keskustelu.id = Viesti.keskustelu "
                +"AND Viesti.id = ? ");

        stmt.setInt(1, id);

        ResultSet rs = stmt.executeQuery();
        String keskusteluID = "";
        
        while (rs.next()) {
            keskusteluID = rs.getString("id");
            
        }
        rs.close();
        stmt.close();
        connection.close();
        
        return keskusteluID;
    }
    
}

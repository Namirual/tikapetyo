package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Otsake;
import tikape.runko.domain.Viesti;
import tikape.runko.domain.Kayttaja;

public class KayttajaDao {

    private Database database;

    public KayttajaDao(Database database) {
        this.database = database;
    }

    public int findIdFromKayttajatunnus(String tunnus) throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT id FROM Kayttaja WHERE tunnus = ?");

        stmt.setString(1, tunnus);

        ResultSet rs = stmt.executeQuery();
        //List<Viesti> viestit = new ArrayList<>();

        int id = -1;

        while (rs.next()) {
            id = rs.getInt("id");
        }

        rs.close();
        stmt.close();
        connection.close();

        return id;
    }

    public Kayttaja findByTunnusAndSalasana(String tunnus, String salasana) throws SQLException {
        Connection connection = database.getConnection();

        List<Kayttaja> kayttaja = new ArrayList();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM kayttaja WHERE tunnus = ? AND salasana = ?");

        stmt.setString(1, tunnus);
        stmt.setString(2, salasana);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("id");
            String email = rs.getString("email");
            String nimi = rs.getString("nimi");
            int kayttajataso = rs.getInt("kayttajataso");
            kayttaja.add(new Kayttaja(id, email, tunnus, salasana, nimi, kayttajataso));
        }
        
        rs.close();
        stmt.close();
        connection.close();

        if (kayttaja.isEmpty()) {
            return null;
        }
        return kayttaja.get(0);
        
        
    }

    public Kayttaja findById(int id) throws SQLException {
        Connection connection = database.getConnection();

        List<Kayttaja> kayttaja = new ArrayList();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM kayttaja WHERE id = ?");

        stmt.setInt(1, id);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String email = rs.getString("email");
            String nimi = rs.getString("nimi");
            String tunnus = rs.getString("tunnus");
            String salasana = rs.getString("salasana");
            int kayttajataso = rs.getInt("kayttajataso");
            kayttaja.add(new Kayttaja(id, email, tunnus, salasana, nimi, kayttajataso));
        }
        
        rs.close();
        stmt.close();
        connection.close();

        if (kayttaja.isEmpty()) {
            return null;
        }
        return kayttaja.get(0);
    }

    public void createKayttaja(String email, String tunnus, String salasana, String nimi) throws SQLException {
        database.update("INSERT INTO Kayttaja (email, tunnus, salasana, nimi, kayttajataso) VALUES (?, ?, ?, ?, 1)", email, tunnus, salasana, nimi);
    }
    
    public int findLuokka(int id) throws SQLException{
        Connection connection = database.getConnection();

        PreparedStatement stmt = connection.prepareStatement("SELECT Kayttaja.kayttajataso FROM kayttaja WHERE id = ?");

        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        
        int luokka = 0;
        
        while(rs.next()){
            luokka = rs.getInt("kayttajataso");
        }
        
        rs.close();
        stmt.close();
        connection.close();
        
        
        return luokka;
    }
    
    //toistoa mutta pidän vielä hetken
    public void vaihdaLuokka(int id, int luokka) throws SQLException{
        database.update("UPDATE Kayttaja SET kayttajataso = ? WHERE Kayttaja.id = ?", luokka, id);
    }
    
    public List<Kayttaja> findAll() throws SQLException {
        List<Kayttaja> kayttajat = new ArrayList<>();
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Kayttaja");


        ResultSet rs = stmt.executeQuery();
        //List<Viesti> viestit = new ArrayList<>();




        while (rs.next()) {
            int id = rs.getInt("id");
            String email = rs.getString("email");
            String tunnus = rs.getString("tunnus");
            String salasana = rs.getString("salasana");
            String nimi = rs.getString("nimi");
            int kayttajataso = rs.getInt("kayttajataso");
            kayttajat.add(new Kayttaja(id, email, tunnus, salasana, nimi, kayttajataso));
        }


        rs.close();
        stmt.close();
        connection.close();
        return kayttajat;
    }
    
    public void editKayttajataso(int id, int taso) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("UPDATE Kayttaja SET kayttajataso = ? WHERE id = ?");
        stmt.setInt(1, taso);
        stmt.setInt(2, id);
        stmt.execute();
        stmt.close();
        connection.close(); 
    }
    
    public void editNimi(int id, String nimi) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("UPDATE Kayttaja SET nimi = ? WHERE id = ?");
        stmt.setString(1, nimi);
        stmt.setInt(2, id);
        stmt.execute();
        stmt.close();
        connection.close(); 
    }


}

package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Otsake;
import java.sql.Timestamp;
import tikape.runko.domain.Viesti;

public class KeskusteluDao {

    private Database database;

    public KeskusteluDao(Database database) {
        this.database = database;
    }

    public List<Otsake> findAlue(int alue) throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Keskustelu WHERE alue = ? ORDER BY luontiaika DESC");

        stmt.setInt(1, alue);

        ResultSet rs = stmt.executeQuery();
        List<Otsake> alueet = new ArrayList<>();
        while (rs.next()) {
            String otsikko = rs.getString("otsikko");
            Timestamp luontiaika = rs.getTimestamp("luontiaika");
            String id = rs.getString("id");
            alueet.add(new Otsake(otsikko, "0", luontiaika, id));
        }
        rs.close();
        stmt.close();
        connection.close();

        return alueet;
    }

    public List<Otsake> findKeskusteluAndAlue(int id) throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT Keskustelu.id as k_id, "
                + "Keskustelu.otsikko as k_otsikko, Keskustelu.luontiaika as k_luontiaika, "
                + "Alue.id as a_id, Alue.nimi as a_nimi, Alue.luontiaika as a_luontiaika "
                + "FROM Keskustelu, Alue WHERE Keskustelu.Alue = Alue.id AND Keskustelu.id = ?");

        stmt.setInt(1, id);

        ResultSet rs = stmt.executeQuery();
        List<Otsake> otsikot = new ArrayList<>();
        while (rs.next()) {
            String k_otsikko = rs.getString("k_otsikko");
            Timestamp k_luontiaika = rs.getTimestamp("k_luontiaika");
            String k_id = rs.getString("k_id");

            String a_nimi = rs.getString("a_nimi");
            Timestamp a_luontiaika = rs.getTimestamp("a_luontiaika");
            String a_id = rs.getString("a_id");
            otsikot.add(new Otsake(k_otsikko, "0", k_luontiaika, k_id));
            otsikot.add(new Otsake(a_nimi, "0", a_luontiaika, a_id));
        }
        rs.close();
        stmt.close();
        connection.close();

        return otsikot;
    }

    public void createVoid(String alue, String otsikko, int luoja) throws SQLException {
        int a = Integer.parseInt(alue);
        KayttajaDao kaDao = new KayttajaDao(database);
        
        if(kaDao.findLuokka(luoja)==0){
            return;
        }
        

        if (!database.getDatabaseAddress().contains("postgres")) {
            database.update("INSERT INTO Keskustelu (alue, luoja, otsikko, luontiaika) "
                    + "VALUES (?, ?, ?, strftime('%Y-%m-%d %H:%M:%f', 'now'))", a,luoja, otsikko);
            return;
        }

        database.update("INSERT INTO Keskustelu (alue, luoja, otsikko, luontiaika) "
                + "VALUES (?, ?, ?, CURRENT_TIMESTAMP)", a,luoja, otsikko);
    }

    public void delete(int keskustelu) throws SQLException {
        ViestiDao vDao = new ViestiDao(database);
        List<Viesti> viestit = vDao.findKeskustelu(keskustelu);

        for (Viesti viesti : viestit) {
            database.update("DELETE FROM Viesti WHERE Viesti.id = ?", Integer.parseInt(viesti.getLinkki()));
        }
        database.update("DELETE FROM Keskustelu WHERE Keskustelu.id = ?", keskustelu);

    }

    public String millaAlueella(int keskustelu) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT Alue.id AS id FROM Alue, Keskustelu "
                + "WHERE Alue.id = Keskustelu.alue "
                + "AND Keskustelu.id = ? ");

        stmt.setInt(1, keskustelu);

        ResultSet rs = stmt.executeQuery();
        String alueID = "";

        while (rs.next()) {
            alueID = rs.getString("id");

        }
        rs.close();
        stmt.close();
        connection.close();

        return alueID;
    }
    
    public List<Otsake> findKeskustelutByLuoja(int kayttaja_id) throws SQLException {


        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Keskustelu WHERE luoja = ? ORDER BY luontiaika DESC");


        stmt.setInt(1, kayttaja_id);


        ResultSet rs = stmt.executeQuery();
        List<Otsake> alueet = new ArrayList<>();
        while (rs.next()) {
            String otsikko = rs.getString("otsikko");
            Timestamp luontiaika = rs.getTimestamp("luontiaika");
            String id = rs.getString("id");
            alueet.add(new Otsake(otsikko, "0", luontiaika, id));
        }
        rs.close();
        stmt.close();
        connection.close();


        return alueet;
    }

}

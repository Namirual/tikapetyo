
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

public class AlueDao {
    
    private Database database;
    
    public AlueDao(Database database) {
        this.database = database;
    }
    
    public List<Otsake> findAll() throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement(""
                +"SELECT * From Alue "
                +"ORDER BY luontiaika DESC");

        ResultSet rs = stmt.executeQuery();
        List<Otsake> alueet = new ArrayList<>();
        while (rs.next()) {
            String nimi = rs.getString("nimi");
            Timestamp luontiaika = rs.getTimestamp("luontiaika");
            String id = rs.getString("id");
            alueet.add(new Otsake(nimi, "0", luontiaika, id));
        }
        rs.close();
        stmt.close();
        connection.close();
        
        return alueet;
    }
    
    public void createVoid(String name, int luoja) throws SQLException{
        KayttajaDao kaDao = new KayttajaDao(database);
        
        if(kaDao.findLuokka(luoja)==0){
            return;
        }
        
        
        if (!database.getDatabaseAddress().contains("postgres")) {
            database.update("INSERT INTO Alue (luoja, nimi, luontiaika) VALUES (?,?, strftime('%Y-%m-%d %H:%M:%f', 'now'))",luoja,name);
            return;
        }
        
        database.update("INSERT INTO Alue (luoja, nimi, luontiaika) VALUES (?,?, CURRENT_TIMESTAMP)",luoja,name);
    }
    
    public void delete(String id) throws SQLException{
        ViestiDao vDao = new ViestiDao(database);
        KeskusteluDao kDao = new KeskusteluDao(database);
        
        List<Otsake> keskustelut = kDao.findAlue(Integer.parseInt(id));
        
        for(Otsake otsake : keskustelut){
            List<Viesti> viestit = vDao.findKeskustelu(Integer.parseInt(otsake.getOsoite()));
            for(Viesti viesti : viestit){
                database.update("DELETE FROM Viesti WHERE Viesti.id = ?", Integer.parseInt(viesti.getLinkki()));
            }
            database.update("DELETE FROM Keskustelu WHERE Keskustelu.id = ?", Integer.parseInt(otsake.getOsoite()));
        }
        database.update("DELETE FROM Alue WHERE Alue.id = ?", Integer.parseInt(id));
    }
    
    public Otsake findByTunnus(String id) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement(""
                +"SELECT * From Alue WHERE id = ?");
        int intID = Integer.parseInt(id);
        
        stmt.setInt(1, intID);
        
        ResultSet rs = stmt.executeQuery();
        List<Otsake> alueet = new ArrayList<>();
        while (rs.next()) {
            String nimi = rs.getString("nimi");
            Timestamp luontiaika = rs.getTimestamp("luontiaika");
            alueet.add(new Otsake(nimi, "0", luontiaika, String.valueOf(id)));
        }
        rs.close();
        stmt.close();
        connection.close();
        return alueet.get(0);
    }

}

package tikape.runko.database;

import java.util.ArrayList;
import java.util.List;

public class Lauseet {
    
    public Lauseet(){
        
    }
    
    public List<String> postgreLauseet() {
        ArrayList<String> lista = new ArrayList<>();

        // tietokantataulujen luomiseen tarvittavat komennot suoritusjärjestyksessä
        // heroku käyttää SERIAL-avainsanaa uuden tunnuksen automaattiseen luomiseen
        
        //pudotus, voi ottaa pois jos haluaa:
        lista.add("DROP TABLE Kayttaja, Alue, Keskustelu, Viesti CASCADE;");
        
        lista.add("CREATE TABLE Kayttaja ("+
            "id SERIAL PRIMARY KEY,"+
            "email varchar(200) NOT NULL UNIQUE,"+
            "tunnus varchar(200) NOT NULL UNIQUE,"+
            "salasana varchar(200) NOT NULL,"+
            "nimi varchar(200) NOT NULL,"+
            "kayttajataso integer NOT NULL);");

        lista.add(" CREATE TABLE Alue ("+
            "id SERIAL PRIMARY KEY,"+
            "luoja integer NOT NULL,"+
            "nimi varchar(200) NOT NULL UNIQUE,"+
            "luontiaika timestamp NOT NULL,"+
            "FOREIGN KEY(luoja) REFERENCES Kayttaja(id)); ");

        lista.add("CREATE TABLE Keskustelu ("+
            "id SERIAL PRIMARY KEY,"+
            "alue integer NOT NULL,"+
            "luoja integer NOT NULL,"+
            "otsikko varchar(200) NOT NULL,"+ 
            "luontiaika timestamp NOT NULL,"+
            "FOREIGN KEY(alue) REFERENCES Alue(id),"+
            "FOREIGN KEY(luoja) REFERENCES Kayttaja(id));");

        lista.add("CREATE TABLE Viesti ("+
            "id SERIAL PRIMARY KEY,"+
            "keskustelu integer NOT NULL,"+
            "luoja integer NOT NULL,"+
            "viesti varchar(2000) NOT NULL,"+
            "luontiaika timestamp NOT NULL,"+
            "FOREIGN KEY(keskustelu) REFERENCES Keskustelu(id),"+
            "FOREIGN KEY(luoja) REFERENCES Kayttaja(id));");
        
        //testikäyttäjä:
        lista.add("INSERT INTO Kayttaja (email, tunnus, salasana, nimi, kayttajataso) VALUES ('email', 'tunnus', 'salasana', 'nimi', 1)");
        lista.add("INSERT INTO Kayttaja (email, tunnus, salasana, nimi, kayttajataso) VALUES ('email2', 'tunnus2', 'salasana', 'Mechanic', 1)");
        lista.add("INSERT INTO Kayttaja (email, tunnus, salasana, nimi, kayttajataso) VALUES ('email3', 'tunnus3', 'salasana', 'Operator', 1)");
        lista.add("INSERT INTO Kayttaja (email, tunnus, salasana, nimi, kayttajataso) VALUES ('email4', 'tunnus4', 'salasana', 'CATS', 1)");
        lista.add("INSERT INTO Kayttaja (email, tunnus, salasana, nimi, kayttajataso) VALUES ('email5', 'tunnus5', 'salasana', 'Captain', 1)");
        
        //masterkäyttäjä:
        lista.add("INSERT INTO Kayttaja (email, tunnus, salasana, nimi, kayttajataso) VALUES ('email7', 'master', 'master', 'master', 2)");
        
        //bannattu käyttäjä:
        lista.add("INSERT INTO Kayttaja (email, tunnus, salasana, nimi, kayttajataso) VALUES ('email8', 'bannattu', 'bannattu', 'pekka', 0)");
        
        
        //testialueita:
        lista.add("INSERT INTO Alue (luoja, nimi, luontiaika) VALUES (1, 'Kissakerho', CURRENT_TIMESTAMP)");
        lista.add("INSERT INTO Alue (luoja, nimi, luontiaika) VALUES (1, 'Koirakerho', CURRENT_TIMESTAMP)");
        
        //testikeskustelu
        lista.add("INSERT INTO Keskustelu (alue, luoja, otsikko, luontiaika) VALUES (1, 1, 'A Treatise on Cats by Willian Faulkner', CURRENT_TIMESTAMP) ");
        
        //bannattu viesti
        lista.add("INSERT INTO Viesti (keskustelu, luoja, viesti, luontiaika) VALUES (1, 7, 'Kissat on mälsiä', CURRENT_TIMESTAMP)");

        
        return lista;
    }
    
    public List<String> sqliteLauseet() {
        ArrayList<String> lista = new ArrayList<>();

        // tietokantataulujen luomiseen tarvittavat komennot suoritusjärjestyksessä
        //EI PUDOTETA VALMIITA TAULUKKOJA. Pudottaa voi vaikkapa deletoimalla db:n
        
        lista.add("PRAGMA foreign_keys = ON");
        
        lista.add("CREATE TABLE Kayttaja ("+
            "id integer PRIMARY KEY,"+
            "email varchar(200) NOT NULL UNIQUE,"+
            "tunnus varchar(200) NOT NULL UNIQUE,"+
            "salasana varchar(200) NOT NULL,"+
            "nimi varchar(200) NOT NULL,"+
            "kayttajataso integer NOT NULL); ");

        lista.add(" CREATE TABLE Alue ("+
            "id integer PRIMARY KEY,"+
            "luoja integer NOT NULL,"+
            "nimi varchar(200) NOT NULL UNIQUE,"+
            "luontiaika timestamp NOT NULL,"+
            "FOREIGN KEY(luoja) REFERENCES Kayttaja(id)); ");

        lista.add("CREATE TABLE Keskustelu ("+
            "id integer PRIMARY KEY,"+
            "alue integer NOT NULL,"+
            "luoja integer NOT NULL,"+
            "otsikko varchar(200) NOT NULL,"+ 
            "luontiaika timestamp NOT NULL,"+
            "FOREIGN KEY(alue) REFERENCES Alue(id), "+
            "FOREIGN KEY(luoja) REFERENCES Kayttaja(id));");

        lista.add("CREATE TABLE Viesti ("+
            "id integer PRIMARY KEY,"+
            "keskustelu integer NOT NULL,"+
            "luoja integer NOT NULL,"+
            "viesti varchar(2000) NOT NULL,"+
            "luontiaika timestamp NOT NULL,"+
            "FOREIGN KEY(keskustelu) REFERENCES Keskustelu(id),"+
            "FOREIGN KEY(luoja) REFERENCES Kayttaja(id));");
        
        lista.add("INSERT INTO Kayttaja VALUES (1, 'email', 'tunnus', 'salasana', 'nimi', 1)");
        lista.add("INSERT INTO Kayttaja VALUES (2, 'email2', 'tunnus2', 'salasana', 'Mechanic', 1)");
        lista.add("INSERT INTO Kayttaja VALUES (3, 'email3', 'tunnus3', 'salasana', 'Operator', 1)");
        lista.add("INSERT INTO Kayttaja VALUES (4, 'email4', 'tunnus4', 'salasana', 'CATS', 1)");
        lista.add("INSERT INTO Kayttaja VALUES (5, 'email5', 'tunnus5', 'salasana', 'Captain', 1)");
        
        //master käyttäjä:
        lista.add("INSERT INTO Kayttaja VALUES (6, 'email7', 'master', 'master', 'master', 2)");
        
        //bannattu käyttäjä:
        lista.add("INSERT INTO Kayttaja (email, tunnus, salasana, nimi, kayttajataso) VALUES ('email8', 'bannattu', 'bannattu', 'pekka', 0)");
        
        //testialueita:
        lista.add("INSERT INTO Alue (luoja, nimi, luontiaika) VALUES (1, 'Kissakerho', strftime('%Y-%m-%d %H:%M:%f', 'now'))");
        lista.add("INSERT INTO Alue (luoja, nimi, luontiaika) VALUES (1, 'Koirakerho', strftime('%Y-%m-%d %H:%M:%f', 'now'))");
        
        //testikeskustelu
        lista.add("INSERT INTO Keskustelu (alue, luoja, otsikko, luontiaika) VALUES (1, 1, 'A Treatise on Cats by Willian Faulkner', strftime('%Y-%m-%d %H:%M:%f', 'now')) ");
        
        //bannattu viesti
        lista.add("INSERT INTO Viesti (keskustelu, luoja, viesti, luontiaika) VALUES (1, 7, 'Kissat on mälsiä', strftime('%Y-%m-%d %H:%M:%f', 'now'))");
        
        return lista;
    }
}
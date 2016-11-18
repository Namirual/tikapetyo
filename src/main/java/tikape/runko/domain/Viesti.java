
package tikape.runko.domain;

import java.sql.Timestamp;

public class Viesti {
    String jarjestys;
    String luoja;
    String viesti;
    Timestamp luontiaika;
    String linkki;
    int luojaID;

    public Viesti(String jarjestys, String luoja, String viesti, Timestamp luontiaika, String linkki, int luojaID) {
        this.jarjestys = jarjestys;
        this.luoja = luoja;
        this.viesti = viesti;
        this.luontiaika = luontiaika;
        this.linkki = linkki;
        this.luojaID = luojaID;
    }
    
    public int getLuojaID(){
        return this.luojaID;
    }
    
    public void setLuojaID(int id){
        this.luojaID = id;
    }
    
    public String getJarjestys() {
        return jarjestys;
    }
    
    public String getLuoja() {
        return luoja;
    }

    public String getViesti() {
        return viesti;
    }

    public String getLuontiaika() {
        String aikaString = luontiaika.toString();
        aikaString = aikaString.substring(0, 19);
        
        return aikaString;
    }

    public String getLinkki() {
        return linkki;
    }

    public void setJarjestys(String jarjestys) {
        this.jarjestys = jarjestys;
    }
    
    public void setLuoja(String luoja) {
        this.luoja = luoja;
    }

    public void setViesti(String viesti) {
        this.viesti = viesti;
    }

    public void setLuontiaika(Timestamp luontiaika) {
        this.luontiaika = luontiaika;
    }

    public void setLinkki(String linkki) {
        this.linkki = linkki;
    }
}
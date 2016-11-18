
package tikape.runko.domain;

import java.sql.Timestamp;
import java.util.Comparator;

public class Otsake implements Comparable<Otsake> {
    
    private String otsikko;
    private String viestit;
    private Timestamp aika;
    private Timestamp luontiaika;
    private String osoite;

    public Otsake(String otsikko, String viestit, Timestamp luontiaika, String osoite) {
        this.otsikko = otsikko;
        this.viestit = viestit;
        this.luontiaika = luontiaika;
        this.osoite = osoite;
    }

    public String getAika() {
        if(aika==null){
            return "";
        }
        
        String aikaString = aika.toString();
        aikaString = aikaString.substring(0,19);
        
        return aikaString;
    }

    public String getOtsikko() {
        return otsikko;
    }

    public String getViestit() {
        return viestit;
    }

    public String getOsoite() {
        return osoite;
    }
    
    public void setAika(Timestamp aika) {
        this.aika = aika;
    }

    public void setOtsikko(String otsikko) {
        this.otsikko = otsikko;
    }

    public void setViestit(String viestit) {
        this.viestit = viestit;
    }    

    public void setOsoite(String osoite) {
        this.osoite = osoite;
    }
    
    @Override
    public int compareTo(Otsake verrattava) {
        if(this.aika.before(verrattava.aika)){
            return -1;
        }
        if(this.aika.after(verrattava.aika)){
            return 1;
        }
        return 0;
    }
    
    public static Comparator<Otsake> aikaVertailu
                          = new Comparator<Otsake>() {

	    public int compare(Otsake o1, Otsake o2) {
                if(o1.aika==null){
                    return 1;
                }
                if(o2.aika==null){
                    return -1;
                }
                
                if(o1.aika.before(o2.aika)){
                    return 1;
                }
                if(o1.aika.after(o2.aika)){
                    return -1;
                }
                return 0;
	    }

    };
}
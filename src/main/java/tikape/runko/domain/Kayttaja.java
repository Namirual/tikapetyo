/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.domain;

/**
 *
* @author eero sipinen
 */

public class Kayttaja {
    
    private Integer id;
    private String email;
    private String kayttajanimi;
    private String salasana;
    private String nimi;
    private int kayttajataso;


    public Kayttaja(Integer id, String email, String kayttajanimi, String salasana, String nimi, int kayttajataso) {
        this.id = id;
        this.email = email;
        this.nimi = nimi;
        this.kayttajanimi = kayttajanimi;
        this.salasana = salasana;
        this.kayttajataso = kayttajataso;
    }
    
     public Kayttaja(Integer id, String nimi){
        this.id = id;
        this.email = "email";
        this.nimi = nimi;
        this.kayttajanimi = "12";
        this.salasana ="12";
        this.kayttajataso = 1;
    }
   


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }
    
     public String getKayttajanimi() {
        return kayttajanimi;
    }

    public void setKayttajanimi(String kayttajanimi) {
        this.kayttajanimi = kayttajanimi;
    }

    
    public String getSalasana() {
        return salasana;
    }

    public void setSalasana(String salasana) {
        this.salasana = salasana;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }

    public int getKayttajataso() {
        return kayttajataso;
    }

    public void setKayttajataso(int kayttajaTaso) {
        this.kayttajataso = kayttajaTaso;
    }
}

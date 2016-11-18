package tikape.runko;

import java.util.HashMap;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.Database;
import tikape.runko.database.AlueDao;
import tikape.runko.database.ViestiDao;
import tikape.runko.database.KeskusteluDao;
import tikape.runko.database.KayttajaDao;

import java.util.*;
import tikape.runko.domain.Otsake;
import tikape.runko.domain.Viesti;
import tikape.runko.database.Lauseet;
import java.sql.Timestamp;
import tikape.runko.domain.Kayttaja;

public class Main {
    
    public static void main(String[] args) throws Exception {

        if (System.getenv("PORT") != null) {
            port(Integer.valueOf(System.getenv("PORT")));
        }

        String jdbcOsoite = "jdbc:sqlite:kanta.db";
        if (System.getenv("DATABASE_URL") != null) {
            jdbcOsoite = System.getenv("DATABASE_URL");
            System.out.println("Saatiin uusi osoite: " + jdbcOsoite);
        }
        System.out.println("Luodaan database osoitteella: " + jdbcOsoite);
        Database database = new Database(jdbcOsoite);
        AlueDao aDao = new AlueDao(database);
        ViestiDao vDao = new ViestiDao(database);
        KeskusteluDao kDao = new KeskusteluDao(database);
        KayttajaDao kaDao = new KayttajaDao(database);

        get("/", (req, res) -> {
            HashMap data = new HashMap<>();
            List<Otsake> otsakkeet = aDao.findAll();

            for (Otsake otsake : otsakkeet) {
                Timestamp uusin = vDao.uusinAlueella(Integer.parseInt(otsake.getOsoite()));
                if (uusin != null) {
                    otsake.setAika(uusin);
                }

                String viestit = vDao.lkmAlueella(Integer.parseInt(otsake.getOsoite()));
                if (viestit != null) {
                    otsake.setViestit(viestit);
                }
            }

            otsakkeet.sort(Otsake.aikaVertailu);

            // selaamisfunktio alkaa tästä
            int sivu = 0;
            String sivuluku = req.queryParams("sivu");

            data.put("taakse", 0);

            if (sivuluku != null && Integer.parseInt(sivuluku) > 0) {
                data.put("taakse", 1);
            }

            if (sivuluku != null) {
                sivu = Integer.parseInt(sivuluku);
            }

            if (otsakkeet.size() > 10 + (sivu * 10)) {
                data.put("eteen", 1);
            } else {
                data.put("eteen", 0);
            }

            data.put("seuraava", sivu + 1);
            data.put("edellinen", sivu - 1);
            data.put("otsakkeet", otsakkeet.subList(sivu * 10, Math.min(sivu * 10 + 10, otsakkeet.size())));

            // Käyttäjäcookie tsekataan tässä:
            int kayttajaluku = -1;

            if (req.cookie("kayttajaid") != null) {
                kayttajaluku = Integer.parseInt(req.cookie("kayttajaid"));
            }

            Kayttaja kayttaja = kaDao.findById(kayttajaluku);

            data.put("kayttaja", kayttaja);

            return new ModelAndView(data, "index");
        }, new ThymeleafTemplateEngine());

        post("/", (req, res) -> {

            if (req.queryParams("login") != null) {
                Kayttaja kayttaja = kaDao.findByTunnusAndSalasana(req.queryParams("login"), req.queryParams("password"));

                if (kayttaja != null) {
                    res.cookie("kayttajaid", kayttaja.getId().toString(), 600);
                }
            }
            
            System.out.println("Toimiiks edes tässä? " + req.cookie("kayttajaid"));
            
            
            String name = req.queryParams("name");
            if (name != null && !name.isEmpty()) {
                if (req.cookie("kayttajaid") != null) {
                    aDao.createVoid(name, Integer.parseInt(req.cookie("kayttajaid")));
                }
            }

	    // katotaan pitääkö cookie poistaa
            if (req.queryParams("poistacookie") != null) {
                res.removeCookie("kayttajaid");
                res.redirect("/logout");
            }

            res.redirect("/");
            return "";
        });

        get("/luokayttaja/", (req, res) -> {
            HashMap data = new HashMap<>();
            return new ModelAndView(data, "luokayttaja");
        }, new ThymeleafTemplateEngine());
        
        get("/kayttajat/", (req, res) -> {


            HashMap data = new HashMap<>();
            List<Kayttaja> kayttajat = kaDao.findAll();
            int moderaattori = 0;
            
            //Oikeudenhallinta alkaa tÃ¤stÃ¤
            if (req.cookie("kayttajaid") != null) {
                Kayttaja kayttaja = kaDao.findById(Integer.parseInt(req.cookie("kayttajaid")));
                System.out.println("mitÃ¤s nyt sitten? " + kayttaja.getKayttajataso());
                if (kayttaja.getKayttajataso() == 2) {
                    System.out.print("MikÃ¤s nyt? ");
                    System.out.println(kayttaja.getKayttajataso());
                    moderaattori = 1;
                }
            }
            data.put("moderaattori", moderaattori);
            data.put("kayttajat",kayttajat);


            return new ModelAndView(data, "kayttajat");
        }, new ThymeleafTemplateEngine());

        
        post("/luokayttaja", (req, res) -> {

            if (req.queryParams("tunnus") != null) {
                System.out.println("OK so far");
                if (kaDao.findIdFromKayttajatunnus(req.queryParams("tunnus")) == -1) {
                    System.out.println("tuli tännekin");
                    kaDao.createKayttaja(req.queryParams("email"), req.queryParams("tunnus"), req.queryParams("salasana"),
                            req.queryParams("nimi"));
                }
            }
            res.redirect("/");
            return "";
        });

        get("/alue/:luku", (req, res) -> {
            HashMap data = new HashMap<>();

            List<Otsake> otsakkeet = kDao.findAlue(Integer.parseInt(req.params(":luku")));

            for (Otsake otsake : otsakkeet) {
                Timestamp uusin = vDao.uusinKeskustelussa(Integer.parseInt(otsake.getOsoite()));
                if (uusin != null) {
                    otsake.setAika(uusin);
                }

                String viestit = vDao.lkmKeskustelussa(Integer.parseInt(otsake.getOsoite()));
                if (viestit != null) {
                    otsake.setViestit(viestit);
                }
            }
                    
            otsakkeet.sort(Otsake.aikaVertailu);
            
            data.put("alue", "alue/" + req.params(":luku"));

            // selaamisfunktio alkaa tästä
            int sivu = 0;
            String sivuluku = req.queryParams("sivu");

            data.put("taakse", 0);

            if (sivuluku != null && Integer.parseInt(sivuluku) > 0) {
                data.put("taakse", 1);
            }

            if (sivuluku != null) {
                sivu = Integer.parseInt(sivuluku);
            }

            if (otsakkeet.size() > 10 + (sivu * 10)) {
                data.put("eteen", 1);
            } else {
                data.put("eteen", 0);
            }

	    //Oikeudenhallinta alkaa tästä
            int moderaattori = 0;
            if (req.cookie("kayttajaid") != null) {
                Kayttaja kayttaja = kaDao.findById(Integer.parseInt(req.cookie("kayttajaid")));
                System.out.println("mitäs nyt sitten? " + kayttaja.getKayttajataso());
                if (kayttaja.getKayttajataso() == 2) {
                    System.out.print("Mikäs nyt? ");
                    System.out.println(kayttaja.getKayttajataso());
                    moderaattori = 1;
                }
            }

		// Käyttäjäcookie tsekataan tässä:
            
            int kayttajaluku = -1;

            if (req.cookie("kayttajaid") != null) {
                kayttajaluku = Integer.parseInt(req.cookie("kayttajaid"));
            }

            Kayttaja kayttaja = kaDao.findById(kayttajaluku);

            data.put("kayttaja", kayttaja);
            data.put("moderaattori", moderaattori);



            data.put("seuraava", sivu + 1);
            data.put("edellinen", sivu - 1);
            data.put("otsakkeet", otsakkeet.subList(sivu * 10, Math.min(sivu * 10 + 10, otsakkeet.size())));
	    data.put("otsikko", aDao.findByTunnus(req.params(":luku")));


            return new ModelAndView(data, "alue");
        }, new ThymeleafTemplateEngine());

        post("/alue/:luku", (req, res) -> {

            String name = req.queryParams("name");
            
            if (req.cookie("kayttajaid") != null) {
                kDao.createVoid(req.params(":luku"), name, Integer.parseInt(req.cookie("kayttajaid")));
            }

            res.redirect("/alue/" + req.params(":luku"));
            

            return "";
        });

        get("/keskustelu/:luku", (req, res) -> {
            HashMap data = new HashMap<>();

            List<Viesti> viestit = new ArrayList<>();
            List<Otsake> otsikot = new ArrayList<>();
            List<Integer> sivut = new ArrayList<>();

            viestit = vDao.findKeskustelu(Integer.parseInt(req.params(":luku")));
            for(Viesti viesti : viestit){
                if(kaDao.findLuokka(viesti.getLuojaID())==0){
                    viesti.setLuoja(viesti.getLuoja()+" (BANNED)");
                }
            }
            
            
            otsikot = kDao.findKeskusteluAndAlue(Integer.parseInt(req.params(":luku")));
            int moderaattori = 0;

            data.put("keskusteluotsikko", otsikot.get(0));
            data.put("alueotsikko", otsikot.get(1));
            data.put("keskustelu", "keskustelu/" + req.params(":luku"));

            // selaamisfunktio alkaa tästä
            int sivu = 0;
            String sivuluku = req.queryParams("sivu");

            data.put("taakse", 0);

            if (sivuluku != null && Integer.parseInt(sivuluku) > 0) {
                data.put("taakse", 1);
            }

            if (sivuluku != null) {
                sivu = Integer.parseInt(sivuluku);
            }

            if (viestit.size() > 10 + (sivu * 10)) {
                data.put("eteen", 1);
            } else {
                data.put("eteen", 0);
            }

            data.put("seuraava", sivu + 1);
            data.put("edellinen", sivu - 1);
            data.put("viestit", viestit.subList(sivu * 10, Math.min(sivu * 10 + 10, viestit.size())));

            data.put("sivu", sivu); // tämän avulla tallennetaan sivunumero kun kirjoitetaan uusia viestejä

            //Oikeudenhallinta alkaa tästä
            if (req.cookie("kayttajaid") != null) {
                Kayttaja kayttaja = kaDao.findById(Integer.parseInt(req.cookie("kayttajaid")));
                System.out.println("mitäs nyt sitten? " + kayttaja.getKayttajataso());
                if (kayttaja.getKayttajataso() == 2) {
                    System.out.print("Mikäs nyt? ");
                    System.out.println(kayttaja.getKayttajataso());
                    moderaattori = 1;
                }
            }

            // Käyttäjäcookie tsekataan tässä:
            
            int kayttajaluku = -1;

            if (req.cookie("kayttajaid") != null) {
                kayttajaluku = Integer.parseInt(req.cookie("kayttajaid"));
            }

            Kayttaja kayttaja = kaDao.findById(kayttajaluku);

            data.put("kayttaja", kayttaja);
            data.put("moderaattori", moderaattori);

            for (int luku = 0; luku <= Integer.parseInt(req.params(":luku")); luku++) {
                sivut.add(luku);
            }

            return new ModelAndView(data, "keskustelu");
        }, new ThymeleafTemplateEngine());

        post("/keskustelu/:luku", (req, res) -> {
            String viesti = req.queryParams("viesti");

            if (req.cookie("kayttajaid") != null) {
                vDao.createVoid(req.params(":luku"), viesti, Integer.parseInt(req.cookie("kayttajaid")));
                res.redirect("/keskustelu/" + req.params(":luku") + "?sivu=" + req.queryParams("sivu"));
            }
            res.redirect("/keskustelu/" + req.params(":luku") + "?sivu=" + req.queryParams("sivu"));
            return "";
        });

        post("/delete/alue/:luku", (req, res) -> {
            String alueID = req.params(":luku");
            aDao.delete(alueID);

            res.redirect("/");
            return "";
        });

        post("/delete/keskustelu/:luku", (req, res) -> {
            String keskusteluID = req.params(":luku");
            String alueID = kDao.millaAlueella(Integer.parseInt(keskusteluID)); //Haetaan alue, mille palataan
            kDao.delete(Integer.parseInt(keskusteluID));

            res.redirect("/alue/" + alueID);
            return "";
        });

        post("/delete/viesti/:luku", (req, res) -> {
            String viestiID = req.params(":luku");
            String keskusteluID = vDao.missaKeskustelussa(Integer.parseInt(viestiID)); //Haetaan keskustelu, jolle palataan
            vDao.delete(Integer.parseInt(viestiID));

            res.redirect("/keskustelu/" + keskusteluID);
            return "";
        });

        post("/valtaa/keskustelu/:luku", (req, res) -> {
            String keskusteluID = req.params(":luku");
            vDao.createVoid(keskusteluID, "Somebody set up us the bomb.", 2);
            vDao.createVoid(keskusteluID, "Main screen turn on.", 3);
            vDao.createVoid(keskusteluID, "All your base are belong to us.", 4);
            vDao.createVoid(keskusteluID, "You have no chance to survive make your time.", 4);
            vDao.createVoid(keskusteluID, "Take off every 'ZIG'.", 5);
            vDao.createVoid(keskusteluID, "For great justice.", 5);

            res.redirect("/keskustelu/" + keskusteluID);
            return "";
        });
        
        //laurin dumpista kopioitu
        get("/kayttaja/:luku", (req, res) -> {
            HashMap data = new HashMap<>();
            
            int moderaattori = 0;
            
            if (req.cookie("kayttajaid") == null) {
                res.redirect("/");
            }
                        
            if (Integer.parseInt(req.cookie("kayttajaid")) != Integer.parseInt(req.params(":luku"))) {
                res.redirect("/");
            }
            
            List<Otsake> otsakkeet = kDao.findKeskustelutByLuoja(Integer.parseInt(req.cookie("kayttajaid")));


            for (Otsake otsake : otsakkeet) {
                Timestamp uusin = vDao.uusinKeskustelussa(Integer.parseInt(otsake.getOsoite()));
                if (uusin != null) {
                    otsake.setAika(uusin);
                }


                String viestit = vDao.lkmKeskustelussa(Integer.parseInt(otsake.getOsoite()));
                if (viestit != null) {
                    otsake.setViestit(viestit);
                }
            }


            data.put("alue", "alue/" + req.params(":luku"));


            // selaamisfunktio alkaa tästä
            int sivu = 0;
            String sivuluku = req.queryParams("sivu");
            
            data.put("kayttajaosoite", "kayttaja/" + req.params(":luku"));

            data.put("taakse", 0);


            if (sivuluku != null && Integer.parseInt(sivuluku) > 0) {
                data.put("taakse", 1);
            }


            if (sivuluku != null) {
                sivu = Integer.parseInt(sivuluku);
            }


            if (otsakkeet.size() > 10 + (sivu * 10)) {
                data.put("eteen", 1);
            } else {
                data.put("eteen", 0);
            }


            data.put("seuraava", sivu + 1);
            data.put("edellinen", sivu - 1);
            data.put("otsakkeet", otsakkeet.subList(sivu * 10, Math.min(sivu * 10 + 10, otsakkeet.size())));
            
            data.put("sivu", sivu); // tämän avulla tallennetaan sivunumero kun kirjoitetaan uusia viestejä


            //Oikeudenhallinta alkaa tästä
            if (req.cookie("kayttajaid") != null) {
                Kayttaja kayttaja = kaDao.findById(Integer.parseInt(req.cookie("kayttajaid")));
                System.out.println("mitäs nyt sitten? " + kayttaja.getKayttajataso());
                if (kayttaja.getKayttajataso() == 2) {
                    System.out.print("Mikäs nyt? ");
                    System.out.println(kayttaja.getKayttajataso());
                    moderaattori = 1;
                }
            }


            // Käyttäjäcookie tsekataan tässä:
            
            int kayttajaluku = -1;


            if (req.cookie("kayttajaid") != null) {
                kayttajaluku = Integer.parseInt(req.cookie("kayttajaid"));
            }


            Kayttaja kayttaja = kaDao.findById(kayttajaluku);


            data.put("kayttaja", kayttaja);
            data.put("moderaattori", moderaattori);


            return new ModelAndView(data, "kayttaja");
        }, new ThymeleafTemplateEngine());
        
        //omia kokeiluja
        post("/bannaa/kayttaja/:osoite/:kayttaja", (req, res) -> {
            int kayttajaID = Integer.parseInt(req.params(":kayttaja"));
            String keskusteluID = vDao.missaKeskustelussa(Integer.parseInt(req.params(":osoite"))); //Haetaan keskustelu, jolle palataan
            
            kaDao.vaihdaLuokka(kayttajaID, 0);

            res.redirect("/keskustelu/" + keskusteluID);
            return "";
        });
        
        post("/kayttajataso/:id", (req, res) -> {
           String id = req.params(":id");
           String taso = req.queryParams("valinta");
           kaDao.editKayttajataso(Integer.parseInt(id), Integer.parseInt(taso));
           res.redirect("/luokayttaja/");
           return "";
        });
        
        post("/kayttaja/:luku", (req, res) -> {


            if (req.cookie("kayttajaid") == null) {
                res.redirect("/");
            }


            if (Integer.parseInt(req.cookie("kayttajaid")) != Integer.parseInt(req.params(":luku"))) {
                res.redirect("/");
            }


            if (req.queryParams("uusinimi") != null) {
                System.out.println("FUNFUNFUN");
                System.out.println(req.queryParams("uusinimi"));
                kaDao.editNimi(Integer.parseInt(req.cookie("kayttajaid")), req.queryParams("uusinimi"));
            }
            
            res.redirect("/kayttaja/" + req.params(":luku"));
            return "";
        });
        
        get("/virtualtour", (req, res) -> {
            HashMap<String, String> data = new HashMap();

        return new ModelAndView(data, "virtualtour");
        }, new ThymeleafTemplateEngine());

        get("/logout", (req, res) -> {
            HashMap<String, String> data = new HashMap();

        return new ModelAndView(data, "logout");
        }, new ThymeleafTemplateEngine());

    }
}

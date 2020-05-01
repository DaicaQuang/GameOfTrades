package io.gameoftrades.student17;

import io.gameoftrades.model.Wereld;
import io.gameoftrades.model.kaart.Coordinaat;
import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Stad;
import io.gameoftrades.model.kaart.Terrein;
import io.gameoftrades.model.kaart.TerreinType;
import io.gameoftrades.model.lader.WereldLader;
import io.gameoftrades.model.markt.Handel;
import io.gameoftrades.model.markt.HandelType;
import io.gameoftrades.model.markt.Handelswaar;
import io.gameoftrades.model.markt.Markt;

import java.io.InputStream;
import java.util.Scanner;

import java.util.*;

/*
 * Project: Game of Trades
 * Authors: Quang Hang, Alexander Franse, Tim Bekema
 * Group: got18-team17
 * Date: 14-10-2018
 */
public class WereldLaderImpl implements WereldLader {

    private Kaart kaart;
    private List<Stad> steden;
    private Scanner sc;
    private int mapBreedte;
    private int mapHoogte;

    @Override
    public Wereld laad(String resource) {
        //
        // Gebruik this.getClass().getResourceAsStream(resource) om een resource van het classpath te lezen.
        //
        // Kijk in src/test/resources voor voorbeeld kaarten.
        //
        // TODO Laad de wereld!
        //

        // laad de text file (map) in
        InputStream inputStream = this.getClass().getResourceAsStream(resource);

        // scanner om elke regel uit de file te lezen
        this.sc = new Scanner(inputStream);

        bepaalKaart();
        voegStedenToe();
        Markt markt = maakMarktAan();
        sc.close();

        return new Wereld(kaart, steden, markt);
    }

    public Markt maakMarktAan() {
        //Markt
        int aantalHandels = Integer.parseInt(this.sc.nextLine().trim());

        // nieuwe handel list aanmaken omdat in de constructor van markt een List meegegeven wordt
        List<Handel> handels = new ArrayList<>();

        // loop over alle handelroutes heen
        for (int i = 0; i < aantalHandels; i++) {
            // pak een lijn uit txt bestand en splist het op de komma
            String[] handelRegel = this.sc.nextLine().trim().split(",");

            String stadsNaam = handelRegel[0];
            HandelType ht;

            // bepaal het handeltype a.d.h.v. de string uit het txt bestand
            if ("BIEDT".equals(handelRegel[1])) {
                ht = HandelType.BIEDT;
            } else if ("VRAAGT".equals(handelRegel[1])) {
                ht = HandelType.VRAAGT;
            } else {
                throw new IllegalArgumentException();
            }

            // handelswaar, schapen stenen enz.
            String soortHandelsWaar = handelRegel[2];
            Handelswaar hw = new Handelswaar(soortHandelsWaar);

            // prijs van de handel (laatste cijfer op de regel in txt bestand)
            int prijs = Integer.parseInt(handelRegel[3]);

            // zorg ervoor dat je een Stad (type) kan meegeven aan de constructor
            // van Handel() want stadsNaam/handelRegel[0] is van het type String!
            Stad std = null;

            // door de lijst van steden heen die je al hebt gemaakt, en dan een
            // waarde toe kennen aan std.
            for (int j = 0; j < steden.size(); j++) {
                // controleer of de stadsnaam bekend is in de lijst van steden
                if (steden.get(j).getNaam().equals(stadsNaam)) {
                    std = steden.get(j);
                }
            }

            // vul de lijst met Handel objecten
            handels.add(new Handel(std, ht, hw, prijs));
        }
        return new Markt(handels);
    }

    public List<Stad> voegStedenToe() {
        steden = new ArrayList<>();
        String aantalSteden = this.sc.nextLine().trim();
        int maxSteden = Integer.parseInt(aantalSteden);

        int stadTeller = 0;

        for (int i = 0; i < maxSteden; i++) {
            if (this.sc.hasNextLine()) {
                String stedenRegel[] = this.sc.nextLine().trim().split(",");
                int xStad = Integer.parseInt(stedenRegel[0]) - 1;
                int yStad = Integer.parseInt(stedenRegel[1]) - 1;

//              Check of de positie van de stad overeenkomt met de mapbreedte- en hoogte, zo niet gooi exception
                if (xStad < 0 || xStad >= this.mapBreedte || yStad < 0 || yStad >= this.mapHoogte) {
                    throw new IllegalArgumentException();
                }
                Coordinaat stadCoordinaat = Coordinaat.op(xStad, yStad);
                String naamStad = stedenRegel[2];
                steden.add(new Stad(stadCoordinaat, naamStad));
                stadTeller++;
            }
        }
        return steden;
    }

    public Kaart bepaalKaart() {
        // de eerste regel zijn de breedte en hoogte coordinaten gescheiden door een komma
        String[] line = this.sc.nextLine().trim().split(",");
        this.mapBreedte = Integer.parseInt(line[0]);
        this.mapHoogte = Integer.parseInt(line[1]);

        kaart = new Kaart(mapBreedte, mapHoogte);

        for (int YCoordinaat = 0; YCoordinaat < this.mapHoogte; YCoordinaat++) {
            if (this.sc.hasNextInt()) {
                throw new IllegalArgumentException();
            }
            if (this.sc.hasNext()) {
                // check of de kaartregel en mapbreedte aan elkaar gelijk zijn, zo niet gooi een exception
                String kaartRegel = this.sc.nextLine().trim();
                if (kaartRegel.length() != mapBreedte) {
                    throw new IllegalArgumentException();
                }
                // loop door een regel en bepaal van elke coordinaat het type
                for (int XCoordinaat = 0; XCoordinaat < kaartRegel.length(); XCoordinaat++) {
                    char coordinaatType = kaartRegel.charAt(XCoordinaat);
                    // Verkrijg het coordinaat van de huidige leter.
                    Coordinaat terreinCoordinaat = Coordinaat.op(XCoordinaat, YCoordinaat);
                    // Vorm de leter om naar een tereintype
                    TerreinType type = TerreinType.fromLetter(coordinaatType);

                    // maak het terrein aan
                    Terrein terrein = new Terrein(kaart, terreinCoordinaat, type);
                }
            }
        }
        return kaart;
    }

}

package io.gameoftrades.student17;

import io.gameoftrades.student17.HandelaarImpl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import io.gameoftrades.model.Handelaar;
import io.gameoftrades.model.Wereld;
import io.gameoftrades.model.algoritme.HandelsplanAlgoritme;
import io.gameoftrades.model.algoritme.SnelstePadAlgoritme;
import io.gameoftrades.model.algoritme.StedenTourAlgoritme;
import io.gameoftrades.model.kaart.Coordinaat;
import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Pad;
import io.gameoftrades.model.kaart.Stad;
import io.gameoftrades.model.lader.WereldLader;
import io.gameoftrades.model.markt.Handelsplan;
import io.gameoftrades.model.markt.actie.Actie;
import io.gameoftrades.model.markt.actie.BeweegActie;
import io.gameoftrades.model.markt.actie.HandelsPositie;

/**
 * Een verzameling eenvoudige tests om te kijken of de handelaar werkt.
 *
 * Breid deze uit en of maak je eigen oplossing specifieke tests.
 */
public class HandelaarImplTest {

    private Handelaar handelaar;

    @Before
    public void init() {
        handelaar = new HandelaarImpl();
    }

    @Test
    public void zouWereldMoetenLaden() {
        WereldLader lader = handelaar.nieuweWereldLader();
        assertNotNull(lader);

        Wereld wereld = lader.laad("/kaarten/voorbeeld-kaart.txt");
        assertNotNull(wereld);
    }

    @Test
    public void zouEenPadMoetenVinden() {
        Wereld wereld = handelaar.nieuweWereldLader().laad("/kaarten/voorbeeld-kaart.txt");
        assertNotNull(wereld);

        Kaart kaart = wereld.getKaart();
        Stad van = wereld.getSteden().get(0);
        Stad naar = wereld.getSteden().get(1);

        SnelstePadAlgoritme algoritme = handelaar.nieuwSnelstePadAlgoritme();
        assertNotNull(algoritme);

        Pad pad = algoritme.bereken(kaart, van.getCoordinaat(), naar.getCoordinaat());

        assertNotNull(pad.getBewegingen());

        // 19 is de tijd voor de meest optimale route, om de bergen heen.
        assertEquals(19, pad.getTotaleTijd());

        // Heen
        Coordinaat bestemming = pad.volg(van.getCoordinaat());
        assertEquals(naar.getCoordinaat(), bestemming);

        // En Terug
        Coordinaat bron = pad.omgekeerd().volg(naar.getCoordinaat());
        assertEquals(van.getCoordinaat(), bron);
    }

    @Test
    public void kanNietOverDeZee() {

        Wereld wereld = handelaar.nieuweWereldLader().laad("/kaarten/voorbeeld-kaart.txt");

        Kaart kaart = wereld.getKaart();

        assertEquals(kaart.getTerreinOp(Coordinaat.op(0, 0)).getTerreinType().isToegankelijk(), false);

    }

    @Test
    public void heeftAlleStedenGehad() {

        /*Maakt 2 lijsten aan. 1 om alle steden op te halen en 1 om het daadwerkelijke aantal steden te bepalen aan de hand van het algoritme.
         Zet een boolean op true, loop daarna door de list van steden heen.
         heeft resultaat dezelde waarde als allesteden, wordt boolean isAllesBezocht op true gezet. Zo niet, dan blijft ie op false.*/
        
        Wereld wereld = handelaar.nieuweWereldLader().laad("/kaarten/voorbeeld-kaart.txt");
        StedenTourAlgoritme stedentour = new StedenTourAlgoritmeImpl();

        Kaart kaart = wereld.getKaart();

        List<Stad> alleSteden = wereld.getSteden();
        List<Stad> resultaat = stedentour.bereken(kaart, alleSteden);
        boolean isAllesBezocht = true;
        // resultaat.remove(0);
        for (int i = 0; i < alleSteden.size(); i++) {
            if (!resultaat.contains(alleSteden.get(i))) {
                isAllesBezocht = false;
                break;
            }
        }

        assertEquals(isAllesBezocht, true);

    }

    @Test
    public void zouEenRouteMoetenVinden() {
        Wereld wereld = handelaar.nieuweWereldLader().laad("/kaarten/voorbeeld-kaart.txt");
        assertNotNull(wereld);

        StedenTourAlgoritme algoritme = handelaar.nieuwStedenTourAlgoritme();
        assertNotNull(algoritme);

        List<Stad> result = algoritme.bereken(wereld.getKaart(), wereld.getSteden());

        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals(4, new HashSet<>(result).size()); // check duplicaten.
    }

    @Test
    public void zouEenHandelsplanMoetenMaken() {
        Wereld wereld = handelaar.nieuweWereldLader().laad("/kaarten/voorbeeld-kaart.txt");
        assertNotNull(wereld);

        Stad startStad = wereld.getSteden().get(0);

        HandelsplanAlgoritme algoritme = handelaar.nieuwHandelsplanAlgoritme();
        Handelsplan plan = algoritme.bereken(wereld, new HandelsPositie(wereld, startStad, 150, 10, 50));

        assertNotNull(plan);
        assertNotNull(plan.getActies());

        for (Actie a : plan.getActies()) {
            assertNotNull(a);
            assertFalse("BeweegActie gevonden, zet deze om in NavigeerActies", a instanceof BeweegActie);
        }
    }

}
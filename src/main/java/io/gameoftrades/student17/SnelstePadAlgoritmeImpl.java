package io.gameoftrades.student17;

import io.gameoftrades.debug.Debuggable;
import io.gameoftrades.debug.Debugger;
import io.gameoftrades.debug.DummyDebugger;
import io.gameoftrades.model.algoritme.SnelstePadAlgoritme;
import io.gameoftrades.model.kaart.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/*
 * Project: Game of Trades
 * Authors: Quang Hang, Alexander Franse, Tim Bekema
 * Group: got18-team17
 * Date: 14-10-2018
 */
public class SnelstePadAlgoritmeImpl implements SnelstePadAlgoritme, Debuggable {

    private HashMap<Coordinaat, PadImpl> paden;
    private HashSet<Coordinaat> bezocht;
    private Coordinaat huidig;
    private PadImpl huidigPad;

    private static final double heuristischGewicht = 1.5;
    private Debugger debugger = new DummyDebugger();

    @Override
    public Pad bereken(Kaart kaart, Coordinaat start, Coordinaat eind) {

        paden = new HashMap<>();
        bezocht = new HashSet<>();
        return algoritmeLoop(kaart, start, eind);
    }

    /*
     * Deze methode genereert de heuristische afstanden
     */
    private static HashMap<Coordinaat, Integer> genereerHeuristischeAfstanden(Kaart kaart, Coordinaat eind) {
        HashMap<Coordinaat, Integer> afstanden = new HashMap<>();

        for (int x = 0; x < kaart.getBreedte(); x++) {
            for (int y = 0; y < kaart.getHoogte(); y++) {
                Coordinaat coordinaat = Coordinaat.op(x, y);
                afstanden.put(coordinaat, (int) eind.afstandTot(coordinaat));
            }
        }
        return afstanden;
    }

    /*
     * Deze methode kijkt naar het coordinaat met de laagste kosten
     * Het algoritme kiest deze laagste 'neighbour' uit en gaat vervolgens verder
     */
    private static Coordinaat laagstOnbezochteCoordinaat(HashMap<Coordinaat, PadImpl> paden,
            HashSet<Coordinaat> bezocht,
            HashMap<Coordinaat, Integer> heuristischeAfstanden) {
        Coordinaat resultaat = null;
        Integer kortsteAfstand = Integer.MAX_VALUE;
        for (Map.Entry<Coordinaat, PadImpl> paar : paden.entrySet()) {
            Coordinaat coordinaat = paar.getKey();
            int afstand = paar.getValue().getTotaleTijd()
                    + (int) (heuristischeAfstanden.get(coordinaat) * heuristischGewicht);
            if (kortsteAfstand >= afstand && !bezocht.contains(coordinaat)) {
                kortsteAfstand = afstand;
                resultaat = coordinaat;
            }
        }
        return resultaat;
    }

    
    //berekent het algoritme 
    private Pad algoritmeLoop(Kaart kaart, Coordinaat start, Coordinaat eind) {
        HashMap<Coordinaat, Integer> heuristischeAfstanden = genereerHeuristischeAfstanden(kaart, eind);

        this.huidig = start;
        paden.put(start, new PadImpl(0, new Richting[0]));
        while (!eind.equals(huidig) && huidig != null) {
            bezocht.add(huidig);
            Terrein terrein = kaart.getTerreinOp(huidig);
            this.huidigPad = paden.get(huidig);
            debugger.debugPad(kaart, start, huidigPad);
            //debugger.debugCoordinaten(kaart, paden);

            checkBuren(terrein);

            do {
                this.huidig = laagstOnbezochteCoordinaat(paden, bezocht, heuristischeAfstanden);
            } while (bezocht.contains(this.huidig));
        }
        debugger.debugPad(kaart, start, paden.get(eind));
        return paden.get(eind);
    }
  
    //kijkt naar tegels om het huidige vakje
    public void checkBuren(Terrein terrein) {

        for (Richting richting : terrein.getMogelijkeRichtingen()) {
            Coordinaat buur = this.huidig.naar(richting);
            int afstand = terrein.getTerreinType().getBewegingspunten();
            PadImpl nieuwPad = this.huidigPad.verleng(afstand, richting);
            if (paden.containsKey(buur)) {
                PadImpl oudePad = paden.get(buur);
                if (oudePad.getTotaleTijd() < nieuwPad.getTotaleTijd()) {
                    nieuwPad = oudePad;
                }
            }
            paden.put(buur, nieuwPad);
        }
    }

    @Override
    public void setDebugger(Debugger debugger) {
        this.debugger = debugger;
    }

    @Override
    public String toString() {
        return "A* (" + heuristischGewicht + ")";
    }
}

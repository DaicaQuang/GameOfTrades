package io.gameoftrades.student17;

import io.gameoftrades.debug.Debuggable;
import io.gameoftrades.debug.Debugger;
import io.gameoftrades.debug.DummyDebugger;
import io.gameoftrades.model.algoritme.SnelstePadAlgoritme;
import io.gameoftrades.model.algoritme.StedenTourAlgoritme;
import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Pad;
import io.gameoftrades.model.kaart.Stad;

import java.util.ArrayList;
import java.util.List;

/*
 * Project: Game of Trades
 * Authors: Quang Hang, Alexander Franse, Tim Bekema
 * Group: got18-team17
 * Date: 14-10-2018
 */

public class StedenTourAlgoritmeImpl implements StedenTourAlgoritme, Debuggable {

    private Debugger debugger = new DummyDebugger();

    @Override
    public List<Stad> bereken(Kaart kaart, List<Stad> list) {

        SnelstePadAlgoritme AStar = new SnelstePadAlgoritmeImpl();
        List<Stad> bezocht = new ArrayList<>();

        /* List met de steden begint bij 0 (beginstad)
         * De variabele 'stad' is anders bij elke iteratie ervan
         */
        Stad stad = list.get(0);
        bezocht.add(stad);
        debugger.debugSteden(kaart, bezocht);

        while (list.size() != bezocht.size()) {

            Stad dichtsbijzijndeStad = null;

            int kortsteAfstand = Integer.MAX_VALUE;

            for (Stad vergelijkStad : list) {
                /*
                 * Checkt of de stad al bezocht is
                 */
                if (bezocht.contains(vergelijkStad)) {
                    continue;
                }
                Pad tussenPad = AStar.bereken(kaart, stad.getCoordinaat(), vergelijkStad.getCoordinaat());
                int afstand = tussenPad.getTotaleTijd();
                if (kortsteAfstand > afstand) {
                    kortsteAfstand = afstand;
                    dichtsbijzijndeStad = vergelijkStad;
                }
            }
            stad = dichtsbijzijndeStad;
            bezocht.add(stad);
            debugger.debugSteden(kaart, bezocht);
        }
        return bezocht;
    }

    @Override
    public void setDebugger(Debugger debugger) {
        this.debugger = debugger;
    }

    @Override
    public String toString() {
        return "StedenTourAlgoritme";
    }
}

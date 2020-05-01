package io.gameoftrades.student17;

import io.gameoftrades.model.kaart.Coordinaat;
import io.gameoftrades.model.kaart.Pad;
import io.gameoftrades.model.kaart.Richting;

/*
 * Project: Game of Trades
 * Authors: Quang Hang, Alexander Franse, Tim Bekema
 * Group: got18-team17
 * Date: 14-10-2018
 */

public class PadImpl implements Pad {

    private final int totaleTijd;
    private final Richting[] richtingen;

    public PadImpl(int totaleTijd, Richting[] richtingen) {
        this.totaleTijd = totaleTijd;
        this.richtingen = richtingen;
    }

    public PadImpl verleng(int afstand, Richting richting) {
        int nieuweTijd = totaleTijd + afstand;
        Richting[] nieuweRichtingen = new Richting[richtingen.length + 1];
        System.arraycopy(richtingen, 0, nieuweRichtingen, 0, richtingen.length);
        nieuweRichtingen[richtingen.length] = richting;
        return new PadImpl(nieuweTijd, nieuweRichtingen);
    }

    @Override
    public int getTotaleTijd() {
        return this.totaleTijd;
    }

    @Override
    public Richting[] getBewegingen() {
        return this.richtingen;
    }

    @Override
    public Pad omgekeerd() {
        Richting[] richtingen = new Richting[this.richtingen.length];
        for (int i = 0; i < richtingen.length; i++) {
            richtingen[i] = this.richtingen[this.richtingen.length - 1 - i].omgekeerd();
        }
        return new PadImpl(totaleTijd, richtingen);
    }

    @Override
    public Coordinaat volg(Coordinaat start) {
        for (Richting richting : richtingen) {
            start = start.naar(richting);
        }
        return start;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Richting richting : richtingen) {
            sb.append(richting).append(' ');
        }
        return sb.toString().trim();
    }
}

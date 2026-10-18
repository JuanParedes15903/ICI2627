package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.Fantasmas.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.Fantasmas.GhostInput;
import es.ucm.fdi.ici.fsm.Transition;

public class TiempoComestibleCeroTransition implements Transition {
    private final boolean msPacmanCerca;

    public TiempoComestibleCeroTransition(boolean msPacmanCerca) {
        this.msPacmanCerca = msPacmanCerca;
    }

    @Override
    public boolean evaluate(Input in) {
        GhostInput input = (GhostInput) in;
        boolean tCero = input.getTiempoComestible() == 0;
        return tCero && (msPacmanCerca ? input.isMsPacmanCerca() : input.isMsPacmanLejos());
    }
}
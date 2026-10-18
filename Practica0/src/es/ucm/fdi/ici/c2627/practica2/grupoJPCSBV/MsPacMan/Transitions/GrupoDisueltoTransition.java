package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan.Transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan.MsPacManInput;
import es.ucm.fdi.ici.fsm.Transition;

public class GrupoDisueltoTransition implements Transition {

    private final int radio;

    public GrupoDisueltoTransition(int radio) {
        this.radio = radio;
    }

    @Override
    public boolean evaluate(Input in) {
    	MsPacManInput input = (MsPacManInput) in;
        return input.getFantasmasCercaPorDetras() < 2;
    }

    @Override
    public String toString() {
        return "El grupo de fantasmas está disuelto";
    }
}

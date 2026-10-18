package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan.Transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan.MsPacManInput;
import es.ucm.fdi.ici.fsm.Transition;

public class NoHayFantasmasPorDelantetransition implements Transition {

    @Override
    public boolean evaluate(Input in) {
    	MsPacManInput input = (MsPacManInput) in;
        return !input.getFantasmasPorDelante();
    }

    @Override
    public String toString() {
        return "No hay fantasmas por delante";
    }
}

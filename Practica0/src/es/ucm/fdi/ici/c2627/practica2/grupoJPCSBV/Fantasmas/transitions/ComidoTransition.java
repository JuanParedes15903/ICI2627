package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.Fantasmas.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.Fantasmas.GhostInput;
import es.ucm.fdi.ici.fsm.Transition;

public class ComidoTransition implements Transition {
    @Override
    public boolean evaluate(Input in) {
        GhostInput input = (GhostInput) in;
        return input.isComido();
    }
}
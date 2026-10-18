package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.fsm.Transition;
import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.GhostInput;

public class ExcesoPerseguidoresTransition implements Transition {

    @Override
    public boolean evaluate(Input in) {
        GhostInput input = (GhostInput) in;
        return input.isDebeDejarDePerseguir();
    }

    @Override
    public String toString() {
        return "Demasiados perseguidores, forzando cambio de rol";
    }
}
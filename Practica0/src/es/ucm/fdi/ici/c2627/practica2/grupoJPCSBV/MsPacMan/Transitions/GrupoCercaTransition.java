package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan.Transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan.MsPacManInput;
import es.ucm.fdi.ici.fsm.Transition;

public class GrupoCercaTransition implements Transition {

    private final int radio, cuantos;

    public GrupoCercaTransition(int radio, int cuantos) {
        this.radio = radio;
        this.cuantos = cuantos;
    }

    @Override
    public boolean evaluate(Input in) {
        MsPacManInput input = (MsPacManInput) in;
        return input.getPildorasDePoder() > 0 && input.amenazasA(radio) >= cuantos;
    }

    @Override
    public String toString() {
        return cuantos + " o más a menos de " + radio;
    }

    
}
package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.Fantasmas.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.Fantasmas.GhostInput;
import es.ucm.fdi.ici.fsm.Transition;

public class MasCercaAMsPacmanQueAPPTransition implements Transition {
    @Override
    public boolean evaluate(Input in) {
        GhostInput input = (GhostInput) in;
        
        // Solo puede empezar a perseguir si:
        // 1. No es comestible.
        // 2. El sistema global le da permiso (hay menos de 2 fantasmas persiguiendo).
        // 3. Ms Pacman está más cerca de él que la PP más cercana (o no quedan PPs).
        return input.getTiempoComestible() == 0 &&
               input.isPersecucionPermitida() &&
               (input.getDistanciaMejorPP() == -1 || input.getDistanciaMsPacman() < input.getDistanciaMejorPP());
    }
}
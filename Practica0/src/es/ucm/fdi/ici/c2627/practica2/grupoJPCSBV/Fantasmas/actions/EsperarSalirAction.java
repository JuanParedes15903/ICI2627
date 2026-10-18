package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.Fantasmas.actions;

import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.Fantasmas.GhostInput;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class EsperarSalirAction implements Action {
    private final GHOST ghost;

    public EsperarSalirAction(GHOST ghost) {
        this.ghost = ghost;
    }

    @Override
    public MOVE execute(Game game) {
        GhostInput.registrarNoPersiguiendo(ghost);
        return MOVE.NEUTRAL;
    }

    @Override
    public String getActionId() {
        return ghost + " Esperando a salir";
    }
}
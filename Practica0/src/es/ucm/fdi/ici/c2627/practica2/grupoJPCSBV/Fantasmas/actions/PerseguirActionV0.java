package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.Fantasmas.actions;

import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.Fantasmas.GhostInput;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class PerseguirActionV0 implements Action {
    private final GHOST ghost;

    public PerseguirActionV0(GHOST ghost) {
        this.ghost = ghost;
    }

    @Override
    public MOVE execute(Game game) {
        GhostInput.registrarPersiguiendo(ghost);

        int yo = game.getGhostCurrentNodeIndex(ghost);
        int pacman = game.getPacmanCurrentNodeIndex();
        MOVE ultimo = game.getGhostLastMoveMade(ghost);
        
        return game.getApproximateNextMoveTowardsTarget(yo, pacman, ultimo, DM.PATH);
    }

    @Override
    public String getActionId() {
        return ghost + " Persiguiendo";
    }
}
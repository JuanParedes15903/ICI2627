package es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.ghosts.actions;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/** Merodea cerca de la PP que Ms. Pac-Man tiene mas cerca; si esta alli, presiona. */
public class GuardiaPPAction implements Action {

    private static final int RADIO = 4;
    private final GHOST yo;

    public GuardiaPPAction(GHOST yo) { this.yo = yo; }

    @Override
    public MOVE execute(Game game) {
        int miNodo = game.getGhostCurrentNodeIndex(yo);
        MOVE miUltimo = game.getGhostLastMoveMade(yo);
        int pac = game.getPacmanCurrentNodeIndex();

        int pp = -1, mejorD = Integer.MAX_VALUE;
        for (int p : game.getActivePowerPillsIndices()) {
            int d = game.getShortestPathDistance(pac, p);
            if (d != -1 && d < mejorD) { mejorD = d; pp = p; }
        }
        if (pp != -1) {
            int miD = game.getShortestPathDistance(miNodo, pp, miUltimo);
            if (miD == -1 || miD > RADIO) {
                return game.getNextMoveTowardsTarget(miNodo, pp, miUltimo, DM.PATH);
            }
        }
        // Ya estoy en la guardia (o no quedan PPs): presiono
        return game.getApproximateNextMoveTowardsTarget(miNodo, pac, miUltimo, DM.PATH);
    }

    @Override public String getActionId() { return "guardia_pp"; }
    @Override public String toString() { return yo + " " + getActionId(); }
}
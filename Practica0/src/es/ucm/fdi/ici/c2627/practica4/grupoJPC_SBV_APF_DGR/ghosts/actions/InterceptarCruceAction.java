package es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.ghosts.actions;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/** Va al siguiente cruce de Ms. Pac-Man si llega antes que ella; si no, persigue. */
public class InterceptarCruceAction implements Action {

    private static final int MAX_PASOS = 12;
    private final GHOST yo;

    public InterceptarCruceAction(GHOST yo) { this.yo = yo; }

    @Override
    public MOVE execute(Game game) {
        int miNodo = game.getGhostCurrentNodeIndex(yo);
        MOVE miUltimo = game.getGhostLastMoveMade(yo);
        int pac = game.getPacmanCurrentNodeIndex();

        int[] c = siguienteCruce(game, pac, game.getPacmanLastMoveMade());
        if (c != null && c[0] != miNodo) {
            int dYo = game.getShortestPathDistance(miNodo, c[0], miUltimo);
            if (dYo != -1 && dYo < c[1]) {
                return game.getNextMoveTowardsTarget(miNodo, c[0], miUltimo, DM.PATH);
            }
        }
        return game.getApproximateNextMoveTowardsTarget(miNodo, pac, miUltimo, DM.PATH);
    }

    /** @return {nodoCruce, pasos} o null si no hay vecino en esa direccion. */
    private int[] siguienteCruce(Game game, int desde, MOVE dir) {
        if (dir == null) return null;
        int nodo = game.getNeighbour(desde, dir);
        if (nodo == -1) return null;
        MOVE actual = dir;
        int pasos = 1;
        while (pasos < MAX_PASOS && !game.isJunction(nodo)) {
            MOVE[] sig = game.getPossibleMoves(nodo, actual);
            if (sig == null || sig.length == 0) break;
            int s = game.getNeighbour(nodo, sig[0]);
            if (s == -1) break;
            actual = sig[0];
            nodo = s;
            pasos++;
        }
        return new int[] { nodo, pasos };
    }

    @Override public String getActionId() { return "interceptar_cruce"; }
    @Override public String toString() { return yo + " " + getActionId(); }
}
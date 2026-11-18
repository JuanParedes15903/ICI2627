package es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.ghosts.actions;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/** Huye hacia el cruce al que llego con mas ventaja sobre Ms. Pac-Man (evita callejones). */
public class HuirSeguroAction implements Action {

    private static final int MAX_PASOS = 12;
    private final GHOST yo;

    public HuirSeguroAction(GHOST yo) { this.yo = yo; }

    @Override
    public MOVE execute(Game game) {
        int miNodo = game.getGhostCurrentNodeIndex(yo);
        MOVE miUltimo = game.getGhostLastMoveMade(yo);
        int pac = game.getPacmanCurrentNodeIndex();

        MOVE[] posibles = game.getPossibleMoves(miNodo, miUltimo);
        if (posibles == null || posibles.length == 0) return MOVE.NEUTRAL;
        if (posibles.length == 1) return posibles[0];

        MOVE mejor = posibles[0];
        double mejorScore = -Double.MAX_VALUE;

        for (MOVE m : posibles) {
            int[] c = siguienteCruce(game, miNodo, m);
            if (c == null) continue;

            int dPac = game.getShortestPathDistance(pac, c[0]);
            if (dPac == -1) dPac = 200;
            int margen = dPac - c[1];                 // >0: llego antes que ella

            // Separacion respecto al comestible aliado mas cercano (para no ir todos juntos)
            int sep = 60;
            for (GHOST g : GHOST.values()) {
                if (g == yo || game.getGhostEdibleTime(g) <= 0 || game.getGhostLairTime(g) > 0) continue;
                int d = game.getShortestPathDistance(c[0], game.getGhostCurrentNodeIndex(g));
                if (d != -1 && d < sep) sep = d;
            }

            double score = 2.0 * margen + 0.5 * sep + 0.1 * dPac;
            if (margen <= 0) score -= 30;             // ella llega antes: ruta muy mala
            if (score > mejorScore) { mejorScore = score; mejor = m; }
        }
        return mejor;
    }

    /** @return {nodoCruce, pasos} o null si no hay vecino en esa direccion. */
    private int[] siguienteCruce(Game game, int desde, MOVE dir) {
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

    @Override public String getActionId() { return "huir_seguro"; }
    @Override public String toString() { return yo + " " + getActionId(); }
}
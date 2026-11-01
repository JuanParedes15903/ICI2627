package es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.ghosts.actions;

import es.ucm.fdi.ici.rules.RulesAction;
import jess.Fact;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class CortarCaminoPPAction implements RulesAction {

    private static final int MARGEN = 3;   // nodos de ventaja que exijo para bloquear
    private final GHOST yo;

    public CortarCaminoPPAction(GHOST yo) { this.yo = yo; }

    @Override
    public MOVE execute(Game game) {
        int miNodo = game.getGhostCurrentNodeIndex(yo);
        MOVE miUltimo = game.getGhostLastMoveMade(yo);
        int pac = game.getPacmanCurrentNodeIndex();
        MOVE pacUltimo = game.getPacmanLastMoveMade();

        int pp = ppMasCercana(game, pac);
        if (pp != -1) {
            int[] ruta = game.getShortestPath(pac, pp, pacUltimo);
            // De Ms. Pac-Man hacia la PP: el primer nodo al que llego antes que ella es el mejor bloqueo
            for (int nodo : ruta) {
                int dPac = game.getShortestPathDistance(pac, nodo, pacUltimo);
                int dYo  = game.getShortestPathDistance(miNodo, nodo, miUltimo);
                if (dPac == -1 || dYo == -1) continue;
                if (dYo + MARGEN < dPac) {
                    if (nodo == miNodo) break;   // ya estoy en el bloqueo: presiono
                    return game.getNextMoveTowardsTarget(miNodo, nodo, miUltimo, DM.PATH);
                }
            }
        }
        // Sin punto de bloqueo viable: perseguir
        return game.getApproximateNextMoveTowardsTarget(miNodo, pac, miUltimo, DM.PATH);
    }

    // Misma definición que en GhostsInput: la PP activa más cercana a 'desde'
    private int ppMasCercana(Game game, int desde) {
        int mejor = -1, mejorD = Integer.MAX_VALUE;
        for (int pp : game.getActivePowerPillsIndices()) {
            int d = game.getShortestPathDistance(desde, pp);
            if (d != -1 && d < mejorD) { mejorD = d; mejor = pp; }
        }
        return mejor;
    }

    @Override public void parseFact(Fact actionFact) { }
    @Override public String getActionId() { return "cortar_pp"; }
}
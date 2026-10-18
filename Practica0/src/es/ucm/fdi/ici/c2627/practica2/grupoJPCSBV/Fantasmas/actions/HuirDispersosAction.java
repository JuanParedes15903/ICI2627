package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.Fantasmas.actions;

import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.Fantasmas.GhostInput;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class HuirDispersosAction implements Action {
    private final GHOST ghost;

    public HuirDispersosAction(GHOST ghost) {
        this.ghost = ghost;
    }

    @Override
    public MOVE execute(Game game) {
        GhostInput.registrarNoPersiguiendo(ghost);

        int yo = game.getGhostCurrentNodeIndex(ghost);
        int pacman = game.getPacmanCurrentNodeIndex();
        MOVE ultimo = game.getGhostLastMoveMade(ghost);
        
        MOVE[] posibles = game.getPossibleMoves(yo, ultimo);
        MOVE mejorMovimiento = MOVE.NEUTRAL;
        double mejorPuntuacion = -Double.MAX_VALUE;

        for (MOVE move : posibles) {
            int vecino = game.getNeighbour(yo, move);
            if (vecino == -1) continue; 
            
            double distPacman = game.getShortestPathDistance(vecino, pacman);
            double minDistOtroFantasma = Double.MAX_VALUE;

            for (GHOST otro : GHOST.values()) {
                if (otro != ghost && game.getGhostLairTime(otro) == 0) {
                    int otroNodo = game.getGhostCurrentNodeIndex(otro);
                    double dist = game.getShortestPathDistance(vecino, otroNodo);
                    if (dist != -1 && dist < minDistOtroFantasma) {
                        minDistOtroFantasma = dist;
                    }
                }
            }

            if (minDistOtroFantasma == Double.MAX_VALUE) {
                minDistOtroFantasma = 50;
            }

            double puntuacion = distPacman + (minDistOtroFantasma * 2.0);

            if (puntuacion > mejorPuntuacion) {
                mejorPuntuacion = puntuacion;
                mejorMovimiento = move;
            }
        }

        return mejorMovimiento != MOVE.NEUTRAL ? mejorMovimiento : posibles[0];
    }

    @Override
    public String getActionId() {
        return ghost + " Huyendo disperso garantizado";
    }
}
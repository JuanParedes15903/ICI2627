package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.Fantasmas.actions;

import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.Fantasmas.GhostInput;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class HuirMsPacmanAction implements Action {
    private final GHOST ghost;

    public HuirMsPacmanAction(GHOST ghost) {
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

            double puntuacion = game.getShortestPathDistance(vecino, pacman);

            for (GHOST otro : GHOST.values()) {
                if (otro != ghost && game.getGhostLairTime(otro) == 0) {
                    int otroNodo = game.getGhostCurrentNodeIndex(otro);
                    int distOtro = game.getShortestPathDistance(vecino, otroNodo);
                    
                    if (distOtro < 15) {
                        puntuacion -= (1000.0 / (distOtro + 1));
                    }
                }
            }

            if (puntuacion > mejorPuntuacion) {
                mejorPuntuacion = puntuacion;
                mejorMovimiento = move;
            }
        }

        if (mejorMovimiento == MOVE.NEUTRAL && posibles.length > 0) {
            return game.getApproximateNextMoveAwayFromTarget(yo, pacman, ultimo, DM.PATH);
        }

        return mejorMovimiento;
    }

    @Override
    public String getActionId() {
        return ghost + " Huyendo urgente y separado";
    }
}
package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.Fantasmas.actions;

import java.util.EnumMap;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class PerseguirAction implements Action {
	private final GHOST ghost;
    // 1. Añadir el atributo del mapa
    private final EnumMap<GHOST, Integer> tiempoPersiguiendo;

    // 2. Modificar el constructor
    public PerseguirAction(GHOST ghost, EnumMap<GHOST, Integer> tiempoPersiguiendo) {
        this.ghost = ghost;
        this.tiempoPersiguiendo = tiempoPersiguiendo;
    }

    @Override
    public MOVE execute(Game game) {
        // Registra globalmente que este fantasma está persiguiendo (+1 al contador)
    	this.tiempoPersiguiendo.put(ghost, this.tiempoPersiguiendo.getOrDefault(ghost, 0) + 1);
        int yo = game.getGhostCurrentNodeIndex(ghost);
        int pacman = game.getPacmanCurrentNodeIndex();
        MOVE ultimo = game.getGhostLastMoveMade(ghost);
        
        MOVE[] posibles = game.getPossibleMoves(yo, ultimo);
        MOVE mejorMovimiento = MOVE.NEUTRAL;
        double mejorPuntuacion = -Double.MAX_VALUE;

        for (MOVE move : posibles) {
            int vecino = game.getNeighbour(yo, move);
            if (vecino == -1) continue;

            int distPacman = game.getShortestPathDistance(vecino, pacman);
            if (distPacman == -1) continue; // Filtro de seguridad para nodos inalcanzables

            // Puntuación base: menor distancia a Ms. Pac-Man -> mayor puntuación
            double puntuacion = -distPacman;

         // Evaluamos la proximidad ÚNICAMENTE respecto a otros fantasmas activos que TAMBIÉN estén persiguiendo
            for (GHOST otro : GHOST.values()) {
                if (otro != ghost && game.getGhostLairTime(otro) == 0 && game.getGhostEdibleTime(otro) == 0) {
                    
                    // CAMBIO AQUÍ: Usamos directamente el mapa 'tiempoPersiguiendo' de esta clase
                    if (this.tiempoPersiguiendo.getOrDefault(otro, 0) > 0) {
                        
                        int otroNodo = game.getGhostCurrentNodeIndex(otro);
                        int distOtro = game.getShortestPathDistance(vecino, otroNodo);

                        // Penalización si el movimiento nos coloca a menos de 5 nodos de otro perseguidor
                        if (distOtro != -1 && distOtro < 5) {
                            puntuacion -= (1000.0 / (distOtro + 1));
                        }
                    }
                }
            }

            if (puntuacion > mejorPuntuacion) {
                mejorPuntuacion = puntuacion;
                mejorMovimiento = move;
            }
        }

        // Respaldo por si no se puede evaluar puntuación
        if (mejorMovimiento == MOVE.NEUTRAL && posibles.length > 0) {
            return game.getApproximateNextMoveTowardsTarget(yo, pacman, ultimo, DM.PATH);
        }

        return mejorMovimiento;
    }

    @Override
    public String getActionId() {
        return ghost + " Persiguiendo coordinado";
    }
}
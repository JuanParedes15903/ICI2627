package es.ucm.fdi.ici.c2627.practica1.grupoJPCSBV;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import pacman.controllers.GhostController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;


public class BlockingGhosts extends GhostController {

	private final EnumMap<GHOST, MOVE> myMoves = new EnumMap<GHOST, MOVE>(GHOST.class);

    @Override
    public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
        myMoves.clear();

        int pacmanPos = game.getPacmanCurrentNodeIndex();

        // 1. Obtener y ordenar los cruces más cercanos para tapar salidas
        int[] allJunctions = game.getJunctionIndices();
        List<Integer> sortedJunctions = new ArrayList<>();
        for (int j : allJunctions) {
            sortedJunctions.add(j);
        }

        // Ordenar de menor a mayor distancia respecto a Ms. Pac-Man
        sortedJunctions.sort((j1, j2) -> {
            int d1 = game.getShortestPathDistance(pacmanPos, j1);
            int d2 = game.getShortestPathDistance(pacmanPos, j2);
            return Integer.compare(d1, d2);
        });

        // Filtrar los 3 cruces más cercanos, omitiendo distancia 0 (por si está parada justo en uno)
        List<Integer> targetJunctions = new ArrayList<>();
        for (int j : sortedJunctions) {
            if (game.getShortestPathDistance(pacmanPos, j) > 0) {
                targetJunctions.add(j);
            }
            if (targetJunctions.size() >= 3) {
                break;
            }
        }

        // 2. Asignar movimiento a cada fantasma
        for (GHOST ghost : GHOST.values()) {
            if (game.doesGhostRequireAction(ghost)) {
                int ghostPos = game.getGhostCurrentNodeIndex(ghost);
                int distToPacman = game.getShortestPathDistance(ghostPos, pacmanPos);

                // REGLA: Estado comestible
                // Si está comestible, huye mientras la mitad del tiempo restante sea mayor o igual a la distancia.
                // Si la mitad del tiempo es estrictamente menor, o ya se lo han comido (pasa a no comestible), vuelve a su rol.
                // Nota: El motor del juego manda automáticamente a los fantasmas comidos a la jaula. Al salir, isGhostEdible es false.
                if (game.isGhostEdible(ghost)) {
                    int edibleTime = game.getGhostEdibleTime(ghost);
                    if ((edibleTime / 2.0) >= distToPacman) {
                        myMoves.put(ghost, game.getNextMoveAwayFromTarget(ghostPos, pacmanPos, DM.PATH));
                        continue; // Termina el turno de este fantasma
                    }
                }

                // REGLA: Roles normales
                if (ghost == GHOST.BLINKY) {
                    // Blinky persigue directamente todo el rato
                    myMoves.put(ghost, game.getNextMoveTowardsTarget(ghostPos, pacmanPos, DM.PATH));
                } else {
                    // Pinky, Inky y Sue van a los distintos cruces más cercanos
                    int targetNode = pacmanPos; // Fallback por seguridad
                    
                    if (ghost == GHOST.PINKY && targetJunctions.size() > 0) {
                        targetNode = targetJunctions.get(0);
                    } else if (ghost == GHOST.INKY && targetJunctions.size() > 1) {
                        targetNode = targetJunctions.get(1);
                    } else if (ghost == GHOST.SUE && targetJunctions.size() > 2) {
                        targetNode = targetJunctions.get(2);
                    }

                    myMoves.put(ghost, game.getNextMoveTowardsTarget(ghostPos, targetNode, DM.PATH));
                }
            }
        }

        return myMoves;
    }
}
package es.ucm.fdi.ici.c2627.practica1.grupoJPCSBV;

import pacman.controllers.GhostController;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class AdvancedBlockingGhosts extends GhostController{

	private final EnumMap<GHOST, MOVE> myMoves = new EnumMap<GHOST, MOVE>(GHOST.class);

    @Override
    public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
        myMoves.clear();

        int pacmanPos = game.getPacmanCurrentNodeIndex();

        // 1. Obtener y ordenar los cruces más cercanos para tapar salidas (Rol por defecto)
        int[] allJunctions = game.getJunctionIndices();
        List<Integer> sortedJunctions = new ArrayList<>();
        for (int j : allJunctions) {
            sortedJunctions.add(j);
        }

        sortedJunctions.sort((j1, j2) -> {
            int d1 = game.getShortestPathDistance(pacmanPos, j1);
            int d2 = game.getShortestPathDistance(pacmanPos, j2);
            return Integer.compare(d1, d2);
        });

        List<Integer> targetJunctions = new ArrayList<>();
        for (int j : sortedJunctions) {
            if (game.getShortestPathDistance(pacmanPos, j) > 0) {
                targetJunctions.add(j);
            }
            if (targetJunctions.size() >= 3) break;
        }

        // 2. Lógica de Píldora de Poder (Anticipación e Intercepción)
        int nearestPowerPill = getNearestPowerPillToPacman(game, pacmanPos);
        GHOST powerPillInterceptor = null;
        boolean allGhostsFlee = false;

        if (nearestPowerPill != -1) {
            int junctionsToPowerPill = countJunctionsBetween(game, pacmanPos, nearestPowerPill);

            // Si está a 3 cruces o menos
            if (junctionsToPowerPill <= 3) {
                GHOST closestGhostToPill = getClosestGhostToNode(game, nearestPowerPill);
                
                if (closestGhostToPill != null) {
                    int ghostPos = game.getGhostCurrentNodeIndex(closestGhostToPill);
                    int pacmanDistToPill = game.getShortestPathDistance(pacmanPos, nearestPowerPill);
                    int ghostDistToPill = game.getShortestPathDistance(ghostPos, nearestPowerPill);

                    // Si Ms. Pac-Man llega antes, TODOS huyen anticipando que se la coma
                    if (pacmanDistToPill < ghostDistToPill) {
                        allGhostsFlee = true;
                    } else {
                        // Si el fantasma llega antes o a la vez, se le asigna bloquearla
                        powerPillInterceptor = closestGhostToPill;
                    }
                }
            }
        }

        // 3. Asignar movimiento a cada fantasma
        int junctionIndex = 0; // Para repartir los cruces entre los fantasmas trampa

        for (GHOST ghost : GHOST.values()) {
            if (game.doesGhostRequireAction(ghost)) {
                int ghostPos = game.getGhostCurrentNodeIndex(ghost);
                int distToPacman = game.getShortestPathDistance(ghostPos, pacmanPos);

                // REGLA A: Estado comestible individual
                if (game.isGhostEdible(ghost)) {
                    int edibleTime = game.getGhostEdibleTime(ghost);
                    if ((edibleTime / 2.0) >= distToPacman) {
                        myMoves.put(ghost, game.getNextMoveAwayFromTarget(ghostPos, pacmanPos, DM.PATH));
                        continue;
                    }
                }

                // REGLA B: Anticipación global de Píldora de Poder (Ms. Pac-Man llega antes)
                if (allGhostsFlee) {
                    myMoves.put(ghost, game.getNextMoveAwayFromTarget(ghostPos, pacmanPos, DM.PATH));
                    continue;
                }

                // REGLA C: Interceptor de Píldora de Poder (Fantasma llega antes)
                if (ghost == powerPillInterceptor) {
                    if (ghostPos == nearestPowerPill) {
                        // Si ya está en la píldora bloqueando, va a por Ms. Pac-Man para comérsela
                        myMoves.put(ghost, game.getNextMoveTowardsTarget(ghostPos, pacmanPos, DM.PATH));
                    } else {
                        // Va directo a la píldora de poder para tapar el camino
                        myMoves.put(ghost, game.getNextMoveTowardsTarget(ghostPos, nearestPowerPill, DM.PATH));
                    }
                    continue;
                }

                // REGLA D: Roles normales
                if (ghost == GHOST.BLINKY) {
                    myMoves.put(ghost, game.getNextMoveTowardsTarget(ghostPos, pacmanPos, DM.PATH));
                } else {
                    int targetNode = pacmanPos;
                    if (junctionIndex < targetJunctions.size()) {
                        targetNode = targetJunctions.get(junctionIndex);
                        junctionIndex++;
                    }
                    myMoves.put(ghost, game.getNextMoveTowardsTarget(ghostPos, targetNode, DM.PATH));
                }
            }
        }

        return myMoves;
    }

    private int getNearestPowerPillToPacman(Game game, int pacmanPos) {
        int[] activePowerPills = game.getActivePowerPillsIndices();
        int minDistance = Integer.MAX_VALUE;
        int targetPowerPill = -1;

        for (int powerPill : activePowerPills) {
            int dist = game.getShortestPathDistance(pacmanPos, powerPill);
            if (dist < minDistance) {
                minDistance = dist;
                targetPowerPill = powerPill;
            }
        }
        return targetPowerPill;
    }

    private int countJunctionsBetween(Game game, int nodeA, int nodeB) {
        int[] path = game.getShortestPath(nodeA, nodeB);
        int[] junctions = game.getJunctionIndices();
        int count = 0;

        for (int nodeInPath : path) {
            for (int junction : junctions) {
                if (nodeInPath == junction) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    private GHOST getClosestGhostToNode(Game game, int targetNode) {
        GHOST closest = null;
        int minDistance = Integer.MAX_VALUE;

        for (GHOST ghost : GHOST.values()) {
            if (game.getGhostLairTime(ghost) == 0) { // Solo fantasmas fuera de la jaula
                int pos = game.getGhostCurrentNodeIndex(ghost);
                int dist = game.getShortestPathDistance(pos, targetNode);
                if (dist < minDistance) {
                    minDistance = dist;
                    closest = ghost;
                }
            }
        }
        return closest;
    }
}
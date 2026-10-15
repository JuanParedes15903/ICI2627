package es.ucm.fdi.ici.c2627.practica1.grupoJPCSBV;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import pacman.controllers.GhostController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GhostsZones extends GhostController {

    private final EnumMap<GHOST, MOVE> myMoves = new EnumMap<GHOST, MOVE>(GHOST.class);

    // Listas para guardar los nodos que pertenecen a cada sector
    private final List<Integer> q1Nodes = new ArrayList<>(); // Arriba-Izquierda
    private final List<Integer> q2Nodes = new ArrayList<>(); // Arriba-Derecha
    private final List<Integer> q3Nodes = new ArrayList<>(); // Abajo-Derecha
    private final List<Integer> q4Nodes = new ArrayList<>(); // Abajo-Izquierda

    private boolean mapInitialized = false;
    private int midX, midY;

    @Override
    public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
        myMoves.clear();
        
        if (!mapInitialized) {
            initMap(game);
        }

        int pacmanPos = game.getPacmanCurrentNodeIndex();
        int nearestPP = getNearestPowerPillToPacman(game, pacmanPos);

        for (GHOST ghost : GHOST.values()) {
            if (game.doesGhostRequireAction(ghost)) {
                int ghostPos = game.getGhostCurrentNodeIndex(ghost);
                if (ghostPos == -1) continue; // Si está en la jaula, ignorar de momento

                int distToPacman = game.getShortestPathDistance(ghostPos, pacmanPos);

                // ==============================================================================
                // REGLA 1: HUIDA POR ESTADO COMESTIBLE (Condición de finalización de huida)
                // ==============================================================================
                if (game.isGhostEdible(ghost)) {
                    int edibleTime = game.getGhostEdibleTime(ghost);
                    // "durará hasta que el tiempo en el que son comestibles partido de dos sea menor que la distancia"
                    if ((edibleTime / 2.0) >= distToPacman) {
                        myMoves.put(ghost, game.getNextMoveAwayFromTarget(ghostPos, pacmanPos, DM.PATH));
                        continue;
                    }
                }

                // ==============================================================================
                // REGLA 2: HUIDA PREVENTIVA (Ms Pac-Man muy cerca de una píldora de poder)
                // ==============================================================================
                if (nearestPP != -1) {
                    int distPacmanToPP = game.getShortestPathDistance(pacmanPos, nearestPP);
                    int distGhostToPP = game.getShortestPathDistance(ghostPos, nearestPP);
                    
                    int nearestJunctionToPacman = getNearestJunctionToNode(game, pacmanPos);
                    int distPacmanToJunction = game.getShortestPathDistance(pacmanPos, nearestJunctionToPacman);

                    // Si Ms Pacman está a un cruce o menos de la píldora, y el fantasma está más lejos
                    if (distPacmanToJunction != -1 && distPacmanToPP <= distPacmanToJunction && distPacmanToPP < distGhostToPP) {
                        myMoves.put(ghost, game.getNextMoveAwayFromTarget(ghostPos, pacmanPos, DM.PATH));
                        continue;
                    }
                }

                // ==============================================================================
                // REGLA 3: EMBOSCADA EN CRUCE (Llegar antes al cruce hacia el que va)
                // ==============================================================================
                int junctionAhead = getJunctionAhead(game, pacmanPos, game.getPacmanLastMoveMade());
                if (junctionAhead != -1) {
                    int distGhostToJunc = game.getShortestPathDistance(ghostPos, junctionAhead);
                    int distPacmanToJunc = game.getShortestPathDistance(pacmanPos, junctionAhead);
                    
                    // Si el fantasma llega antes a ese cruce, va hacia él para interceptar
                    if (distGhostToJunc != -1 && distGhostToJunc < distPacmanToJunc) {
                        myMoves.put(ghost, game.getNextMoveTowardsTarget(ghostPos, junctionAhead, DM.PATH));
                        continue;
                    }
                }

                // ==============================================================================
                // REGLA 4: COMPORTAMIENTO POR SECTORES (ZONAS)
                // ==============================================================================
                int pacmanQuad = getQuadrant(game, pacmanPos);
                int ghostQuad = getAssignedQuadrant(ghost);

                if (pacmanQuad == ghostQuad) {
                    // Ms. Pac-Man YA ESTÁ en el sector: Tapar camino a la píldora de poder
                    if (nearestPP != -1) {
                        myMoves.put(ghost, game.getNextMoveTowardsTarget(ghostPos, nearestPP, DM.PATH));
                    } else {
                        // Si no hay píldoras, la persigue directamente
                        myMoves.put(ghost, game.getNextMoveTowardsTarget(ghostPos, pacmanPos, DM.PATH));
                    }
                } else {
                    // Ms. Pac-Man NO ESTÁ en el sector: Tapar la entrada más probable
                    // (El nodo dentro del sector del fantasma que esté más cerca de Ms. Pac-Man)
                    int targetEntryNode = getClosestNodeInQuadrant(game, ghostQuad, pacmanPos);
                    if (targetEntryNode != -1) {
                        myMoves.put(ghost, game.getNextMoveTowardsTarget(ghostPos, targetEntryNode, DM.PATH));
                    } else {
                        myMoves.put(ghost, game.getNextMoveTowardsTarget(ghostPos, pacmanPos, DM.PATH));
                    }
                }
            }
        }
        return myMoves;
    }

    /**
     * Inicializa el mapa la primera vez que se llama, calculando el centro
     * y dividiendo todos los nodos navegables en los 4 cuadrantes.
     */
    private void initMap(Game game) {
        int numNodes = game.getNumberOfNodes();
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

        // 1. Encontrar los límites del mapa
        for (int i = 0; i < numNodes; i++) {
            int x = game.getNodeXCood(i);
            int y = game.getNodeYCood(i);
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
        }

        midX = (minX + maxX) / 2;
        midY = (minY + maxY) / 2;

        // 2. Clasificar los nodos por cuadrantes
        for (int i = 0; i < numNodes; i++) {
            int q = getQuadrant(game, i);
            switch (q) {
                case 1: q1Nodes.add(i); break;
                case 2: q2Nodes.add(i); break;
                case 3: q3Nodes.add(i); break;
                case 4: q4Nodes.add(i); break;
            }
        }
        mapInitialized = true;
    }

    /**
     * Determina en qué cuadrante está un nodo dado sus coordenadas X e Y.
     */
    private int getQuadrant(Game game, int node) {
        int x = game.getNodeXCood(node);
        int y = game.getNodeYCood(node);
        
        if (x < midX && y < midY) return 1;       // Arriba-Izquierda
        if (x >= midX && y < midY) return 2;      // Arriba-Derecha
        if (x >= midX && y >= midY) return 3;     // Abajo-Derecha
        return 4;                                 // Abajo-Izquierda
    }

    /**
     * Asigna a cada fantasma un cuadrante fijo en sentido horario.
     */
    private int getAssignedQuadrant(GHOST ghost) {
        switch (ghost) {
            case BLINKY: return 1; // Arriba-Izquierda
            case PINKY: return 2;  // Arriba-Derecha
            case INKY: return 3;   // Abajo-Derecha
            case SUE: return 4;    // Abajo-Izquierda
            default: return 1;
        }
    }

    /**
     * Proyecta el movimiento de Ms. Pac-Man hacia el frente hasta chocar con el siguiente cruce.
     */
    private int getJunctionAhead(Game game, int pos, MOVE lastMove) {
        if (lastMove == MOVE.NEUTRAL) return -1;
        
        int currentPos = pos;
        MOVE currentDir = lastMove;
        int steps = 0;
        
        while (!game.isJunction(currentPos) && steps < 100) {
            MOVE[] possibleMoves = game.getPossibleMoves(currentPos, currentDir);
            if (possibleMoves.length == 0) break; // Callejón sin salida
            
            currentDir = possibleMoves[0]; 
            currentPos = game.getNeighbour(currentPos, currentDir);
            steps++;
        }
        return game.isJunction(currentPos) ? currentPos : -1;
    }

    /**
     * Busca el nodo que pertenece al sector del fantasma que se encuentre más cerca de Ms. Pac-Man.
     * Sirve para "tapar la entrada probable" al sector.
     */
    private int getClosestNodeInQuadrant(Game game, int quadrant, int sourceNode) {
        List<Integer> nodes;
        switch (quadrant) {
            case 1: nodes = q1Nodes; break;
            case 2: nodes = q2Nodes; break;
            case 3: nodes = q3Nodes; break;
            case 4: nodes = q4Nodes; break;
            default: return -1;
        }

        int closest = -1;
        int minDist = Integer.MAX_VALUE;
        for (int node : nodes) {
            int dist = game.getShortestPathDistance(node, sourceNode);
            if (dist != -1 && dist < minDist) {
                minDist = dist;
                closest = node;
            }
        }
        return closest;
    }

    private int getNearestJunctionToNode(Game game, int node) {
        int[] junctions = game.getJunctionIndices();
        int closest = -1;
        int minDist = Integer.MAX_VALUE;
        for (int j : junctions) {
            int dist = game.getShortestPathDistance(node, j);
            if (dist != -1 && dist < minDist) {
                minDist = dist;
                closest = j;
            }
        }
        return closest;
    }

    private int getNearestPowerPillToPacman(Game game, int pacmanPos) {
        int[] activePowerPills = game.getActivePowerPillsIndices();
        int minDistance = Integer.MAX_VALUE;
        int targetPowerPill = -1;

        for (int powerPill : activePowerPills) {
            int dist = game.getShortestPathDistance(pacmanPos, powerPill);
            if (dist != -1 && dist < minDistance) {
                minDistance = dist;
                targetPowerPill = powerPill;
            }
        }
        return targetPowerPill;
    }
}
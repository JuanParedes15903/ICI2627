package es.ucm.fdi.ici.c2627.practica1.grupoJPCSBV;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import pacman.controllers.GhostController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Ghosts3 extends GhostController {

    private enum Role {
        PURSUER,                 // Persigue a Ms. Pac-Man
        POWER_PILL_INTERCEPTOR,  // Tapa el camino a la píldora de poder más cercana
        PILL_INTERCEPTOR,        // Tapa el camino a la baya/píldora normal más cercana
        LAIR_PATROLLER           // Patrulla la salida de la jaula
    }

    private final EnumMap<GHOST, MOVE> myMoves = new EnumMap<GHOST, MOVE>(GHOST.class);
    
    // Distancia máxima a la que Pac-Man debe estar de la Power Pill para considerar peligro inminente
    private static final int POWER_PILL_DANGER_DISTANCE = 15;

    // Fantasma que persigue por proximidad (adicional al PURSUER oficial)
    private GHOST secondaryPursuer = null;

    @Override
    public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
        myMoves.clear();

        int pacmanPos = game.getPacmanCurrentNodeIndex();

        // 1. Determinar los nodos objetivo para cada rol
        int targetPursuer = pacmanPos;

        int nearestPowerPill = getNearestPowerPillToPacman(game, pacmanPos);
        int targetPowerPill = (nearestPowerPill != -1) ? nearestPowerPill : pacmanPos;

        int targetPill = getNearestPillToPacman(game, pacmanPos);
        if (targetPill == -1) targetPill = pacmanPos;

        int targetLair = getLairPatrolTarget(game);

        // 2. Asignar roles dinámicamente solo entre fantasmas fuera de la jaula
        EnumMap<GHOST, Role> assignedRoles = assignRoles(game, targetPursuer, targetPowerPill, targetPill);

        // --- INICIO LÓGICA: MÁXIMO DOS PERSEGUIDORES ---
        GHOST officialPursuer = null;
        for (GHOST g : assignedRoles.keySet()) {
            if (assignedRoles.get(g) == Role.PURSUER) {
                officialPursuer = g;
                break;
            }
        }

        List<GHOST> proximityGhosts = new ArrayList<>();
        for (GHOST g : GHOST.values()) {
            if (g == officialPursuer) continue; // El PURSUER oficial no entra en esta lista
            if (game.getGhostLairTime(g) > 0) continue;
            
            int ghostPos = game.getGhostCurrentNodeIndex(g);
            if (ghostPos == -1) continue;
            
            int dist = game.getShortestPathDistance(ghostPos, pacmanPos);
            if (dist != -1 && dist < 15) {
                proximityGhosts.add(g);
            }
        }

        if (proximityGhosts.isEmpty()) {
            secondaryPursuer = null;
        } else {
            if (proximityGhosts.contains(secondaryPursuer)) {
                if (proximityGhosts.size() > 1) {
                    // Se quiere unir un tercer fantasma. Eliminamos de la persecución
                    // al que NO tiene el rol de perseguidor (el actual secondaryPursuer)
                    proximityGhosts.remove(secondaryPursuer);
                    secondaryPursuer = proximityGhosts.get(0); // Reemplazado por el nuevo
                }
                // Si el tamaño es 1, el secondaryPursuer sigue siendo el mismo.
            } else {
                // El secondaryPursuer anterior ya no está cerca; asignamos uno nuevo.
                secondaryPursuer = proximityGhosts.get(0);
            }
        }
        // --- FIN LÓGICA ---

        // 3. Calcular el movimiento de cada fantasma activo
        for (GHOST ghost : GHOST.values()) {
            if (game.doesGhostRequireAction(ghost)) {
                Role ghostRole = assignedRoles.get(ghost);
                MOVE move = getGhostMove(game, ghost, ghostRole, targetPursuer, nearestPowerPill, targetPowerPill, targetPill, targetLair);
                myMoves.put(ghost, move);
            }
        }

        return myMoves;
    }

    private EnumMap<GHOST, Role> assignRoles(Game game, int targetPursuer, int targetPowerPill, int targetPill) {
        EnumMap<GHOST, Role> roleMap = new EnumMap<>(GHOST.class);
        
        // Solo considerar fantasmas fuera de la jaula
        List<GHOST> availableGhosts = new ArrayList<>();
        for (GHOST g : GHOST.values()) {
            if (game.getGhostLairTime(g) <= 0) {
                availableGhosts.add(g);
            }
        }

        // 1. PERSEGUIDOR
        GHOST pursuer = getClosestGhost(game, availableGhosts, targetPursuer);
        if (pursuer != null) {
            roleMap.put(pursuer, Role.PURSUER);
            availableGhosts.remove(pursuer);
        }

        // 2. INTERCEPTOR DE PÍLDORA DE PODER
        GHOST powerPillInterceptor = getClosestGhost(game, availableGhosts, targetPowerPill);
        if (powerPillInterceptor != null) {
            roleMap.put(powerPillInterceptor, Role.POWER_PILL_INTERCEPTOR);
            availableGhosts.remove(powerPillInterceptor);
        }

        // 3. INTERCEPTOR DE PÍLDORA NORMAL
        GHOST pillInterceptor = getClosestGhost(game, availableGhosts, targetPill);
        if (pillInterceptor != null) {
            roleMap.put(pillInterceptor, Role.PILL_INTERCEPTOR);
            availableGhosts.remove(pillInterceptor);
        }

        // 4. PATRULLERO DE LA JAULA
        for (GHOST remaining : availableGhosts) {
            roleMap.put(remaining, Role.LAIR_PATROLLER);
        }

        return roleMap;
    }

    private GHOST getClosestGhost(Game game, List<GHOST> ghosts, int targetNode) {
        GHOST closest = null;
        int minDistance = Integer.MAX_VALUE;

        for (GHOST g : ghosts) {
            int pos = game.getGhostCurrentNodeIndex(g);
            if (pos == -1) continue;
            
            int dist = game.getShortestPathDistance(pos, targetNode);
            if (dist != -1 && dist < minDistance) {
                minDistance = dist;
                closest = g;
            }
        }
        return closest;
    }

    private MOVE getGhostMove(Game game, GHOST ghost, Role role, int targetPursuer, int nearestPowerPill, int targetPowerPill, int targetPill, int targetLair) {
        int ghostPos = game.getGhostCurrentNodeIndex(ghost);
        int pacmanPos = game.getPacmanCurrentNodeIndex();
        int distToPacman = game.getShortestPathDistance(ghostPos, pacmanPos);

        // REGLA 1: ESTADO COMESTIBLE
        if (game.isGhostEdible(ghost)) {
            int edibleTime = game.getGhostEdibleTime(ghost);
            if ((edibleTime / 2.0) >= distToPacman) {
                return game.getNextMoveAwayFromTarget(ghostPos, pacmanPos, DM.PATH);
            }
        }

        // REGLA DE PREVENCIÓN: Solo huir si Ms. Pac-Man está CERCA de la Power Pill (<= 15 casillas) 
        // y además va a llegar antes/al mismo tiempo que el fantasma.
        if (nearestPowerPill != -1) {
            int distPacmanToPowerPill = game.getShortestPathDistance(pacmanPos, nearestPowerPill);
            int distGhostToPowerPill = game.getShortestPathDistance(ghostPos, nearestPowerPill);

            if (distPacmanToPowerPill != -1 && distGhostToPowerPill != -1) {
                if (distPacmanToPowerPill <= POWER_PILL_DANGER_DISTANCE && distPacmanToPowerPill <= distGhostToPowerPill) {
                    return game.getNextMoveAwayFromTarget(ghostPos, pacmanPos, DM.PATH);
                }
            }
        }

        // REGLA 2: PROXIMIDAD (Si está a menos de 15 nodos de Pac-Man, ataca directamente)
        if (distToPacman != -1 && distToPacman < 15) {
            // VERIFICACIÓN AÑADIDA: Solo persigue si es el PURSUER oficial o el perseguidor secundario activo
            if (role == Role.PURSUER || ghost == secondaryPursuer) {
                return game.getNextMoveTowardsTarget(ghostPos, pacmanPos, DM.PATH);
            }
        }

        // REGLA 3: MOVER HACIA EL OBJETIVO DEL ROL ASIGNADO
        int targetNode = pacmanPos;
        if (role != null) {
            switch (role) {
                case PURSUER:
                    targetNode = targetPursuer;
                    break;
                case POWER_PILL_INTERCEPTOR:
                    targetNode = targetPowerPill;
                    break;
                case PILL_INTERCEPTOR:
                    targetNode = targetPill;
                    break;
                case LAIR_PATROLLER:
                    targetNode = targetLair;
                    break;
            }
        }

        return game.getNextMoveTowardsTarget(ghostPos, targetNode, DM.PATH);
    }

    private int getLairPatrolTarget(Game game) {
        int lairNode = game.getGhostInitialNodeIndex();
        int[] junctions = game.getJunctionIndices();

        int nearestJunction = -1;
        int minDist = Integer.MAX_VALUE;

        for (int junction : junctions) {
            int dist = game.getShortestPathDistance(lairNode, junction);
            if (dist > 0 && dist < minDist) {
                minDist = dist;
                nearestJunction = junction;
            }
        }

        return nearestJunction != -1 ? nearestJunction : lairNode;
    }

    private int getNearestPillToPacman(Game game, int pacmanPos) {
        int[] activePills = game.getActivePillsIndices();
        int minDistance = Integer.MAX_VALUE;
        int targetPill = -1;

        for (int pill : activePills) {
            int dist = game.getShortestPathDistance(pacmanPos, pill);
            if (dist != -1 && dist < minDistance) {
                minDistance = dist;
                targetPill = pill;
            }
        }
        return targetPill;
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
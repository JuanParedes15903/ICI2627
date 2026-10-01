package es.ucm.fdi.ici.c2627.practica1.grupoXX;

import java.util.ArrayList;
import java.util.Arrays;

//En esta segunda iteración, se va a implementar que los fantasmas vayan rotando los roles
//en función de cuál tiene la mejor posición para hacer cada cosa
//Es decir, el que esté más cerca de ms pacman la perseguirá, no tiene por qué ser Blinky todo el rato

import java.util.EnumMap;
import java.util.List;
import java.util.Random;

import pacman.controllers.GhostController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Ghosts2 extends GhostController {

	// Definición de roles disponibles
    private enum Role {
        PURSUER,                 // Persigue a Ms. Pac-Man
        POWER_PILL_INTERCEPTOR,  // Tapa el camino a la píldora de poder más cercana
        PILL_INTERCEPTOR,        // Tapa el camino a la baya/píldora normal más cercana
        LAIR_PATROLLER           // Patrulla la salida de la jaula
    }

    private final EnumMap<GHOST, MOVE> myMoves = new EnumMap<GHOST, MOVE>(GHOST.class);

    @Override
    public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
        myMoves.clear();

        int pacmanPos = game.getPacmanCurrentNodeIndex();

        // 1. Determinar los nodos objetivo para cada rol
        int targetPursuer = pacmanPos;

        int targetPowerPill = getNearestPowerPillToPacman(game, pacmanPos);
        if (targetPowerPill == -1) targetPowerPill = pacmanPos; // Si no quedan, persigue

        int targetPill = getNearestPillToPacman(game, pacmanPos);
        if (targetPill == -1) targetPill = pacmanPos; // Si no quedan, persigue

        int targetLair = getLairPatrolTarget(game);

        // 2. Asignar roles dinámicamente sin repetir fantasmas
        EnumMap<GHOST, Role> assignedRoles = assignRoles(game, targetPursuer, targetPowerPill, targetPill);

        // 3. Calcular el movimiento de cada fantasma según las reglas y su rol asignado
        for (GHOST ghost : GHOST.values()) {
            if (game.doesGhostRequireAction(ghost)) {
                Role ghostRole = assignedRoles.get(ghost);
                MOVE move = getGhostMove(game, ghost, ghostRole, targetPursuer, targetPowerPill, targetPill, targetLair);
                myMoves.put(ghost, move);
            }
        }

        return myMoves;
    }

    /**
     * Asigna un único rol a cada fantasma basándose en la cercanía al objetivo de dicho rol.
     */
    private EnumMap<GHOST, Role> assignRoles(Game game, int targetPursuer, int targetPowerPill, int targetPill) {
        EnumMap<GHOST, Role> roleMap = new EnumMap<>(GHOST.class);
        List<GHOST> availableGhosts = new ArrayList<>(Arrays.asList(GHOST.values()));

        // 1. El fantasma más cercano a Ms. Pac-Man es el PERSEGUIDOR
        GHOST pursuer = getClosestGhost(game, availableGhosts, targetPursuer);
        if (pursuer != null) {
            roleMap.put(pursuer, Role.PURSUER);
            availableGhosts.remove(pursuer);
        }

        // 2. De los restantes, el más cercano a la Píldora de Poder es el INTERCEPTOR DE PÍLDORA DE PODER
        GHOST powerPillInterceptor = getClosestGhost(game, availableGhosts, targetPowerPill);
        if (powerPillInterceptor != null) {
            roleMap.put(powerPillInterceptor, Role.POWER_PILL_INTERCEPTOR);
            availableGhosts.remove(powerPillInterceptor);
        }

        // 3. De los restantes, el más cercano a la Píldora Normal es el INTERCEPTOR DE BAYA
        GHOST pillInterceptor = getClosestGhost(game, availableGhosts, targetPill);
        if (pillInterceptor != null) {
            roleMap.put(pillInterceptor, Role.PILL_INTERCEPTOR);
            availableGhosts.remove(pillInterceptor);
        }

        // 4. El fantasma restante es el PATRULLERO DE LA JAULA
        if (!availableGhosts.isEmpty()) {
            roleMap.put(availableGhosts.get(0), Role.LAIR_PATROLLER);
        }

        return roleMap;
    }

    /**
     * Busca qué fantasma de la lista dada está más cerca de un nodo objetivo.
     */
    private GHOST getClosestGhost(Game game, List<GHOST> ghosts, int targetNode) {
        GHOST closest = null;
        int minDistance = Integer.MAX_VALUE;

        for (GHOST g : ghosts) {
            int pos = game.getGhostCurrentNodeIndex(g);
            int dist = game.getShortestPathDistance(pos, targetNode);
            if (dist < minDistance) {
                minDistance = dist;
                closest = g;
            }
        }
        return closest;
    }

    private MOVE getGhostMove(Game game, GHOST ghost, Role role, int targetPursuer, int targetPowerPill, int targetPill, int targetLair) {
        int ghostPos = game.getGhostCurrentNodeIndex(ghost);
        int pacmanPos = game.getPacmanCurrentNodeIndex();
        int distToPacman = game.getShortestPathDistance(ghostPos, pacmanPos);

        // REGLA 1: ESTADO COMESTIBLE
        if (game.isGhostEdible(ghost)) {
            int edibleTime = game.getGhostEdibleTime(ghost);
            // Huye si (edibleTime / 2.0) >= distancia a Ms. Pac-Man. Si es menor, vuelve a su comportamiento normal.
            if ((edibleTime / 2.0) >= distToPacman) {
                return game.getNextMoveAwayFromTarget(ghostPos, pacmanPos, DM.PATH);
            }
        }

        // REGLA 2: SOBRESCRITURA POR PROXIMIDAD (Menos de un cruce de distancia)
        if (isWithinOneJunction(game, ghostPos, pacmanPos)) {
            return game.getNextMoveTowardsTarget(ghostPos, pacmanPos, DM.PATH);
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

    /**
     * Comprueba si Ms. Pac-Man está más cerca del fantasma que la distancia al cruce más cercano.
     */
    private boolean isWithinOneJunction(Game game, int ghostPos, int pacmanPos) {
        int distToPacman = game.getShortestPathDistance(ghostPos, pacmanPos);
        int[] junctions = game.getJunctionIndices();

        int minJunctionDist = Integer.MAX_VALUE;
        for (int junction : junctions) {
            int dist = game.getShortestPathDistance(pacmanPos, junction);
            if (dist < minJunctionDist) {
                minJunctionDist = dist;
            }
        }

        return distToPacman < minJunctionDist;
    }

    /**
     * Obtiene el cruce más cercano a la jaula (posición inicial) de los fantasmas.
     */
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
            if (dist < minDistance) {
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
            if (dist < minDistance) {
                minDistance = dist;
                targetPowerPill = powerPill;
            }
        }
        return targetPowerPill;
    }
}
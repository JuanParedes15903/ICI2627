package es.ucm.fdi.ici.c2627.practica1.grupoXX;

import java.util.EnumMap;
import java.util.Random;

import pacman.controllers.GhostController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Ghosts extends GhostController {

	private final EnumMap<GHOST, MOVE> myMoves = new EnumMap<GHOST, MOVE>(GHOST.class);
    private final Random random = new Random();

    @Override
    public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
        myMoves.clear();

        for (GHOST ghost : GHOST.values()) {
            if (game.doesGhostRequireAction(ghost)) {
                myMoves.put(ghost, getGhostMove(game, ghost));
            }
        }

        return myMoves;
    }

    private MOVE getGhostMove(Game game, GHOST ghost) {
        int ghostPos = game.getGhostCurrentNodeIndex(ghost);
        int pacmanPos = game.getPacmanCurrentNodeIndex();
        int distToPacman = game.getShortestPathDistance(ghostPos, pacmanPos);

        // 1. ESTADO COMESTIBLE
        if (game.isGhostEdible(ghost)) {
            int edibleTime = game.getGhostEdibleTime(ghost);
            
            // Huye mientras la mitad del tiempo restante sea mayor o igual a la distancia a Ms. Pac-Man.
            // Cuando (edibleTime / 2.0) < distToPacman, se considera seguro y vuelve a su rol normal.
            if ((edibleTime / 2.0) >= distToPacman) {
                return game.getNextMoveAwayFromTarget(ghostPos, pacmanPos, DM.PATH);
            }
        }

        // 2. ESTADO NORMAL: Sobrescritura por cercanía (menos de un cruce)
        if (isWithinOneJunction(game, ghostPos, pacmanPos)) {
            return game.getNextMoveTowardsTarget(ghostPos, pacmanPos, DM.PATH);
        }

        // 3. ESTRATEGIA SEGÚN EL ROL
        GHOST effectiveRole = ghost;

        // Sue alterna aleatoriamente entre los roles de Blinky, Pinky e Inky
        if (ghost == GHOST.SUE) {
            GHOST[] roles = {GHOST.BLINKY, GHOST.PINKY, GHOST.INKY};
            effectiveRole = roles[random.nextInt(roles.length)];
        }

        switch (effectiveRole) {
            case BLINKY:
                // Persigue directamente a Ms. Pac-Man
                return game.getNextMoveTowardsTarget(ghostPos, pacmanPos, DM.PATH);

            case PINKY:
                // Intercepta el camino hacia la baya (píldora normal) más cercana a Ms. Pac-Man
                int nearestPill = getNearestPillToPacman(game, pacmanPos);
                if (nearestPill != -1) {
                    return game.getNextMoveTowardsTarget(ghostPos, nearestPill, DM.PATH);
                }
                return game.getNextMoveTowardsTarget(ghostPos, pacmanPos, DM.PATH);

            case INKY:
                // Intercepta el camino hacia la baya de poder (power pill) más cercana a Ms. Pac-Man
                int nearestPowerPill = getNearestPowerPillToPacman(game, pacmanPos);
                if (nearestPowerPill != -1) {
                    return game.getNextMoveTowardsTarget(ghostPos, nearestPowerPill, DM.PATH);
                }
                return game.getNextMoveTowardsTarget(ghostPos, pacmanPos, DM.PATH);

            default:
                return game.getNextMoveTowardsTarget(ghostPos, pacmanPos, DM.PATH);
        }
    }

    /**
     * Comprueba si Ms. Pac-Man está a una distancia menor que la distancia al cruce más cercano.
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
     * Obtiene la posición de la baya (píldora normal) más cercana a Ms. Pac-Man.
     */
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

    /**
     * Obtiene la posición de la baya de poder más cercana a Ms. Pac-Man.
     */
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

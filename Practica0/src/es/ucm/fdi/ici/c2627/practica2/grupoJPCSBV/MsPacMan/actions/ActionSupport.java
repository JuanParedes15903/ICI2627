package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan.actions;

import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

final class ActionSupport {

    private static final int GHOST_PATH_MARGIN = 4;

    private ActionSupport() {
    }

    static MOVE moveTowardsNearest(Game game, int[] targets, boolean avoidGhosts) {
        return chooseMove(game, targets, false, avoidGhosts, false, false);
    }

    static MOVE moveTowardsPowerPill(Game game, boolean avoidGhosts) {
        return chooseMove(game, game.getActivePowerPillsIndices(), true,
                avoidGhosts, false, false);
    }

    static MOVE moveTowardsSafePill(Game game) {
        return chooseMove(game, game.getActivePillsIndices(), false,
                true, false, false);
    }

    static MOVE moveAlongPillRichPath(Game game) {
        return chooseMove(game, game.getActivePillsIndices(), false,
                true, true, false);
    }

    static MOVE moveAwayFromGhosts(Game game, boolean towardsPowerPill) {
        int[] targets = towardsPowerPill
                ? game.getActivePowerPillsIndices()
                : game.getActivePillsIndices();
        return chooseMove(game, targets, towardsPowerPill, true, false, true);
    }

    static MOVE approachSafeGhost(Game game) {
        int pacman = game.getPacmanCurrentNodeIndex();
        MOVE lastMove = game.getPacmanLastMoveMade();
        if (pacman < 0 || lastMove == null) {
            return MOVE.NEUTRAL;
        }

        MOVE bestMove = MOVE.NEUTRAL;
        double bestScore = Double.NEGATIVE_INFINITY;
        for (MOVE move : game.getPossibleMoves(pacman, lastMove)) {
            int next = game.getNeighbour(pacman, move);
            if (next < 0 || isUnsafe(game, next)) {
                continue;
            }

            int nearestGhost = nearestReachableGhostDistance(game, next);
            if (nearestGhost <= 2) {
                continue;
            }

            int closestGhost = closestGhostDistance(game, next);
            if (!canReachNextJunctionFirst(game, next, move)) {
                continue;
            }
            double score = -closestGhost + nearestGhost * 0.05;
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove == MOVE.NEUTRAL
                ? safestMove(game, false)
                : bestMove;
    }

    private static MOVE chooseMove(Game game, int[] targets, boolean powerTarget,
            boolean avoidGhosts, boolean valuePills, boolean moveAway) {
        int pacman = game.getPacmanCurrentNodeIndex();
        MOVE lastMove = game.getPacmanLastMoveMade();
        if (pacman < 0 || lastMove == null) {
            return MOVE.NEUTRAL;
        }

        MOVE bestMove = MOVE.NEUTRAL;
        double bestScore = Double.NEGATIVE_INFINITY;
        for (MOVE move : game.getPossibleMoves(pacman, lastMove)) {
            int next = game.getNeighbour(pacman, move);
            if (next < 0) {
                continue;
            }

            int ghostDistance = nearestReachableGhostDistance(game, next);
            if (avoidGhosts && ghostDistance <= 1) {
                continue;
            }

            int target = nearestTarget(game, next, targets);
            int[] path = target < 0 ? new int[0] : game.getShortestPath(next, target);
            if (avoidGhosts && pathIsInterceptable(game, path, 1)) {
                continue;
            }
            if (valuePills
                    && (contains(game.getActivePowerPillsIndices(), next)
                            || pathContainsPowerPill(path, game))) {
                continue;
            }

            double score = moveAway
                    ? ghostDistance * 20.0 + targetScore(game, next, targets) * 2.0
                    : targetScore(game, next, targets);

            if (valuePills && !moveAway) {
                score += pillsOnBestPath(game, next, targets) * 8.0;
            }
            if (powerTarget && contains(game.getActivePowerPillsIndices(), next)) {
                score += 1000.0;
            }
            if (avoidGhosts) {
                score += ghostDistance * 4.0;
            }

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove == MOVE.NEUTRAL
                ? safestMove(game, valuePills)
                : bestMove;
    }

    private static double targetScore(Game game, int from, int[] targets) {
        if (targets.length == 0) {
            return 0;
        }

        int nearest = Integer.MAX_VALUE;
        for (int target : targets) {
            nearest = Math.min(nearest, game.getShortestPathDistance(from, target));
        }
        return -nearest;
    }

    private static int nearestTarget(Game game, int from, int[] targets) {
        int nearestTarget = -1;
        int nearestDistance = Integer.MAX_VALUE;
        for (int target : targets) {
            int distance = game.getShortestPathDistance(from, target);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestTarget = target;
            }
        }
        return nearestTarget;
    }

    private static boolean pathIsInterceptable(Game game, int[] path,
            int initialPacmanSteps) {
        int pacmanSteps = initialPacmanSteps;
        for (int node : path) {
            if (isDangerousNode(game, node)
                    || (game.isJunction(node)
                            && !pacmanArrivesBeforeGhosts(game, node, pacmanSteps))) {
                return true;
            }
            pacmanSteps++;
        }
        return false;
    }

    private static boolean pacmanArrivesBeforeGhosts(Game game, int junction,
            int pacmanSteps) {
        for (GHOST ghost : GHOST.values()) {
            if (Boolean.TRUE.equals(game.isGhostEdible(ghost))
                    || game.getGhostLairTime(ghost) > 0) {
                continue;
            }

            int ghostNode = game.getGhostCurrentNodeIndex(ghost);
            MOVE ghostLastMove = game.getGhostLastMoveMade(ghost);
            if (ghostNode < 0 || ghostLastMove == null) {
                continue;
            }

            int ghostSteps = game.getShortestPathDistance(
                    ghostNode, junction, ghostLastMove);
            if (ghostSteps < pacmanSteps + GHOST_PATH_MARGIN) {
                return false;
            }
        }
        return true;
    }

    private static boolean isDangerousNode(Game game, int node) {
        for (GHOST ghost : GHOST.values()) {
            if (!Boolean.TRUE.equals(game.isGhostEdible(ghost))
                    && game.getGhostLairTime(ghost) <= 0
                    && game.getGhostCurrentNodeIndex(ghost) == node) {
                return true;
            }
        }
        return false;
    }

    private static boolean pathContainsPowerPill(int[] path, Game game) {
        int[] powerPills = game.getActivePowerPillsIndices();
        for (int node : path) {
            if (contains(powerPills, node)) {
                return true;
            }
        }
        return false;
    }

    private static int pillsOnBestPath(Game game, int from, int[] targets) {
        int bestCount = 0;
        int[] pills = game.getActivePillsIndices();
        for (int target : targets) {
            int[] path = game.getShortestPath(from, target);
            int count = 0;
            for (int node : path) {
                if (contains(pills, node)) {
                    count++;
                }
            }
            bestCount = Math.max(bestCount, count);
        }
        return bestCount;
    }

    private static boolean isUnsafe(Game game, int node) {
        return nearestReachableGhostDistance(game, node) <= 1;
    }

    private static int nearestReachableGhostDistance(Game game, int node) {
        int nearest = Integer.MAX_VALUE;
        for (GHOST ghost : GHOST.values()) {
            if (Boolean.TRUE.equals(game.isGhostEdible(ghost))
                    || game.getGhostLairTime(ghost) > 0) {
                continue;
            }
            int ghostNode = game.getGhostCurrentNodeIndex(ghost);
            if (ghostNode >= 0) {
                int distance;
                MOVE ghostLastMove = game.getGhostLastMoveMade(ghost);
                if (ghostLastMove == null) {
                    distance = game.getShortestPathDistance(ghostNode, node);
                } else {
                    distance = game.getShortestPathDistance(
                            ghostNode, node, ghostLastMove);
                }
                nearest = Math.min(nearest, distance);
            }
        }
        return nearest;
    }

    private static int closestGhostDistance(Game game, int node) {
        int nearest = Integer.MAX_VALUE;
        for (GHOST ghost : GHOST.values()) {
            if (game.getGhostCurrentNodeIndex(ghost) >= 0) {
                nearest = Math.min(nearest,
                        game.getShortestPathDistance(
                                game.getGhostCurrentNodeIndex(ghost), node));
            }
        }
        return nearest;
    }

    private static MOVE safestMove(Game game, boolean avoidPowerPills) {
        int pacman = game.getPacmanCurrentNodeIndex();
        MOVE lastMove = game.getPacmanLastMoveMade();
        if (pacman < 0 || lastMove == null) {
            return MOVE.NEUTRAL;
        }

        MOVE bestMove = MOVE.NEUTRAL;
        int bestDistance = Integer.MIN_VALUE;
        for (MOVE move : game.getPossibleMoves(pacman, lastMove)) {
            int next = game.getNeighbour(pacman, move);
            if (avoidPowerPills
                    && contains(game.getActivePowerPillsIndices(), next)) {
                continue;
            }
            if (isDangerousNode(game, next)
                    || !canReachNextJunctionFirst(game, next, move)) {
                continue;
            }
            int distance = nearestReachableGhostDistance(game, next);
            if (distance > bestDistance) {
                bestDistance = distance;
                bestMove = move;
            }
        }

        if (bestMove != MOVE.NEUTRAL) {
            return bestMove;
        }

        return MOVE.NEUTRAL;
    }

    private static boolean canReachNextJunctionFirst(Game game, int node,
            MOVE direction) {
        int steps = 1;
        int current = node;
        MOVE currentDirection = direction;

        while (current >= 0 && !game.isJunction(current)) {
            MOVE[] moves = game.getPossibleMoves(current, currentDirection);
            if (moves.length == 0) {
                return true;
            }
            currentDirection = moves[0];
            current = game.getNeighbour(current, currentDirection);
            steps++;
        }

        return current < 0 || pacmanArrivesBeforeGhosts(game, current, steps);
    }

    private static boolean contains(int[] values, int value) {
        for (int candidate : values) {
            if (candidate == value) {
                return true;
            }
        }
        return false;
    }
}

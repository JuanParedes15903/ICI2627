package es.ucm.fdi.ici.c2627.practica0.grupoIndividual;

import java.awt.Color;
import pacman.controllers.PacmanController;
import pacman.game.Game;
import pacman.game.GameView;
import pacman.game.Constants;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public class MsPacManPr0Individual extends PacmanController {
	private Color[] colours = { Color.RED, Color.PINK, Color.CYAN, Color.ORANGE };

	@Override
	public MOVE getMove(Game game, long timeDue) {
		int limit = 30;
		int posPacman = game.getPacmanCurrentNodeIndex();
		GHOST nearestGhost = getNearestChasingGhost(limit, game);
		if (nearestGhost != null) {
			GameView.addPoints(game, colours[0],
					game.getShortestPath(game.getGhostCurrentNodeIndex(nearestGhost), posPacman));
			return game.getApproximateNextMoveAwayFromTarget(posPacman, game.getGhostCurrentNodeIndex(nearestGhost),
					game.getPacmanLastMoveMade(), Constants.DM.PATH);
		}
		nearestGhost = getNearestEdibleGhost(limit, game);
		if (nearestGhost != null) {
			GameView.addPoints(game, colours[2],
					game.getShortestPath(game.getGhostCurrentNodeIndex(nearestGhost), posPacman));
			return game.getApproximateNextMoveTowardsTarget(posPacman, game.getGhostCurrentNodeIndex(nearestGhost),
					game.getPacmanLastMoveMade(), Constants.DM.PATH);
		}
		int nearestPill = game.getClosestNodeIndexFromNodeIndex(posPacman, game.getActivePillsIndices(),
				Constants.DM.PATH);
		GameView.addPoints(game, colours[3], game.getShortestPath(nearestPill, posPacman));
		return game.getApproximateNextMoveTowardsTarget(posPacman, nearestPill, game.getPacmanLastMoveMade(),
				Constants.DM.PATH);

	}

	private GHOST getNearestEdibleGhost(int limit, Game game) {
		GHOST nearestGhost = null;
		double minDistance = limit;
		for (GHOST ghostType : GHOST.values()) {
			if (game.getGhostLairTime(ghostType) <= 0 && game.getGhostEdibleTime(ghostType) > 0) {
				double ghostDistance = game.getDistance(game.getPacmanCurrentNodeIndex(),
						game.getGhostCurrentNodeIndex(ghostType), game.getGhostLastMoveMade(ghostType),
						Constants.DM.PATH);
				if (ghostDistance < minDistance && ghostDistance <= limit) {
					minDistance = ghostDistance;
					nearestGhost = ghostType;
				}
			}
		}
		return nearestGhost;
	}

	private GHOST getNearestChasingGhost(int limit, Game game) {
		GHOST nearestGhost = null;
		double minDistance = limit;
		for (GHOST ghostType : GHOST.values()) {
			if (game.getGhostLairTime(ghostType) <= 0 && game.getGhostEdibleTime(ghostType) <= 0) {
				double ghostDistance = game.getDistance(game.getGhostCurrentNodeIndex(ghostType),
						game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghostType), Constants.DM.PATH);
				if (ghostDistance < minDistance && ghostDistance <= limit) {
					minDistance = ghostDistance;
					nearestGhost = ghostType;
				}
			}
		}
		return nearestGhost;
	}

	public String getName() {
		return "MsPacMan";
	}

}

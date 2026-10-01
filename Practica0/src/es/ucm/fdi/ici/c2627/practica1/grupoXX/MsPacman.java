package es.ucm.fdi.ici.c2627.practica1.grupoXX;

import java.awt.Color;
import pacman.controllers.PacmanController;
import pacman.game.Game;
import pacman.game.GameView;
import pacman.game.Constants;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public class MsPacMan extends PacmanController {
	private Color[] colours = { Color.RED, Color.PINK, Color.CYAN, Color.ORANGE }; // DEPURACIÓN

	@Override
	public MOVE getMove(Game game, long timeDue) {

		int posPacman = game.getPacmanCurrentNodeIndex();
		if (game.isJunction(posPacman)) { // Solo hace decisiones si está en un cruce
			int limit = 30;
			
			
			GHOST nearestGhost = getNearestChasingGhost(limit, game); // Prioriza que no haya fantasmas cerca
			if (nearestGhost != null) {
				GameView.addPoints(game, colours[0], // DEPURACIÓN
						game.getShortestPath(game.getGhostCurrentNodeIndex(nearestGhost), posPacman));
				return game.getApproximateNextMoveAwayFromTarget(posPacman, game.getGhostCurrentNodeIndex(nearestGhost),
						game.getPacmanLastMoveMade(), Constants.DM.PATH);
			}
			nearestGhost = getNearestEdibleGhost(limit, game); // Después prioriza que haya fantasmas que se puedan
																// comer
			if (nearestGhost != null) {
				GameView.addPoints(game, colours[2], // DEPURACIÓN
						game.getShortestPath(game.getGhostCurrentNodeIndex(nearestGhost), posPacman));
				return game.getApproximateNextMoveTowardsTarget(posPacman, game.getGhostCurrentNodeIndex(nearestGhost),
						game.getPacmanLastMoveMade(), Constants.DM.PATH);
			}
			int nearestPill = game.getClosestNodeIndexFromNodeIndex(posPacman, game.getActivePillsIndices(),
					Constants.DM.PATH); // Por último busca la pildora más cercana
			GameView.addPoints(game, colours[3], game.getShortestPath(nearestPill, posPacman)); // DEPURACIÓN
			return game.getApproximateNextMoveTowardsTarget(posPacman, nearestPill, game.getPacmanLastMoveMade(),
					Constants.DM.PATH);
		} else
			return null;
	}

	private GHOST getNearestEdibleGhost(int limit, Game game) { // devuelve el fantasma comestible más cercano
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

	private GHOST getNearestChasingGhost(int limit, Game game) { // devuelve el fantasma más cercano
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
	
	tuple[] verSiguientesCruces(int node, Game game) {
		MOVE direccionActual= game.getPacmanLastMoveMade();
		MOVE[] direcciones=game.getPossibleMoves(node, direccionActual);
		for(MOVE m: direcciones) {
			int siguienteNodo= game.getNeighbour(node, m);	
			
			}
		
		return {numFantasmas, numComestibles, numPildoras, numPPoder}
	}

	public String getName() {
		return "MsPacManPr1_JPC_SBV";
	}

}

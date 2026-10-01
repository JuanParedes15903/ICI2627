package es.ucm.fdi.ici.c2627.practica0.grupoIndividual;

import pacman.controllers.PacmanController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacManRunAway extends PacmanController{

	private GHOST nearestGhost;
	private double minDist = Double.MAX_VALUE;
	@Override
	public MOVE getMove(Game game, long timeDue) {
		for(GHOST ghostType : GHOST.values()) {
			if(minDist > game.getDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(ghostType), DM.PATH)) {
				nearestGhost = ghostType;
			}
		}
		minDist = Double.MAX_VALUE;
		return game.getApproximateNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(nearestGhost), game.getPacmanLastMoveMade(), DM.PATH);
	}
	
	public String getName() {
		return "MsPacManRunAway";
	}
}

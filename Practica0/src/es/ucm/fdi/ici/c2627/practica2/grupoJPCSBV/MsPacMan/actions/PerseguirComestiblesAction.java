package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan.actions;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class PerseguirComestiblesAction implements Action{

	@Override
	public String getActionId() {
		return "Perseguir fantasmas comestibles";
	}

	@Override
	public MOVE execute(Game game) {
		int[] edibleGhosts = new int[pacman.game.Constants.GHOST.values().length];
		int count = 0;
		for (pacman.game.Constants.GHOST ghost : pacman.game.Constants.GHOST.values()) {
			if (Boolean.TRUE.equals(game.isGhostEdible(ghost))
					&& game.getGhostCurrentNodeIndex(ghost) >= 0) {
				edibleGhosts[count++] = game.getGhostCurrentNodeIndex(ghost);
			}
		}
		int[] targets = new int[count];
		System.arraycopy(edibleGhosts, 0, targets, 0, count);
		return ActionSupport.moveTowardsNearest(game, targets, true);
	}

}

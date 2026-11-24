package es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.mspacman;

import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class MsPacManInputV3 extends MsPacManInputV2 {

	public static final int DISTANCIA_ENCIERRO = 30;

	public MsPacManInputV3(Game game) {
		super(game);
	}

	@Override
	protected boolean usarEncierro() {
		int curr = game.getPacmanCurrentNodeIndex();
		for (GHOST g : GHOST.values())
			if (!game.isGhostEdible(g) && ghostTimeTo(game, g, curr) <= distanciaEncierro()) return true;
		return false;
	}

	protected int distanciaEncierro() {
		return DISTANCIA_ENCIERRO;
	}
}

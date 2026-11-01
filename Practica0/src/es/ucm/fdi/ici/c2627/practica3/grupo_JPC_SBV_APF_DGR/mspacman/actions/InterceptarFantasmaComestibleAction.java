package es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.actions;

import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * InterceptarFantasmaComestible (Ataque)
 * Va al cruce hacia el que se dirige un fantasma comestible, si llega a tiempo de comérselo.
 */
public class InterceptarFantasmaComestibleAction extends JugarSinPeligroAction {

	public InterceptarFantasmaComestibleAction() {
	}

	public InterceptarFantasmaComestibleAction(boolean mirarDireccion) {
		super(mirarDireccion);
	}

	/** Va al cruce hacia el que se dirige un fantasma comestible, si llega a tiempo de comérselo. */
	@Override
	public MOVE execute(Game game) {
		GHOST g = fantasmaAInterceptar(game);
		if (g == null) return null;
		int cruce = cruceAlQueVa(game, g);
		int objetivo = game.getPacmanCurrentNodeIndex() == cruce ? game.getGhostCurrentNodeIndex(g) : cruce;
		return moverSeguroHacia(game, objetivo, clasificarMovimientos(game));
	}
}

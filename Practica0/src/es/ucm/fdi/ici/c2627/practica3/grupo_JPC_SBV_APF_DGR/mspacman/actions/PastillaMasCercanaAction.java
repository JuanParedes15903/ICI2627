package es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.actions;

import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * PastillaMasCercana (Pastillas)
 * Va hacia la pastilla más cercana.
 */
public class PastillaMasCercanaAction extends JugarSinPeligroAction {

	public PastillaMasCercanaAction() {
	}

	public PastillaMasCercanaAction(boolean mirarDireccion) {
		super(mirarDireccion);
	}

	/** Va hacia la pastilla más cercana. */
	@Override
	public MOVE execute(Game game) {
		int pastilla = getNearestPill(game, game.getPacmanCurrentNodeIndex());
		if (pastilla == -1) return null;
		return moverSeguroHacia(game, pastilla, clasificarMovimientos(game));
	}
}

package es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.actions;

import java.util.List;

import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * CazarEnCadena (Ataque)
 * Busca un recorrido con el que comerse varios fantasmas comestibles seguidos.
 */
public class CazarEnCadenaAction extends JugarSinPeligroAction {

	public CazarEnCadenaAction() {
	}

	public CazarEnCadenaAction(boolean mirarDireccion) {
		super(mirarDireccion);
	}

	/** Busca un recorrido con el que comerse varios fantasmas comestibles seguidos. */
	@Override
	public MOVE execute(Game game) {
		List<GHOST> plan = planCadena(game);
		if (plan.isEmpty()) return null;
		int objetivo = game.getGhostCurrentNodeIndex(plan.get(0));
		return moverSeguroHacia(game, objetivo, clasificarMovimientos(game));
	}
}

package es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.actions;

import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman.Movimientos;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * IrAPorPastillaDePoder (PastillasPoder)
 * Va hacia la pastilla de poder más cercana.
 */
public class IrAPorPastillaDePoderAction extends JugarSinPeligroAction {

	public IrAPorPastillaDePoderAction() {
	}

	public IrAPorPastillaDePoderAction(boolean mirarDireccion) {
		super(mirarDireccion);
	}

	/** Va hacia la pastilla de poder más cercana. */
	@Override
	public MOVE execute(Game game) {
		Movimientos movimientos = clasificarMovimientos(game);
		int pastilla = pastillaDePoderMasCercana(game, game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade());
		if (pastilla == -1) return movimientos.seguros.length > 0 ? movimientos.seguros[0] : null;
		return moverSeguroHacia(game, pastilla, movimientos);
	}
}

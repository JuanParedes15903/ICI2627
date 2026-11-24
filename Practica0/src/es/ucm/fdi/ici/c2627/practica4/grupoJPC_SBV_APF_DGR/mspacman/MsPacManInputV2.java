package es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.mspacman;

import pacman.game.Game;

public class MsPacManInputV2 extends MsPacManInput implements Encierro {

	public MsPacManInputV2(Game game) {
		super(game);
	}

	@Override
	protected Movimientos calcularMovimientos() {
		Movimientos m = super.calcularMovimientos();
		if (!usarEncierro()) return m;
		return filtrarConEncierro(game, m);
	}

	protected boolean usarEncierro() {
		return true;
	}
}

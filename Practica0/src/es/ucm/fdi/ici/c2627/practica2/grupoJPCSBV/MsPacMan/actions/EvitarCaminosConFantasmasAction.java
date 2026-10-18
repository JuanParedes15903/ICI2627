package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan.actions;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class EvitarCaminosConFantasmasAction implements Action{

	@Override
	public String getActionId() {
		return "Evitar caminos por los que vienen fantasmas";
	}

	@Override
	public MOVE execute(Game game) {
		return ActionSupport.moveAwayFromGhosts(game, false);
	}

}

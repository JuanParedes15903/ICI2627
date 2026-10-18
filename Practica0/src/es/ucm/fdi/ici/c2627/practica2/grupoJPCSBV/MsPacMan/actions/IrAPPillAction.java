package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan.actions;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class IrAPPillAction implements Action{

	@Override
	public String getActionId() {
		return "Ir a por pildora de poder más cercana";
	}

	@Override
	public MOVE execute(Game game) {
		return ActionSupport.moveTowardsPowerPill(game, true);
	}

}

package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan.actions;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class AcercarseAFantasmasAction implements Action{

	@Override
	public String getActionId() {
		return "Acercarse a los fantasmas para atraerlos";
	}

	@Override
	public MOVE execute(Game game) {
		return ActionSupport.approachSafeGhost(game);
	}

}

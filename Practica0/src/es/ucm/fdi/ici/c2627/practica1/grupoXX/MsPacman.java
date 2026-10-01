package es.ucm.fdi.ici.c2627.practica1.grupoXX;

import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacman extends PacmanController{

	@Override
	public MOVE getMove(Game game, long timeDue) {
		MOVE sol = MOVE.NEUTRAL; //sol de solucion
		if(game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade()).length > 1) { //Sirve para determinar si estoy en un cruce o no (en un pasillo sólo tendré 1 movimiento posible)
			
		}
		return sol;
	}

}

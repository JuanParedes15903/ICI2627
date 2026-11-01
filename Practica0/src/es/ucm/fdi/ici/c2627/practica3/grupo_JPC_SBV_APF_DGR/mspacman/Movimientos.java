package es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.mspacman;

import pacman.game.Constants.MOVE;

/** Los movimientos posibles divididos en tres arrays (función de JugarSinPeligro). */
public class Movimientos {
	/** Movimientos seguros. */
	public MOVE[] seguros;
	/** Movimientos mortales seguros. */
	public MOVE[] mortalesSeguros;
	/** Movimientos mortales si los fantasmas van hacia allí. */
	public MOVE[] mortalesSiVan;
}

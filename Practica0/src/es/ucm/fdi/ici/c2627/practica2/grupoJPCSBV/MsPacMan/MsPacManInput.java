package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan;

import es.ucm.fdi.ici.Input;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class MsPacManInput extends Input {

    private int[] distanciaAmenaza;     // por fantasma: pasos que le faltan para llegar a mí
    private int pildorasDePoder;

    public MsPacManInput(Game game) {
        super(game);
    }

    @Override
    public void parseInput() {
        int yo = game.getPacmanCurrentNodeIndex();
        distanciaAmenaza = new int[GHOST.values().length];
        for (GHOST g : GHOST.values()) {
            if (game.isGhostEdible(g) || game.getGhostLairTime(g) > 0)
                distanciaAmenaza[g.ordinal()] = Integer.MAX_VALUE;   // no me puede comer
            else
                distanciaAmenaza[g.ordinal()] = game.getShortestPathDistance(
                        game.getGhostCurrentNodeIndex(g), yo, game.getGhostLastMoveMade(g));
        }
        pildorasDePoder = game.getNumberOfActivePowerPills();
    }

    /** Pasos que le faltan a la amenaza más próxima para alcanzarme. */
    public int amenazaMasCercana() {
        int min = Integer.MAX_VALUE;
        for (int d : distanciaAmenaza)
            min = Math.min(min, d);
        return min;
    }

    /** Cuántos fantasmas que pueden comerme están a menos de {@code radio} pasos. */
    public int amenazasA(int radio) {
        int n = 0;
        for (int d : distanciaAmenaza)
            if (d < radio) n++;
        return n;
    }

    public int getPildorasDePoder() { return pildorasDePoder; }
}
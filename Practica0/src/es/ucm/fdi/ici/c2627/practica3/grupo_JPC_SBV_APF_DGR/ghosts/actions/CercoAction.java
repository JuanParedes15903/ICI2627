package es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.ghosts.actions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import es.ucm.fdi.ici.rules.RulesAction;
import jess.Fact;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class CercoAction implements RulesAction {

    private static final int MAX_PASOS = 12;   // longitud máxima del pasillo hasta el cruce
    private final GHOST yo;

    public CercoAction(GHOST yo) { this.yo = yo; }

    @Override
    public MOVE execute(Game game) {
        int pac = game.getPacmanCurrentNodeIndex();
        int miNodo = game.getGhostCurrentNodeIndex(yo);
        MOVE miUltimo = game.getGhostLastMoveMade(yo);

        // 1. Equipo: fantasmas libres y no comestibles, de más cerca a más lejos de Ms. Pac-Man
        List<GHOST> equipo = new ArrayList<>();
        for (GHOST g : GHOST.values()) {
            if (game.getGhostLairTime(g) == 0 && !game.isGhostEdible(g)) equipo.add(g);
        }
        equipo.sort(Comparator.comparingInt((GHOST g) -> distAPacman(game, g, pac))
                              .thenComparingInt(GHOST::ordinal));

        if (equipo.size() < 2 || equipo.get(0) == yo || !equipo.contains(yo)) {
            return perseguir(game, miNodo, miUltimo, pac);
        }

        // 2. Salidas de Ms. Pac-Man: el siguiente cruce en cada dirección posible (sin repetidos)
        List<Integer> salidas = new ArrayList<>();
        for (MOVE m : game.getPossibleMoves(pac)) {
            int s = siguienteCruce(game, pac, m);
            if (s != -1 && !salidas.contains(s)) salidas.add(s);
        }

        // 3. La salida más cercana al acosador ya la cubre él: la quito del reparto
        GHOST acosador = equipo.get(0);
        int nodoAc = game.getGhostCurrentNodeIndex(acosador);
        MOVE ultimoAc = game.getGhostLastMoveMade(acosador);
        int salidaAc = -1, mejorAc = Integer.MAX_VALUE;
        for (int s : salidas) {
            int d = game.getShortestPathDistance(nodoAc, s, ultimoAc);
            if (d != -1 && d < mejorAc) { mejorAc = d; salidaAc = s; }
        }
        if (salidaAc != -1) salidas.remove(Integer.valueOf(salidaAc));

        // 4. Reparto voraz: de más cerca a más lejos, cada uno coge la salida libre que le queda más cerca
        for (int i = 1; i < equipo.size() && !salidas.isEmpty(); i++) {
            GHOST g = equipo.get(i);
            int nodoG = game.getGhostCurrentNodeIndex(g);
            MOVE ultimoG = game.getGhostLastMoveMade(g);
            int elegida = -1, mejorD = Integer.MAX_VALUE;
            for (int s : salidas) {
                int d = game.getShortestPathDistance(nodoG, s, ultimoG);
                if (d != -1 && d < mejorD) { mejorD = d; elegida = s; }
            }
            if (elegida == -1) continue;
            salidas.remove(Integer.valueOf(elegida));

            if (g == yo) {
                if (miNodo == elegida) return perseguir(game, miNodo, miUltimo, pac); // ya cubro la salida
                return game.getNextMoveTowardsTarget(miNodo, elegida, miUltimo, DM.PATH);
            }
        }
        // Sin salida asignada (hay más fantasmas que salidas): presiono
        return perseguir(game, miNodo, miUltimo, pac);
    }

    // Camina por el pasillo en la dirección 'dir' hasta el primer cruce
    private int siguienteCruce(Game game, int desde, MOVE dir) {
        int nodo = game.getNeighbour(desde, dir);
        MOVE actual = dir;
        for (int i = 0; i < MAX_PASOS && nodo != -1; i++) {
            if (game.isJunction(nodo)) return nodo;
            MOVE[] sig = game.getPossibleMoves(nodo, actual);
            if (sig == null || sig.length == 0) return nodo;
            actual = sig[0];
            int siguiente = game.getNeighbour(nodo, actual);
            if (siguiente == -1) return nodo;
            nodo = siguiente;
        }
        return nodo;
    }

    private int distAPacman(Game game, GHOST g, int pac) {
        int d = game.getShortestPathDistance(game.getGhostCurrentNodeIndex(g), pac, game.getGhostLastMoveMade(g));
        return d == -1 ? 1000 : d;
    }

    private MOVE perseguir(Game game, int miNodo, MOVE miUltimo, int pac) {
        return game.getApproximateNextMoveTowardsTarget(miNodo, pac, miUltimo, DM.PATH);
    }

    @Override public void parseFact(Fact actionFact) { }
    @Override public String getActionId() { return "cerco"; }
}
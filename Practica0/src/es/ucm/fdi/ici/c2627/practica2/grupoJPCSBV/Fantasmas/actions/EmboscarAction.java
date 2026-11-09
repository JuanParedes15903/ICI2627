package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.Fantasmas.actions;
import java.util.EnumMap;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class EmboscarAction implements Action {
    private final GHOST ghost;

    private final EnumMap<GHOST, Integer> tiempoPersiguiendo;
    
    public EmboscarAction(GHOST ghost, EnumMap<GHOST, Integer> tiempoPersiguiendo) {
        this.ghost = ghost;
        this.tiempoPersiguiendo = tiempoPersiguiendo;
    }

    @Override
    public MOVE execute(Game game) {
    	this.tiempoPersiguiendo.put(ghost, 0);
        int yo = game.getGhostCurrentNodeIndex(ghost);
        int pacman = game.getPacmanCurrentNodeIndex();
        MOVE ultimoMovimiento = game.getGhostLastMoveMade(ghost);
        
        int nodoDestinoPredicho = pacman;
        
        int[] powerPillsActivas = game.getActivePowerPillsIndices();
        int[] pillsActivas = game.getActivePillsIndices();

        if (powerPillsActivas.length > 0) {
            int ppMasCercana = game.getClosestNodeIndexFromNodeIndex(pacman, powerPillsActivas, DM.PATH);
            int distA_PP = game.getShortestPathDistance(pacman, ppMasCercana);
            
            // FILTRO: Validar que no sea -1
            if (distA_PP >= 0 && distA_PP < 30) {
                nodoDestinoPredicho = ppMasCercana;
            }
        }
        
        if (nodoDestinoPredicho == pacman && pillsActivas.length > 0) {
            int pillMasCercana = game.getClosestNodeIndexFromNodeIndex(pacman, pillsActivas, DM.PATH);
            if (game.getShortestPathDistance(pacman, pillMasCercana) != -1) {
                nodoDestinoPredicho = pillMasCercana;
            }
        }

        return game.getApproximateNextMoveTowardsTarget(yo, nodoDestinoPredicho, ultimoMovimiento, DM.PATH);
    }

    @Override
    public String getActionId() {
        return ghost + " Emboscando (Interceptando)";
    }
}
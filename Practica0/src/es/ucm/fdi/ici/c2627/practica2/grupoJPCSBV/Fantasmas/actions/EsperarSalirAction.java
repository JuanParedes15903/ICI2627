package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.Fantasmas.actions;

import java.util.EnumMap; // 1. IMPORTANTE: Añadir este import
import es.ucm.fdi.ici.Action;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class EsperarSalirAction implements Action {
    private final GHOST ghost;
    // 2. Añadimos el mapa
    private final EnumMap<GHOST, Integer> tiempoPersiguiendo; 

    // 3. Modificamos el constructor para recibir el mapa
    public EsperarSalirAction(GHOST ghost, EnumMap<GHOST, Integer> tiempoPersiguiendo) {
        this.ghost = ghost;
        this.tiempoPersiguiendo = tiempoPersiguiendo;
    }

    @Override
    public MOVE execute(Game game) {
        // 4. Actualizamos directamente el mapa poniéndolo a 0
        this.tiempoPersiguiendo.put(ghost, 0);
        
        return MOVE.NEUTRAL;
    }

    @Override
    public String getActionId() {
        return ghost + " Esperando a salir";
    }
}
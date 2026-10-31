package es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.ghosts.actions;

import es.ucm.fdi.ici.rules.RulesAction;
import jess.Fact;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class PatrullarAction implements RulesAction {

    private final GHOST yo;

    public PatrullarAction(GHOST yo) { 
        this.yo = yo; 
    }

    @Override
    public MOVE execute(Game game) {
        int myNode = game.getGhostCurrentNodeIndex(yo);
        MOVE lastMove = game.getGhostLastMoveMade(yo); // Guardamos el último movimiento
        
        // Obtiene las Power Pills activas y visibles
        int[] powerPills = game.getActivePowerPillsIndices();
        
        // Si hay power pills visibles, vamos a la que esté más lejos de nosotros
        if (powerPills != null && powerPills.length > 0) {
            
            // 1. Usamos el método de la API para sacar el nodo más lejano directamente (sustituye al bucle for)
            int furthestPPill = game.getFarthestNodeIndexFromNodeIndex(myNode, powerPills, DM.PATH);
            
            // 2. Usamos el método correcto de la API para acercarnos al objetivo
            return game.getNextMoveTowardsTarget(myNode, furthestPPill, lastMove, DM.PATH);
        }
        
        // Si no quedan Power Pills a la vista, nos movemos aleatoriamente (pero de forma válida)
        // 3. Usamos getPossibleMoves pasándole el último movimiento para no dar la vuelta
        MOVE[] posibles = game.getPossibleMoves(myNode, lastMove);
        
        if (posibles != null && posibles.length > 0) {
            // Usamos Math.random() nativo de Java para no depender de game.getRnd() que no está en tu lista de la API
            int rndIndex = (int) (Math.random() * posibles.length);
            return posibles[rndIndex];
        }
        
        return MOVE.NEUTRAL;
    }

    @Override
    public void parseFact(Fact actionFact) { 
        // No requiere implementación
    }

    @Override
    public String getActionId() { 
        return "patrullar"; 
    }
}
package es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.ghosts.actions;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class InterceptarAction implements Action {

    private final GHOST yo;

    public InterceptarAction(GHOST yo) { 
        this.yo = yo; 
    }

    @Override
    public MOVE execute(Game game) {
        int target = game.getPacmanCurrentNodeIndex();
        MOVE pacmanDir = game.getPacmanLastMoveMade();
        
        // Adelantarnos 4 nodos en la dirección a la que mira Ms. Pac-Man
        for (int i = 0; i < 4; i++) {
            int nextNode = game.getNeighbour(target, pacmanDir);
            if (nextNode != -1) {
                target = nextNode;
            } else {
                break; // Si choca con una pared, apuntamos a ese último nodo válido
            }
        }
        
        // Usamos el método correcto de la API
        return game.getNextMoveTowardsTarget(
                game.getGhostCurrentNodeIndex(yo),
                target, 
                game.getGhostLastMoveMade(yo), 
                DM.PATH
        );
    }

    @Override
    public String getActionId() { 
        return "interceptar"; 
    }

    @Override
    public String toString() { 
        return yo + " " + getActionId(); 
    }
}
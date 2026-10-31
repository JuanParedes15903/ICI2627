package es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.ghosts.actions;

import es.ucm.fdi.ici.rules.RulesAction;
import jess.Fact;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class HuirDispersoAction implements RulesAction {

    private final GHOST yo;

    public HuirDispersoAction(GHOST yo) { 
        this.yo = yo; 
    }

    @Override
    public MOVE execute(Game game) {
        int myNode = game.getGhostCurrentNodeIndex(yo);
        MOVE lastMove = game.getGhostLastMoveMade(yo);
        int pacmanNode = game.getPacmanCurrentNodeIndex();

        MOVE[] possibleMoves = game.getPossibleMoves(myNode, lastMove);
        if (possibleMoves == null || possibleMoves.length == 0) return MOVE.NEUTRAL;
        if (possibleMoves.length == 1) return possibleMoves[0];

        MOVE bestMove = possibleMoves[0];
        int maxScore = -1;

        for (MOVE move : possibleMoves) {
            int nextNode = game.getNeighbour(myNode, move);
            
            // Pasamos 'move' como 3er parámetro, ya que será nuestro "último movimiento" si vamos a nextNode
            int distPacman = game.getShortestPathDistance(nextNode, pacmanNode, move);
            
            // Distancia al otro fantasma comestible más cercano
            int minDistGhost = 150; // Valor tope base si no hay otros fantasmas comestibles
            
            for (GHOST g : GHOST.values()) {
                // Usamos getGhostEdibleTime > 0 en lugar del inexistente isGhostEdible
                if (g != yo && game.getGhostEdibleTime(g) > 0 && game.getGhostLairTime(g) == 0) {
                    int gNode = game.getGhostCurrentNodeIndex(g);
                    int distG = game.getShortestPathDistance(nextNode, gNode, move);
                    
                    if (distG != -1 && distG < minDistGhost) {
                        minDistGhost = distG;
                    }
                }
            }
            
            // La fórmula suma ambas distancias. Ponderamos x2 la separación 
            // entre fantasmas para forzar a que no vayan por el mismo pasillo.
            int score = distPacman + (minDistGhost * 2);

            if (score > maxScore) {
                maxScore = score;
                bestMove = move;
            }
        }
        return bestMove;
    }

    @Override
    public void parseFact(Fact actionFact) { }

    @Override
    public String getActionId() { 
        return "huir_disperso"; 
    }
}
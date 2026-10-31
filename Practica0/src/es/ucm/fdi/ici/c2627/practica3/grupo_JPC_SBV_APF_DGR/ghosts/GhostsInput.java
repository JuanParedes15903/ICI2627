package es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.ghosts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import es.ucm.fdi.ici.rules.RulesInput;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class GhostsInput extends RulesInput {

    private List<String> facts;

    public GhostsInput(Game game) { 
        super(game); 
    }

    @Override
    public void parseInput() {
        facts = new ArrayList<>();
        int pacmanNodo = game.getPacmanCurrentNodeIndex();

        // 1. Analizar el estado global y de Ms. Pac-Man
        int[] powerPills = game.getActivePowerPillsIndices();
        int numPPActivas = powerPills.length;
        int numPillsActivas = game.getActivePillsIndices().length;
        
        int distPPillMsPacman = 1000;
        int mejorPPNodo = -1;
        
        // Buscar a qué Power Pill está más cerca Ms. Pac-Man
        if (numPPActivas > 0) {
            for (int pp : powerPills) {
                int dist = game.getShortestPathDistance(pacmanNodo, pp);
                if (dist != -1 && dist < distPPillMsPacman) {
                    distPPillMsPacman = dist;
                    mejorPPNodo = pp;
                }
            }
        }

        // Asertamos la información de Ms. Pac-Man y del juego general
        facts.add(String.format("(MSPACMAN (distPPill %d) (numPP %d) (numPills %d))", 
                distPPillMsPacman, numPPActivas, numPillsActivas));

        // 2. Analizar el estado de cada Fantasma
        for (GHOST g : GHOST.values()) {
            int miNodo = game.getGhostCurrentNodeIndex(g);
            boolean enCarcel = game.getGhostLairTime(g) > 0;
            boolean comestible = game.isGhostEdible(g);
            int tiempoComestible = game.getGhostEdibleTime(g);
            
            // Distancia a Ms. Pac-Man
            int distPacman = enCarcel ? 1000 : game.getShortestPathDistance(miNodo, pacmanNodo, game.getGhostLastMoveMade(g));
            
            // Distancia del fantasma a la Power Pill a la que se dirige Ms. Pac-Man (para interceptar)
            int distAMejorPP = 1000;
            if (!enCarcel && mejorPPNodo != -1) {
                int dist = game.getShortestPathDistance(miNodo, mejorPPNodo, game.getGhostLastMoveMade(g));
                distAMejorPP = (dist == -1) ? 1000 : dist;
            }

            // Asertamos los datos del fantasma (hemos añadido tiempoComestible y distMejorPP)
            facts.add(String.format(
                "(FANTASMA (nombre %s) (comestible %s) (tiempoComestible %d) (enCarcel %s) (distPacman %d) (distMejorPP %d))",
                g, comestible, tiempoComestible, enCarcel, distPacman, distAMejorPP
            ));
        }
    }

    @Override
    public Collection<String> getFacts() { 
        return facts; 
    }
}
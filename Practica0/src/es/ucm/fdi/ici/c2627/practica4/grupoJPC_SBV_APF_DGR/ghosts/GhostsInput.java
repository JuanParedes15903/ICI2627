package es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.ghosts;

import java.util.HashMap;

import es.ucm.fdi.ici.fuzzy.FuzzyInput;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

/** 
 * Lo que ve un fantasma adaptado a lógica difusa: distancias, tiempos comestibles, 
 * estado del juego (píldoras) y su relación espacial con el resto de fantasmas (ranking/roles).
 */
public class GhostsInput extends FuzzyInput {

    public static final double MAX_DIST = 300.0; // Límite máximo para las distancias en el FCL

    private final GHOST yo;
    
    // Variables para la fuzzificación
    private double distPacman;
    private double tiempoComestible;
    private double enCarcel;
    private double distPPMsPacman;
    private double ventajaPP;
    private double ranking;
    private double amigosCerca;
    private double numPills;
    private double numPP;

    public GhostsInput(Game game, GHOST yo) {
        super(game);
        this.yo = yo;
        parseInput(); // el constructor de FuzzyInput lo llama antes de saber quién soy: otra vez
    }

    @Override
    public void parseInput() {
        if (yo == null) return; // Evita el NullPointerException de la llamada del padre
        
        int pacmanNodo = game.getPacmanCurrentNodeIndex();
        int miNodo = game.getGhostCurrentNodeIndex(yo);
        
        // 1. Estado general del juego
        int[] powerPills = game.getActivePowerPillsIndices();
        numPP = powerPills.length;
        numPills = game.getActivePillsIndices().length;
        
        // 2. Analizar a Ms. Pac-Man y su Power Pill más cercana
        distPPMsPacman = MAX_DIST;
        int mejorPPNodo = -1;
        
        if (numPP > 0) {
            for (int pp : powerPills) {
                int dist = game.getShortestPathDistance(pacmanNodo, pp);
                if (dist != -1 && dist < distPPMsPacman) {
                    distPPMsPacman = dist;
                    mejorPPNodo = pp;
                }
            }
        }
        
        // 3. Variables individuales de mi fantasma
        boolean estoyEnCarcel = game.getGhostLairTime(yo) > 0;
        enCarcel = estoyEnCarcel ? 1.0 : 0.0;
        tiempoComestible = game.getGhostEdibleTime(yo);
        
        int distReal = estoyEnCarcel ? (int)MAX_DIST : game.getShortestPathDistance(miNodo, pacmanNodo, game.getGhostLastMoveMade(yo));
        distPacman = (distReal == -1) ? MAX_DIST : Math.min(MAX_DIST, distReal);

        // ¿Estoy yo más cerca de la Power Pill objetivo que Ms. Pac-Man?
        ventajaPP = 0.0;
        if (!estoyEnCarcel && mejorPPNodo != -1) {
            int miDistPP = game.getShortestPathDistance(miNodo, mejorPPNodo, game.getGhostLastMoveMade(yo));
            if (miDistPP != -1 && miDistPP < distPPMsPacman) {
                ventajaPP = 1.0; // Sí, le corto el paso
            }
        }

        // 4. Variables de relación con otros fantasmas (Ranking de cercanía y Amigos cerca)
        ranking = 0.0;     // 0 = el más cercano, 1 = el segundo, etc.
        amigosCerca = 0.0; // Cuántos aliados libres hay relativamente cerca de Ms. Pac-Man
        
        for (GHOST g : GHOST.values()) {
            if (g == yo) continue;
            
            boolean gEnCarcel = game.getGhostLairTime(g) > 0;
            boolean gComestible = game.isGhostEdible(g);
            
            if (!gEnCarcel && !gComestible) {
                int distOtro = game.getShortestPathDistance(game.getGhostCurrentNodeIndex(g), pacmanNodo, game.getGhostLastMoveMade(g));
                
                // Si el otro fantasma está más cerca de Ms. Pac-Man que yo, bajo en el ranking
                if (distOtro != -1 && distOtro < distPacman) {
                    ranking += 1.0;
                }
                
                // Contar aliados en un radio de cerco (aprox < 200 nodos hacia Ms. Pac-Man)
                if (distOtro != -1 && distOtro < 200) {
                    amigosCerca += 1.0;
                }
            }
        }
    }

    @Override
    public HashMap<String, Double> getFuzzyValues() {
        HashMap<String, Double> vars = new HashMap<>();
        vars.put("distPacman", distPacman);
        vars.put("tiempoComestible", tiempoComestible);
        vars.put("enCarcel", enCarcel);
        vars.put("distPPMsPacman", distPPMsPacman);
        vars.put("ventajaPP", ventajaPP);
        vars.put("ranking", ranking);
        vars.put("amigosCerca", amigosCerca);
        vars.put("numPills", numPills);
        vars.put("numPP", numPP);
        return vars;
    }
}
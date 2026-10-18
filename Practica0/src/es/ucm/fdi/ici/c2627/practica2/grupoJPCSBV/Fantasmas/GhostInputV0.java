package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.Fantasmas;

import es.ucm.fdi.ici.Input;
import pacman.game.Constants.GHOST;
import pacman.game.Game;
import java.util.EnumMap;

public class GhostInputV0 extends Input {

    private static final EnumMap<GHOST, Integer> tiempoPersiguiendo = new EnumMap<>(GHOST.class);
    static {
        for (GHOST g : GHOST.values()) tiempoPersiguiendo.put(g, 0);
    }

    private final GHOST ghost;
    private boolean comido;
    private int tiempoComestible;
    private int distanciaMsPacman;
    private int distanciaMejorPP;
    private boolean msPacmanCerca;
    private boolean msPacmanLejos;
    private int numPPActivas;
    private int numPillsActivas;
    private boolean esElMasCercaAMejorPP;
    private boolean debeDejarDePerseguir;
    private boolean persecucionPermitida;

    public GhostInputV0(GHOST ghost, Game game) {
        super(game);         
        this.ghost = ghost;   
        parseInput();         
    }

    @Override
    public void parseInput() {
        if (this.ghost == null) return;

        int miNodo = game.getGhostCurrentNodeIndex(ghost);
        int pacmanNodo = game.getPacmanCurrentNodeIndex();

        this.tiempoComestible = game.getGhostEdibleTime(ghost);
        this.comido = game.getGhostLairTime(ghost) > 0;
        
        int distReal = game.getShortestPathDistance(miNodo, pacmanNodo);
        this.distanciaMsPacman = (distReal == -1) ? Integer.MAX_VALUE : distReal;
        
        this.msPacmanCerca = distanciaMsPacman < 80;
        this.msPacmanLejos = !msPacmanCerca;

        int[] powerPills = game.getActivePowerPillsIndices();
        this.numPPActivas = powerPills.length;
        this.numPillsActivas = game.getActivePillsIndices().length;
        this.esElMasCercaAMejorPP = false; 

        if (this.numPPActivas == 0) {
            this.distanciaMejorPP = -1; 
        } else {
            this.distanciaMejorPP = Integer.MAX_VALUE;
            int mejorPPNodo = -1;
            for (int pp : powerPills) {
                int dist = game.getShortestPathDistance(pacmanNodo, pp);
                if (dist != -1 && dist < distanciaMejorPP) {
                    distanciaMejorPP = dist;
                    mejorPPNodo = pp;
                }
            }

            if (mejorPPNodo != -1) {
                this.esElMasCercaAMejorPP = true;
                int miDistanciaAPP = game.getShortestPathDistance(miNodo, mejorPPNodo);
                if (miDistanciaAPP != -1) {
                    for (GHOST g : GHOST.values()) {
                        if (g != ghost && game.getGhostLairTime(g) == 0) {
                            int distOtro = game.getShortestPathDistance(game.getGhostCurrentNodeIndex(g), mejorPPNodo);
                            if (distOtro != -1 && distOtro < miDistanciaAPP) {
                                this.esElMasCercaAMejorPP = false;
                                break;
                            }
                        }
                    }
                }
            }
        }

        evaluarLogicaPersecucion();
    }

    private void evaluarLogicaPersecucion() {
        int perseguidoresActivos = 0;
        GHOST fantasmaMasVeterano = null;
        int maxTiempo = -1;

        for (GHOST g : GHOST.values()) {
            int tiempo = tiempoPersiguiendo.get(g);
            if (tiempo > 0) {
                perseguidoresActivos++;
                if (tiempo > maxTiempo) {
                    maxTiempo = tiempo;
                    fantasmaMasVeterano = g;
                }
            }
        }

        // ¿Soy el que lleva más tiempo de los 3?
        this.debeDejarDePerseguir = (perseguidoresActivos > 2 && ghost == fantasmaMasVeterano);
        
        // Bloqueo de oscilación: Solo puedo entrar a perseguir si hay hueco libre (< 2 persiguiendo)
        // o si YO ya estoy persiguiendo legítimamente y no me toca salir.
        this.persecucionPermitida = (perseguidoresActivos < 2) || (tiempoPersiguiendo.get(ghost) > 0 && !this.debeDejarDePerseguir);
    }

    public static void registrarPersiguiendo(GHOST g) { tiempoPersiguiendo.put(g, tiempoPersiguiendo.get(g) + 1); }
    public static void registrarNoPersiguiendo(GHOST g) { tiempoPersiguiendo.put(g, 0); }

    // Getters...
    public GHOST getGhost() { return ghost; }
    public boolean isComido() { return comido; }
    public int getTiempoComestible() { return tiempoComestible; }
    public int getDistanciaMsPacman() { return distanciaMsPacman; }
    public int getDistanciaMejorPP() { return distanciaMejorPP; }
    public boolean isMsPacmanCerca() { return msPacmanCerca; }
    public boolean isMsPacmanLejos() { return msPacmanLejos; }
    public int getNumPPActivas() { return numPPActivas; }
    public int getNumPillsActivas() { return numPillsActivas; }
    public boolean isEsElMasCercaAMejorPP() { return esElMasCercaAMejorPP; }
    public boolean isDebeDejarDePerseguir() { return debeDejarDePerseguir; }
    public boolean isPersecucionPermitida() { return persecucionPermitida; }
}
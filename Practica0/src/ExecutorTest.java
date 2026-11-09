import es.ucm.fdi.ici.c2627.practica0.grupoIndividual.*;


import es.ucm.fdi.ici.c2627.practica1.grupoJPCSBV.*;
import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.MsPacMan.*;
import es.ucm.fdi.ici.rivales.*;
import pacman.Executor;
import pacman.controllers.GhostController;
import pacman.controllers.PacmanController;
import pacman.game.util.Stats;

public class ExecutorTest {

    private static final boolean VER = false;   // true: una partida mirándola
    private static final int     N   = 300;     // false: N partidas midiendo

    /** Imprime el total y la media de cambios de estado de un controlador FSM. */
    private static void printCambiosEstado(String nombre, int total, int nPartidas) {
        System.out.printf("Cambios de estado %s: total=%d  media=%.2f%n",
                nombre, total, (double) total / nPartidas);
    }

    public static void main(String[] args) {

        PacmanController pacMan =  new MsPacManV3();   
        GhostController  ghosts = new GhostsAvanzado();

        Executor executor = new Executor.Builder()
                .setTickLimit(4000)
                .setPacmanPO(false)
                .setGhostPO(false)
                .setGhostsMessage(false)
                .setVisual(VER)
                .setScaleFactor(2.5)
                .build();

        if (VER) {
            executor.runGame(pacMan, ghosts, 2);
        } else {
            Stats[] stats = executor.runExperiment(pacMan, ghosts, N, "nuestros contra nuestros");
            System.out.println(stats[0]);   // puntuación
            System.out.println(stats[1]);   // turnos por partida

            // Cambios de estado según la versión activa
            if (pacMan instanceof MsPacManV1) {
                MsPacManV1 v1 = (MsPacManV1) pacMan;
                printCambiosEstado("MsPacManV1 (FSM exterior)  ", v1.getNumCambiosEstado(),   N);
                printCambiosEstado("MsPacManV1 (sub-FSM estandar)", v1.getNumCambiosEstandar(), N);
            } else if (pacMan instanceof MsPacManV3) {
                MsPacManV3 v3 = (MsPacManV3) pacMan;
                printCambiosEstado("MsPacManV3 (FSM exterior)  ", v3.getNumCambiosEstado(),   N);
                printCambiosEstado("MsPacManV3 (sub-FSM estandar)", v3.getNumCambiosEstandar(), N);
            }
        }
    }
}
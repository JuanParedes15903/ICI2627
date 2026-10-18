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

    public static void main(String[] args) {
        PacmanController pacMan = new MsPacManV2();
        GhostController  ghosts = new GhostsBasico();

        Executor executor = new Executor.Builder()
                .setTickLimit(4000)
                .setPacmanPO(false)
                .setGhostPO(false)
                .setGhostsMessage(false)
                .setVisual(VER)
                .setScaleFactor(2.5)
                .build();

        if (VER) {
            executor.runGame(pacMan, ghosts, 20);
        } else {
            Stats[] stats = executor.runExperiment(pacMan, ghosts, N, "nuestros contra nuestros");
            System.out.println(stats[0]);   // puntuación
            System.out.println(stats[1]);   // turnos por partida
        }
    }
}
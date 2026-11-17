import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.Ghosts;
import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.MsPacMan;
import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.depurar.TrazaBorrosa;
import pacman.Executor;

/** Una partida en pantalla, con la traza borrosa de Ms. Pac-Man en la consola. */
public class VerPartida {

    public static void main(String[] args) {
        Executor executor = new Executor.Builder()
                .setTickLimit(4000)
                .setVisual(true)
                .setScaleFactor(3.0)
                .build();

        MsPacMan pacman = new MsPacMan();
        Ghosts ghosts = new Ghosts();
        TrazaBorrosa traza = new TrazaBorrosa("FANTASMAS", pacman.getMotor(), true);   // true: sólo los cambios

        executor.runGame(pacman, ghosts, 40);   // 40 ms por turno: más, para verlo despacio
        traza.resumen();
    }
}

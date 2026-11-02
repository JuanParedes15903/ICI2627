import java.util.EnumMap;

import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.Ghosts;
import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.MsPacMan;
import es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.depurar.TrazaReglas;
import pacman.Executor;
import pacman.game.Constants.GHOST;

public class VerPartida {

    public static void main(String[] args) {
        Executor executor = new Executor.Builder()
                .setTickLimit(4000)
                .setVisual(true)
                .setScaleFactor(3.0)
                .build();

        MsPacMan pacman = new MsPacMan();
        Ghosts ghosts = new Ghosts("GhostsRolesv3.clp");

        //TrazaReglas traza = new TrazaReglas("MSPACMAN", pacman.getMotor(), true);   // true: sólo los cambios
        
        EnumMap<GHOST, TrazaReglas> trazas = new EnumMap<>(GHOST.class);
        
        for (GHOST ghost : GHOST.values()) {
            trazas.put(
                ghost,
                new TrazaReglas(ghost.name(), ghosts.getMotor(ghost), false)
            );
        }

        executor.runGame(pacman, ghosts, 1);
        
       /* executor.runGame(pacman, new GhostsAggressive(), 40);   // 40 ms por turno: más, para verlo despacio
        traza.resumen();*/
        
        for (TrazaReglas traza : trazas.values()) {
            traza.resumen();
        }
    }
}
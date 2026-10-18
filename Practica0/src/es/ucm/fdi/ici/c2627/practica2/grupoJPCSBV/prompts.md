## Prompt detección de errores versión 0
encuentra errores en las siguientes clases:
package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV;

import java.util.EnumMap;

import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.actions.*;
import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.transitions.*;
import es.ucm.fdi.ici.fsm.CompoundState;
import es.ucm.fdi.ici.fsm.FSM;
import es.ucm.fdi.ici.fsm.SimpleState;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.controllers.GhostController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GhostFSMController extends GhostController {

    private final EnumMap<GHOST, FSM> fsms;

    public GhostFSMController() {
        this.fsms = new EnumMap<>(GHOST.class);
        for (GHOST ghost : GHOST.values()) {
            this.fsms.put(ghost, buildFSM(ghost));
        }
    }

    private FSM buildFSM(GHOST ghost) {
        FSM mainFSM = new FSM(ghost + "_MainFSM");

        Action perseguirAct = new PerseguirAction(ghost);
        Action huirMsPacmanAct = new HuirMsPacmanAction(ghost);
        Action huirDispersosAct = new HuirDispersosAction(ghost);
        Action emboscarAct = new EmboscarAction(ghost); 
        Action esperarAct = new EsperarSalirAction(ghost);
        Action protegerFantasmaAct = new PerseguirAction(ghost);

        SimpleState perseguirState = new SimpleState("Perseguir", perseguirAct);
        SimpleState esperarState = new SimpleState("Esperar a salir", esperarAct);
        SimpleState protegerFantasmaState = new SimpleState("Proteger fantasma comestible", protegerFantasmaAct);

        // =========================================================================
        // 1. ESTADO COMPUESTO SUPERIOR (ESTRATEGIA NORMAL)
        // =========================================================================
        FSM fsmSuperior = new FSM("FSM_Superior_" + ghost);
        
        SimpleState movAleatoriosState = new SimpleState("Movimientos aleatorios", huirDispersosAct);
        SimpleState cercarPPState = new SimpleState("Cercar PP activas", perseguirAct);
        SimpleState cercarPillsState = new SimpleState("Cercar pills activas", perseguirAct);
        SimpleState emboscarState = new SimpleState("Emboscar", emboscarAct);

        fsmSuperior.add(movAleatoriosState, 
            new GenericTransition("PP activas > 1", in -> ((GhostInput)in).getNumPPActivas() > 1), 
            cercarPPState);

        fsmSuperior.add(movAleatoriosState, 
            new GenericTransition("Pills activas > 1", in -> ((GhostInput)in).getNumPillsActivas() > 1), 
            cercarPillsState);

        fsmSuperior.add(movAleatoriosState, 
            new GenericTransition("PP y pills ya asignadas", in -> ((GhostInput)in).getNumPPActivas() <= 1), 
            emboscarState);

        fsmSuperior.add(cercarPPState, 
            new GenericTransition("Otro fantasma más cerca a la mejor PP", in -> !((GhostInput)in).isEsElMasCercaAMejorPP()), 
            cercarPillsState);

        fsmSuperior.add(cercarPillsState, 
            new GenericTransition("Es el fantasma más cerca a la mejor PP", in -> ((GhostInput)in).isEsElMasCercaAMejorPP()), 
            cercarPPState);

        fsmSuperior.ready(movAleatoriosState);
        CompoundState bloqueSuperior = new CompoundState("Estrategia Normal", fsmSuperior);


        // =========================================================================
        // 2. ESTADO COMPUESTO INFERIOR (ESTRATEGIA HUIDA)
        // =========================================================================
        FSM fsmInferior = new FSM("FSM_Inferior_" + ghost);

        SimpleState alejarseState = new SimpleState("Alejarse de Ms Pacman", huirMsPacmanAct);
        SimpleState protegerPPState = new SimpleState("Proteger PP", perseguirAct);
        SimpleState huidaEficienteState = new SimpleState("Huida eficiente", huirDispersosAct);

        fsmInferior.add(alejarseState, 
            new GenericTransition("Fantasma + cerca de PP", in -> ((GhostInput)in).getDistanciaMejorPP() < ((GhostInput)in).getDistanciaMsPacman()), 
            protegerPPState);

        fsmInferior.add(alejarseState, 
            new GenericTransition("Comestible && distancia > 20", in -> ((GhostInput)in).getTiempoComestible() > 0 && ((GhostInput)in).getDistanciaMsPacman() > 20), 
            huidaEficienteState);

        fsmInferior.add(huidaEficienteState, 
            new GenericTransition("Comestible && distancia <= 20", in -> ((GhostInput)in).getTiempoComestible() > 0 && ((GhostInput)in).getDistanciaMsPacman() <= 20), 
            alejarseState);

        fsmInferior.add(protegerPPState, 
            new GenericTransition("Comestible && Ms Pacman cerca", in -> ((GhostInput)in).getTiempoComestible() > 0 && ((GhostInput)in).getDistanciaMsPacman() <= 20), 
            alejarseState);

        fsmInferior.ready(alejarseState);
        CompoundState bloqueInferior = new CompoundState("Estrategia Huida", fsmInferior);


        // =========================================================================
        // 3. ENSAMBLADO DE LA FSM PRINCIPAL
        // =========================================================================
        Transition aPerseguirTrans = new MasCercaAMsPacmanQueAPPTransition();
        Transition aProtegerPPTrans = new GenericTransition("Ms Pacman a menos de un cruce de PP", 
            in -> ((GhostInput)in).getDistanciaMejorPP() <= ((GhostInput)in).getDistanciaMsPacman());

        mainFSM.add(bloqueSuperior, aPerseguirTrans, perseguirState);
        mainFSM.add(bloqueSuperior, aProtegerPPTrans, bloqueInferior);

        // NUEVA TRANSICIÓN: Expulsa al veterano si hay > 2 persiguiendo, devolviéndolo a sus otros roles
        mainFSM.add(perseguirState, new ExcesoPerseguidoresTransition(), bloqueSuperior);

        Transition comestibleMayorCeroTrans = new GenericTransition("Tiempo comestible > 0", in -> ((GhostInput)in).getTiempoComestible() > 0);
        mainFSM.add(perseguirState, comestibleMayorCeroTrans, protegerFantasmaState);

        mainFSM.add(protegerFantasmaState, new TiempoComestibleCeroTransition(true), perseguirState);
        mainFSM.add(protegerFantasmaState, new TiempoComestibleCeroTransition(false), bloqueSuperior);

        mainFSM.add(esperarState, comestibleMayorCeroTrans, protegerFantasmaState);
        mainFSM.add(esperarState, new TiempoComestibleCeroTransition(false), bloqueSuperior);
        mainFSM.add(esperarState, new TiempoComestibleCeroTransition(true), perseguirState);

        mainFSM.add(bloqueInferior, new ComidoTransition(), esperarState);

        Transition comestibleCeroTrans = new GenericTransition("Tiempo comestible = 0", in -> ((GhostInput)in).getTiempoComestible() == 0);
        mainFSM.add(bloqueInferior, comestibleCeroTrans, bloqueSuperior);

        mainFSM.ready(bloqueSuperior);

        return mainFSM;
    }

    @Override
    public void preCompute(String opponent) {
        for (FSM fsm : fsms.values()) {
            fsm.reset();
        }
    }

    @Override
    public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
        EnumMap<GHOST, MOVE> moves = new EnumMap<>(GHOST.class);

        for (GHOST ghost : GHOST.values()) {
            GhostInput input = new GhostInput(ghost, game);
            FSM fsm = fsms.get(ghost);
            moves.put(ghost, fsm.run(input));
        }

        return moves;
    }
}

package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV;

import es.ucm.fdi.ici.Input;
import pacman.game.Constants.GHOST;
import pacman.game.Game;
import java.util.EnumMap;

public class GhostInput extends Input {

    // Memoria global compartida entre todos los fantasmas
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
    private boolean debeDejarDePerseguir; // Nueva variable para la transición

    public GhostInput(GHOST ghost, Game game) {
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
        this.distanciaMsPacman = game.getShortestPathDistance(miNodo, pacmanNodo);
        
        int UMBRAL_CERCA = 80;
        this.msPacmanCerca = distanciaMsPacman < UMBRAL_CERCA;
        this.msPacmanLejos = !msPacmanCerca;

     // Búsqueda de Power Pills (PP) y Pills
        int[] powerPills = game.getActivePowerPillsIndices();
        this.numPPActivas = powerPills.length;
        this.numPillsActivas = game.getActivePillsIndices().length;

        // Si no quedan PP, evitamos el Integer.MAX_VALUE para no romper las transiciones
        if (this.numPPActivas == 0) {
            this.distanciaMejorPP = -1; 
        } else {
            this.distanciaMejorPP = Integer.MAX_VALUE;
            int mejorPPNodo = -1;
            for (int pp : powerPills) {
                int dist = game.getShortestPathDistance(pacmanNodo, pp);
                if (dist < distanciaMejorPP) {
                    distanciaMejorPP = dist;
                    mejorPPNodo = pp;
                }
            }
        

        this.esElMasCercaAMejorPP = true;
        if (mejorPPNodo != -1) {
            int miDistanciaAPP = game.getShortestPathDistance(miNodo, mejorPPNodo);
            for (GHOST g : GHOST.values()) {
                if (g != ghost && game.getGhostLairTime(g) == 0) {
                    int distOtro = game.getShortestPathDistance(game.getGhostCurrentNodeIndex(g), mejorPPNodo);
                    if (distOtro < miDistanciaAPP) {
                        this.esElMasCercaAMejorPP = false;
                        break;
                    }
                }
            }
        }
        }

        // Lógica de expulsión por exceso de perseguidores
        evaluarExcesoPerseguidores();
    }

    private void evaluarExcesoPerseguidores() {
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

        // Si hay más de 2 persiguiendo y este es el más veterano, debe salir del estado
        this.debeDejarDePerseguir = (perseguidoresActivos > 2 && ghost == fantasmaMasVeterano);
    }

    // Métodos globales para actualizar el estado desde las Actions
    public static void registrarPersiguiendo(GHOST g) {
        tiempoPersiguiendo.put(g, tiempoPersiguiendo.get(g) + 1);
    }

    public static void registrarNoPersiguiendo(GHOST g) {
        tiempoPersiguiendo.put(g, 0);
    }

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
}

package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.actions;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class EmboscarAction implements Action {
    private final GHOST ghost;

    public EmboscarAction(GHOST ghost) {
        this.ghost = ghost;
    }

    @Override
    public MOVE execute(Game game) {
        int yo = game.getGhostCurrentNodeIndex(ghost);
        int pacman = game.getPacmanCurrentNodeIndex();
        MOVE ultimoMovimiento = game.getGhostLastMoveMade(ghost);
        
        int nodoDestinoPredicho = pacman;
        
        int[] powerPillsActivas = game.getActivePowerPillsIndices();
        int[] pillsActivas = game.getActivePillsIndices();

        // 1. Asumimos que su máxima prioridad es ir a por una Power Pill si la tiene muy cerca
        if (powerPillsActivas.length > 0) {
            int ppMasCercana = game.getClosestNodeIndexFromNodeIndex(pacman, powerPillsActivas, DM.PATH);
            int distA_PP = game.getShortestPathDistance(pacman, ppMasCercana);
            
            // Si está a menos de 30 nodos de una Power Pill, casi seguro va hacia ella
            if (distA_PP < 30) {
                nodoDestinoPredicho = ppMasCercana;
            }
        }
        
        // 2. Si no iba a por una Power Pill, predecimos que va a por la píldora normal más cercana
        if (nodoDestinoPredicho == pacman && pillsActivas.length > 0) {
            nodoDestinoPredicho = game.getClosestNodeIndexFromNodeIndex(pacman, pillsActivas, DM.PATH);
        }

        // El fantasma calcula su ruta para llegar a ese objetivo ANTES que Ms. Pac-Man, cortándole el paso
        return game.getApproximateNextMoveTowardsTarget(yo, nodoDestinoPredicho, ultimoMovimiento, DM.PATH);
    }

    @Override
    public String getActionId() {
        return ghost + " Emboscando (Interceptando)";
    }
}

package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.actions;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

// Acción de espera en la cueva / salir
public class EsperarSalirAction implements Action {
    private final GHOST ghost;

    public EsperarSalirAction(GHOST ghost) {
        this.ghost = ghost;
    }

    @Override
    public MOVE execute(Game game) {
        return MOVE.NEUTRAL;
    }

    @Override
    public String getActionId() {
        return ghost + " Esperando a salir";
    }
}

package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.actions;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class HuirDispersosAction implements Action {
    private final GHOST ghost;

    public HuirDispersosAction(GHOST ghost) {
        this.ghost = ghost;
    }

    @Override
    public MOVE execute(Game game) {
        int yo = game.getGhostCurrentNodeIndex(ghost);
        int pacman = game.getPacmanCurrentNodeIndex();
        MOVE ultimo = game.getGhostLastMoveMade(ghost);
        
        MOVE[] posibles = game.getPossibleMoves(yo, ultimo);
        MOVE mejorMovimiento = MOVE.NEUTRAL;
        double mejorPuntuacion = -Double.MAX_VALUE;

        for (MOVE move : posibles) {
            int vecino = game.getNeighbour(yo, move);
            if (vecino == -1) continue; 
            
            double distPacman = game.getShortestPathDistance(vecino, pacman);
            double minDistOtroFantasma = Double.MAX_VALUE;

            // Buscar la distancia al fantasma que quedaría MÁS CERCA si tomamos este camino
            for (GHOST otro : GHOST.values()) {
                if (otro != ghost && game.getGhostLairTime(otro) == 0) {
                    int otroNodo = game.getGhostCurrentNodeIndex(otro);
                    double dist = game.getShortestPathDistance(vecino, otroNodo);
                    if (dist < minDistOtroFantasma) {
                        minDistOtroFantasma = dist;
                    }
                }
            }

            if (minDistOtroFantasma == Double.MAX_VALUE) {
                minDistOtroFantasma = 50; // Si es el único fantasma vivo
            }

            // Maximizar ambas distancias, dando el doble de peso a alejarse de sus compañeros
            double puntuacion = distPacman + (minDistOtroFantasma * 2.0);

            if (puntuacion > mejorPuntuacion) {
                mejorPuntuacion = puntuacion;
                mejorMovimiento = move;
            }
        }

        return mejorMovimiento != MOVE.NEUTRAL ? mejorMovimiento : posibles[0];
    }

    @Override
    public String getActionId() {
        return ghost + " Huyendo disperso garantizado";
    }
}

package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.actions;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class HuirMsPacmanAction implements Action {
    private final GHOST ghost;

    public HuirMsPacmanAction(GHOST ghost) {
        this.ghost = ghost;
    }

    @Override
    public MOVE execute(Game game) {
        int yo = game.getGhostCurrentNodeIndex(ghost);
        int pacman = game.getPacmanCurrentNodeIndex();
        MOVE ultimo = game.getGhostLastMoveMade(ghost);
        
        MOVE[] posibles = game.getPossibleMoves(yo, ultimo);
        MOVE mejorMovimiento = MOVE.NEUTRAL;
        double mejorPuntuacion = -Double.MAX_VALUE;

        for (MOVE move : posibles) {
            int vecino = game.getNeighbour(yo, move);
            if (vecino == -1) continue;

            // Puntuación base: alejarse de Ms. Pac-Man
            double puntuacion = game.getShortestPathDistance(vecino, pacman);

            // Penalizar caminos donde ya haya otro fantasma cerca
            for (GHOST otro : GHOST.values()) {
                if (otro != ghost && game.getGhostLairTime(otro) == 0) {
                    int otroNodo = game.getGhostCurrentNodeIndex(otro);
                    int distOtro = game.getShortestPathDistance(vecino, otroNodo);
                    
                    // Si el camino lleva a estar a menos de 15 nodos de otro fantasma, se descarta prácticamente
                    if (distOtro < 15) {
                        puntuacion -= (1000.0 / (distOtro + 1));
                    }
                }
            }

            if (puntuacion > mejorPuntuacion) {
                mejorPuntuacion = puntuacion;
                mejorMovimiento = move;
            }
        }

        // Respaldo por si hay bloqueos
        if (mejorMovimiento == MOVE.NEUTRAL && posibles.length > 0) {
            return game.getApproximateNextMoveAwayFromTarget(yo, pacman, ultimo, DM.PATH);
        }

        return mejorMovimiento;
    }

    @Override
    public String getActionId() {
        return ghost + " Huyendo urgente y separado";
    }
}

package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.actions;

import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.GhostInput;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class PerseguirAction implements Action {
    private final GHOST ghost;

    public PerseguirAction(GHOST ghost) {
        this.ghost = ghost;
    }

    @Override
    public MOVE execute(Game game) {
        // Registra globalmente que este fantasma está persiguiendo (suma +1 al tiempo)
        GhostInput.registrarPersiguiendo(ghost);

        int yo = game.getGhostCurrentNodeIndex(ghost);
        int pacman = game.getPacmanCurrentNodeIndex();
        MOVE ultimo = game.getGhostLastMoveMade(ghost);
        
        return game.getApproximateNextMoveTowardsTarget(yo, pacman, ultimo, DM.PATH);
    }

    @Override
    public String getActionId() {
        return ghost + " Persiguiendo";
    }
}

package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.GhostInput;
import es.ucm.fdi.ici.fsm.Transition;

public class ComidoTransition implements Transition {
    @Override
    public boolean evaluate(Input in) {
        GhostInput input = (GhostInput) in;
        return input.isComido();
    }
}

package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.fsm.Transition;
import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.GhostInput;

public class ExcesoPerseguidoresTransition implements Transition {
    
    @Override
    public boolean evaluate(Input in) {
        GhostInput input = (GhostInput) in;
        return input.isDebeDejarDePerseguir();
    }

    @Override
    public String toString() {
        return "Demasiados perseguidores, forzando cambio de rol";
    }
}

package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.fsm.Transition;
import java.util.function.Predicate;

public class GenericTransition implements Transition {

    private final String description;
    private final Predicate<Input> condition;

    public GenericTransition(String description, Predicate<Input> condition) {
        this.description = description;
        this.condition = condition;
    }

    @Override
    public boolean evaluate(Input in) {
        return condition.test(in);
    }

    @Override
    public String toString() {
        return description;
    }
}

package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.GhostInput;
//import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.GhostInput;
import es.ucm.fdi.ici.fsm.Transition;

// Transición corregida con evaluación de distancia explícita (Propuesta 5)
public class MasCercaAMsPacmanQueAPPTransition implements Transition {
    @Override
    public boolean evaluate(Input in) {
        GhostInput input = (GhostInput) in;
        return input.getTiempoComestible() == 0 &&
               input.getDistanciaMsPacman() < input.getDistanciaMejorPP();
    }
}

package es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2627.practica2.grupoJPCSBV.GhostInput;
//import es.ucm.fdi.ici.c2627.practica2.grupoYY.ghosts.GhostInput;
import es.ucm.fdi.ici.fsm.Transition;

public class TiempoComestibleCeroTransition implements Transition {
    private final boolean msPacmanCerca;

    public TiempoComestibleCeroTransition(boolean msPacmanCerca) {
        this.msPacmanCerca = msPacmanCerca;
    }

    @Override
    public boolean evaluate(Input in) {
        GhostInput input = (GhostInput) in;
        boolean tCero = input.getTiempoComestible() == 0;
        return tCero && (msPacmanCerca ? input.isMsPacmanCerca() : input.isMsPacmanLejos());
    }
}
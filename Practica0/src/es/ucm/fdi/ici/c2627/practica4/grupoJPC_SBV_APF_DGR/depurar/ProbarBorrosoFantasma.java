package es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.depurar;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Rule;
import net.sourceforge.jFuzzyLogic.rule.Variable;

/**
 * Prueba los .fcl de los fantasmas (v1 y v2) sin jugar: se escriben a mano los
 * valores de las entradas y se mira qué salida gana, como hace el MaxActionSelector.
 * Cada caso es una pregunta con su respuesta esperada en cada versión.
 */
public class ProbarBorrosoFantasma {

    static final String CARPETA = "src/es/ucm/fdi/ici/c2627/practica4/grupoJPC_SBV_APF_DGR/";
    static final String[] VERSIONES = { "GhostsRolesFuzzyv1.fcl", "GhostsRolesFuzzyv2.fcl" };
    static final String BLOQUE = "Ghost";

    // Orden en el que se pasan los valores en caso() y evaluar()
    static final String[] NOMBRES = { "distPacman", "tiempoComestible", "enCarcel", "distPPMsPacman",
            "ventajaPP", "ranking", "amigosCerca", "numPills", "numPP" };
    static int fallos = 0;
    static int version = 0;   // 0 = v1, 1 = v2

    public static void main(String[] args) {
        int fallosTotales = 0;
        for (int v = 0; v < VERSIONES.length; v++) {
            version = v;
            fallos = 0;
            String ruta = CARPETA + VERSIONES[v];
            FIS fis = FIS.load(ruta, true);        // true: si hay un error de sintaxis, lo dice
            if (fis == null) { System.out.println("No encuentro " + ruta); continue; }
            FunctionBlock fb = fis.getFunctionBlock(BLOQUE);
            if (fb == null) { System.out.println("El FCL no tiene el bloque " + BLOQUE); continue; }

            System.out.println("==================== " + VERSIONES[v] + " ====================");
            probar(fb);
            System.out.println(fallos == 0 ? "\nTodos los casos bien en " + VERSIONES[v] + ".\n"
                                           : "\n" + fallos + " casos MAL en " + VERSIONES[v] + ".\n");
            fallosTotales += fallos;
        }
        System.out.println(fallosTotales == 0 ? "TODO BIEN." : fallosTotales + " casos MAL en total.");
    }

    static void probar(FunctionBlock fb) {
        //   nombre del caso                          esperada v1     esperada v2          dist comest carcel distPP ventaja rank amigos pills PP
        caso(fb, "en la cárcel",                      "salir_carcel", "salir_carcel",       150,   0,    1,   150,    0,     0,   0,    150,  4);
        caso(fb, "comestible y Ms. Pac-Man cerca",    "huir",         "huir_seguro",         20, 100,    0,   150,    0,     0,   0,    150,  4);
        caso(fb, "comestible poco tiempo y cerca",    "huir",         "huir_seguro",         20,  10,    0,   150,    0,     0,   0,    150,  4);
        caso(fb, "comestible y distancia media",      "huir_disperso","huir_disperso",      100, 100,    0,   150,    0,     0,   0,    150,  4);
        caso(fb, "comestible poco y lejos",           "perseguir",    "perseguir",          250,  10,    0,   150,    0,     0,   0,    150,  4);
        caso(fb, "comestible mucho y lejos (hueco v0)","huir_disperso","huir_disperso",     250, 100,    0,   150,    0,     0,   0,    150,  4);
        caso(fb, "PP peligrosa y sin ventaja: huye",  "huir",         "huir_seguro",        100,   0,    0,    10,    0,     0,   0,    150,  4);
        caso(fb, "PP peligrosa pero con ventaja",     "cortar_pp",    "cortar_pp",          100,   0,    0,    10,    1,     0,   0,    150,  4);
        caso(fb, "PP cercana y con ventaja: corta",   "cortar_pp",    "cortar_pp",          100,   0,    0,    60,    1,     0,   0,    150,  4);
        caso(fb, "ventaja pero PP lejos: no corta",   "perseguir",    "perseguir",          100,   0,    0,   150,    1,     0,   0,    150,  4);
        caso(fb, "fase final sin PP: persigue",       "perseguir",    "perseguir",          100,   0,    0,   150,    0,     1,   0,     10,  0);
        caso(fb, "cerco gana al rol (hay aliados)",   "cerco",        "cerco",              100,   0,    0,   150,    0,     0,   1,    150,  4);
        caso(fb, "primer fantasma libre",             "perseguir",    "perseguir",          100,   0,    0,   150,    0,     0,   0,    150,  4);
        caso(fb, "segundo fantasma libre",            "interceptar",  "interceptar_cruce",  100,   0,    0,   150,    0,     1,   0,    150,  4);
        caso(fb, "tercer fantasma libre",             "emboscar",     "emboscar",           100,   0,    0,   150,    0,     2,   0,    150,  4);
        caso(fb, "cuarto fantasma con PP",            "patrullar",    "guardia_pp",         150,   0,    0,   150,    0,     3,   0,    150,  2);
        caso(fb, "cuarto fantasma sin PP",            "cerco",        "cerco",              150,   0,    0,   150,    0,     3,   0,    150,  0);

        // Curva de control 1: qué sale según se acerca Ms. Pac-Man, estando comestible
        System.out.println("\ndistPacman (comestible)  salidas → gana");
        for (int d = 0; d <= 300; d += 10) {
            evaluar(fb, d, 100, 0, 150, 0, 0, 0, 150, 4);
            System.out.printf("%5d   %s → %s%n", d, salidas(fb), gana(fb));
        }

        // Curva de control 2: qué sale según la distancia de Ms. Pac-Man a su PP, sin ventaja
        System.out.println("\ndistPPMsPacman (libre, sin ventaja)  salidas → gana");
        for (int d = 0; d <= 120; d += 10) {
            evaluar(fb, 100, 0, 0, d, 0, 0, 0, 150, 4);
            System.out.printf("%5d   %s → %s%n", d, salidas(fb), gana(fb));
        }
    }

    /** valores: distPacman, tiempoComestible, enCarcel, distPPMsPacman, ventajaPP, ranking, amigosCerca, numPills, numPP */
    static void caso(FunctionBlock fb, String nombre, String esperadaV1, String esperadaV2, double... valores) {
        String esperada = (version == 0) ? esperadaV1 : esperadaV2;
        evaluar(fb, valores);
        String gana = gana(fb);
        StringBuilder activas = new StringBuilder();
        for (Rule r : fb.getRuleBlocks().values().iterator().next().getRules())
            if (r.getDegreeOfSupport() > 0) activas.append(String.format(" R%s:%.2f", r.getName(), r.getDegreeOfSupport()));

        boolean bien = gana.equals(esperada);
        if (!bien) fallos++;
        System.out.printf("%s  %-44s gana %-18s (esperaba %s)%n      salidas: %s%n      reglas: %s%n",
                bien ? "BIEN" : "MAL ", nombre, gana, esperada, salidas(fb), activas);
    }

    static void evaluar(FunctionBlock fb, double... valores) {
        if (valores.length != NOMBRES.length)
            throw new IllegalArgumentException("Se esperan " + NOMBRES.length + " valores y hay " + valores.length);
        for (int i = 0; i < NOMBRES.length; i++)
            fb.setVariable(NOMBRES[i], valores[i]);   // a todas las entradas, cada vez: si no, se quedan con el valor anterior
        fb.evaluate();
    }

    /** La salida más alta, como el MaxActionSelector (en un empate, cualquiera de las dos). */
    static String gana(FunctionBlock fb) {
        String gana = "(ninguna)";
        double max = Double.NEGATIVE_INFINITY;
        for (Variable v : fb.getVariables().values())
            if (v.isOutput() && v.getLatestDefuzzifiedValue() > max) {
                max = v.getLatestDefuzzifiedValue();
                gana = v.getName();
            }
        return gana;
    }

    static String salidas(FunctionBlock fb) {
        StringBuilder sb = new StringBuilder();
        for (Variable v : new java.util.TreeMap<>(fb.getVariables()).values())
            if (v.isOutput() && v.getLatestDefuzzifiedValue() > 0)
                sb.append(String.format("%s=%.1f ", v.getName(), v.getLatestDefuzzifiedValue()));
        return sb.toString().trim();
    }
}
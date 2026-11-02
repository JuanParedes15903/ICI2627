package es.ucm.fdi.ici.c2627.practica3.grupo_JPC_SBV_APF_DGR.depurar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jess.Fact;
import jess.JessException;
import jess.Rete;

/**
 * Prueba las reglas de los fantasmas sin jugar: se escribe una situación a mano,
 * como hechos (YO, MSPACMAN, FANTASMA...), y se mira qué ACTION gana.
 * Cada caso es una pregunta con su respuesta esperada.
 *
 * Ojo: todos los fantasmas usan las mismas reglas; lo único que cambia entre ellos
 * es el hecho (YO (nombre X)). Aquí "yo" es siempre BLINKY y los demás son rivales/compañeros.
 */
public class ProbarReglasFantasmas {

    // AJUSTAD estas dos líneas a donde esté vuestro .clp en el classpath
    static final String RUTA = "es/ucm/fdi/ici/c2627/practica3/grupo_JPC_SBV_APF_DGR/";
    static final String FICHERO = "GhostsRolesv3.clp";

    // Qué reglas tiene el .clp que probáis (si no están, esos casos darían MAL sin ser un fallo real)
    static final boolean CON_CERCO_Y_CORTAR_PP = true;   // R5 cortarCaminoPP y R6 cerrarCerco
    static final boolean CON_G2C_Y_FINAL_SIN_PP = true;  // comestibleSeguro y finalSinPP

    static final boolean VERBOSO = false;                // true: Jess escribe cada regla que dispara

    static int fallos = 0;

    public static void main(String[] args) throws JessException {
        Rete jess = new Rete();
        jess.batch(RUTA + FICHERO);
        if (VERBOSO) jess.eval("(watch rules)");

        System.out.println("===== " + FICHERO);

        // ---- Roles (distancias >= 200 para que el cerco no interfiera) -----------------
        caso(jess, "Acosador: el mas cercano", "BLINKY", "perseguir",
                fantasma("BLINKY", "distPacman 50"),
                fantasma("PINKY", "distPacman 100"),
                fantasma("INKY", "distPacman 100"),
                fantasma("SUE", "distPacman 100"),
                mspacman());

        caso(jess, "Bloqueador: hay uno mas cerca", "BLINKY", "interceptar",
                fantasma("BLINKY", "distPacman 250"),
                fantasma("PINKY", "distPacman 100"),
                mspacman());

        caso(jess, "Emboscador: hay dos mas cerca", "BLINKY", "emboscar",
                fantasma("BLINKY", "distPacman 250"),
                fantasma("PINKY", "distPacman 100"),
                fantasma("INKY", "distPacman 120"),
                mspacman());

        caso(jess, "Dispersor: hay tres mas cerca", "BLINKY", "patrullar",
                fantasma("BLINKY", "distPacman 300"),
                fantasma("PINKY", "distPacman 100"),
                fantasma("INKY", "distPacman 120"),
                fantasma("SUE", "distPacman 150"),
                mspacman());

        // ---- Estados criticos ------------------------------------------------------------
        caso(jess, "Carcel", "BLINKY", "salir_carcel",
                fantasma("BLINKY", "enCarcel true", "distPacman 1000"),
                fantasma("PINKY", "distPacman 100"),
                mspacman());

        caso(jess, "Comestible muy cerca: huir", "BLINKY", "huir",
                fantasma("BLINKY", "comestible true", "tiempoComestible 100", "distPacman 20"),
                mspacman());

        caso(jess, "Comestible a media distancia: huir disperso", "BLINKY", "huir_disperso",
                fantasma("BLINKY", "comestible true", "tiempoComestible 100", "distPacman 60"),
                mspacman());

        caso(jess, "Ms. Pac-Man junto a PPill: no acercarse", "BLINKY", "huir",
                fantasma("BLINKY", "distPacman 50"),
                fantasma("PINKY", "distPacman 100"),
                mspacman("distPPill 10"));

        // ---- Reglas de las versiones nuevas --------------------------------------------------
        if (CON_G2C_Y_FINAL_SIN_PP) {
            caso(jess, "Comestible seguro: perseguir (G2c)", "BLINKY", "perseguir",
                    fantasma("BLINKY", "comestible true", "tiempoComestible 20", "distPacman 150"),
                    mspacman());

            caso(jess, "Fase final sin PP: presion total", "BLINKY", "perseguir",
                    fantasma("BLINKY", "distPacman 250"),
                    fantasma("PINKY", "distPacman 100"),
                    mspacman("numPP 0", "numPills 10"));
        }

        caso(jess, "Pocas pildoras pero quedan PP: sigue el rol", "BLINKY", "interceptar",
                fantasma("BLINKY", "distPacman 250"),
                fantasma("PINKY", "distPacman 100"),
                mspacman("numPP 2", "numPills 10"));

        if (CON_CERCO_Y_CORTAR_PP) {
            caso(jess, "Cerco (25) le gana al bloqueador (20)", "BLINKY", "cerco",
                    fantasma("BLINKY", "distPacman 150"),
                    fantasma("PINKY", "distPacman 100"),
                    mspacman());

            caso(jess, "Sin compañeros cerca: no hay cerco", "BLINKY", "perseguir",
                    fantasma("BLINKY", "distPacman 150"),
                    fantasma("PINKY", "distPacman 300"),
                    mspacman());

            caso(jess, "Cortar camino a la PP", "BLINKY", "cortar_pp",
                    fantasma("BLINKY", "distPacman 250", "distMejorPP 40"),
                    fantasma("PINKY", "distPacman 100"),
                    mspacman("distPPill 100"));

            caso(jess, "No corto si otro esta mas cerca de la PP", "BLINKY", "interceptar",
                    fantasma("BLINKY", "distPacman 250", "distMejorPP 40"),
                    fantasma("PINKY", "distPacman 100", "distMejorPP 30"),
                    mspacman("distPPill 100"));

            caso(jess, "Ms. Pac-Man casi en la PP: G3 (40) manda sobre cortar_pp (38)", "BLINKY", "huir",
                    fantasma("BLINKY", "distPacman 50", "distMejorPP 5"),
                    fantasma("PINKY", "distPacman 100"),
                    mspacman("distPPill 10"));
        }

        System.out.println(fallos == 0 ? "\nTodos los casos bien." : "\n" + fallos + " casos MAL.");
    }

    // ---- construcción de hechos ---------------------------------------------------------------

    /** Un fantasma por defecto: libre, no comestible, lejos de todo. */
    static String fantasma(String nombre, String... cambios) {
        Map<String, String> slots = new LinkedHashMap<>();
        slots.put("nombre", nombre);
        slots.put("comestible", "false");
        slots.put("tiempoComestible", "0");
        slots.put("enCarcel", "false");
        slots.put("distPacman", "100");
        slots.put("distMejorPP", "1000");
        return hecho("FANTASMA", slots, cambios);
    }

    /** Ms. Pac-Man por defecto: PP lejos, quedan 2 PP y muchas pildoras. */
    static String mspacman(String... cambios) {
        Map<String, String> slots = new LinkedHashMap<>();
        slots.put("distPPill", "1000");
        slots.put("numPP", "2");
        slots.put("numPills", "100");
        return hecho("MSPACMAN", slots, cambios);
    }

    static String hecho(String plantilla, Map<String, String> slots, String... cambios) {
        for (String cambio : cambios) {
            String[] partes = cambio.split(" ");
            if (!slots.containsKey(partes[0])) throw new IllegalArgumentException("Slot desconocido: " + partes[0]);
            slots.put(partes[0], partes[1]);
        }
        StringBuilder hecho = new StringBuilder("(" + plantilla);
        slots.forEach((slot, valor) -> hecho.append(" (").append(slot).append(' ').append(valor).append(')'));
        return hecho.append(')').toString();
    }

    // ---- ejecución de un caso ---------------------------------------------------------------------

    static void caso(Rete jess, String nombre, String yo, String esperada, String... hechos) throws JessException {
        System.out.println("\n--- " + nombre);
        jess.reset();
        jess.assertString("(YO (nombre " + yo + "))");
        for (String h : hechos) jess.assertString(h);
        jess.run();

        // Tras run() la regla seleccionar-mejor-accion ya ha quitado las ACTION de menor prioridad:
        // las que queden deberían ser una sola (o varias con la misma prioridad = empate).
        int mejor = Integer.MIN_VALUE;
        Set<String> ganadoras = new LinkedHashSet<>();
        List<String> todas = new ArrayList<>();
        for (Iterator<?> it = jess.listFacts(); it.hasNext(); ) {
            Fact f = (Fact) it.next();
            if (!f.getName().equals("MAIN::ACTION")) continue;
            int p = f.getSlotValue("priority").intValue(null);
            String id = f.getSlotValue("id").toString();
            todas.add(id + "(" + p + ")");
            if (p > mejor) { mejor = p; ganadoras.clear(); ganadoras.add(id); }
            else if (p == mejor) ganadoras.add(id);
        }

        String gana = ganadoras.isEmpty() ? "(ninguna)" : String.join(" / ", ganadoras);
        boolean bien = ganadoras.size() == 1 && ganadoras.contains(esperada);
        if (!bien) fallos++;
        System.out.printf("%s  gana %s, esperaba %s%s%n", bien ? "BIEN" : "MAL ", gana, esperada,
                ganadoras.size() > 1 ? "   <-- EMPATE de prioridad" : "");
        if (!bien) System.out.println("      ACTION que quedaron: " + todas);
    }
}
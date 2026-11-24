package es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.depurar;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.MsPacMan;
import es.ucm.fdi.ici.c2627.practica4.grupoJPC_SBV_APF_DGR.mspacman.MsPacManInputV4;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Rule;
import net.sourceforge.jFuzzyLogic.rule.Variable;

public class ProbarBorroso {

    static final String[] VERSIONES = { "mspacman-v00.fcl", "mspacman-v01.fcl", "mspacman-v04e.fcl", "mspacman-v05.0.fcl", "mspacman-v05.1.fcl", "mspacman-v06.0.fcl", "mspacman-v06.1.fcl", "mspacman-v07.0.fcl", "mspacman-v07.1.fcl", "mspacman-v08.0.fcl", "mspacman-v08.1.fcl", "mspacman-v10.0.fcl", "mspacman-v10.1.fcl" };
    static final int[] NUMERO = { 0, 1, 4, 5, 5, 6, 6, 7, 7, 8, 8, 10, 10 };
    static final boolean[] PRUDENCIA_DINAMICA = { false, false, true, false, true, false, true, false, true, false, true, false, true };
    static final String BLOQUE = "MsPacMan";
    static final String PRUDENCIA = "prudencia";
    static int fallos = 0;

    public static void main(String[] args) {
        for (int v = 0; v < VERSIONES.length; v++) probar(VERSIONES[v], NUMERO[v], PRUDENCIA_DINAMICA[v]);
        System.out.println(fallos == 0 ? "\nTodos los casos bien." : "\n" + fallos + " casos MAL.");
    }

    static void probar(String fichero, int version, boolean v4) {
        String fcl = MsPacMan.FCL_PATH + fichero;
        FIS fis = FIS.load(fcl, true);
        if (fis == null) { System.out.println("No encuentro " + fcl); fallos++; return; }
        FunctionBlock fb = fis.getFunctionBlock(BLOQUE);
        System.out.println("\n===== " + fcl);

        caso(fb, v4, "nada cerca: come", "PastillaMasCercana");
        caso(fb, v4, "peligroso a 10 con amenaza: huye", "HuirDelFantasmaMasCercano", "peligro", 10, "amenazas", 1);
        caso(fb, v4, "peligroso a 45: todavía come", "PastillaMasCercana", "peligro", 45);
        caso(fb, v4, "comestible a 10, peligroso lejos", "PerseguirFantasmaComestibleMasCercano", "comestible", 10, "margenCaza", 40, "alcanzables", 1);
        caso(fb, v4, "comestible a 10, peligroso a 10", "HuirDelFantasmaMasCercano", "peligro", 10, "amenazas", 1, "comestible", 10, "margenCaza", 40, "alcanzables", 1);
        if (version >= 1) {
            caso(fb, v4, "comestible a 10 al que no llega: come", "PastillaMasCercana", "comestible", 10, "margenCaza", 0);
            caso(fb, v4, "comestible a 10 al que llega por 1 turno", "PerseguirFantasmaComestibleMasCercano", "comestible", 10, "margenCaza", 1, "alcanzables", 1);
        }
        if (version >= 5) {
            caso(fb, v4, "comestible a 80 con margen 40: persigue", "PerseguirFantasmaComestibleMasCercano", "comestible", 80, "margenCaza", 40, "alcanzables", 1);
            caso(fb, v4, "comestible a 80 con margen 5 y 1 alcanzable: come", "PastillaMasCercana", "comestible", 80, "margenCaza", 5, "alcanzables", 1);
            caso(fb, v4, "comestible a 80 con margen 5 y 2 alcanzables: persigue", "PerseguirFantasmaComestibleMasCercano", "comestible", 80, "margenCaza", 5, "alcanzables", 2);
            caso(fb, v4, "comestible a 10 con margen 40 y peligroso a 10: huye", "HuirDelFantasmaMasCercano", "peligro", 10, "amenazas", 1, "comestible", 10, "margenCaza", 40, "alcanzables", 1);
        }
        if (version == 6 || version == 8 || version == 10) {
            caso(fb, v4, "en el cruce de la ppill con combo 0: espera", "EsperarPastillaDePoderHastaCombo", "enCrucePPill", 1, "combo", 0, "ppill", 8);
            caso(fb, v4, "en el cruce de la ppill con combo 3: come", "PastillaMasCercana", "enCrucePPill", 1, "combo", 3, "ppill", 8);
            caso(fb, v4, "en el cruce de la ppill y quedan 2 pasillos: come", "PastillaMasCercana", "enCrucePPill", 1, "combo", 0, "ppill", 8, "pasillos", 2);
        }
        if (version == 7 || version == 8) {
            caso(fb, v4, "peligroso a 10 sin amenaza: come", "PastillaMasCercana", "peligro", 10, "amenazas", 0);
            caso(fb, v4, "peligroso a 10 con 2 amenazas: huye", "HuirDelFantasmaMasCercano", "peligro", 10, "amenazas", 2);
        }
        if (version == 10) {
            caso(fb, v4, "cadena de 2 sin peligro: caza en cadena", "CazarEnCadena", "cadena", 2, "margenCaza", 40, "alcanzables", 2, "comestible", 30);
            caso(fb, v4, "llega al cruce del comestible: intercepta", "InterceptarFantasmaComestible", "interceptar", 1, "margenCaza", 40, "alcanzables", 1, "comestible", 30);
            caso(fb, v4, "cadena de 2 y puede interceptar: caza en cadena", "CazarEnCadena", "cadena", 2, "interceptar", 1, "margenCaza", 40, "alcanzables", 2, "comestible", 30);
            caso(fb, v4, "peligro a 15, ppill a 10 y llega antes: a la ppill", "IrAPorPastillaDePoder", "peligro", 15, "amenazas", 1, "ppill", 10, "ventajaPPill", 10);
            caso(fb, v4, "peligro a 15, ppill a 10 y llega después: huye", "HuirDelFantasmaMasCercano", "peligro", 15, "amenazas", 1, "ppill", 10, "ventajaPPill", -20);
            caso(fb, v4, "única salida hacia la ppill: va por ella", "HuirHaciaPastillaDePoder", "ppillUnicaSalida", 1, "peligro", 15, "amenazas", 1);
            caso(fb, v4, "única salida hacia la ppill y llega antes: va por ella", "HuirHaciaPastillaDePoder", "ppillUnicaSalida", 1, "peligro", 15, "amenazas", 1, "ppill", 10, "ventajaPPill", 10);
            caso(fb, v4, "sin salidas seguras: muerte segura", "MuerteSegura", "salidasSeguras", 0, "peligro", 10, "amenazas", 1);
            caso(fb, v4, "sin salidas seguras y ppill cerca: muerte segura", "MuerteSegura", "salidasSeguras", 0, "peligro", 10, "amenazas", 1, "ppill", 10, "ventajaPPill", 10);
        }
        if (v4) {
            prudencia(fb, "3 vidas, ritmo normal: arriesga", false, "vidas", 3, "ritmo", 1600);
            prudencia(fb, "1 vida: prudente", true, "vidas", 1, "ritmo", 3000);
            prudencia(fb, "2 vidas, pierde una cada 500 turnos: prudente", true, "vidas", 2, "ritmo", 500);
            prudencia(fb, "2 vidas, pierde una cada 3000 turnos: arriesga", false, "vidas", 2, "ritmo", 3000);
            prudencia(fb, "4 vidas, pierde una cada 500 turnos: arriesga", false, "vidas", 4, "ritmo", 500);
        }

        System.out.println("\npeligro  salidas → gana");
        for (int d = 0; d <= 100; d += 5) {
            evaluar(fb, entradas(v4, "peligro", d));
            System.out.printf("%5d   %s → %s%n", d, salidas(fb), gana(fb));
        }
        if (version >= 1) {
            System.out.println("\nmargenCaza (comestible a 10)  salidas → gana");
            for (int m = 0; m <= 5; m++) {
                evaluar(fb, entradas(v4, "comestible", 10, "margenCaza", m));
                System.out.printf("%5d   %s → %s%n", m, salidas(fb), gana(fb));
            }
        }
        if (version >= 5) {
            System.out.println("\nmargenCaza (comestible a 80, 1 alcanzable)  salidas → gana");
            for (int m = 0; m <= 60; m += 5) {
                evaluar(fb, entradas(v4, "comestible", 80, "margenCaza", m, "alcanzables", 1));
                System.out.printf("%5d   %s → %s%n", m, salidas(fb), gana(fb));
            }
            System.out.println("\npeligro (comestible a 80 con margen 40)  salidas → gana");
            for (int d = 0; d <= 70; d += 5) {
                evaluar(fb, entradas(v4, "peligro", d, "comestible", 80, "margenCaza", 40, "alcanzables", 1));
                System.out.printf("%5d   %s → %s%n", d, salidas(fb), gana(fb));
            }
        }
        if (v4) {
            for (int vidas = 1; vidas <= 3; vidas++) {
                System.out.printf("%nritmo con %d vidas  prudencia%n", vidas);
                for (int r = 0; r <= 4000; r += 250) {
                    evaluar(fb, entradas(true, "vidas", vidas, "ritmo", r));
                    double p = fb.getVariable(PRUDENCIA).getLatestDefuzzifiedValue();
                    System.out.printf("%5d   %.1f %s%n", r, p, p > MsPacManInputV4.UMBRAL_PRUDENCIA ? "prudente" : "");
                }
            }
        }
    }

    static Map<String, Double> entradas(boolean v4, Object... cambios) {
        Map<String, Double> e = new LinkedHashMap<>();
        e.put("peligro", 150.0);
        e.put("amenazas", 0.0);
        e.put("salidasSeguras", 2.0);
        e.put("comestible", 150.0);
        e.put("margenCaza", 0.0);
        e.put("alcanzables", 0.0);
        e.put("cadena", 0.0);
        e.put("interceptar", 0.0);
        e.put("ppill", 150.0);
        e.put("ventajaPPill", -50.0);
        e.put("combo", 0.0);
        e.put("enCrucePPill", 0.0);
        e.put("ppillUnicaSalida", 0.0);
        e.put("pasillos", 30.0);
        e.put("pasillosIguales", 0.0);
        if (v4) {
            e.put("vidas", 3.0);
            e.put("turnosPorVida", 1300.0);
            e.put("ritmo", 2000.0);
            e.put("vidasSobrantes", 1.0);
        }
        for (int i = 0; i < cambios.length; i += 2) {
            String nombre = (String) cambios[i];
            if (!e.containsKey(nombre)) throw new IllegalArgumentException("No existe la entrada " + nombre);
            e.put(nombre, ((Number) cambios[i + 1]).doubleValue());
        }
        return e;
    }

    static String activas(FunctionBlock fb) {
        StringBuilder activas = new StringBuilder();
        for (Rule r : fb.getRuleBlocks().values().iterator().next().getRules())
            if (r.getDegreeOfSupport() > 0) activas.append(String.format(" R%s:%.2f", r.getName(), r.getDegreeOfSupport()));
        return activas.toString();
    }

    static void caso(FunctionBlock fb, boolean v4, String nombre, String esperada, Object... cambios) {
        evaluar(fb, entradas(v4, cambios));
        String gana = gana(fb);
        boolean bien = gana.equals(esperada);
        if (!bien) fallos++;
        System.out.printf("%s  %-40s gana %s (esperaba %s)%n      salidas: %s%n      reglas: %s%n",
                bien ? "BIEN" : "MAL ", nombre, gana, esperada, salidas(fb), activas(fb));
    }

    static void prudencia(FunctionBlock fb, String nombre, boolean esperaPrudente, Object... cambios) {
        evaluar(fb, entradas(true, cambios));
        double p = fb.getVariable(PRUDENCIA).getLatestDefuzzifiedValue();
        boolean prudente = p > MsPacManInputV4.UMBRAL_PRUDENCIA;
        boolean bien = prudente == esperaPrudente;
        if (!bien) fallos++;
        System.out.printf("%s  %-48s prudencia %.1f (%s, esperaba %s)%n      reglas: %s%n",
                bien ? "BIEN" : "MAL ", nombre, p, prudente ? "prudente" : "arriesga", esperaPrudente ? "prudente" : "arriesga", activas(fb));
    }

    static void evaluar(FunctionBlock fb, Map<String, Double> entradas) {
        for (Map.Entry<String, Double> e : entradas.entrySet())
            fb.setVariable(e.getKey(), e.getValue());
        fb.evaluate();
    }

    static String gana(FunctionBlock fb) {
        String gana = "(ninguna)";
        double max = Double.NEGATIVE_INFINITY;
        for (Variable v : fb.getVariables().values())
            if (v.isOutput() && !v.getName().equals(PRUDENCIA) && v.getLatestDefuzzifiedValue() > max) {
                max = v.getLatestDefuzzifiedValue();
                gana = v.getName();
            }
        return gana;
    }

    static String salidas(FunctionBlock fb) {
        StringBuilder sb = new StringBuilder();
        for (Variable v : new TreeMap<>(fb.getVariables()).values())
            if (v.isOutput()) sb.append(String.format("%s=%.1f ", v.getName(), v.getLatestDefuzzifiedValue()));
        return sb.toString().trim();
    }
}

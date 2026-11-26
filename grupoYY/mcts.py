"""Monte Carlo Tree Search para ajedrez. Grupo YY.

Es la segunda parte de la guía. Cada función con
    raise NotImplementedError
es un hueco. Cuando rellenéis el del paso M1, M2…, comprobadlo con
    python comprobar.py m1
(o, en VS Code, "Comprobar paso…" en Ejecutar y depurar).
"""
import math
import random
import time

import chess

from ajedrez import material


class Nodo:
    """Un nodo del árbol: una posición a la que se llega con `jugada` desde `padre`."""

    def __init__(self, tablero, padre=None, jugada=None):
        self.padre = padre
        self.jugada = jugada                          # la jugada que lleva del padre aquí
        self.hijos = []                               # los nodos ya creados debajo
        self.sin_probar = list(tablero.legal_moves)   # las jugadas que todavía no tienen hijo
        random.shuffle(self.sin_probar)
        self.visitas = 0                              # N: cuántas simulaciones han pasado por aquí
        self.valor = 0.0                              # W: la suma de sus recompensas
        self.color = not tablero.turn                 # quien hizo `jugada` (os hará falta en el reto "El rival también elige")

#------------------------------------------------heurística para potenciar control del centro y no regalar piezas
VALOR = {chess.PAWN: 1, chess.KNIGHT: 3, chess.BISHOP: 3, chess.ROOK: 5, chess.QUEEN: 9, chess.KING: 100}
CENTRO = (chess.D4, chess.E4, chess.D5, chess.E5)


def puntos_centro(tablero, color):
    """Ocupar y atacar las 4 casillas centrales, mío menos del rival."""
    p = 0.0
    for casilla in CENTRO:
        pieza = tablero.piece_at(casilla)
        if pieza:
            p += 0.3 if pieza.color == color else -0.3
        p += 0.1 * (len(tablero.attackers(color, casilla)) - len(tablero.attackers(not color, casilla)))
    return p


def perdida_max(tablero, bando):
    """Lo máximo que perdería `bando` si el rival capturara ahora la pieza que mejor le convenga."""
    peor = 0
    for casilla, pieza in tablero.piece_map().items():
        if pieza.color != bando or pieza.piece_type == chess.KING:
            continue
        atacantes = tablero.attackers(not bando, casilla)
        if not atacantes:
            continue
        valor = VALOR[pieza.piece_type]
        if tablero.is_attacked_by(bando, casilla):          # está defendida: sólo pierdo si el atacante vale menos
            menor = min(VALOR[tablero.piece_type_at(a)] for a in atacantes)
            perdida = max(0, valor - menor)
        else:                                               # colgada
            perdida = valor
        peor = max(peor, perdida)
    return peor


def hacer_recompensa(w_centro=1.0, w_expuesto=1.0):
    def f(tablero, color):
        if tablero.is_checkmate() or tablero.is_stalemate() or tablero.is_insufficient_material():
            return recompensa(tablero, color)
        puntos = material(tablero, color)
        if w_centro:
            puntos += w_centro * puntos_centro(tablero, color)
        if w_expuesto:
            # Sólo captura el bando al que le toca, así que su mejor captura cuenta a su favor
            ganancia = perdida_max(tablero, not tablero.turn)
            puntos += w_expuesto * (ganancia if tablero.turn == color else -ganancia)
        return 1 / (1 + math.exp(-puntos / 3))
    return f

# ---------------------------------------------------------------- paso M1
def recompensa(tablero, color):
    if tablero.is_checkmate():
        return 0.0 if tablero.turn == color else 1.0
    if tablero.is_stalemate() or tablero.is_insufficient_material():
        return 0.5
    return 1 / (1 + math.exp(-material(tablero, color) / 3))


# ---------------------------------------------------------------- paso M2
def ucb1(hijo, c):
    explotar = hijo.valor / hijo.visitas
    explorar = c * math.sqrt(math.log(hijo.padre.visitas) / hijo.visitas)
    return explotar + explorar


def seleccionar(nodo, c):
    return max(nodo.hijos, key=lambda h: ucb1(h, c))

# ---------------------------------------------------------------- paso M3
def expandir(nodo, tablero):
    jugada = nodo.sin_probar.pop()
    tablero.push(jugada)
    hijo = Nodo(tablero, nodo, jugada)
    nodo.hijos.append(hijo)
    return hijo

# ---------------------------------------------------------------- paso M4
def simular(tablero, profundidad, color, recompensa=recompensa):
    for _ in range(profundidad):
        jugadas = list(tablero.legal_moves)
        if not jugadas:
            break
        tablero.push(random.choice(jugadas))
    return recompensa(tablero, color)
# ---------------------------------------------------------------- paso M5
def retropropagar(nodo, r):
    while nodo is not None:
        nodo.visitas += 1
        nodo.valor += r
        nodo = nodo.padre


# ---------------------------------------------------------------- paso M6
class MCTS:
    """El jugador. `c` es la constante de exploración, `profundidad` el corte de la simulación,
    `recompensa` la heurística, e `iteraciones`, si no es None, sustituye al reloj (para las pruebas)."""

    def __init__(self, nombre="grupoYY", c=1.4, profundidad=10, iteraciones=None, recompensa=recompensa):
        self.nombre = nombre
        self.c = c
        self.profundidad = profundidad
        self.iteraciones = iteraciones
        self.recompensa = recompensa    # la heurística: otra función con la misma forma, para probar la vuestra
        self.estadisticas = []      # (jugada, visitas, valor medio) de la raíz: la ventana las pinta

    # Las cuatro fases, como métodos. Una versión nueva hereda de MCTS y redefine sólo la que cambia:
    #     class MCTSCapturas(MCTS):
    #         def simular(self, tablero): ...
    def seleccionar(self, nodo):
        return seleccionar(nodo, self.c)

    def expandir(self, nodo, tablero):
        return expandir(nodo, tablero)

    def simular(self, tablero):
        return simular(tablero, self.profundidad, self.yo, self.recompensa)

    def retropropagar(self, nodo, r):
        retropropagar(nodo, r)

    def elegir(self, tablero, tiempo):
        fin = time.perf_counter() + tiempo * 0.9    # un margen para no pasarse
        self.yo = tablero.turn                      # el que decide: el árbol entero cuenta desde él
        raiz = Nodo(tablero)
        n = 0
        # el bucle de elegir, sin el raise (allí va con dos sangrías más)
        while True:
            t = tablero.copy(stack=False)
            nodo = raiz
            while not nodo.sin_probar and nodo.hijos:      # 1. selección
                nodo = self.seleccionar(nodo)
                t.push(nodo.jugada)
            if nodo.sin_probar:                              # 2. expansión
                nodo = self.expandir(nodo, t)
            r = self.simular(t)                              # 3. simulación
            self.retropropagar(nodo, r)                      # 4. retropropagación
            n += 1
            if (self.iteraciones and n >= self.iteraciones) or \
            (not self.iteraciones and time.perf_counter() > fin):
                break
        self.estadisticas = sorted(((h.jugada, h.visitas, h.valor / h.visitas) for h in raiz.hijos),
                                   key=lambda e: -e[1])
        self.iteraciones_hechas = n
        return max(raiz.hijos, key=lambda h: h.visitas).jugada   # el más visitado

class MCTSRivales(MCTS):
    def seleccionar(self, nodo):
        rival = nodo.padre is not None and nodo.color == self.yo
        if not rival:
            return super().seleccionar(nodo)
        return max(nodo.hijos, key=lambda h: (
            1 - h.valor / h.visitas
            + self.c * math.sqrt(math.log(nodo.visitas) / h.visitas)
        ))
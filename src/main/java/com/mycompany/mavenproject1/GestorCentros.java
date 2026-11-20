package com.mycompany.mavenproject1;

/**
 * Administra los centros de distribución mediante una pila LIFO
 */
public class GestorCentros {

    private static final int CAPACIDAD_MAXIMA = 20;
    // arrayList estatico tamaño fijo de 20 que simula la Pila
    private CentroDistribucion[] centros = new CentroDistribucion[20];
    private int cima;           // puntero que indica la posición actual de la cima de la pila
    private int siguienteId;    // contador global autoincremental para los IDs de los centros

    /**    C:\Users\RizoR\OneDrive\Universidad\Universidad\III 2026\Estructura de Datos\Proyecto Evaluativo\Proyecto_Evalutativo_Ramon_Montenegro\src\main\java\com\mycompany\proyecto_evalutativo_ramon_montenegro    Set-Location -LiteralPath "C:\Users\RizoR\OneDrive\Universidad\Universidad\III 2026\Estructura de Datos\Proyecto Evaluativo\Proyecto_Evalutativo_Ramon_Montenegro\src\main\java\com\mycompany\proyecto_evalutativo_ramon_montenegro"    Set-Location -LiteralPath "C:\Users\RizoR\OneDrive\Universidad\Universidad\III 2026\Estructura de Datos\Proyecto Evaluativo\Proyecto_Evalutativo_Ramon_Montenegro\src\main\java\com\mycompany\proyecto_evalutativo_ramon_montenegro"
     * Inicializa una pila vacía y el consecutivo de identificadores.
     */
    public GestorCentros() {
        centros = new CentroDistribucion[20];
        cima = 0;         // pila inicialmente vacía = cima 0
        siguienteId = 1;  // el primer centro registrado iniciaría con ID 1
    }

    /**
     * Registra un centro en la cima de la pila.
     */
    public boolean registrarCentro(String nombre, String provincia) {
        // validar si el nombre está vacío, la provincia no es válida o si la pila ya está al límite de 20 registros
        if (nombre == null || nombre.trim().isEmpty()
                || !esProvinciaValida(provincia) || estaLlena()) {
            return false;
        }

        // almacena el nuevo centro en la posición de la cima además avanzar los contadores 
        centros[cima] = new CentroDistribucion(siguienteId, nombre, provincia);
        cima++;
        siguienteId++;
        return true;
    }

    /**
     * Elimina únicamente el centro ubicado en la cima.
     *
     * @return true si el centro fue eliminado; false si la pila está vacía o
     *         el centro todavía contiene paquetes
     */
    public boolean eliminarCentro() {
        // no se puede eliminar si la pila está vacía o si el centro de la cima aún tiene paquetes 
        if (estaVacia() || centros[cima - 1].getCantidadPaquetes() > 0) {
            return false;
        }

        cima--;
        centros[cima] = null; // limpia la referencia en memoria para evitar fugas 
        return true;
    }

    /**
     * Traslada todos los paquetes del centro origen al final de la cola del
     * centro destino, conservando el orden FIFO y generando nuevos IDs en el
     * destino.
     *
     * @param idOrigen identificador del centro origen
     * @param idDestino identificador del centro destino
     * @return true si todos los paquetes fueron trasladados
     */
    public boolean trasladarPaquetes(int idOrigen, int idDestino) {
        // valida que existan al menos 2 centros además que el origen y destino no sean el mismo
        if (cima < 2 || idOrigen == idDestino) {
            return false;
        }

        CentroDistribucion origen = buscarCentro(idOrigen);
        CentroDistribucion destino = buscarCentro(idDestino);

        // valida que ambos centros existan y que el origen no esté vacío
        if (origen == null || destino == null || origen.estaVacia()) {
            return false;
        }

        int cantidadOrigen = origen.getCantidadPaquetes();
        // valida que el centro destino no exceda el límite de 20 al recibir el traslado 
        if (destino.getCantidadPaquetes() + cantidadOrigen
                > CAPACIDAD_MAXIMA) {
            return false;
        }

        // despachar de orgien e insertar en destno uno por uno respetando la regla FIFO
        for (int i = 0; i < cantidadOrigen; i++) {
            Paquete paquete = origen.despacharPaquete();
            destino.registrarPaquete(paquete.getCodigoEnvio(),
                    paquete.getDestinatario(), paquete.getTipo());
        }
        return true;
    }

    /**
     * Elimina el centro de la cima de la pila, únicamente si su cola está
     * vacía.
     *
     * @return true si el centro fue eliminado
     */
    public boolean eliminarUltimoCentro() {
        return eliminarCentro(); // reutiliza la validación y el comportamiento LIFO
    }

    public boolean estaLlena() {
        // retorna verdadero si la cima llega al límite permitido
        return cima == CAPACIDAD_MAXIMA;
    }

    public boolean estaVacia() {
        // retorna verdadero si la pila no contiene centros registrados
        return cima == 0;
    }

    public int getCantidadCentros() {
        return cima;
    }

    /**
     * Retorna una copia de los centros actuales, desde la cima hacia el fondo.
     * La copia evita exponer el arreglo interno de la pila.
     *
     * @return arreglo con los centros registrados
     */
    public CentroDistribucion[] getCentrosActuales() {
        CentroDistribucion[] copia = new CentroDistribucion[cima];

        // recorre el arrayList en orden inverso para que la cima aparezca primero visualmente
        for (int i = 0; i < cima; i++) {
            copia[i] = centros[cima - 1 - i];
        }
        return copia;
    }

    public CentroDistribucion getCentroEnLaCima() {
        if (estaVacia()) {
            return null;
        }
        return centros[cima - 1];
    }

    private CentroDistribucion buscarCentro(int id) {
        // busqueda lineal para localizar un centro por su respectivo ID dentro de la pila acvita
        for (int i = 0; i < cima; i++) {
            if (centros[i].getId() == id) {
                return centros[i];
            }
        }
        return null;
    }

    private boolean esProvinciaValida(String provincia) {
        // restricción para las 7 provincias
        return "San José".equals(provincia)
                || "Alajuela".equals(provincia)
                || "Cartago".equals(provincia)
                || "Heredia".equals(provincia)
                || "Guanacaste".equals(provincia)
                || "Puntarenas".equals(provincia)
                || "Limón".equals(provincia);
    }
}

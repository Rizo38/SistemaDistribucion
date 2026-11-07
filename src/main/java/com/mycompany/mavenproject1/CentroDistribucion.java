package com.mycompany.mavenproject1;

/**
 * Centro de distribución con una cola FIFO de hasta 20 paquetes
 */
public class CentroDistribucion {

    private int id;
    private String nombre;
    private String provincia;

    // arrayList estatico con capacidad fija de 20 paquetes el cual es el limite establecido en la rubrica
    private Paquete[] paquetes = new Paquete[20];
    private int frente;              // puntero que indica el inicio de la cola
    private int fin;                 // puntero que indica el final de la cola
    private int cantidad;            // cantidad actual de paquetes almacenados
    private int siguienteIdPaquete;  // control de ID autoincremental para los paquetes

    public CentroDistribucion(int id, String nombre, String provincia) {
        this.id = id;
        this.nombre = nombre;
        setProvincia(provincia);
        siguienteIdPaquete = 1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        // validación estrica: solo se permiten 7 provincias nada más
        if (!"San José".equals(provincia)
                && !"Alajuela".equals(provincia)
                && !"Cartago".equals(provincia)
                && !"Heredia".equals(provincia)
                && !"Guanacaste".equals(provincia)
                && !"Puntarenas".equals(provincia)
                && !"Limón".equals(provincia)) {
            throw new IllegalArgumentException(
                    "La provincia no corresponde a una provincia válida.");
        }
        this.provincia = provincia;
    }

    /**
     * Inserta un paquete al final de la cola.
     */
    public boolean insertarPaquete(Paquete p) {
        // valida si el paquete es nulo o si la cola ya llegó al límite (20)
        if (p == null || estaLlena()) {
            return false;
        }

        paquetes[fin] = p;
        fin = (fin + 1) % paquetes.length; // desplazamiento circular del índice "fin" usando operador módulo
        cantidad++;
        return true;
    }

    /**
     * crea y registra un paquete al final de la cola FIFO
     */
    public boolean registrarPaquete(String codigoEnvio, String destinatario,
            String tipo) {
        // validación de capacidad (max 20) y control de campos vacíos o tipos incorrectos
        if (estaLlena() || textoVacio(codigoEnvio)
                || textoVacio(destinatario) || !esTipoValido(tipo)) {
            return false; // retorna falso si viola algún límite o regla 
        }

        Paquete paquete = new Paquete(siguienteIdPaquete, codigoEnvio,
                destinatario, tipo);
        // almacena en la posición actual del puntero "fin"
        paquetes[fin] = paquete;
        // actualizar punteros y contenedores de la cola circular
        fin = (fin + 1) % paquetes.length;
        cantidad++;
        siguienteIdPaquete++;
        return true;
    }

    /**
     * Retira y retorna el paquete que lleva más tiempo en la cola.
     */
    public Paquete despacharPaquete() {
        // si la cola está vacía no se puede despachar nada 
        if (estaVacia()) {
            return null;
        }

        // se encarga de la extracción del paquete ubicado al frente de la cosa (FIFO)
        Paquete paquete = paquetes[frente];
        paquetes[frente] = null; // limpia la referencia en memoria
        frente = (frente + 1) % paquetes.length; // mueve el puntero "frente" circularmente
        cantidad--;
        return paquete;
    }

    public boolean estaLlena() {
        // retorna verdadero si la cantidad llegó exactamente al tamaño máximo del arrayList (20)
        return cantidad == paquetes.length;
    }

    public boolean estaVacia() {
        // retorna verdadero si no hay paquetes en el centro
        return cantidad == 0;
    }

    public int getCantidadPaquetes() {
        return cantidad;
    }

    /**
     * Retorna una copia ordenada desde el frente hasta el final de la cola.
     */
    public Paquete[] getPaquetesActuales() {
        Paquete[] copia = new Paquete[cantidad];

        // recorre la cola respetando la estructura circular basada en el "frente"
        for (int i = 0; i < cantidad; i++) {
            copia[i] = paquetes[(frente + i) % paquetes.length];
        }
        return copia;
    }

    private boolean textoVacio(String texto) {
        return texto == null || texto.trim().isEmpty(); // solo evalua si está vacío
    }

    private boolean esTipoValido(String tipo) {
        // validación de las categorias permitidas por el sistema
        return "Documento".equals(tipo)
                || "Paquete estándar".equals(tipo)
                || "Frágil".equals(tipo);
    }
}

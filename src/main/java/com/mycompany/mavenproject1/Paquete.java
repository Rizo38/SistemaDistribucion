package com.mycompany.mavenproject1;

/**
 * Representa un paquete pendiente de ser procesado por un centro de
 * distribución
 */
public class Paquete {

    private int id;                // identificaro único del paquete dentro de la cola del centro
    private String codigoEnvio;    // código alfanumerico único de rasreo o envío
    private String destinatario;   // nombre de la persona o entidad receptora del paquete
    private String tipo;           // categoría del paquete : _ documento, paquete estándar o frágil _ 

    public Paquete(int id, String codigoEnvio, String destinatario, String tipo) {
        this.id = id;
        this.codigoEnvio = codigoEnvio;
        this.destinatario = destinatario;
        setTipo(tipo); // validación y asignación segura a través del método set 
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigoEnvio() {
        return codigoEnvio;
    }

    public void setCodigoEnvio(String codigoEnvio) {
        this.codigoEnvio = codigoEnvio;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        // validación solo permite las tres categorías 
        if (!"Documento".equals(tipo)
                && !"Paquete estándar".equals(tipo)
                && !"Frágil".equals(tipo)) {
            throw new IllegalArgumentException(
                    "El tipo debe ser Documento, Paquete estándar o Frágil.");
        }
        this.tipo = tipo;
    }
}
// ha sido un bonito proyecto!!
// versión 1.4 git

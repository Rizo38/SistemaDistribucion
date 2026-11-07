package com.mycompany.mavenproject1;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * Ventana principal del sistema de gestión de centros y paquetes
 */
public class VentanaPrincipal extends JFrame {

    // instania del gestor logístico y elementos de la tabla principal
    private final GestorCentros gestor;
    private final DefaultTableModel modeloTabla;
    private final JTable tablaCentros;
    private final JLabel etiquetaEstado;

    // campos de texto y selectores para la entrada de datos de la interfaz (JComboBox)
    private final JTextField campoNombreCentro;
    private final JComboBox<String> comboProvincia;
    private final JComboBox<Integer> comboCentroPaquetes;
    private final JTextField campoCodigoEnvio;
    private final JTextField campoDestinatario;
    private final JComboBox<String> comboTipoPaquete;
    private final JComboBox<Integer> comboOrigen;
    private final JComboBox<Integer> comboDestino;

    public VentanaPrincipal() {
        // inicialización del gesor de datos y los componenetes de entrada
        gestor = new GestorCentros();
        campoNombreCentro = new JTextField();
        // carga de las provincias permitidas en el ComboBox
        comboProvincia = new JComboBox<>(new String[]{
            "San José", "Alajuela", "Cartago", "Heredia",
            "Guanacaste", "Puntarenas", "Limón"
        });
        comboCentroPaquetes = new JComboBox<>();
        campoCodigoEnvio = new JTextField();
        campoDestinatario = new JTextField();
        // tipos de paquetes permitidos por la lógica del negocio
        comboTipoPaquete = new JComboBox<>(new String[]{
            "Documento", "Paquete estándar", "Frágil"
        });
        comboOrigen = new JComboBox<>();
        comboDestino = new JComboBox<>();
        etiquetaEstado = new JLabel("Listo.");

        // configuración del modelo de la tabla para mostrar los centros registrados lo cual no 
        //será editable de manera directa
        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Provincia", "Cantidad de Paquetes"},
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // bloquea la edición directa de celdas en la tabla (muy importante)
            }
        };
        tablaCentros = new JTable(modeloTabla);
        
        // bloquea el movimiento / reordenamiento de columnas de la tabla  
        tablaCentros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // configura selección única de filas en la tabla
        tablaCentros.getSelectionModel().addListSelectionListener(evento -> {
            
            // listener para sincronzar la selección de la tabla con el ComboBox de administración de paquetes
            if (!evento.getValueIsAdjusting()
                    && tablaCentros.getSelectedRow() >= 0) {
                int id = (Integer) modeloTabla.getValueAt(
                        tablaCentros.getSelectedRow(), 0);
                seleccionarCentro(comboCentroPaquetes, id);
            }
        });

        construirVentana();
        actualizarVista();
    }

    /**
     * *
     * construye y organiza la disposición visual de todos los paneles en la venta 
     */
    private void construirVentana() {
        setTitle("Sistema de Gestión de Centros de Distribución");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        // panel superior que agrupa el formulario de registros de centros y paquetes
        JPanel formularios = new JPanel(new GridLayout(1, 2, 8, 8));
        formularios.add(crearPanelCentro());
        formularios.add(crearPanelPaquetes());

        // panel inferior que agrupa las operaciones especiales : _ tralsados y eliminación _ y la barra de estado
        JPanel panelTraslado = crearPanelTraslado();
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(panelTraslado, BorderLayout.CENTER);
        etiquetaEstado.setBorder(BorderFactory.createEmptyBorder(0, 8, 4, 8));
        panelInferior.add(etiquetaEstado, BorderLayout.SOUTH);
        
        // agrega los cocmponentes principales al JFrame empleando BorderLayout
        add(formularios, BorderLayout.NORTH);
        add(new JScrollPane(tablaCentros), BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);

        setSize(900, 600);
        setLocationRelativeTo(null);
    }
    
    /**
     * 
     * @return
     * crea el panel visual para registrar nuevos centros 
     */

    private JPanel crearPanelCentro() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Registrar centro"));
        GridBagConstraints restricciones = restriccionesBase();

        agregarEtiqueta(panel, "Nombre:", restricciones, 0, 0);
        restricciones.gridx = 1;
        restricciones.weightx = 1;
        panel.add(campoNombreCentro, restricciones);

        agregarEtiqueta(panel, "Provincia:", restricciones, 0, 1);
        restricciones.gridx = 1;
        panel.add(comboProvincia, restricciones);

        JButton botonGuardar = new JButton("Guardar centro");
        botonGuardar.addActionListener(evento -> guardarCentro());
        restricciones.gridx = 1;
        restricciones.gridy = 2;
        restricciones.weightx = 0;
        panel.add(botonGuardar, restricciones);
        return panel;
    }

    /**
     * 
     * 
     * @return 
     * crea el panel visual para administrar paquetes : _ registrar y despachar_ en el centro seleccionado
     */
    private JPanel crearPanelPaquetes() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Administrar paquetes"));
        GridBagConstraints restricciones = restriccionesBase();

        agregarEtiqueta(panel, "Centro:", restricciones, 0, 0);
        restricciones.gridx = 1;
        restricciones.weightx = 1;
        panel.add(comboCentroPaquetes, restricciones);

        agregarEtiqueta(panel, "Código:", restricciones, 0, 1);
        restricciones.gridx = 1;
        panel.add(campoCodigoEnvio, restricciones);

        agregarEtiqueta(panel, "Destinatario:", restricciones, 0, 2);
        restricciones.gridx = 1;
        panel.add(campoDestinatario, restricciones);

        agregarEtiqueta(panel, "Tipo:", restricciones, 0, 3);
        restricciones.gridx = 1;
        panel.add(comboTipoPaquete, restricciones);

        JPanel botones = new JPanel();
        JButton botonRegistrar = new JButton("Guardar paquete");
        botonRegistrar.addActionListener(evento -> guardarPaquete());
        JButton botonDespachar = new JButton("Despachar frente");
        botonDespachar.addActionListener(evento -> despacharPaquete());
        botones.add(botonRegistrar);
        botones.add(botonDespachar);
        restricciones.gridx = 1;
        restricciones.gridy = 4;
        restricciones.weightx = 0;
        panel.add(botones, restricciones);
        return panel;
    }

    /**
     * 
     * 
     * @return 
     * crea el panel para las operaciones globales de centros : _ traslados y eliminaciones LIFO _
     */
    private JPanel crearPanelTraslado() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Operaciones de centros"));

        panel.add(new JLabel("Origen:"));
        panel.add(comboOrigen);
        panel.add(new JLabel("Destino:"));
        panel.add(comboDestino);

        JButton botonTrasladar = new JButton("Trasladar paquetes");
        botonTrasladar.addActionListener(evento -> trasladarPaquetes());
        panel.add(botonTrasladar);

        JButton botonEliminar = new JButton("Eliminar último centro");
        botonEliminar.addActionListener(evento -> eliminarCentro());
        panel.add(botonEliminar);
        return panel;
    }

    private GridBagConstraints restriccionesBase() {
        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.insets = new Insets(3, 5, 3, 5);
        restricciones.fill = GridBagConstraints.HORIZONTAL;
        restricciones.anchor = GridBagConstraints.WEST;
        restricciones.gridy = 0;
        return restricciones;
    }

    private void agregarEtiqueta(JPanel panel, String texto,
            GridBagConstraints restricciones, int columna, int fila) {
        restricciones.gridx = columna;
        restricciones.gridy = fila;
        restricciones.weightx = 0;
        panel.add(new JLabel(texto), restricciones);
    }

    /**
     * 
     * lóogica ejecutada al hacer clic en guardar centro validando el límite de 20
     * 
     */
    private void guardarCentro() {
        // validar si la pila ya alcanzó el límite esrticto de 20 centros antes de intentar registrar
        if (gestor.estaLlena()) {
            mostrarEstado("Límite alcanzado: ya existen 20 centros. "
                    + "Elimine uno manualmente antes de registrar otro.");
            return;
        }

        String nombre = campoNombreCentro.getText();
        String provincia = (String) comboProvincia.getSelectedItem();
        if (gestor.registrarCentro(nombre, provincia)) {
            campoNombreCentro.setText(""); // limpia campo luego de éxito
            actualizarVista();
            mostrarEstado("Centro guardado correctamente.");
        } else {
            mostrarEstado("No se pudo guardar: el nombre no puede estar vacío.");
        }
    }

    /**
     * 
     * lógica ejecutada para registrar un paquete en la cola del centro seleccionado
     */
    private void guardarPaquete() {
        Integer idCentro = (Integer) comboCentroPaquetes.getSelectedItem();
        if (idCentro == null) {
            mostrarEstado("Seleccione un centro antes de registrar paquetes.");
            return;
        }

        CentroDistribucion centro = buscarCentro(idCentro);
        boolean registrado = centro != null && centro.registrarPaquete(
                campoCodigoEnvio.getText(),
                campoDestinatario.getText(),
                (String) comboTipoPaquete.getSelectedItem());
        if (registrado) {
            campoCodigoEnvio.setText("");
            campoDestinatario.setText("");
            actualizarVista();
            seleccionarCentro(comboCentroPaquetes, idCentro);
            mostrarEstado("Paquete guardado correctamente.");
        } else {
            mostrarEstado("No se pudo guardar: revise los datos o la capacidad.");
        }
    }

    /**
     * 
     * despacha (saca de la cola FIFO) el primer paquete del centro seleccionado
     */
    private void despacharPaquete() {
        Integer idCentro = (Integer) comboCentroPaquetes.getSelectedItem();
        CentroDistribucion centro = idCentro == null ? null : buscarCentro(idCentro);
        if (centro == null) {
            mostrarEstado("Seleccione un centro antes de despachar.");
            return;
        }

        if (centro.despacharPaquete() == null) {
            mostrarEstado("El centro seleccionado no tiene paquetes.");
        } else {
            actualizarVista();
            seleccionarCentro(comboCentroPaquetes, idCentro);
            mostrarEstado("Paquete despachado correctamente.");
        }
    }

    /**
     * 
     *  se encarga de trasladar entre el centro origen y el centro destino seleccionados
     */
    private void trasladarPaquetes() {
        Integer origen = (Integer) comboOrigen.getSelectedItem();
        Integer destino = (Integer) comboDestino.getSelectedItem();
        if (origen == null || destino == null) {
            mostrarEstado("Seleccione origen y destino.");
            return;
        }

        if (gestor.trasladarPaquetes(origen, destino)) {
            actualizarVista();
            mostrarEstado("Paquetes trasladados correctamente.");
        } else {
            mostrarEstado("No se pudo trasladar: revise origen, destino y capacidad.");
        }
    }

    /**
     * elimina el último centro registrado (cima de la pila) si cumple con las condiciones 
     * 
     */
    private void eliminarCentro() {
        if (gestor.eliminarUltimoCentro()) {
            actualizarVista();
            mostrarEstado("Último centro eliminado correctamente.");
        } else {
            mostrarEstado("No se puede eliminar: la cima tiene paquetes o está vacía.");
        }
    }

    /**
     * permite actualuzar la vista de la tabla y recarga los ComboBox con los centros activos
     * 
     */
    private void actualizarVista() {
        modeloTabla.setRowCount(0); // limpia filas actuales de la tabla
        CentroDistribucion[] centros = gestor.getCentrosActuales();
        for (int i = 0; i < centros.length; i++) {
            CentroDistribucion centro = centros[i];
            modeloTabla.addRow(new Object[]{
                centro.getId(), centro.getNombre(), centro.getProvincia(),
                centro.getCantidadPaquetes()
            });
        }
        // actualizar los elementos de los ComboBox de selección
        cargarCentros(comboCentroPaquetes);
        cargarCentros(comboOrigen);
        cargarCentros(comboDestino);
    }

    /**
     * 
     * 
     * @param combo 
     * 
     * recarga los identificadores de los centros activos en un ComboBox especifico
     */
    private void cargarCentros(JComboBox<Integer> combo) {
        Integer seleccionado = (Integer) combo.getSelectedItem();
        combo.removeAllItems();
        CentroDistribucion[] centros = gestor.getCentrosActuales();
        for (int i = 0; i < centros.length; i++) {
            combo.addItem(centros[i].getId());
        }
        if (seleccionado != null) {
            seleccionarCentro(combo, seleccionado);
        }
    }

    // selecciona programáticamente un ID específico dentro de un ComboBox
    private void seleccionarCentro(JComboBox<Integer> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i) == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    // busca y retorna un objeto CentroDistribución en base a su ID 
    private CentroDistribucion buscarCentro(int id) {
        CentroDistribucion[] centros = gestor.getCentrosActuales();
        for (int i = 0; i < centros.length; i++) {
            if (centros[i].getId() == id) {
                return centros[i];
            }
        }
        return null;
    }

    /**
     * 
     * @param mensaje 
     * 
     * se encarga de actuaizar el texto de la etiqueta inferior para mostrar mensajers de estado al usuario
     */
    private void mostrarEstado(String mensaje) {
        etiquetaEstado.setText(mensaje);
    }

    public static void main(String[] args) {
        // se encarga de lanzar la interfax gráfica en el hilo de despacho de eventos de Swing 
        java.awt.EventQueue.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}

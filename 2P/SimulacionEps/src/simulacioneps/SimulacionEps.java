/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulacioneps;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class SimulacionEps extends JFrame {
    private JTextField txtCedula;
    private JComboBox<String> cmbCategoria, cmbServicio;
    private JTable tablaRegistros;
    private DefaultTableModel modeloTabla;
    private JLabel lblMensaje;
    private int contadorRegistros = 0;
    private JSlider sliderTiempo;
    private JLabel lblTiempoRestante;
    private Timer temporizador;
    private int tiempoRestante;
    private int indicePacienteActual = 0;

    public SimulacionEps() {
        super("Simulación de EPS");
        setLayout(new BorderLayout());

        // Panel para el registro del paciente
        JPanel panelRegistro = new JPanel(new GridLayout(5, 2, 10, 10));

        panelRegistro.add(new JLabel("Cédula:"));
        txtCedula = new JTextField();
        panelRegistro.add(txtCedula);

        panelRegistro.add(new JLabel("Categoría:"));
        cmbCategoria = new JComboBox<>(new String[]{"Menor de 60 años", "Adulto mayor", "Persona con discapacidad"});
        panelRegistro.add(cmbCategoria);

        panelRegistro.add(new JLabel("Servicio Solicitado:"));
        cmbServicio = new JComboBox<>(new String[]{"Consulta médico general", "Consulta médico especializado", "Prueba de laboratorio", "Imágenes diagnósticas"});
        panelRegistro.add(cmbServicio);

        JButton btnRegistrar = new JButton("Registrar Paciente");
        panelRegistro.add(btnRegistrar);

        // Panel para la tabla
        modeloTabla = new DefaultTableModel(new String[]{"Cédula", "Categoría", "Servicio", "Hora de Registro", "Hora de Salida"}, 0);
        tablaRegistros = new JTable(modeloTabla);

        // Ajustar ancho de la columna "Servicio"
        TableColumnModel columnModel = tablaRegistros.getColumnModel();
        columnModel.getColumn(2).setPreferredWidth(150); // Aumentar el ancho de la columna "Servicio"

        // Centrar texto en la tabla
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tablaRegistros.setDefaultRenderer(Object.class, centerRenderer);

        // Mensaje al superar 10 registros
        lblMensaje = new JLabel("");
        lblMensaje.setForeground(Color.RED);
        lblMensaje.setHorizontalAlignment(JLabel.CENTER);
        lblMensaje.setFont(new Font("Arial", Font.BOLD, 16)); // Aumentar tamaño de letra

        // Slider para ajustar el tiempo, ahora permite 0 minutos
        sliderTiempo = new JSlider(0, 15, 1);
        sliderTiempo.setMajorTickSpacing(2);
        sliderTiempo.setMinorTickSpacing(1);
        sliderTiempo.setPaintTicks(true);
        sliderTiempo.setPaintLabels(true);

        lblTiempoRestante = new JLabel("Tiempo de atención: 1 minuto");

        sliderTiempo.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int tiempoSeleccionado = sliderTiempo.getValue();
                lblTiempoRestante.setText("Tiempo de atención: " + tiempoSeleccionado + " minuto" + (tiempoSeleccionado != 1 ? "s" : ""));
                tiempoRestante = tiempoSeleccionado * 60; // Cambiar el tiempo restante en tiempo real
            }
        });

        // Acción del botón Registrar
        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarPaciente();
            }
        });

        // Agregar componentes al JFrame
        add(panelRegistro, BorderLayout.NORTH);
        add(new JScrollPane(tablaRegistros), BorderLayout.CENTER);
        add(lblMensaje, BorderLayout.SOUTH);
        add(sliderTiempo, BorderLayout.EAST);
        add(lblTiempoRestante, BorderLayout.WEST);

        // Configurar la ventana
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void registrarPaciente() {
        String cedula = txtCedula.getText();
        String categoria = (String) cmbCategoria.getSelectedItem();
        String servicio = (String) cmbServicio.getSelectedItem();
        String horaRegistro = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        // Agregar datos a la tabla
        modeloTabla.addRow(new Object[]{cedula, categoria, servicio, horaRegistro, ""});
        contadorRegistros++;

        // Limpiar campos
        txtCedula.setText("");

        // Mostrar mensaje cuando haya 10 registros
        if (contadorRegistros >= 10 && temporizador == null) {
            lblMensaje.setText("El servicio de atención en la EPS ha iniciado.");
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(() -> atenderPacientes());
                }
            }, 2000); // Esperar 2 segundos antes de iniciar la atención
        }
    }

    private void atenderPacientes() {
        if (indicePacienteActual < modeloTabla.getRowCount()) {
            String cedula = (String) modeloTabla.getValueAt(indicePacienteActual, 0);
            String servicio = (String) modeloTabla.getValueAt(indicePacienteActual, 2);
            lblMensaje.setText("Atendiendo al paciente #" + cedula + ", Servicio: " + servicio);

            tiempoRestante = sliderTiempo.getValue() * 60; // Convertir minutos a segundos
            temporizador = new Timer();
            temporizador.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(() -> actualizarTemporizador());
                }
            }, 0, 1000); // Actualización cada segundo
        } else {
            lblMensaje.setText("La EPS ha cerrado.");
        }
    }

    private void actualizarTemporizador() {
        if (tiempoRestante > 0) {
            tiempoRestante--;
        }
        lblTiempoRestante.setText("Tiempo restante: " + (tiempoRestante / 60) + ":" + String.format("%02d", (tiempoRestante % 60)) + " minutos");

        if (tiempoRestante <= 0) {
            temporizador.cancel();
            String horaSalida = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            modeloTabla.setValueAt(horaSalida, indicePacienteActual, 4); // Actualizar hora de salida
            indicePacienteActual++;
            sliderTiempo.setValue(2); // Restablecer a 1 minuto (2 unidades)
            atenderPacientes(); // Pasar al siguiente paciente
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SimulacionEps());
    }
}

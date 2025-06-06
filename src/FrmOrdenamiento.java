import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import servicios.Arbol;
import servicios.ServicioDocumento;
import servicios.Util;
import entidades.Documento;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FrmOrdenamiento extends JFrame {

    private JButton btnOrdenarBurbuja;
    private JButton btnOrdenarRapido;
    private JButton btnOrdenarInsercion;
    private JToolBar tbOrdenamiento;
    private JComboBox<String> cmbCriterio;
    private JTextField txtTiempo;
    private JButton btnBuscar;
    private JTextField txtBuscar;

    private JTable tblDocumentos;
    private Arbol arbolDocumentos;
    private int criterioActual = 0; 
    public FrmOrdenamiento() {
        tbOrdenamiento = new JToolBar();
        btnOrdenarBurbuja = new JButton();
        btnOrdenarInsercion = new JButton();
        btnOrdenarRapido = new JButton();
        cmbCriterio = new JComboBox<>();
        txtTiempo = new JTextField(8);

        btnBuscar = new JButton();
        txtBuscar = new JTextField(10);

        tblDocumentos = new JTable();

        setSize(600, 400);
        setTitle("Ordenamiento Documentos");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        btnOrdenarBurbuja.setIcon(new ImageIcon(getClass().getResource("/iconos/Ordenar.png")));
        btnOrdenarBurbuja.setToolTipText("Ordenar Burbuja");
        btnOrdenarBurbuja.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnOrdenarBurbujaClick(evt);
            }
        });
        tbOrdenamiento.add(btnOrdenarBurbuja);

        btnOrdenarRapido.setIcon(new ImageIcon(getClass().getResource("/iconos/OrdenarRapido.png")));
        btnOrdenarRapido.setToolTipText("Ordenar Rápido");
        btnOrdenarRapido.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnOrdenarRapidoClick(evt);
            }
        });
        tbOrdenamiento.add(btnOrdenarRapido);

        btnOrdenarInsercion.setIcon(new ImageIcon(getClass().getResource("/iconos/OrdenarInsercion.png")));
        btnOrdenarInsercion.setToolTipText("Ordenar Inserción");
        btnOrdenarInsercion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnOrdenarInsercionClick(evt);
            }
        });
        tbOrdenamiento.add(btnOrdenarInsercion);

        cmbCriterio.setModel(new DefaultComboBoxModel<>(
                new String[] { 
                    "Nombre Completo, Tipo de Documento", 
                    "Tipo de Documento, Nombre Completo" 
                }));
        cmbCriterio.setSelectedIndex(0);
        tbOrdenamiento.add(cmbCriterio);
        tbOrdenamiento.add(txtTiempo);

        btnBuscar.setIcon(new ImageIcon(getClass().getResource("/iconos/Buscar.png")));
        btnBuscar.setToolTipText("Buscar");
        btnBuscar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnBuscarClick(evt);
            }
        });
        tbOrdenamiento.add(btnBuscar);
        tbOrdenamiento.add(txtBuscar);

        JScrollPane spDocumentos = new JScrollPane(tblDocumentos);
        getContentPane().add(tbOrdenamiento, BorderLayout.NORTH);
        getContentPane().add(spDocumentos, BorderLayout.CENTER);

        String nombreArchivo = System.getProperty("user.dir") + "/src/datos/Datos.csv";
        ServicioDocumento.desdeArchivo(nombreArchivo);
        ServicioDocumento.mostrar(tblDocumentos);

        criterioActual = cmbCriterio.getSelectedIndex();
        arbolDocumentos = ServicioDocumento.getArbol(criterioActual);
    }

    private void btnOrdenarBurbujaClick(ActionEvent evt) {
        if (cmbCriterio.getSelectedIndex() >= 0) {
            criterioActual = cmbCriterio.getSelectedIndex();
            Util.iniciarCronometro();
            ServicioDocumento.ordenarBurbuja(criterioActual);
            txtTiempo.setText(Util.getTextoTiempoCronometro());
            ServicioDocumento.mostrar(tblDocumentos);

            arbolDocumentos = ServicioDocumento.getArbol(criterioActual);
        } else {
            JOptionPane.showMessageDialog(this, "Elija el criterio de ordenamiento");
        }
    }

    private void btnOrdenarRapidoClick(ActionEvent evt) {
        if (cmbCriterio.getSelectedIndex() >= 0) {
            criterioActual = cmbCriterio.getSelectedIndex();
            Util.iniciarCronometro();
            ServicioDocumento.ordenarRapido(0, ServicioDocumento.getTamaño() - 1, criterioActual);
            txtTiempo.setText(Util.getTextoTiempoCronometro());
            ServicioDocumento.mostrar(tblDocumentos);

            // Reconstruir el árbol con la lista ya ordenada
            arbolDocumentos = ServicioDocumento.getArbol(criterioActual);
        } else {
            JOptionPane.showMessageDialog(this, "Elija el criterio de ordenamiento");
        }
    }

    private void btnOrdenarInsercionClick(ActionEvent evt) {
        if (cmbCriterio.getSelectedIndex() >= 0) {
            criterioActual = cmbCriterio.getSelectedIndex();
            Util.iniciarCronometro();

            arbolDocumentos = ServicioDocumento.getArbol(criterioActual);

            arbolDocumentos.mostrar(tblDocumentos);

            txtTiempo.setText(Util.getTextoTiempoCronometro());
        } else {
            JOptionPane.showMessageDialog(this, "Elija el criterio de ordenamiento");
        }
    }

    private void btnBuscarClick(ActionEvent evt) {
        String claveBusqueda = txtBuscar.getText().trim();
        if (claveBusqueda.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debes ingresar un valor para buscar.", "Atención",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Asegurarse de que el árbol usa el mismo criterio
        arbolDocumentos.setCriterio(criterioActual);

        // Realizar la búsqueda recursiva
        Documento encontrado = arbolDocumentos.buscarRecursivo(claveBusqueda);

        if (encontrado != null) {
            JOptionPane.showMessageDialog(this,
                    "¡Documento encontrado!\n" +
                    encontrado.getApellido1() + " " +
                    encontrado.getApellido2() + " " +
                    encontrado.getNombre() + "\n" +
                    "Cédula: " + encontrado.getDocumento(),
                    "Resultado de búsqueda",
                    JOptionPane.INFORMATION_MESSAGE
            );
            // Resaltar en la tabla la fila donde esté ese documento
resaltarEnTabla(String.valueOf(encontrado.getDocumento()));
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se encontró ninguna coincidencia para: " + claveBusqueda,
                    "Resultado de búsqueda",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    /**
     * Recorre la JTable y selecciona la fila en la que la columna "Documento"
     * (índice 3) coincide con el texto buscado.
     */
    private void resaltarEnTabla(String documentoBuscado) {
        DefaultTableModel modelo = (DefaultTableModel) tblDocumentos.getModel();
        for (int fila = 0; fila < modelo.getRowCount(); fila++) {
            String valorCedula = modelo.getValueAt(fila, 3).toString();
            if (valorCedula.equalsIgnoreCase(documentoBuscado)) {
                tblDocumentos.setRowSelectionInterval(fila, fila);
                tblDocumentos.scrollRectToVisible(tblDocumentos.getCellRect(fila, 0, true));
                return;
            }
        }
        // Si no se encuentra (caso poco probable, pues buscarRecursivo devolvió algo),
        // no hacemos nada.
    }

    public static void main(String[] args) {
        FrmOrdenamiento ventana = new FrmOrdenamiento();
        ventana.setVisible(true);
    }
}

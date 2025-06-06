package servicios;

import java.io.BufferedReader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import entidades.Documento;

public class ServicioDocumento {

    private static List<Documento> documentos = new ArrayList<>();
    private static String[] encabezados;

    public static int getTamaño() {
        return documentos.size();
    }

    public static String[] getEncabezados() {
        return encabezados;
    }

    public static void setEncabezados(String[] encabezados) {
        ServicioDocumento.encabezados = encabezados;
    }

    public static void desdeArchivo(String nombreArchivo) {
        documentos.clear();
        BufferedReader br = Archivo.abrirArchivo(nombreArchivo);
        if (br != null) {
            try {
                
                String linea = br.readLine();
                if (linea == null) return;

                encabezados = linea.split(";");

             
                linea = br.readLine();
                while (linea != null) {
                    String[] textos = linea.split(";");
                    if (textos.length >= encabezados.length) {
                       
                        String valorDocumento = textos[3].trim();
                        Documento documento = new Documento(
                            textos[0].trim(),   // apellido1
                            textos[1].trim(),   // apellido2
                            textos[2].trim(),   // nombre
                            valorDocumento      // documento como String
                        );
                        documentos.add(documento);
                    }
                    linea = br.readLine();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                    null,
                    "Error al leer el archivo:\n" + ex.getMessage(),
                    "Error de lectura",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    public static void mostrar(JTable tbl) {
        if (encabezados == null || documentos.isEmpty()) {
            return;
        }

        String[][] datos = new String[documentos.size()][encabezados.length];
        int fila = 0;
        for (Documento d : documentos) {
            datos[fila][0] = d.getApellido1();
            datos[fila][1] = d.getApellido2();
            datos[fila][2] = d.getNombre();
            datos[fila][3] = d.getDocumento(); 
            fila++;
        }
        DefaultTableModel dtm = new DefaultTableModel(datos, encabezados);
        tbl.setModel(dtm);
    }

    private static final Collator collator = Collator.getInstance(new Locale("es", "ES"));

    public static boolean esMayor(Documento d1, Documento d2, int criterio) {
        collator.setStrength(Collator.PRIMARY);

        int cmpNombre = collator.compare(d1.getNombreCompleto(), d2.getNombreCompleto());
        String doc1 = d1.getDocumento();
        String doc2 = d2.getDocumento();

        if (criterio == 0) {
            int cmpDoc = collator.compare(doc1, doc2);
            return (cmpNombre > 0) || (cmpNombre == 0 && cmpDoc > 0);
        } else {
            int cmpDoc = collator.compare(doc1, doc2);
            return (cmpDoc > 0) || (cmpDoc == 0 && cmpNombre > 0);
        }
    }

    private static void intercambiar(int origen, int destino) {
        Documento temporal = documentos.get(origen);
        documentos.set(origen, documentos.get(destino));
        documentos.set(destino, temporal);
    }

    public static void ordenarBurbuja(int criterio) {
        for (int i = 0; i < documentos.size() - 1; i++) {
            for (int j = i + 1; j < documentos.size(); j++) {
                if (esMayor(documentos.get(i), documentos.get(j), criterio)) {
                    intercambiar(i, j);
                }
            }
        }
    }

    private static int localizarPivote(int inicio, int fin, int criterio) {
        int pivote = inicio;
        Documento dPivote = documentos.get(pivote);

        for (int i = inicio + 1; i <= fin; i++) {
            if (esMayor(dPivote, documentos.get(i), criterio)) {
                pivote++;
                intercambiar(i, pivote);
            }
        }
        if (inicio != pivote) {
            intercambiar(inicio, pivote);
        }
        return pivote;
    }

    public static void ordenarRapido(int inicio, int fin, int criterio) {
        if (inicio >= fin) {
            return;
        }
        int pivote = localizarPivote(inicio, fin, criterio);
        ordenarRapido(inicio, pivote - 1, criterio);
        ordenarRapido(pivote + 1, fin, criterio);
    }

 
    public static Arbol getArbol(int criterio) {
        Arbol arbol = new Arbol();
        arbol.setCriterio(criterio);
        for (Documento documento : documentos) {
            arbol.agregar(new Nodo(documento));
        }
        return arbol;
    }

    public static void setDocumentos(List<Documento> lista) {
        ServicioDocumento.documentos = lista;
    }
}

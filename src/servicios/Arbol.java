package servicios;

import java.io.BufferedReader;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import entidades.Documento;

public class Arbol {
    private Nodo raiz;
    private int criterio;
    private int totalNodos;

    public Arbol() {
        raiz = null;
        totalNodos = 0;
    }

    public int getCriterio() {
        return criterio;
    }

    public void setCriterio(int criterio) {
        this.criterio = criterio;
    }

    public void agregar(Nodo nodo) {
        agregar(raiz, nodo);
    }

    private void agregar(Nodo actual, Nodo nodo) {
        if (actual == null) {
            raiz = nodo;
            totalNodos++;
        } else {
           
            if (nodo.getDocumento().equals(actual.getDocumento())) {
                return;
            } else if (ServicioDocumento.esMayor(nodo.getDocumento(), actual.getDocumento(), criterio)) {
            
                if (actual.derecha == null) {
                    actual.derecha = nodo;
                    totalNodos++;
                } else {
                    agregar(actual.derecha, nodo);
                }
            } else {
           
                if (actual.izquierda == null) {
                    actual.izquierda = nodo;
                    totalNodos++;
                } else {
                    agregar(actual.izquierda, nodo);
                }
            }
        }
    }

    public void desdeArchivo(String nombreArchivo) {
        BufferedReader br = Archivo.abrirArchivo(nombreArchivo);
        if (br != null) {
            try {
                String linea = br.readLine();
                ServicioDocumento.setEncabezados(linea.split(";"));
                linea = br.readLine();
                while (linea != null) {
                    String[] textos = linea.split(";");
                    if (textos.length >= ServicioDocumento.getEncabezados().length) {
                      
                        Documento documento = new Documento(
                            textos[0].trim(),
                            textos[1].trim(),
                            textos[2].trim(),
                            textos[3].trim()   
                        );
                        agregar(new Nodo(documento));
                    }
                    linea = br.readLine();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void recorrerInOrden() {
        recorrerInOrden(raiz);
    }

    private void recorrerInOrden(Nodo nodo) {
        if (nodo != null) {
            recorrerInOrden(nodo.izquierda);
            System.out.println(
                nodo.getDocumento().getNombreCompleto() + 
                " - " + nodo.getDocumento().getDocumento()
            );
            recorrerInOrden(nodo.derecha);
        }
    }

    public void mostrar(JTable tbl) {
        String[][] datos = new String[totalNodos][ServicioDocumento.getEncabezados().length];
        int fila = -1;
        llenarDatos(raiz, datos, fila);

        DefaultTableModel dtm = new DefaultTableModel(datos, ServicioDocumento.getEncabezados());
        tbl.setModel(dtm);
    }

    private int llenarDatos(Nodo nodo, String[][] datos, int fila) {
        if (nodo != null) {
            fila = llenarDatos(nodo.izquierda, datos, fila);
            fila++;
            datos[fila][0] = nodo.getDocumento().getApellido1();
            datos[fila][1] = nodo.getDocumento().getApellido2();
            datos[fila][2] = nodo.getDocumento().getNombre();
            datos[fila][3] = nodo.getDocumento().getDocumento(); 
            fila = llenarDatos(nodo.derecha, datos, fila);
        }
        return fila;
    }

    private int compararSegunCriterio(Documento doc, String clave) {
        switch (criterio) {
            case 1:
                return clave.compareToIgnoreCase(doc.getApellido1());
            case 2:
                return clave.compareToIgnoreCase(doc.getApellido2());
            case 3:
                return clave.compareToIgnoreCase(doc.getNombre());
            case 4:
                return clave.compareToIgnoreCase(doc.getDocumento());
            default:
                return clave.compareToIgnoreCase(doc.getDocumento());
        }
    }

    public Documento buscarRecursivo(String clave) {
        return buscarRecursivo(raiz, clave);
    }

  
    private Documento buscarRecursivo(Nodo nodo, String clave) {
        if (nodo == null) {
            return null;
        }

        int cmp = compararSegunCriterio(nodo.getDocumento(), clave);

        if (cmp == 0) {
            return nodo.getDocumento();
        } else if (cmp < 0) {
          
            return buscarRecursivo(nodo.izquierda, clave);
        } else {
        
            return buscarRecursivo(nodo.derecha, clave);
        }
    }
}

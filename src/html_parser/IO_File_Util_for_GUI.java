/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package html_parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import javax.swing.JTextArea;

/**
 *
 * @author sergio
 */
public class IO_File_Util_for_GUI {

    private final String file_path;
    private final String file_name;
    private BufferedWriter bw;
    private boolean can_write;
    private final File file;
    private javax.swing.JTextArea LOG;

    public IO_File_Util_for_GUI(String file_path, String file_name, JTextArea LOG) {
        this.file_path = file_path;
        this.file_name = file_name;
        this.can_write = false;
        
        this.LOG = LOG;

        this.file = new File(this.file_path + this.file_name);
    }

    private void add_to_log(String text) {
        LOG.setText(LOG.getText() + "\n [" + (new Date()).toString() + "] [IO_File_Util] " + text);
    }
    
    public boolean create_file() {
        try {
            if (this.file.createNewFile()) {
                add_to_log("[INFO] Fichero creado." + "(" + this.file_name + ")");
                return true;
            } else {
                add_to_log("[INFO] El fichero ya existe. No es necesario crearlo." + "(" + this.file_name + ")");
                return true;
            }
        } catch (IOException ex) {
            add_to_log("[ERROR] El fichero no se ha podido crear. (" + ex.toString() + ")" + "(" + this.file_name + ")");
            return false;
        }
    }

    public boolean open_write_stream() {
        if (this.file.exists() && this.file.isFile() && this.file.canWrite()) {
            try {
                bw = new BufferedWriter(new FileWriter(this.file));
                this.can_write = true;
                add_to_log("[INFO] Flujo de escritura abierto" + "(" + this.file_name + ")");
                return true;
            } catch (IOException ex) {
                //Logger.getLogger(IO_File_Util.class.getName()).log(Level.SEVERE, null, ex);
                this.can_write = false;
                add_to_log("[ERROR] No se ha podido abrir flujo de escritura. (" + ex.toString() + ")" + "(" + this.file_name + ")");
                return false;
            }
        } else {
            //El fichero al que se trata de acceder no es viable.
            this.can_write = false;
            add_to_log("[WARNING] El fichero no se ha podido abrir." + "(" + this.file_name + ")");
            return false;
        }
    }

    public boolean close_write_stream() {
        try {
            this.bw.close();
            this.can_write = false;
            add_to_log("[INFO] Flujo de escritura cerrado." + "(" + this.file_name + ")");
            return true;
        } catch (Exception ex) {
            //Logger.getLogger(IO_File_Util.class.getName()).log(Level.SEVERE, null, ex);
            add_to_log("[ERROR] Fallo al cerrar el flujo. (" + ex.toString() + ")" + "(" + this.file_name + ")");
            return false;
        }
    }

    public boolean write_on_file(String stream) {
        if (this.can_write) {
            try {
                bw.write(stream);
                return true;
            } catch (IOException ex) {
                //Logger.getLogger(IO_File_Util.class.getName()).log(Level.SEVERE, null, ex);
                add_to_log("[ERROR] Fallo al escribir. (" + ex.toString() + ")" + "(" + this.file_name + ")");
                return false;
            }
        } else {
            add_to_log("[WARNING] No se puede escribir en el fichero." + "(" + this.file_name + ")");
            return false;
        }
    }
    
    public boolean write_on_file_new_line(){
        if (this.can_write) {
            try {
                bw.newLine();
                return true;
            } catch (IOException ex) {
                //Logger.getLogger(IO_File_Util.class.getName()).log(Level.SEVERE, null, ex);
                add_to_log("[ERROR] Fallo al escribir. (" + ex.toString() + ")" + "(" + this.file_name + ")");
                return false;
            }
        } else {
            add_to_log("[WARNING] No se puede escribir en el fichero." + "(" + this.file_name + ")");
            return false;
        }
    }
}

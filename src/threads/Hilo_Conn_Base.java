/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threads;

import html_parser.IO_File_Util_for_GUI;
import GUI.GUI_JFrame;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author sergio
 */
public class Hilo_Conn_Base extends Thread {

    private String filePath;
    private String fileName;
    private javax.swing.JTextArea LOG;
    private Elements lista_enlaces;
    private boolean can_continue = false;
    private boolean ok_initialization = false;
    private boolean halt_request = false;
    private GUI_JFrame JF_content;

    public Hilo_Conn_Base() {
    }

    public Hilo_Conn_Base(String filePath, String fileName, JTextArea LOG, GUI_JFrame gui) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.lista_enlaces = new Elements();
        this.LOG = LOG;
        this.JF_content = gui;
        initialize_components();
    }

    private void initialize_components() {
        add_to_log("Iniciando componentes");
        try {
            add_to_log("Parsing file......");
            Document doc = Jsoup.parse(new File(filePath + fileName), "UTF-8");
            Element first = doc.select("body").first();
            String attr = first.attr("data-content");
            if (!attr.isEmpty() && attr.equalsIgnoreCase("parser_format")) {
                lista_enlaces = doc.select("a");
                add_to_log("Se tendrán que realizar " + lista_enlaces.size() + " peticiones.");
                this.ok_initialization = true;
                add_to_log("[INFO] Inicialización finalizada");
            } else {
                this.ok_initialization = false;
                add_to_log("[WARNING] ===========> El formato del fichero no era el esperado :(");
            }
        } catch (IOException ex) {
            add_to_log("[ERROR] No se pudo leer el fichero.");
        }
    }

    private void add_to_log(String text) {
        LOG.setText(LOG.getText() + "\n [" + (new Date()).toString() + "] [Hilo_Connection] " + text);
    }

    private void finalize_components() {
        String html_skeleton_begin = " <!DOCTYPE html\n" + "PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n"
                + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" + "<html>\n" + "\n" + "<head>\n"
                + "<title>App List</title>\n" + "</head>\n" + "\n" + "<body>";
        String html_skeleton_end = "</body>\n" + "</html>";

        String fileName_save = "partial_" + (new Date()).toString().replaceAll(":", "_") + "_app_list.html";
        IO_File_Util_for_GUI html_simple = new IO_File_Util_for_GUI(filePath, fileName_save, this.LOG);
        add_to_log("Se intentará crear el fichero: " + fileName_save);
        if (html_simple.create_file()) {
            if (html_simple.open_write_stream()) {
                add_to_log("Guardando información.....");
                html_simple.write_on_file(html_skeleton_begin);

                Iterator<Element> it = lista_enlaces.iterator();
                Element next;
                while (it.hasNext()) {
                    next = it.next();
                    html_simple.write_on_file("\n<a title=\"" + next.attr("title") + "\" href=\""
                            + next.attr("href") + "\">" + next.text() + "</a>");
                }
                //Cerrar el fichero
                html_simple.write_on_file(html_skeleton_end);
                html_simple.close_write_stream();
                add_to_log("Guardado finalizado.");
            }
        }
    }

    public void stop_temporarly() {
        this.can_continue = false;
    }

    public void initalize_request() {
        this.can_continue = true;
    }

    public void halt_request() {
        this.halt_request = true;
        this.can_continue = false;
    }

    public boolean isOk_initialization() {
        return ok_initialization;
    }

    @Override
    public void run() {
        if (ok_initialization) {

            Element element;
            String link;
            boolean state_ok;

            String fileName_save = "partial_" + (new Date()).toString().replaceAll(":", "_") + "_apps_details.txt";

            File f = new File(filePath + "details_folder"+this.JF_content.get_base_path());
            f.mkdirs();

            IO_File_Util_for_GUI details_file = new IO_File_Util_for_GUI(filePath + "details_folder"+this.JF_content.get_base_path(), fileName_save, this.LOG);
            add_to_log("Se intentará crear el fichero: " + fileName_save);
            if (details_file.create_file()) {
                if (details_file.open_write_stream()) {
                    details_file.write_on_file("App#;Number of Reviews;Rating 5;Rating 4;Rating 3;Rating 2;Rating 1;Number of Downlds");
                    details_file.write_on_file_new_line();
                    add_to_log("--->Iniciando peticiones");
                    int index = 0;
                    while (!halt_request) {
                        while (can_continue && !lista_enlaces.isEmpty()) {
                            //Coger el elemento a analizar.
                            element = lista_enlaces.get(index);
                            add_to_log("Se analiza la aplicación " + element.text());
                            link = element.attr("href");
                            add_to_log("-----> Se realizará petición a (" + link + ")");

                            /*=====================================================================================*/
                            /*========== Aquí se realizará la petición a Google y se analiza la respuesta =========*/
                            state_ok = connect_and_parse(link, element, details_file);
                            /*=====================================================================================*/

                            if (state_ok) {
                                //Borrar el elemento de la lista.
                                add_to_log("Se elimina el objeto de la lista");
                                lista_enlaces.remove(index);
                                index--;
                            }
                            index++;
                            add_to_log("esperamos...");
                            esperarXsegundos(4);
                            add_to_log("reanudar");
                            if (index >= lista_enlaces.size()) {
                                can_continue = false;
                                halt_request = true;
                            }
                        }
                        if (halt_request) {
                            add_to_log("Abortando ejecución.....");
                        }
                    }
                    add_to_log("<-----> Guardando datos de peticiones restantes (" + lista_enlaces.size() + " peticiones)");
                    details_file.close_write_stream();
                    finalize_components();
                    add_to_log("Retornando el control al JFrame.");

                    //this.JF_content.getjButton_Initialize().setEnabled(true);
                    this.JF_content.toggleButtons(true);
                    add_to_log("\n=====================\n===== PARSED OK =====\n=====================");
                } else {
                    add_to_log("[WARNING] No se puede escribir en el fichero. Abortando ejecución.");
                }
            } else {
                add_to_log("[WARNING] No se pudo crear el fichero. Abortando ejecución.");
            }
        } else {
            add_to_log("[WARNING] No se permite la ejecución del hilo.");
        }
    }

    private void esperarXsegundos(int segundos) {
        try {
            Thread.sleep(segundos * 1000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean connect_and_parse(String link, Element app, IO_File_Util_for_GUI details_file) {
        boolean state_ok = false;
        int numb_of_req = 0;

        while (!state_ok && (numb_of_req < 4)) {
            try {
                add_to_log("Connecting to Google....");
                Document doc = Jsoup.connect(link).get();
                add_to_log("Connection successful!!");

                String reviews_num = doc.select(".reviews-num").first().text().replaceAll("\\.", "");
                String rating_five = doc.select(".rating-bar-container.five .bar-number").first().text().replaceAll("\\.", "");
                String rating_four = doc.select(".rating-bar-container.four .bar-number").first().text().replaceAll("\\.", "");
                String rating_three = doc.select(".rating-bar-container.three .bar-number").first().text().replaceAll("\\.", "");
                String rating_two = doc.select(".rating-bar-container.two .bar-number").first().text().replaceAll("\\.", "");
                String rating_one = doc.select(".rating-bar-container.one .bar-number").first().text().replaceAll("\\.", "");
                String meta_info = doc.select(".details-section-contents .meta-info .content").get(2).text().replaceAll("\\.", "");

                add_to_log("======> [" + app.attr("title") + "] Rev:" + reviews_num + " .Rat5:" + rating_five + " .Rat4:" + rating_four
                        + " .Rat3:" + rating_three + " .Rat2:" + rating_two + " .Rat1:" + rating_one + " .Downlds:" + meta_info);
                details_file.write_on_file(app.attr("title") + ";" + reviews_num + ";" + rating_five + ";" + rating_four
                        + ";" + rating_three + ";" + rating_two + ";" + rating_one + ";" + meta_info);
                details_file.write_on_file_new_line();
                state_ok = true;
            } catch (IOException ex) {
                add_to_log("No se pudo recuperar los datos de \"" + app.text() + "\"");
                add_to_log("      Exception: " + ex.toString());
            } catch (Exception ex) {
                add_to_log("Upssss!!! Falló la lectura de \"" + app.text() + "\"");
                add_to_log("      Exception: " + ex.toString());
            }
            numb_of_req++;
            if (!state_ok) {
                esperarXsegundos(2);
            }
        }
        if (!state_ok) {
            add_to_log("-------> [" + app.attr("title") + "] No se puede analizar esta app. Pasamos a otra.");
        }
        return state_ok;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package html_parser;

import GUI.GUI_JFrame;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author sergio
 */
public class ParserClass {

    private GUI_JFrame JF_content;

    public ParserClass(GUI_JFrame JF_content) {
        this.JF_content = JF_content;
    }

    public void generate_hyperlink_file(String folder_path, String parsed_file_name) {
        IO_File_Util_for_GUI summary = new IO_File_Util_for_GUI(folder_path, "summary.txt", this.JF_content.getjTextArea1());
        IO_File_Util_for_GUI csv_file = new IO_File_Util_for_GUI(folder_path, "csv_details.txt", this.JF_content.getjTextArea1());
        IO_File_Util_for_GUI html_simple = new IO_File_Util_for_GUI(folder_path, "app_list.html", this.JF_content.getjTextArea1());
        try {
            if (summary.create_file() & csv_file.create_file() & html_simple.create_file()) {
                if (summary.open_write_stream() && csv_file.open_write_stream() && html_simple.open_write_stream()) {
                    /*==============================================================*/
                    String html_skeleton_begin = " <!DOCTYPE html\n" + "PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n"
                            + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" + "<html>\n" + "\n" + "<head>\n"
                            + "<title>App List</title>\n" + "</head>\n" + "\n"
                            + "<body data-content=\"parser_format\">";
                    String html_skeleton_end = "</body>\n" + "</html>";

                    html_simple.write_on_file(html_skeleton_begin);
                    /*==============================================================*/
                    summary.write_on_file("Parsed file: " + parsed_file_name);
                    summary.write_on_file_new_line();
                    summary.write_on_file("---- Parsed date: " + (new Date()).toString().replaceAll(":", "_") + " --------");
                    summary.write_on_file_new_line();

                    File file = new File(folder_path + parsed_file_name);
                    Elements boxes_full = generateBoxes(file);

                    summary.write_on_file("There were " + boxes_full.size() + " apps.");
                    summary.write_on_file_new_line();

                    Iterator<Element> iterator = boxes_full.iterator();

                    Element box_app;
                    int i = 0, paid_apps = 0;
                    summary.write_on_file_new_line();
                    summary.write_on_file("--------------------");
                    summary.write_on_file_new_line();
                    summary.write_on_file("Apps without rating.");
                    summary.write_on_file_new_line();
                    String paid_app_details = "";
                    html_simple.write_on_file("<h3>Hyperlink App List</h3>");
                    while (iterator.hasNext()) {
                        i++;
                        box_app = iterator.next();
                        Elements details = box_app.select(".details");
                        Elements reason_set = box_app.select(".reason-set");

                        String nombre_app = details.first().select("a.title").first().text();
                        String href_app = details.first().select("a.title").first().attr("href");

                        String valoracion = reason_set.first().select(".tiny-star.star-rating-non-editable-container").first().attr("aria-label");
                        //"Valoración: 4,4 estrellas de cinco"
                        String[] valoracionComoArray = valoracion.split(" ");

                        valoracion = valoracionComoArray[1];
                        if (valoracion.contains("0,0")) {
                            summary.write_on_file("[" + i + "] App \"" + nombre_app + "\" does not hava rating (" + valoracion + ")");
                            summary.write_on_file_new_line();
                        }

                        String precio = reason_set.first().select(".display-price").first().text();
                        if (precio.isEmpty()) {
                            //this.JF_content.add_to_log("[" + i + "]name: " + nombre_app + " ----- " + valoracion + " ----- precio: " + precio);
                            precio = "Gratis";
                        } else {
                            paid_app_details += "\n[" + i + "] App: \"" + nombre_app + "\". Precio: " + precio;
                            paid_apps++;
                        }
                        csv_file.write_on_file(i + ";" + nombre_app + ";" + valoracion + ";" + precio.trim() + ";" + href_app);
                        csv_file.write_on_file_new_line();

                        if (((i - 1) % 50 == 0) && i != 1) {
                            html_simple.write_on_file("<p>" + (i - 1) + "</p><br />");
                        }
                        html_simple.write_on_file("\n<a title=\"" + i + "\" href=\"" + href_app + "\">[" + i + "] " + nombre_app + "</a>");
                        html_simple.write_on_file("<br />");
                    }

                    //Escribir resumen
                    summary.write_on_file_new_line();
                    summary.write_on_file("--------------------");
                    summary.write_on_file_new_line();
                    summary.write_on_file("There were " + paid_apps + " paid apps.");
                    summary.write_on_file_new_line();
                    summary.write_on_file(paid_app_details);
                    summary.write_on_file_new_line();

                    //--------------------------------------------------
                    //Finalmente, cerrar los flujos de escritura.
                    summary.close_write_stream();
                    csv_file.close_write_stream();

                    html_simple.write_on_file(html_skeleton_end);
                    html_simple.close_write_stream();
                    this.JF_content.add_to_log("\n\n");
                    this.JF_content.add_to_log("=============================================================================");
                    this.JF_content.add_to_log("La operación finalizó con éxito.\n");
                    this.JF_content.add_to_log("Se han generado los siguientes ficheros:");
                    this.JF_content.add_to_log("(1) summary.txt (Resumen)");
                    this.JF_content.add_to_log("(2) csv_details.txt (CSV para usar en Excel)");
                    this.JF_content.add_to_log("(3) app_list.html <--- (Este fichero contiene los links)\n");
                    this.JF_content.add_to_log("En el Directorio: " + folder_path);
                    this.JF_content.add_to_log("=============================================================================");
                    this.JF_content.getjButton_Initialize().setEnabled(true);
                } else {
                    this.JF_content.add_to_log("[WARNING] Files are not writable. No further actions will be taken.");
                }
            } else {
                this.JF_content.add_to_log("[WARNING] Files were not created. No further actions will be taken.");
            }
        } catch (Exception ex) {
            this.JF_content.add_to_log(":( Se ha producido un fallo al generar los links.");
            this.JF_content.add_to_log("   _" + ex);
        }
    }

    private Elements generateBoxes(File file) {
        try {
            Document doc = Jsoup.parse(file, "UTF-8");
            String box_class = doc.select(".card-list div").first().attr("class").replaceAll(" ", "\\.");
            //this.JF_content.add_to_log("-----------------------------------------------");
            //this.JF_content.add_to_log("--------->" + box_class);
            //this.JF_content.add_to_log("-----------------------------------------------");
            Elements boxes_full = doc.select("."+box_class);
            //Elements boxes_full = doc.select(".card.no-rationale.square-cover.apps.small");
            //Elements boxes_full = doc.select(".card.no-rationale.square-cover.apps.tiny");
            return boxes_full;
        } catch (IOException ex) {
            this.JF_content.add_to_log(":( Se ha producido un fallo al generar los links y no se han podido recuperar las apps del fichero.");
            this.JF_content.add_to_log("   _" + ex);
            return new Elements();
        }
    }
}

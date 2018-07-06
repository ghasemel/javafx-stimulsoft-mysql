package info.elyasi.java.javafx_stimulsoft_maven_mysql;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;


/**
 * This program shows how to integrate Stimulsoft Reporting tools with Java.<p>
 * It includes following tools/technologies:
 *      <ul>
 *          <li>Stimulsoft Reporting</li>
 *          <li>JavaFx</li>
 *          <li>Swing</li>
 *          <li>MySQL</li>
 *          <li>Maven</li>
 *          <li>Localization</li>
 *      </ul><br>
 * It also contains a <b>Customized Report Viewer</b> ({@link ReportViewer} class) with more controls on stimulsoft report viewer UI ({@link com.stimulsoft.viewer.StiViewerFx} class).<p>
 * With <b>localization</b> simplified feature, you can easily localize your report viewer. Two locale files are placed in Localization directory.<br>
 * For supporting more languages just put your specific language file in this directory and send the name of it to {@link ReportViewer} class.
 *
 * @see <a href="https://www.stimulsoft.com" target="_blank">Stimulsoft</a>
 * @see <a href="https://elyasi.info" target="_blank">Author's website</a>
 *
 * @author Ghasem Elyasi (ghasem.elyasi@gmail.com)
 * @version 1.0
 * @since 2018-07-06
 */
public class Main extends Application {

    private static final String MYSQL_IP = "localhost";
    private static final int MYSQL_PORT = 4405;
    private static final String DB_NAME = "mysql";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASS = "951963";

    /**
     * start method of JavaFx application that is overridden
     * @param primaryStage main stage of JavaFx application
     * @throws Exception in case of any failures at beginning of the program
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/sample_form.fxml"));
        primaryStage.setTitle("JavaFx & Stimulsoft & MySql & Maven");
        primaryStage.setScene(new Scene(root));

        primaryStage.setOnCloseRequest(event -> {
            ReportViewer.close();
            Platform.exit();
        });
        primaryStage.show();


    }


    /**
     * initial method of JavaFx Application that is overridden. we set all the configuration related to database and {@link ReportViewer}
     * in this method
     * @throws Exception in case of any initialization failures
     */
    @Override
    public void init() throws Exception {
        super.init();
        ReportViewer.UIFontConfiguration(new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,12));
        ReportViewer.localizationConfiguration("fa", true);
        ReportViewer.setNavigationToolbarToTopConfiguration(true);
        ReportViewer.setDisableToolTipsConfiguration(true);
        ReportViewer.initial(MYSQL_IP, MYSQL_PORT, MYSQL_USER, MYSQL_PASS, DB_NAME);
    }


    /**
     * Entry point of the application
     * @param args input parameters to the program
     */
    public static void main(String[] args) {
        launch(args);
    }
}

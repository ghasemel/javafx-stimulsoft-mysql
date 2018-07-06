package info.elyasi.java.javafx_stimulsoft_maven_mysql;

import com.stimulsoft.report.StiReport;
import javafx.fxml.FXML;


/**
 * JavaFx controller of /src/main/resources/sample_form.fxml file.
 * This form shows two sample reports using {@link ReportViewer}.
 * Sample reports are placed in Reports directory.
 *
 * @see <a href="https://elyasi.info" target="_blank">Author's website</a>
 *
 * @author Ghasem Elyasi (ghasem.elyasi@gmail.com)
 * @version 1.0
 * @since 2018-07-06
 */
public class SampleFormController {


    /**
     * shows sample report 1 by click on "Show Report 1" Button.
     */
    @FXML
    private void btnShowReport1_Click() {
        try {
            StiReport report = ReportViewer.createReport("Reports/Report1.mrt");
            ReportViewer.showReport(report, "Report 1");
        } catch (Exception _e) {
            _e.printStackTrace();
        }
    }


    /**
     * shows sample report 2 by click on "Show Report 2" Button.
     */
    @FXML
    private void btnShowReport2_Click() {
        try {
            StiReport report = ReportViewer.createReport("Reports/Report2.mrt");
            ReportViewer.showReport(report,"Report 2");
        } catch (Exception _e) {
            _e.printStackTrace();
        }
    }

}

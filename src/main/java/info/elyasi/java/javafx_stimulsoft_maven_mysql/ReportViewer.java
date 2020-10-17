package info.elyasi.java.javafx_stimulsoft_maven_mysql;

import com.stimulsoft.base.exception.StiException;
import com.stimulsoft.base.exception.StiExceptionProvider;
import com.stimulsoft.base.localization.StiLocalization;
import com.stimulsoft.base.serializing.StiDeserializationException;
import com.stimulsoft.flex.StiLocalizationAction;
import com.stimulsoft.report.StiReport;
import com.stimulsoft.report.StiSerializeManager;
import com.stimulsoft.report.dictionary.databases.StiMySqlDatabase;
import com.stimulsoft.report.saveLoad.StiDocument;
import com.stimulsoft.viewer.StiViewerFx;
import com.stimulsoft.viewer.controls.visual.StiFlatButton;
import com.stimulsoft.viewer.controls.visual.StiMultipageButton;
import com.stimulsoft.viewer.controls.visual.StiToggleFlatButton;
import com.stimulsoft.viewer.events.StiViewCommonEvent;
import com.stimulsoft.viewer.form.StiGoToPageDialog;
import com.stimulsoft.viewer.panels.StiMainToolBar;
import com.stimulsoft.viewer.panels.StiNavigateToolBar;
import com.stimulsoft.viewer.panels.StiViewModeToolBar;
import lombok.Getter;
import lombok.Setter;
import org.xml.sax.SAXException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;


/**
 * Custom report viewer for stimulsoft with focus to provide more controls on UI. Makes the localization simple and fix right_to_left issues of {@link StiViewerFx}.<br>
 * To localize a language, just put the corresponding file in the Localization directory and call localizationConfiguration method with file name and right to left value for specified language.
 *
 * @author Ghasem Elyasi (ghasem.elyasi@gmail.com)
 * @version 1.0
 * @see <a href="https://www.stimulsoft.com" target="_blank">Stimulsoft</a>
 * @see <a href="https://elyasi.info" target="_blank">Author's website</a>
 * @since 2018-07-06
 */
public class ReportViewer extends StiViewerFx {

    /**
     * default dimension for JFrame
     */
    private static final Dimension FRAME_SIZE = new Dimension(800, 800);


    /**
     * This method must be call before using {@link ReportViewer} to initialize it. it also must be called
     * after any configuration methods of this class.
     * Data Source name in the reports files must be "DataSet1"
     *
     * @param _server   MySQL server ip or address
     * @param _port     MySQL server port
     * @param _username MySQL username
     * @param _password MySql password
     * @param _dbName   database name of related database in MySQL server.
     */
    public static void initial(String _server, int _port, String _username, String _password, String _dbName) {
        frame = new JFrame();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(FRAME_SIZE);

        tabbedPane = new JTabbedPane();
        tabbedPane.addChangeListener(e -> {
            ReportViewer viewer = ((ReportViewer) tabbedPane.getSelectedComponent());
            frame.setTitle(viewer.getTitle() + " - " + viewer.getReportPath());
        });
        panel.add(tabbedPane);

        if (rightToLeftConfiguration)
            panel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        frame.add(panel);
        frame.setSize(FRAME_SIZE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        db = new StiMySqlDatabase(
                "DataSet1", "DataSet1",
                String.format("url=jdbc:mysql://%s:%s/%s?useSSL=false;database=%s;port=%s;",
                        _server, _port, _dbName, _dbName, _port
                )
        );

        db.setUser(_username);
        db.setPassword(_password);
    }


    /**
     * set localization of {@link ReportViewer}
     *
     * @param _langName      name of the locale file in Localization directory without extension
     * @param _isRightToLeft is the language right to left or not
     * @throws FileNotFoundException throws on wrong locale file path
     * @throws StiException          in case of any exception in Stimulsoft classes
     */
    public static void localizationConfiguration(String _langName, boolean _isRightToLeft) throws FileNotFoundException, StiException {
        StiLocalizationAction action = new StiLocalizationAction();
        StiLocalization localization = StiLocalization.load(action.getLocalization(_langName + ".xml"));
        StiLocalization.setLocalization(localization);
        StiViewerFx.setDefaultLocale(new Locale("fa", "IR"));
        rightToLeftConfiguration = _isRightToLeft;
    }

    /**
     * create a {@link StiReport} using report file path
     *
     * @param _reportPath report file path
     * @return a {@link StiReport} object of given report file path
     * @throws StiDeserializationException in case of any exception in Stimulsoft classes
     * @throws SAXException                in case of any exception in ReportFile loading
     * @throws IOException                 in case of any exception given file path
     */
    public static StiReport createReport(String _reportPath) throws StiDeserializationException, SAXException, IOException {
        StiReport report = StiSerializeManager.deserializeReport(new File(_reportPath));

        report.getDictionary().getDatabases().clear();
        report.getDictionary().getDatabases().add(db);

        report.Render();
        return report;
    }


    /**
     * close the ReportViewer window
     */
    public static void close() {
        if (frame != null) {
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        }
    }

    /**
     * shows created report in a JFrame
     *
     * @param _stiReport a {@link StiReport} object created by createReport() method
     * @param _title     title of report. it will be shown as tab title
     */
    public static void showReport(StiReport _stiReport, String _title) {
        SwingUtilities.invokeLater(() -> {
            try {
                ReportViewer reportViewerPanel = new ReportViewer(frame);
                reportViewerPanel.setTitle(_title);
                reportViewerPanel.setReportPath(_stiReport.getReportFile());

                tabbedPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5>" + _title + "</body></html>", reportViewerPanel);
                tabbedPane.setSelectedComponent(reportViewerPanel);

                frame.setVisible(true);

                reportViewerPanel.getStiViewModel()
                        .getEventDispatcher()
                        .dispatchStiEvent(
                                new StiViewCommonEvent(StiViewCommonEvent.DOCUMENT_FILE_LOADED, new StiDocument(_stiReport), null)
                        );


            } catch (Exception _e) {
                StiExceptionProvider.show(_e, null);
            }
        });
    }

    /**
     * set font for {@link ReportViewer} ui
     *
     * @param _font a font object
     */
    public static void UIFontConfiguration(javax.swing.plaf.FontUIResource _font) {
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put(key, _font);
        }
    }


    /**
     * Class constructor
     *
     * @param parentFrame {@link JFrame} object for {@link ReportViewer}
     */
    private ReportViewer(JFrame parentFrame) {
        super(parentFrame);

        configMainToolBar();
        configNavigationToolBar();
        configViewModeToolBar();

        if (isDisableToolTipsConfiguration()) {
            clearDefaultToolTipForMainToolBar();
            clearDefaultToolTipForNavigationToolBar();
            clearDefaultToolTipForViewModeToolBar();
        }
    }

    /**
     * configure and extract MainToolBar components
     */
    private void configMainToolBar() {
        StiMainToolBar mainToolBar = getStiMainToolBar();

        btnPrint = ((StiFlatButton) mainToolBar.getComponent(0));
        btnOpen = ((StiFlatButton) mainToolBar.getComponent(1));
        btnSave = ((StiFlatButton) mainToolBar.getComponent(2));
        btnSaveEmail = ((StiFlatButton) mainToolBar.getComponent(3));
        btnBookmarks = ((StiToggleFlatButton) mainToolBar.getComponent(5));
        btnParameters = ((StiFlatButton) mainToolBar.getComponent(6));
        btnThumbs = ((StiToggleFlatButton) mainToolBar.getComponent(7));
        btnFind = ((StiToggleFlatButton) mainToolBar.getComponent(9));
        btnFullScreen = ((StiFlatButton) mainToolBar.getComponent(11));
        btnZoomOnPage = ((StiToggleFlatButton) mainToolBar.getComponent(12));
        btnZoomTwoPages = ((StiToggleFlatButton) mainToolBar.getComponent(13));
        btnZoomMultiplePage = (StiMultipageButton) mainToolBar.getComponent(14);
        btnZoomPageWidth = ((StiToggleFlatButton) mainToolBar.getComponent(15));

        if (rightToLeftConfiguration)
            mainToolBar.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    }

    /**
     * remove default tooltips from MainToolBar components
     */
    public void clearDefaultToolTipForMainToolBar() {
        btnPrint.setToolTipText(null);
        btnOpen.setToolTipText(null);
        btnSave.setToolTipText(null);
        btnSaveEmail.setToolTipText(null);
        btnBookmarks.setToolTipText(null);
        btnParameters.setToolTipText(null);
        btnThumbs.setToolTipText(null);
        btnFind.setToolTipText(null);
        btnFullScreen.setToolTipText(null);
        btnZoomOnPage.setToolTipText(null);
        btnZoomTwoPages.setToolTipText(null);
        btnZoomMultiplePage.setToolTipText(null);
        btnZoomPageWidth.setToolTipText(null);
    }


    /**
     * remove default tooltips from NavigationToolBar components
     */
    public void clearDefaultToolTipForNavigationToolBar() {
        btnFirstPage.setToolTipText(null);
        btnPrevPage.setToolTipText(null);
        btnGoToPage.setToolTipText(null);
        btnNextPage.setToolTipText(null);
        btnLastPage.setToolTipText(null);
    }

    /**
     * configure and extract NavigationToolBar components
     */
    private void configNavigationToolBar() {
        StiNavigateToolBar navigateToolBar = getStiNavigateToolBar();

        btnFirstPage = (StiFlatButton) navigateToolBar.getComponent(0);
        btnPrevPage = (StiFlatButton) navigateToolBar.getComponent(1);
        btnGoToPage = (StiFlatButton) navigateToolBar.getComponent(3);
        btnNextPage = (StiFlatButton) navigateToolBar.getComponent(5);
        btnLastPage = (StiFlatButton) navigateToolBar.getComponent(6);

        if (rightToLeftConfiguration) {
            Arrays.stream(btnGoToPage.getActionListeners()).forEach(a -> btnGoToPage.removeActionListener(a));
            btnGoToPage.addActionListener(e -> {
                StiGoToPageDialog stiGoToPageDialog = new StiGoToPageDialog(getStiViewModel(), navigateToolBar, getParentFrame());
                stiGoToPageDialog.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                stiGoToPageDialog.setVisible(true);
            });
        }

        moveNavigationToolbarToTop();
    }

    /**
     * moves the NavigationToolBar location to the MainToolBar. based on rightToLeft property of locale
     * language it will arrange the UI.
     */
    private void moveNavigationToolbarToTop() {
        if (navigationToolbarToTopConfiguration) {
            int index = 0;
            if (rightToLeftConfiguration) {
                getStiMainToolBar().add(btnLastPage, index++);
                getStiMainToolBar().add(btnNextPage, index++);
                getStiMainToolBar().add(createSeparator(), index++);
                getStiMainToolBar().add(btnGoToPage, index++);
                getStiMainToolBar().add(createSeparator(), index++);
                getStiMainToolBar().add(btnPrevPage, index++);
                getStiMainToolBar().add(btnFirstPage, Math.abs(index));
            } else {
                getStiMainToolBar().add(btnFirstPage, index++);
                getStiMainToolBar().add(btnPrevPage, index++);
                getStiMainToolBar().add(createSeparator(), index++);
                getStiMainToolBar().add(btnGoToPage, index++);
                getStiMainToolBar().add(createSeparator(), index++);
                getStiMainToolBar().add(btnNextPage, index++);
                getStiMainToolBar().add(btnLastPage, index);
            }

            getStiMainToolBar().add(createSeparator(), 7);
            getStiMainToolBar().add(createSeparator(), 8);
        }
    }

    /**
     * create a {@link javax.swing.JToolBar.Separator} component
     *
     * @return a {@link javax.swing.JToolBar.Separator} component object
     */
    private JToolBar.Separator createSeparator() {
        return new JToolBar.Separator(null);
    }


    /**
     * configure and extract ViewModeToolBar components
     */
    private void configViewModeToolBar() {
        StiViewModeToolBar stiViewModeToolBar = getStiViewModeToolBar();

        singlePage = (StiToggleFlatButton) stiViewModeToolBar.getComponent(0);
        continuousPage = (StiToggleFlatButton) stiViewModeToolBar.getComponent(1);
        multiplePage = (StiToggleFlatButton) stiViewModeToolBar.getComponent(2);
    }


    /**
     * remove default tooltips from ViewModeToolBar components
     */
    public void clearDefaultToolTipForViewModeToolBar() {
        singlePage.setToolTipText(null);
        continuousPage.setToolTipText(null);
        multiplePage.setToolTipText(null);
    }


    /**
     * value of this property is used for moving NavigationToolBar to top of the form
     */
    @Getter
    @Setter
    private static boolean navigationToolbarToTopConfiguration = false;

    /**
     * determine whether the current locale language has right_to_left feature or not
     */
    @Getter
    private static boolean rightToLeftConfiguration = false;

    /**
     * value of this property will be use to disable tooltips
     */
    @Getter
    @Setter
    private static boolean disableToolTipsConfiguration = false;


    private static JTabbedPane tabbedPane;
    private static JFrame frame;

    /**
     * database of the report
     */
    @Getter
    static private StiMySqlDatabase db;


    @Getter
    @Setter
    private String title = "";
    @Getter
    @Setter
    private String reportPath = "";

    @Getter
    private StiToggleFlatButton singlePage;
    @Getter
    private StiToggleFlatButton continuousPage;
    @Getter
    private StiToggleFlatButton multiplePage;

    @Getter
    private StiFlatButton btnPrint;
    @Getter
    private StiFlatButton btnOpen;
    @Getter
    private StiFlatButton btnSave;
    @Getter
    private StiFlatButton btnSaveEmail;
    @Getter
    private StiToggleFlatButton btnBookmarks;
    @Getter
    private StiFlatButton btnParameters;
    @Getter
    private StiToggleFlatButton btnThumbs;
    @Getter
    private StiToggleFlatButton btnFind;
    @Getter
    private StiFlatButton btnFullScreen;
    @Getter
    private StiToggleFlatButton btnZoomOnPage;
    @Getter
    private StiToggleFlatButton btnZoomTwoPages;
    @Getter
    private StiToggleFlatButton btnZoomPageWidth;


    @Getter
    private StiMultipageButton btnZoomMultiplePage;
    @Getter
    private StiFlatButton btnNextPage;
    @Getter
    private StiFlatButton btnPrevPage;
    @Getter
    private StiFlatButton btnGoToPage;
    @Getter
    private StiFlatButton btnLastPage;
    @Getter
    private StiFlatButton btnFirstPage;

}

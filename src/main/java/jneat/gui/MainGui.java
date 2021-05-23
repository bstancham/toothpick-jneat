package jneat.gui;

import info.bstancham.toothpick.ml.ToothpickTrainingParams;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import jneat.common.EnvConstant;
import jneat.log.*;
import jneat.misc.*;

public class MainGui extends JPanel {

    JFrame f1;

    private Parameter   a_parameter;
    private Session     a_session;
    private Generation  a_generation;
    private Grafi       a_grafi;

    JTabbedPane jtabbedPane1;

    protected HistoryLog logger;

    public static void main(String[] args) {
        launchGui();
    }

    /**
     * Creates a new MainGui and launches it.
     *
     * Returns the MainGui instance which was created.
     */
    public static MainGui launchGui() {

        JFrame jp = null;
        MainGui pn1 = null;

        try {
            
            jp = new JFrame("J N E A T   Java simulator for NeuroEvolution of Augmenting Topologies");
            pn1 = new MainGui(jp);

            //	  jp.getContentPane().add(pn1);
            jp.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }
                });

            jp.pack();
            jp.setSize(800, 600);
            jp.setVisible(true);

        } catch (Exception ex) {
            System.err.println("ERRORE");
        }

        return pn1;
    }

    public MainGui(JFrame _f) {

        f1 = _f;

        a_parameter   = new Parameter(_f);
        a_session     = new Session(_f);
        a_generation  = new Generation(_f);
        a_grafi       = new Grafi(_f);

        logger        = new HistoryLog();

        a_parameter.setLog(logger);
        a_session.setLog(logger);
        a_generation.setLog(logger);
        a_grafi.setLog(logger);

        jtabbedPane1 = new JTabbedPane();
        jtabbedPane1.addTab("jneat parameter",a_parameter.pmain);
        jtabbedPane1.addTab("session parameter",a_session.pmain);
        jtabbedPane1.addTab("start simulation",a_generation.pmain);
        jtabbedPane1.addTab("view graph",a_grafi.pmain);
        jtabbedPane1.setSelectedIndex(0);


        /*
           Container contentPane = f1.getContentPane();
           contentPane.setLayout(new BorderLayout());
           contentPane.add(jtabbedPane1,BorderLayout.CENTER);
           contentPane.add(logger, BorderLayout.SOUTH);

        */

        Container contentPane = f1.getContentPane();
        contentPane.setLayout(new BorderLayout());

        JSplitPane paneSplit1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, jtabbedPane1, logger);
        paneSplit1.setOneTouchExpandable(true);
        paneSplit1.setContinuousLayout(true);
        paneSplit1.setDividerSize(10);
        jtabbedPane1.setMinimumSize(new Dimension(400, 50));
        logger.setMinimumSize(new Dimension(100, 50));

        paneSplit1.setDividerLocation(410);

        paneSplit1.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2),
                                                                BorderFactory.createEmptyBorder(2, 2, 2, 2)));

        contentPane.add(paneSplit1,BorderLayout.CENTER);

    }

    /** Brings the PARAMETER panel to front of the tabbed pane. */
    public void showParameterPanel() {
        jtabbedPane1.setSelectedIndex(0);
    }

    /** Brings the SESSION panel to front of the tabbed pane. */
    public void showSessionPanel() {
        jtabbedPane1.setSelectedIndex(1);
    }

    /** Brings the GENERATION panel to front of the tabbed pane. */
    public void showGenerationPanel() {
        jtabbedPane1.setSelectedIndex(2);
    }

    /** Brings the GRAPH panel to front of the tabbed pane. */
    public void showGraphPanel() {
        jtabbedPane1.setSelectedIndex(3);
    }



    public void loadDefaultJneatParameters() {
        a_parameter.loadDefaultJneatParameters();
    }
    
    public void loadJneatParameters(String filePath) {
        a_parameter.loadJneatParameters(filePath);
    }
    
    public void writeJneatParameters() {
        a_parameter.writeJneatParameters();
    }

    // public void setupSession(jneat.misc.DataInput input, DataTarget target, FitnessTest fitness) {
    //     a_session.setup(input, target, fitness);
    // public void setupSession(String input, String target, String fitness) {
    public void setupSession(String inputClass, String targetClass, String fitnessClass) {
        a_session.setup(inputClass, targetClass, fitnessClass);
    }

    /** WARNING: won't work! */
    public void loadDefaultSessionParameters() {
        a_session.loadDefaultSessionParameters();
    }
    
    public void loadSessionParametersFile(String path) {
        a_session.loadSessionParametersFile(path);
    }
    
    public void writeSessionParameters() {
        a_session.writeSessionParameters();
    }

    public void writeSessionParameters(String params) {
        a_session.writeSessionParameters(params);
    }

    /**
     * Write session parameters, and show them in the session panel.
     */
    public void showAndWriteSessionParameters(String params) {
        String[] lines = a_session.convertToArray(params);
        a_session.clearTextPane();
        a_session.setSourceNew(lines);
        a_session.writeSessionParameters(params);
    }

    public void loadClassFitness() {
        a_session.loadClassFitness();
    }
    
    public void loadClassDataInput() {
        a_session.loadClassDataInput();
    }
    
    public void loadClassDataTarget() {
        a_session.loadClassDataTarget();
    }
    
    public void compile() {
        a_session.compile();
    }

    public void selectOptionGraphChampion() {
        a_generation.selectOptionGraphChampion();
    }

    public void sendToLogger(String msg) {
        logger.sendToLog(msg);
    }

    public void setToothpickTrainingParams(ToothpickTrainingParams params) {
        EnvConstant.TOOTHPICK_TRAINING_PARAMS = params;
    }

    public Generation getGenerationPanel() { return a_generation; }

}

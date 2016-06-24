package wekimini.gui;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import weka.core.Instances;
import wekimini.DataGenerator;
import wekimini.InputGenerator;
import wekimini.InputGenerator.InputMode;
import wekimini.OutputGenerator;
import wekimini.OutputGenerator.OutputMode;
import wekimini.Path;
import wekimini.StatusUpdateCenter;
import wekimini.SupervisedLearningManager;
import wekimini.SupervisedLearningManager.RecordingState;
import wekimini.TrainingRunner;
import wekimini.Wekinator;
import wekimini.kadenze.KadenzeLogger;
import wekimini.kadenze.KadenzeLogging;
import wekimini.util.Util;

/**
 *
 * @author admin
 */
public class DataGeneratorFrame1 extends javax.swing.JFrame {

    private Wekinator w;
    
    private DataGenerator g;
    
    private int minValue = 0;
    private int maxValue = 1;
    private double sliderScale;
    private double sliderScaleInv;
    
    public int modelID = 0;
    public boolean previousButtonHit = false;
    
    private static final Logger logger = Logger.getLogger(SupervisedLearningManager.class.getName());
    /**
     * Creates new form DataGeneratorFrame
     */
    public DataGeneratorFrame1() throws Exception {
        Wekinator ww = null;
        initComponents();
        try {
            ww = Wekinator.TestingWekinator();
        } catch (IOException ex) {
            Logger.getLogger(DataGeneratorFrame1.class.getName()).log(Level.SEVERE, null, ex);
        }
        w = ww;
        g = new DataGenerator(w);
    }
    
    public DataGeneratorFrame1(Wekinator w) throws Exception {
        initComponents();
        this.w = w;
        this.g = new DataGenerator(w);
        setTitle("Data generator");
        
        initialize();
    }
    
    private void initialize() {
        //buttonAdd.setEnabled(false);
        //buttonRecordOutputs.setEnabled(false);
        
        buttonDeleteInputs.setEnabled(false);
        buttonDeleteOutputs.setEnabled(false);
        
        //buttonAddRandom.setEnabled(false);
        //buttonReAddRound.setEnabled(false);
        //buttonReAddRound.setEnabled(false);
        
        //radioButtonOutputsPreset.setEnabled(false);
        //fieldNumSamples.setEnabled(false);
        //labelNumOutputExamples.setEnabled(false);
        
        //sliderModelValue.setEnabled(false);
        
        comboClustering.setEnabled(false);
        
        fieldNumInstances.setEnabled(true);
        fieldNumClusterInstances.setEnabled(false);
        
        buttonEditPresets.setEnabled(false);
        
        //buttonDeleteTrainingInstances.setEnabled(false);
        //buttonTrain.setEnabled(false);
        buttonRun.setEnabled(false);
        
        //panelOutputSpecification.setVisible(false);
        
        buttonLoadPreviousModel.setEnabled(false);
        buttonLoadNextModel.setEnabled(false);
        
        labelStatus.setText("Click on \"Start Recording\" to record inputs.");
        
        g.setInputMode(InputGenerator.InputMode.RANDOM);
        g.setOutputMode(OutputGenerator.OutputMode.RANDOM);
    }
    
    private void setButtonsForLearningState() {
//        buttonRun.setEnabled(w.getSupervisedLearningManager().isAbleToRun());
        buttonRecordInputs.setEnabled(w.getSupervisedLearningManager().isAbleToRecord());

        SupervisedLearningManager.LearningState ls = w.getSupervisedLearningManager().getLearningState();
        if (ls == SupervisedLearningManager.LearningState.NOT_READY_TO_TRAIN) {
//            buttonTrain.setText("Train");
//            buttonTrain.setEnabled(false);
//            buttonTrain.setForeground(Color.BLACK);
            buttonAdd.setEnabled(false);
        } else if (ls == SupervisedLearningManager.LearningState.TRAINING) {
//            buttonTrain.setEnabled(true);
//            buttonTrain.setText("Cancel training");
//            buttonTrain.setForeground(Color.RED);
        } else if (ls == SupervisedLearningManager.LearningState.DONE_TRAINING) {
//            buttonTrain.setEnabled(true); //Don't prevent immediate retraining; some model builders may give different models on same data.
//            buttonTrain.setText("Train");
//            buttonTrain.setForeground(Color.BLACK);
        } else if (ls == SupervisedLearningManager.LearningState.READY_TO_TRAIN) {
//            buttonTrain.setEnabled(true);
//            buttonTrain.setText("Train");
//            buttonTrain.setForeground(Color.BLACK);
            buttonAdd.setEnabled(true);
        }
    }

    private void updateRecordInputsButton() {
        if (w.getSupervisedLearningManager().getRecordingState() == SupervisedLearningManager.RecordingState.RECORDING) {
            buttonRecordInputs.setText("Stop Recording");
            buttonRecordInputs.setForeground(Color.red);
            labelStatus.setText("Recording inputs!");
        } else {
            buttonRecordInputs.setText("Start Recording");
            buttonRecordInputs.setForeground(Color.black);
            labelStatus.setText("<html>Recording done. You can now generate training data, and/or choose generation parameters.</html>");
        }
    }
    private void updateRecordOutputsButton() {
        if (w.getSupervisedLearningManager().getRecordingState() == SupervisedLearningManager.RecordingState.RECORDING) {
            buttonRecordOutputs.setText("Stop Recording");
            buttonRecordOutputs.setForeground(Color.red);
        } else {
            buttonRecordOutputs.setText("Start Recording");
            buttonRecordOutputs.setForeground(Color.black);
        }
    }
    
    private void updateRandomizeButton() {
        if (w.getDataManager().getNumExamples() == 0) {
            buttonAdd.setEnabled(false);
        } else if (w.getDataManager().getNumExamples() != 0) {
            buttonAdd.setEnabled(true);
        }
    }
    
    private void updateForRecordingState(SupervisedLearningManager.RecordingState rs) {
        updateRecordInputsButton();
        updateRecordOutputsButton();
        updateButtonStates();
        if (rs == SupervisedLearningManager.RecordingState.NOT_RECORDING) {
//            updateDeleteLastRoundButton();
        }
    }
    
    private void updateButtonStates() {
        // if ()

        /*if (w.getSupervisedLearningManager().getRecordingState() == SupervisedLearningManager.RecordingState.RECORDING) {
         buttonRecord.setEnabled(true);
         buttonTrain.setEnabled(false);
         buttonRun.setEnabled(false);
         } else if (w.getSupervisedLearningManager().getRunningState() == SupervisedLearningManager.RunningState.RUNNING) {
         buttonRecord.setEnabled(true);
         buttonTrain.setEnabled(false);
         buttonRun.setEnabled(false);
         } */
    }
    
    private void statusUpdated(PropertyChangeEvent evt) {
        StatusUpdateCenter.StatusUpdate u = (StatusUpdateCenter.StatusUpdate) evt.getNewValue();
        setStatus(u.toString());
    }
    
    private void setStatus(String s) {
//        labelStatus.setText(s);
    }
    
    private void updateStatusForLearningState() {
        SupervisedLearningManager.LearningState ls = w.getSupervisedLearningManager().getLearningState();
        if (ls == SupervisedLearningManager.LearningState.NOT_READY_TO_TRAIN) {
           // setStatus("Ready to go! Press \"Start Recording\" above to record some examples.");
            w.getStatusUpdateCenter().update(this, "Ready to go! Press \"Start Recording\" above to record some examples.");

        } else if (ls == SupervisedLearningManager.LearningState.TRAINING) {
            //setStatus("Training...");
            w.getStatusUpdateCenter().update(this, "Training...");

        } else if (ls == SupervisedLearningManager.LearningState.DONE_TRAINING) {
            if (w.getTrainingRunner().wasCancelled()) {
                 w.getStatusUpdateCenter().update(this, "Training was cancelled.");
            } else if (w.getTrainingRunner().errorEncountered()) {
                 w.getStatusUpdateCenter().update(this, "Error(s) encountered during training.");
            } else {
                int n = w.getSupervisedLearningManager().numRunnableModels();
                if (n > 0) {
                     w.getStatusUpdateCenter().update(this, "Training completed. Press \"Run\" to run trained models.");
                } else {
                     w.getStatusUpdateCenter().update(this, "No models are ready to run. Record data and/or train.");
                }
            }
        } else if (ls == SupervisedLearningManager.LearningState.READY_TO_TRAIN) {
            w.getStatusUpdateCenter().update(this, w.getSupervisedLearningManager().getNumExamplesThisRound() + " new examples recorded");
           // setStatus("Examples recorded. Press \"Train\" to build models from data.");
        }
    }
    
    private void learningManagerPropertyChanged(PropertyChangeEvent evt) {
        if (evt.getPropertyName() == SupervisedLearningManager.PROP_RECORDINGSTATE) {
            updateForRecordingState((SupervisedLearningManager.RecordingState) evt.getNewValue());
        } else if (evt.getPropertyName() == SupervisedLearningManager.PROP_LEARNINGSTATE) {
            setButtonsForLearningState();
            updateStatusForLearningState();
            // System.out.println("Learning state updated: " + w.getSupervisedLearningManager().getLearningState());
        } else if (evt.getPropertyName() == SupervisedLearningManager.PROP_RUNNINGSTATE) {
//            updateRunButtonAndText();
        } else if (evt.getPropertyName() == SupervisedLearningManager.PROP_NUMEXAMPLESTHISROUND) {
            //TODO: Update somewhere else
            w.getStatusUpdateCenter().update(this, w.getSupervisedLearningManager().getNumExamplesThisRound() + " new examples recorded");
            //setStatus();
        } else if (evt.getPropertyName() == SupervisedLearningManager.PROP_ABLE_TO_RECORD) {
            setButtonsForLearningState();
        } else if (evt.getPropertyName() == SupervisedLearningManager.PROP_ABLE_TO_RUN) {
            setButtonsForLearningState();
        }
//Could do training state for GUI too... Want to have singleton Training Worker so all GUI elements can access update info
    }
    
    private void trainerUpdated(TrainingRunner.TrainingStatus newStatus) {
        String s = newStatus.getNumTrained() + " of " + newStatus.getNumToTrain()
                + " models trained, " + newStatus.getNumErrorsEncountered()
                + " errors encountered.";
        if (newStatus.isWasCancelled()) {
            s += " Training cancelled.";
        }
      //  setStatus(s);
        w.getStatusUpdateCenter().update(this, s);
    }
    
    private void learningCancelled() {
        //setStatus("Training was cancelled.");
        w.getStatusUpdateCenter().update(this, "Training was cancelled");

    }
    
    private void setupMinMax() {
        minValue = 1;
        //maxValue = rg.getNumInstances();
        maxValue = g.getNumInputInstances();
        setupSliderMinMax();
    }
    
    private void updateNumInputExamples(int num) {
        labelNumInputExamples.setText(Integer.toString(num));
        //labelNumExamples1.setText(Integer.toString(num));
    }
    
    private void updateNumOutputExamples() {
        labelNumOutputPresets.setText(Integer.toString(g.getNumOutputInstances()));
    }
    
    private void updateLabelSliderValue(int num) {
        //labelSliderValue.setText(Integer.toString(num));
    }

    private void setupSliderMinMax() {
        //sliderModelValue.setMinimum((int) minValue);
        //sliderModelValue.setMaximum((int) maxValue);
        sliderScale = 1.0;
        sliderScaleInv = 1.0;
    }

//    private double getSliderValueScaled() {
//        //return sliderModelValue.getValue() * sliderScaleInv;
//    }

    private void setSliderValueScaled(double value) {
        //sliderModelValue.setValue((int) (value * sliderScale));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupOutputMode = new javax.swing.ButtonGroup();
        buttonGroupInputMode = new javax.swing.ButtonGroup();
        frameOptions = new javax.swing.JFrame();
        jPanel3 = new javax.swing.JPanel();
        comboClustering = new javax.swing.JComboBox<>();
        jPanel7 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        fieldNumInstances = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        menuOptions = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        frameEditPresets = new javax.swing.JFrame();
        panelOutputSpecification = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        labelNumOutputPresets = new javax.swing.JLabel();
        buttonRecordOutputs = new javax.swing.JButton();
        buttonDeleteOutputs = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        buttonRecordInputs = new javax.swing.JButton();
        buttonDeleteInputs = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        labelNumInputExamples = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        buttonAdd = new javax.swing.JButton();
        buttonRun = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        buttonLoadPreviousModel = new javax.swing.JButton();
        buttonLoadNextModel = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        labelStatus = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        radioButtonInputsRandom = new javax.swing.JRadioButton();
        radioButtonInputsCluster = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        fieldNumRandomInstances = new javax.swing.JTextField();
        fieldNumClusterInstances = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        radioButtonOutputsRandom = new javax.swing.JRadioButton();
        radioButtonOutputsPresets = new javax.swing.JRadioButton();
        buttonEditPresets = new javax.swing.JButton();

        frameOptions.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        frameOptions.setTitle("Options");
        frameOptions.setSize(new java.awt.Dimension(570, 194));

        comboClustering.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "K-means", "EM algorithm" }));
        comboClustering.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboClusteringItemStateChanged(evt);
            }
        });
        comboClustering.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboClusteringActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(comboClustering, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(comboClustering, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(62, Short.MAX_VALUE))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Dataset specification"));

        jLabel4.setText("Number of examples to be generated:");

        fieldNumInstances.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        fieldNumInstances.setText("3");
        fieldNumInstances.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldNumInstancesActionPerformed(evt);
            }
        });
        fieldNumInstances.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fieldNumInstancesKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(fieldNumInstances, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(fieldNumInstances)
                .addComponent(jLabel4))
        );

        menuOptions.setText("Options");

        jMenuItem1.setText("Display options...");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        menuOptions.add(jMenuItem1);

        jMenuBar1.add(menuOptions);

        frameOptions.setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout frameOptionsLayout = new javax.swing.GroupLayout(frameOptions.getContentPane());
        frameOptions.getContentPane().setLayout(frameOptionsLayout);
        frameOptionsLayout.setHorizontalGroup(
            frameOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(frameOptionsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(417, Short.MAX_VALUE))
            .addGroup(frameOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(frameOptionsLayout.createSequentialGroup()
                    .addGap(131, 131, 131)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(132, Short.MAX_VALUE)))
        );
        frameOptionsLayout.setVerticalGroup(
            frameOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(frameOptionsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(82, Short.MAX_VALUE))
            .addGroup(frameOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(frameOptionsLayout.createSequentialGroup()
                    .addGap(72, 72, 72)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(73, Short.MAX_VALUE)))
        );

        frameEditPresets.setSize(new java.awt.Dimension(334, 126));

        panelOutputSpecification.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel3.setText("# specified presets");

        labelNumOutputPresets.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
        labelNumOutputPresets.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelNumOutputPresets.setText("0");
        labelNumOutputPresets.setToolTipText("");

        buttonRecordOutputs.setText("Add new preset from currently displayed values");
        buttonRecordOutputs.setToolTipText("");
        buttonRecordOutputs.setPreferredSize(new java.awt.Dimension(107, 28));
        buttonRecordOutputs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRecordOutputsActionPerformed(evt);
            }
        });

        buttonDeleteOutputs.setText("Delete all outputs");
        buttonDeleteOutputs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteOutputsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelOutputSpecificationLayout = new javax.swing.GroupLayout(panelOutputSpecification);
        panelOutputSpecification.setLayout(panelOutputSpecificationLayout);
        panelOutputSpecificationLayout.setHorizontalGroup(
            panelOutputSpecificationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOutputSpecificationLayout.createSequentialGroup()
                .addGroup(panelOutputSpecificationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelOutputSpecificationLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(labelNumOutputPresets, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(buttonDeleteOutputs, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                    .addComponent(buttonRecordOutputs, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        panelOutputSpecificationLayout.setVerticalGroup(
            panelOutputSpecificationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOutputSpecificationLayout.createSequentialGroup()
                .addComponent(buttonRecordOutputs, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonDeleteOutputs, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelOutputSpecificationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(labelNumOutputPresets))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout frameEditPresetsLayout = new javax.swing.GroupLayout(frameEditPresets.getContentPane());
        frameEditPresets.getContentPane().setLayout(frameEditPresetsLayout);
        frameEditPresetsLayout.setHorizontalGroup(
            frameEditPresetsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(frameEditPresetsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelOutputSpecification, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        frameEditPresetsLayout.setVerticalGroup(
            frameEditPresetsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(frameEditPresetsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelOutputSpecification, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Data Generator");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Record Inputs"));

        buttonRecordInputs.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        buttonRecordInputs.setText("Start recording");
        buttonRecordInputs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRecordInputsActionPerformed(evt);
            }
        });

        buttonDeleteInputs.setText("Delete recorded inputs");
        buttonDeleteInputs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteInputsActionPerformed(evt);
            }
        });

        jLabel2.setText("# recorded examples:");

        labelNumInputExamples.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
        labelNumInputExamples.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelNumInputExamples.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelNumInputExamples, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(buttonDeleteInputs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonRecordInputs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonRecordInputs, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonDeleteInputs)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(labelNumInputExamples))
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Generate training dataset"));

        buttonAdd.setText("Re-generate training data & Train");
        buttonAdd.setToolTipText("");
        buttonAdd.setMaximumSize(new java.awt.Dimension(107, 28));
        buttonAdd.setMinimumSize(new java.awt.Dimension(107, 28));
        buttonAdd.setPreferredSize(new java.awt.Dimension(107, 28));
        buttonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddActionPerformed(evt);
            }
        });

        buttonRun.setText("Run");
        buttonRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRunActionPerformed(evt);
            }
        });

        buttonLoadPreviousModel.setText("<html>Load previous<br>model<html>");
        buttonLoadPreviousModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLoadPreviousModelActionPerformed(evt);
            }
        });

        buttonLoadNextModel.setText("<html>Load next<br>model<html>");
        buttonLoadNextModel.setToolTipText("");
        buttonLoadNextModel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        buttonLoadNextModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLoadNextModelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(buttonAdd, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
                    .addComponent(buttonRun, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(buttonLoadPreviousModel, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(buttonLoadNextModel)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonRun, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonLoadPreviousModel)
                    .addComponent(buttonLoadNextModel))
                .addContainerGap())
        );

        jLabel1.setText("Status:");

        labelStatus.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelStatus.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 527, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Choose inputs for generating examples"));

        buttonGroupInputMode.add(radioButtonInputsRandom);
        radioButtonInputsRandom.setSelected(true);
        radioButtonInputsRandom.setText("Choose randomly from recorded inputs");
        radioButtonInputsRandom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonInputsRandomActionPerformed(evt);
            }
        });

        buttonGroupInputMode.add(radioButtonInputsCluster);
        radioButtonInputsCluster.setText("Cluster recorded inputs and choose cluster centers");
        radioButtonInputsCluster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonInputsClusterActionPerformed(evt);
            }
        });

        jLabel5.setText("use");

        jLabel6.setText("choose");

        fieldNumRandomInstances.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        fieldNumRandomInstances.setText("3");
        fieldNumRandomInstances.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldNumRandomInstancesActionPerformed(evt);
            }
        });
        fieldNumRandomInstances.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fieldNumRandomInstancesKeyTyped(evt);
            }
        });

        fieldNumClusterInstances.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        fieldNumClusterInstances.setText("3");
        fieldNumClusterInstances.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldNumClusterInstancesActionPerformed(evt);
            }
        });
        fieldNumClusterInstances.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fieldNumClusterInstancesKeyTyped(evt);
            }
        });

        jLabel7.setText("examples");

        jLabel8.setText("clusters");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(radioButtonInputsCluster)
                    .addComponent(radioButtonInputsRandom))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(fieldNumRandomInstances, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                    .addComponent(fieldNumClusterInstances))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radioButtonInputsRandom)
                    .addComponent(jLabel6)
                    .addComponent(fieldNumRandomInstances, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radioButtonInputsCluster)
                    .addComponent(jLabel5)
                    .addComponent(fieldNumClusterInstances, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Choose outputs for generating examples"));

        buttonGroupOutputMode.add(radioButtonOutputsRandom);
        radioButtonOutputsRandom.setSelected(true);
        radioButtonOutputsRandom.setText("Generate random outputs within legal range");
        radioButtonOutputsRandom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonOutputsRandomActionPerformed(evt);
            }
        });

        buttonGroupOutputMode.add(radioButtonOutputsPresets);
        radioButtonOutputsPresets.setText("Use output value presets");
        radioButtonOutputsPresets.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonOutputsPresetsActionPerformed(evt);
            }
        });

        buttonEditPresets.setText("Edit...");
        buttonEditPresets.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditPresetsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(radioButtonOutputsRandom)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(radioButtonOutputsPresets)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buttonEditPresets)
                        .addGap(71, 71, 71))))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(radioButtonOutputsRandom)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radioButtonOutputsPresets)
                    .addComponent(buttonEditPresets))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonRecordInputsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRecordInputsActionPerformed
        if (w.getSupervisedLearningManager().getRecordingState() != SupervisedLearningManager.RecordingState.RECORDING) {
            w.getSupervisedLearningManager().getSupervisedLearningController().startRecord();
            updateRecordInputsButton();
            /* w.getSupervisedLearningManager().startRecording();
            w.getStatusUpdateCenter().update(this, "Recording - waiting for inputs to arrive"); */
        } else {
            w.getSupervisedLearningManager().getSupervisedLearningController().stopRecord();
            updateRecordInputsButton();
            
            int whichRound = w.getDataManager().getMaxRecordingRound();
            
            g.getInputStream(whichRound);
            KadenzeLogging.getLogger().logGeneratorRecordInputs(w,g);
            
            setupMinMax();
            updateNumInputExamples(g.getNumInputInstances());
            buttonDeleteInputs.setEnabled(true);
            buttonRecordInputs.setEnabled(false);
            buttonAdd.setEnabled(true);
                
//            // Older version:
//            try {
//                rg = new RandomDataGenerator(w);
//            } catch (Exception ex) {
//                Logger.getLogger(DataGeneratorFrame.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            int whichRound = w.getDataManager().getMaxRecordingRound();
//            rg.getRecordedStream(whichRound);
//            
//            setupMinMax();
//            updateNumExamples(rg.getNumInstances());
                
                /* w.getSupervisedLearningManager().stopRecording();
                // setStatus("Examples recorded. Press \"Train\" to build models from data.");
                w.getStatusUpdateCenter().update(this, w.getSupervisedLearningManager().getNumExamplesThisRound() + " new examples recorded");*/
        }
    }//GEN-LAST:event_buttonRecordInputsActionPerformed

    private void radioButtonOutputsRandomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonOutputsRandomActionPerformed
        g.setOutputMode(OutputGenerator.OutputMode.RANDOM);
        buttonEditPresets.setEnabled(false);
        //buttonRecordOutputs.setEnabled(false);
        //buttonDeleteOutputs.setEnabled(false);
        //panelOutputSpecification.setVisible(false);
        g.deleteStoredOutputs(); // TODO: maybe "smooth" this operation?
        updateNumOutputExamples();
        labelStatus.setText("Now generating random outputs.");
        //labelNumOutputExamples.setText("0");
        //fieldNumSamples.setEnabled(false);
    }//GEN-LAST:event_radioButtonOutputsRandomActionPerformed

    private void buttonDeleteInputsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteInputsActionPerformed
        g.deleteStoredInputs();
        labelNumInputExamples.setText("0");
        //labelNumExamples1.setText("0");
        buttonAdd.setEnabled(false);
        buttonDeleteInputs.setEnabled(false);
        buttonRecordInputs.setEnabled(true);
        labelStatus.setText("Click on \"Start Recording\" to record inputs.");
    }//GEN-LAST:event_buttonDeleteInputsActionPerformed

    private void radioButtonInputsRandomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonInputsRandomActionPerformed
        comboClustering.setEnabled(false);
        fieldNumRandomInstances.setEnabled(true);
        fieldNumClusterInstances.setEnabled(false);
        fieldNumInstances = fieldNumRandomInstances;
        g.setInputMode(InputGenerator.InputMode.RANDOM);
        labelStatus.setText("Now randomly choosing " + fieldNumRandomInstances.getText().trim() + " examples from recorded inputs.");
    }//GEN-LAST:event_radioButtonInputsRandomActionPerformed

    private void radioButtonInputsClusterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonInputsClusterActionPerformed
        comboClustering.setEnabled(true);
        fieldNumRandomInstances.setEnabled(false);
        fieldNumClusterInstances.setEnabled(true);
        fieldNumInstances = fieldNumClusterInstances;
        if ( comboClustering.getSelectedIndex() == 0 ) {
            g.setInputMode(InputGenerator.InputMode.CLUSTERkm);
            fieldNumInstances.setEnabled(true);
        } else {
            g.setInputMode(InputGenerator.InputMode.CLUSTERem);
            fieldNumInstances.setEnabled(false);
        }
        labelStatus.setText("Now choosing " + fieldNumClusterInstances.getText().trim() + " cluster centers from recorded inputs.");
    }//GEN-LAST:event_radioButtonInputsClusterActionPerformed

    private void buttonRecordOutputsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRecordOutputsActionPerformed
        g.getOutputCurrentValues();
        updateNumOutputExamples();
        buttonDeleteOutputs.setEnabled(true);
        labelStatus.setText("Output preset added. You can click again to add another preset.");
//        RecordingState state;
//        state = w.getSupervisedLearningManager().getRecordingState();
//        logger.log(Level.SEVERE, "State: {0}", state.toString());
//        logger.log(Level.SEVERE, "Clicked!");
//        if (w.getSupervisedLearningManager().getRecordingState() != SupervisedLearningManager.RecordingState.RECORDING) {
//            logger.log(Level.SEVERE, "In the if");
//            
//            w.getSupervisedLearningManager().getSupervisedLearningController().startRecord();
//            updateRecordOutputsButton();
//            
//            g.waitForOutputPresets();
//            /* w.getSupervisedLearningManager().startRecording();
//            w.getStatusUpdateCenter().update(this, "Recording - waiting for inputs to arrive"); */
//        } else {
//            logger.log(Level.SEVERE, "In the else");
//            w.getSupervisedLearningManager().getSupervisedLearningController().stopRecord();
//            updateRecordOutputsButton();
//            
//            int whichRound = w.getDataManager().getMaxRecordingRound();
//            g.getOutputStream(whichRound);
//        }
    }//GEN-LAST:event_buttonRecordOutputsActionPerformed

    private void buttonDeleteOutputsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteOutputsActionPerformed
        g.deleteStoredOutputs();
        labelNumOutputPresets.setText(Integer.toString(0));
        buttonDeleteOutputs.setEnabled(false);
        labelStatus.setText("<html>No output presets. Click on \"Add new preset from currently displayed values\" to add an output preset.</html>");
    }//GEN-LAST:event_buttonDeleteOutputsActionPerformed

    private void radioButtonOutputsPresetsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonOutputsPresetsActionPerformed
        g.setOutputMode(OutputGenerator.OutputMode.PRESET);
        buttonEditPresets.setEnabled(true);
        
        g.deleteStoredOutputs(); // TODO: maybe "smooth" this operation?
        updateNumOutputExamples();
        labelStatus.setText("No output presets. Click on \"Edit...\" to add output presets.");
        //buttonRecordOutputs.setEnabled(true);
        //panelOutputSpecification.setVisible(true);
        //fieldNumSamples.setEnabled(true);
    }//GEN-LAST:event_radioButtonOutputsPresetsActionPerformed

    private void buttonRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRunActionPerformed
        if (w.getSupervisedLearningManager().getRunningState() == SupervisedLearningManager.RunningState.NOT_RUNNING) {
            w.getSupervisedLearningManager().getSupervisedLearningController().startRun();
            buttonRun.setText("Stop running");
            buttonRun.setForeground(Color.RED);
            labelStatus.setText("Models running!");
            //  w.getSupervisedLearningManager().setRunningState(SupervisedLearningManager.RunningState.RUNNING);
        } else {
            w.getSupervisedLearningManager().getSupervisedLearningController().stopRun();
            buttonRun.setText("Run");
            buttonRun.setForeground(Color.BLACK);
            labelStatus.setText("<html>Models stopped. You can either regenerate a new dataset, record new inputs, or edit generation parameters.</html>");
            //w.getSupervisedLearningManager().setRunningState(SupervisedLearningManager.RunningState.NOT_RUNNING);
        }
    }//GEN-LAST:event_buttonRunActionPerformed

    private void fieldNumInstancesKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldNumInstancesKeyTyped
        char enter = evt.getKeyChar();
        if (!(Character.isDigit(enter))) {
            evt.consume();
        }
    }//GEN-LAST:event_fieldNumInstancesKeyTyped

    private void fieldNumInstancesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldNumInstancesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fieldNumInstancesActionPerformed

    private void buttonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddActionPerformed
        if (fieldNumInstances.getText().trim().equals("0") || fieldNumInstances.getText().isEmpty()) {
            Util.showPrettyErrorPane(this, "Choose inputs for generating examples: Please type the number of input examples/clusters you want to use.");
            labelStatus.setText("Please type the number of input examples/clusters you want to use.");
        } else {
            if (g.getOutputMode() == OutputGenerator.OutputMode.PRESET) {
                if (labelNumOutputPresets.getText().trim().equals("0")) {
                    Util.showPrettyErrorPane(this, "Use output value presets: Please add at least one output preset by clicking on 'Edit...' button.");
                    labelStatus.setText("Please add at least one output preset by clicking on \"Edit...\" button.");
                    return;
                }
            }
            
            modelID = modelID + 1;
            updateLoadModelButtons();
            
            int numInstances = Integer.parseInt(fieldNumInstances.getText());
            g.setAsTrainingData(numInstances);
            
            buttonLoadNextModel.setEnabled(false);
            if (!g.isDummyEmpty()) {
                buttonLoadPreviousModel.setEnabled(true);
            }
            //buttonDeleteTrainingInstances.setEnabled(true);
            //buttonTrain.setEnabled(true);
            
            if (w.getSupervisedLearningManager().getLearningState() == SupervisedLearningManager.LearningState.TRAINING) {
                w.getSupervisedLearningManager().getSupervisedLearningController().cancelTrain();
                //w.getSupervisedLearningManager().cancelTraining();
            } else {
                w.getSupervisedLearningManager().getSupervisedLearningController().train();
                KadenzeLogging.getLogger().logGeneratorNewModel(w, g);
                if (g.getOutputMode() == OutputGenerator.OutputMode.PRESET) {
                    KadenzeLogging.getLogger().logGeneratorPresetOutputs(w, g);
                }
                buttonRun.setEnabled(true);
                labelStatus.setText("Dataset generated / Training completed. Press \"Run\" to run trained models.");
                //w.getSupervisedLearningManager().buildAll();
            }
        }
    }//GEN-LAST:event_buttonAddActionPerformed

    private void comboClusteringActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboClusteringActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboClusteringActionPerformed

    private void comboClusteringItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboClusteringItemStateChanged
        if ( comboClustering.getSelectedIndex() == 0 ) {
            g.setInputMode(InputGenerator.InputMode.CLUSTERkm);
            fieldNumInstances.setEnabled(true);
        } else {
            g.setInputMode(InputGenerator.InputMode.CLUSTERem);
            fieldNumInstances.setEnabled(false);
        }
    }//GEN-LAST:event_comboClusteringItemStateChanged

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        frameOptions.setVisible(true);
        frameOptions.toFront();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void fieldNumRandomInstancesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldNumRandomInstancesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fieldNumRandomInstancesActionPerformed

    private void fieldNumRandomInstancesKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldNumRandomInstancesKeyTyped
        char enter = evt.getKeyChar();
        if (!(Character.isDigit(enter))) {
            evt.consume();
        }
        labelStatus.setText("Now randomly choosing " + fieldNumRandomInstances.getText().trim() + " examples from recorded inputs.");
        fieldNumInstances = fieldNumRandomInstances;
    }//GEN-LAST:event_fieldNumRandomInstancesKeyTyped

    private void fieldNumClusterInstancesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldNumClusterInstancesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fieldNumClusterInstancesActionPerformed

    private void fieldNumClusterInstancesKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldNumClusterInstancesKeyTyped
        char enter = evt.getKeyChar();
        if (!(Character.isDigit(enter))) {
            evt.consume();
        }
        labelStatus.setText("Now choosing " + fieldNumClusterInstances.getText().trim() + " cluster centers from recorded inputs.");
        fieldNumInstances = fieldNumClusterInstances;
    }//GEN-LAST:event_fieldNumClusterInstancesKeyTyped

    private void buttonEditPresetsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditPresetsActionPerformed
        frameEditPresets.setVisible(true);
        frameEditPresets.toFront();
    }//GEN-LAST:event_buttonEditPresetsActionPerformed

    private void buttonLoadPreviousModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLoadPreviousModelActionPerformed
        previousButtonHit = true;
        
        g.swapTrainingForDummy();
        
        buttonLoadPreviousModel.setEnabled(false);
        buttonLoadNextModel.setEnabled(true);

        updateInputParameters();
        updateOutputParameters();
        
        if (w.getSupervisedLearningManager().getLearningState() == SupervisedLearningManager.LearningState.TRAINING) {
            w.getSupervisedLearningManager().getSupervisedLearningController().cancelTrain();
            //w.getSupervisedLearningManager().cancelTraining();
        } else {
            w.getSupervisedLearningManager().getSupervisedLearningController().train();
            KadenzeLogging.getLogger().logGeneratorPreviousModel(w);
            buttonRun.setEnabled(true);
            labelStatus.setText("Previous dataset loaded / Training completed. Press \"Run\" to run trained models.");
            //w.getSupervisedLearningManager().buildAll();
        }
    }//GEN-LAST:event_buttonLoadPreviousModelActionPerformed

    private void updateInputParameters() {
        if (g.getInputMode() == InputMode.RANDOM) {
            //buttonGroupInputMode.setSelected(m, rootPaneCheckingEnabled);
            radioButtonInputsRandom.setSelected(true);
            fieldNumRandomInstances.setEnabled(true);
            fieldNumRandomInstances.setText(Integer.toString(w.getDataManager().getNumExamples()));
            fieldNumClusterInstances.setEnabled(false);
        } else {
            radioButtonInputsCluster.setSelected(true);
            fieldNumRandomInstances.setEnabled(false);
            fieldNumClusterInstances.setEnabled(true);
            fieldNumClusterInstances.setText(Integer.toString(w.getDataManager().getNumExamples()));
        }
    }
    
    private void updateOutputParameters() {
        if (g.getOutputMode() == OutputMode.RANDOM) {
            //buttonGroupInputMode.setSelected(m, rootPaneCheckingEnabled);
            radioButtonOutputsRandom.setSelected(true);
        } else {
            radioButtonOutputsPresets.setSelected(true);
        }
    }
    
    private void buttonLoadNextModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLoadNextModelActionPerformed
        previousButtonHit = false;
        
        g.swapTrainingForDummy();
        
        buttonLoadPreviousModel.setEnabled(true);
        buttonLoadNextModel.setEnabled(false);
        
        updateInputParameters();
        updateOutputParameters();
        
        if (w.getSupervisedLearningManager().getLearningState() == SupervisedLearningManager.LearningState.TRAINING) {
            w.getSupervisedLearningManager().getSupervisedLearningController().cancelTrain();
            //w.getSupervisedLearningManager().cancelTraining();
        } else {
            w.getSupervisedLearningManager().getSupervisedLearningController().train();
            KadenzeLogging.getLogger().logGeneratorNextModel(w);
            buttonRun.setEnabled(true);
            labelStatus.setText("Next dataset loaded / Training completed. Press \"Run\" to run trained models.");
            //w.getSupervisedLearningManager().buildAll();
        }
    }//GEN-LAST:event_buttonLoadNextModelActionPerformed

    private void userUpdatedSlider() {
        //setSliderValueScaled(getSliderValueScaled());
        //updateLabelSliderValue((int) getSliderValueScaled());
    }
    
    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(DataGeneratorFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(DataGeneratorFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(DataGeneratorFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(DataGeneratorFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new DataGeneratorFrame().setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAdd;
    private javax.swing.JButton buttonDeleteInputs;
    private javax.swing.JButton buttonDeleteOutputs;
    private javax.swing.JButton buttonEditPresets;
    private javax.swing.ButtonGroup buttonGroupInputMode;
    private javax.swing.ButtonGroup buttonGroupOutputMode;
    private javax.swing.JButton buttonLoadNextModel;
    private javax.swing.JButton buttonLoadPreviousModel;
    private javax.swing.JButton buttonRecordInputs;
    private javax.swing.JButton buttonRecordOutputs;
    private javax.swing.JButton buttonRun;
    private javax.swing.JComboBox<String> comboClustering;
    private javax.swing.JTextField fieldNumClusterInstances;
    private javax.swing.JTextField fieldNumInstances;
    private javax.swing.JTextField fieldNumRandomInstances;
    private javax.swing.JFrame frameEditPresets;
    private javax.swing.JFrame frameOptions;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel labelNumInputExamples;
    private javax.swing.JLabel labelNumOutputPresets;
    private javax.swing.JLabel labelStatus;
    private javax.swing.JMenu menuOptions;
    private javax.swing.JPanel panelOutputSpecification;
    private javax.swing.JRadioButton radioButtonInputsCluster;
    private javax.swing.JRadioButton radioButtonInputsRandom;
    private javax.swing.JRadioButton radioButtonOutputsPresets;
    private javax.swing.JRadioButton radioButtonOutputsRandom;
    // End of variables declaration//GEN-END:variables

    private void updateLoadModelButtons() {
        if (previousButtonHit) {
            buttonLoadPreviousModel.setText("<html>Load previous<br>model (#<html>" + Integer.toString(modelID-2) + ")");
            buttonLoadNextModel.setText("<html>Load next<br>model (#<html>" + Integer.toString(modelID) + ")");
        } else {
            buttonLoadPreviousModel.setText("<html>Load previous<br>model (#<html>" + Integer.toString(modelID-1) + ")");
            buttonLoadNextModel.setText("<html>Load next<br>model (#<html>" + Integer.toString(modelID) + ")");
        }
    }
}

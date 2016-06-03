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
import wekimini.DataGenerator;
import wekimini.InputGenerator;
import wekimini.OutputGenerator;
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
        buttonAdd.setEnabled(false);
        buttonRecordOutputs.setEnabled(false);
        
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
        
        buttonDeleteTrainingInstances.setEnabled(false);
        buttonTrain.setEnabled(false);
        buttonSaveData.setEnabled(false);
        buttonRun.setEnabled(false);
        
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
            labelStatus.setText("Recording done.");
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
        labelNumOutputExamples.setText(Integer.toString(g.getNumOutputInstances()));
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
        jPanel1 = new javax.swing.JPanel();
        buttonRecordInputs = new javax.swing.JButton();
        buttonDeleteInputs = new javax.swing.JButton();
        radioButtonInputsRandom = new javax.swing.JRadioButton();
        radioButtonInputsCluster = new javax.swing.JRadioButton();
        labelNumInputExamples = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        comboClustering = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        radioButtonOutputsRandom = new javax.swing.JRadioButton();
        buttonRecordOutputs = new javax.swing.JButton();
        labelNumOutputExamples = new javax.swing.JLabel();
        buttonDeleteOutputs = new javax.swing.JButton();
        radioButtonOutputsPresets = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        buttonAdd = new javax.swing.JButton();
        fieldNumInstances = new javax.swing.JTextField();
        buttonTrain = new javax.swing.JButton();
        buttonRun = new javax.swing.JButton();
        buttonDeleteTrainingInstances = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        buttonSaveData = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        labelStatus = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Data Generator");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Inputs"));

        buttonRecordInputs.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        buttonRecordInputs.setText("Start recording");
        buttonRecordInputs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRecordInputsActionPerformed(evt);
            }
        });

        buttonDeleteInputs.setText("Delete");
        buttonDeleteInputs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteInputsActionPerformed(evt);
            }
        });

        buttonGroupInputMode.add(radioButtonInputsRandom);
        radioButtonInputsRandom.setSelected(true);
        radioButtonInputsRandom.setText("Select random instances");
        radioButtonInputsRandom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonInputsRandomActionPerformed(evt);
            }
        });

        buttonGroupInputMode.add(radioButtonInputsCluster);
        radioButtonInputsCluster.setText("Cluster");
        radioButtonInputsCluster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonInputsClusterActionPerformed(evt);
            }
        });

        labelNumInputExamples.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
        labelNumInputExamples.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelNumInputExamples.setText("0");

        jLabel2.setText("# recorded examples:");

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(radioButtonInputsRandom)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelNumInputExamples, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(buttonDeleteInputs, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addComponent(radioButtonInputsCluster)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(comboClustering, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(buttonRecordInputs, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(radioButtonInputsRandom)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radioButtonInputsCluster)
                    .addComponent(comboClustering, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(41, 41, 41)
                .addComponent(buttonRecordInputs, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonDeleteInputs)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelNumInputExamples)
                    .addComponent(jLabel2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Outputs"));

        buttonGroupOutputMode.add(radioButtonOutputsRandom);
        radioButtonOutputsRandom.setSelected(true);
        radioButtonOutputsRandom.setText("Generate random instances");
        radioButtonOutputsRandom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonOutputsRandomActionPerformed(evt);
            }
        });

        buttonRecordOutputs.setText("Add");
        buttonRecordOutputs.setToolTipText("");
        buttonRecordOutputs.setPreferredSize(new java.awt.Dimension(107, 28));
        buttonRecordOutputs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRecordOutputsActionPerformed(evt);
            }
        });

        labelNumOutputExamples.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
        labelNumOutputExamples.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelNumOutputExamples.setText("0");
        labelNumOutputExamples.setToolTipText("");

        buttonDeleteOutputs.setText("Delete");
        buttonDeleteOutputs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteOutputsActionPerformed(evt);
            }
        });

        buttonGroupOutputMode.add(radioButtonOutputsPresets);
        radioButtonOutputsPresets.setText("Select presets");
        radioButtonOutputsPresets.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonOutputsPresetsActionPerformed(evt);
            }
        });

        jLabel3.setText("# added examples:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonDeleteOutputs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonRecordOutputs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(radioButtonOutputsPresets)
                    .addComponent(radioButtonOutputsRandom)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelNumOutputExamples, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(radioButtonOutputsRandom)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonOutputsPresets)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addComponent(buttonRecordOutputs, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonDeleteOutputs)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(labelNumOutputExamples))
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Training data"));

        buttonAdd.setText("Add Examples");
        buttonAdd.setToolTipText("");
        buttonAdd.setMaximumSize(new java.awt.Dimension(107, 28));
        buttonAdd.setMinimumSize(new java.awt.Dimension(107, 28));
        buttonAdd.setPreferredSize(new java.awt.Dimension(107, 28));
        buttonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddActionPerformed(evt);
            }
        });

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

        buttonTrain.setText("Train");
        buttonTrain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonTrainActionPerformed(evt);
            }
        });

        buttonRun.setText("Run");
        buttonRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRunActionPerformed(evt);
            }
        });

        buttonDeleteTrainingInstances.setText("Delete training");
        buttonDeleteTrainingInstances.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteTrainingInstancesActionPerformed(evt);
            }
        });

        jLabel4.setText("# examples:");

        buttonSaveData.setText("Save data...");
        buttonSaveData.setToolTipText("");
        buttonSaveData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveDataActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(fieldNumInstances, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(buttonRun, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonTrain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonAdd, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonDeleteTrainingInstances, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonSaveData, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldNumInstances)
                    .addComponent(jLabel4)
                    .addComponent(buttonDeleteTrainingInstances))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonTrain, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonSaveData))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonRun, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        jLabel1.setText("Status:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
            
            setupMinMax();
            updateNumInputExamples(g.getNumInputInstances());
            buttonDeleteInputs.setEnabled(true);
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
        buttonRecordOutputs.setEnabled(false);
        buttonDeleteOutputs.setEnabled(false);
        g.deleteStoredOutputs(); // TODO: maybe "smooth" this operation?
        labelNumOutputExamples.setText("0");
        //fieldNumSamples.setEnabled(false);
    }//GEN-LAST:event_radioButtonOutputsRandomActionPerformed

    private void buttonDeleteInputsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteInputsActionPerformed
        g.deleteStoredInputs();
        labelNumInputExamples.setText("0");
        //labelNumExamples1.setText("0");
        buttonAdd.setEnabled(false);
        buttonDeleteInputs.setEnabled(false);
    }//GEN-LAST:event_buttonDeleteInputsActionPerformed

    private void radioButtonInputsRandomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonInputsRandomActionPerformed
        comboClustering.setEnabled(false);
        fieldNumInstances.setEnabled(true);
        g.setInputMode(InputGenerator.InputMode.RANDOM);
    }//GEN-LAST:event_radioButtonInputsRandomActionPerformed

    private void radioButtonInputsClusterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonInputsClusterActionPerformed
        comboClustering.setEnabled(true);
        if ( comboClustering.getSelectedIndex() == 0 ) {
            g.setInputMode(InputGenerator.InputMode.CLUSTERkm);
            fieldNumInstances.setEnabled(true);
        } else {
            g.setInputMode(InputGenerator.InputMode.CLUSTERem);
            fieldNumInstances.setEnabled(false);
        }
    }//GEN-LAST:event_radioButtonInputsClusterActionPerformed

    private void buttonRecordOutputsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRecordOutputsActionPerformed
        g.getOutputCurrentValues();
        updateNumOutputExamples();
        buttonDeleteOutputs.setEnabled(true);
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
        labelNumOutputExamples.setText(Integer.toString(0));
        buttonDeleteOutputs.setEnabled(false);
    }//GEN-LAST:event_buttonDeleteOutputsActionPerformed

    private void radioButtonOutputsPresetsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonOutputsPresetsActionPerformed
        g.setOutputMode(OutputGenerator.OutputMode.PRESET);
        buttonRecordOutputs.setEnabled(true);
        //fieldNumSamples.setEnabled(true);
    }//GEN-LAST:event_radioButtonOutputsPresetsActionPerformed

    private void buttonDeleteTrainingInstancesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteTrainingInstancesActionPerformed
        w.getDataManager().deleteAll();
        buttonDeleteTrainingInstances.setEnabled(false);
        buttonTrain.setEnabled(false);
        buttonRun.setEnabled(false);
        buttonSaveData.setEnabled(false);
    }//GEN-LAST:event_buttonDeleteTrainingInstancesActionPerformed

    private void buttonRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRunActionPerformed
        if (w.getSupervisedLearningManager().getRunningState() == SupervisedLearningManager.RunningState.NOT_RUNNING) {
            w.getSupervisedLearningManager().getSupervisedLearningController().startRun();
            buttonRun.setText("Stop running");
            buttonRun.setForeground(Color.RED);
            //  w.getSupervisedLearningManager().setRunningState(SupervisedLearningManager.RunningState.RUNNING);
        } else {
            w.getSupervisedLearningManager().getSupervisedLearningController().stopRun();
            buttonRun.setText("Run");
            buttonRun.setForeground(Color.BLACK);
            //w.getSupervisedLearningManager().setRunningState(SupervisedLearningManager.RunningState.NOT_RUNNING);
        }
    }//GEN-LAST:event_buttonRunActionPerformed

    private void buttonTrainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonTrainActionPerformed
        if (w.getSupervisedLearningManager().getLearningState() == SupervisedLearningManager.LearningState.TRAINING) {
            w.getSupervisedLearningManager().getSupervisedLearningController().cancelTrain();
            //w.getSupervisedLearningManager().cancelTraining();
        } else {
            w.getSupervisedLearningManager().getSupervisedLearningController().train();
            KadenzeLogging.getLogger().logGeneratorEvent(w, g);
            buttonRun.setEnabled(true);
            buttonSaveData.setEnabled(true);
            //w.getSupervisedLearningManager().buildAll();
        }
    }//GEN-LAST:event_buttonTrainActionPerformed

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
        //rg.setAsTrainingData(sliderModelValue.getValue());

        //g.setAsTrainingData(sliderModelValue.getValue());

        int numInstances = Integer.parseInt(fieldNumInstances.getText());
        g.setAsTrainingData(numInstances);
        buttonDeleteTrainingInstances.setEnabled(true);
        buttonTrain.setEnabled(true);
        buttonSaveData.setEnabled(true);
    }//GEN-LAST:event_buttonAddActionPerformed

    private void buttonSaveDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveDataActionPerformed
        if (w.getDataManager() != null) {

            File file = Util.findSaveFile("arff",
                    "data",
                    "arff file",
                    this);
            if (file != null) {
                try {
                    w.getDataManager().writeInstancesToArff(file);
                    /* if (WekinatorRunner.isLogging()) {
                     Plog.log(Msg.DATA_VIEWER_SAVE_ARFF_BUTTON, file.getAbsolutePath() + "/" + file.getName());
                     } */
                    // Util.setLastFile(SimpleDataset.getFileExtension(), file);
                } catch (Exception ex) {
                    Logger.getLogger(DatasetViewer.class.getName()).log(Level.WARNING, null, ex);
                    Util.showPrettyErrorPane(this, "Could not save to file: " + ex.getMessage());
                }
            }
        }
    }//GEN-LAST:event_buttonSaveDataActionPerformed

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
    private javax.swing.JButton buttonDeleteTrainingInstances;
    private javax.swing.ButtonGroup buttonGroupInputMode;
    private javax.swing.ButtonGroup buttonGroupOutputMode;
    private javax.swing.JButton buttonRecordInputs;
    private javax.swing.JButton buttonRecordOutputs;
    private javax.swing.JButton buttonRun;
    private javax.swing.JButton buttonSaveData;
    private javax.swing.JButton buttonTrain;
    private javax.swing.JComboBox<String> comboClustering;
    private javax.swing.JTextField fieldNumInstances;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel labelNumInputExamples;
    private javax.swing.JLabel labelNumOutputExamples;
    private javax.swing.JLabel labelStatus;
    private javax.swing.JRadioButton radioButtonInputsCluster;
    private javax.swing.JRadioButton radioButtonInputsRandom;
    private javax.swing.JRadioButton radioButtonOutputsPresets;
    private javax.swing.JRadioButton radioButtonOutputsRandom;
    // End of variables declaration//GEN-END:variables
}

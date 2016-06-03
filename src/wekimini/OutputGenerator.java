/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekimini;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import wekimini.osc.OSCMonitor;
import wekimini.osc.OSCMonitor.OSCReceiveState;
import wekimini.osc.OSCReceiver;

/**
 *
 * @author admin
 */
public class OutputGenerator implements GeneratesTrainingData {
    
    private final Wekinator w;
    private Instances storedOutputs = null;
    public Instances trainingOutputs;
    
    private OutputMode outputMode = OutputMode.RANDOM;
    public static final String PROP_INPUTMODE = "outputMode";
    
    private int nextID = 1;
    
    private static final int idIndex = 0;
    private static final int timestampIndex = 1;
    private static final int recordingRoundIndex = 2;
    
    private final int numInputs;
    private final int numOutputs;
    private static final int numMetaData = 3;
    
    private String[] outputNames;
    
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private ScheduledFuture scheduledFuture;
    
    private static final SimpleDateFormat prettyDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
    private static final Logger logger = Logger.getLogger(SupervisedLearningManager.class.getName());
    
    public static enum OutputMode {
        RANDOM, SAMPLES, PRESET
    };
    
    public OutputGenerator(Wekinator w) throws IOException, Exception {
        this.w = w;
        
        this.numInputs = w.getDataManager().getNumInputs();
        this.numOutputs = w.getDataManager().getNumOutputs();
        
        initializeInstances();
    }
    
    private void initializeInstances() {
        this.storedOutputs = w.getDataManager().getTrainingDataForOutput(0, true);
        this.storedOutputs.delete();
        
        this.trainingOutputs = w.getDataManager().getTrainingDataForOutput(0, true);
        this.trainingOutputs.delete();
        
        this.outputNames = w.getOutputManager().getOutputGroup().getOutputNames();
        for ( int j = 1; j < this.outputNames.length; j++ ) {
            this.storedOutputs.insertAttributeAt(new Attribute(this.outputNames[j]), this.storedOutputs.numAttributes());
            this.trainingOutputs.insertAttributeAt(new Attribute(this.outputNames[j]), this.trainingOutputs.numAttributes());
        }
    }
    
    public OutputMode getOutputMode() {
        return outputMode;
    }
    
    public void setOutputMode(OutputMode outputMode) {
        OutputMode oldOutputMode = this.outputMode;
        this.outputMode = outputMode;
        propertyChangeSupport.firePropertyChange(PROP_INPUTMODE, oldOutputMode, outputMode);
    }
    
    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    
    public void deleteStoredOutputs() {
        storedOutputs.delete();
    }
    
    public int getNumInstances() {
        return storedOutputs.numInstances();
    }
    
    public double[] getRecordedInputValues(int index) {
        double[] inputs = new double[numInputs];
        for (int i = 0 ; i < inputs.length ; i++) {
            inputs[i] = w.getDataManager().getInputValue(index, i);
        }
        return inputs;
    }
    
    public double[] getRecordedOutputValues(int index) {
        double[] outputs = new double[numOutputs];
        for (int i = 0 ; i < outputs.length ; i++) {
            outputs[i] = w.getDataManager().getOutputValue(index, i);
        }
        return outputs;
    }
    
    public void addToStorage(double[] inputs, double[] outputs, boolean[] recordingMask, int recordingRound) {
        
        int thisId = nextID;
        nextID++;

        double myVals[] = new double[numMetaData + numInputs + numOutputs];
        myVals[idIndex] = thisId;
        myVals[recordingRoundIndex] = recordingRound;

        Date now = new Date();
        //myVals[timestampIndex] = Double.parseDouble(dateFormat.format(now)); //Error: This gives us scientific notation!

        String pretty = prettyDateFormat.format(now);
        try {
            myVals[timestampIndex] = storedOutputs.attribute(timestampIndex).parseDate(pretty);
            //myVals[timestampIndex] =
        } catch (ParseException ex) {
            myVals[timestampIndex] = 0;
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        /*for (int i = 0; i < numInputs; i++) {
         myVals[numMetaData + i] = featureVals[i];
         } */
        System.arraycopy(inputs, 0, myVals, numMetaData, inputs.length); //TODO DOUBLECHECK


        /*for (int i = 0; i < numParams; i++) {
         if (isParamDiscrete[i] && (paramVals[i] < 0 || paramVals[i] >= numParamValues[i])) {
         throw new IllegalArgumentException("Invalid value for this discrete parameter");
         }

         myVals[numMetaData + numFeatures + i] = paramVals[i];
         } */
        System.arraycopy(outputs, 0, myVals, numMetaData + numInputs, outputs.length);

        Instance in = new Instance(1.0, myVals);
        for (int i = 0; i < recordingMask.length; i++) {
            if (!recordingMask[i]) {
                in.setMissing(numMetaData + numInputs + i);
            } else {
                w.getDataManager().setNumExamplesPerOutput(i, w.getDataManager().getNumExamplesPerOutput(i) + 1);
                // outputInstanceCounts[i]++;
            }
        }
        in.setDataset(storedOutputs);
        storedOutputs.add(in);
        //setHasInstances(true);
        //fireStateChanged();
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public Instances getStoredOutputs() {
        return storedOutputs;
    }
    
    public void addToTrainingOutputs(double[] inputs, double[] outputs, boolean[] recordingMask, int recordingRound) {
        
        int thisId = nextID;
        nextID++;

        double myVals[] = new double[numMetaData + numInputs + numOutputs];
        myVals[idIndex] = thisId;
        myVals[recordingRoundIndex] = recordingRound;

        Date now = new Date();
        //myVals[timestampIndex] = Double.parseDouble(dateFormat.format(now)); //Error: This gives us scientific notation!

        String pretty = prettyDateFormat.format(now);
        try {
            myVals[timestampIndex] = trainingOutputs.attribute(timestampIndex).parseDate(pretty);
            //myVals[timestampIndex] =
        } catch (ParseException ex) {
            myVals[timestampIndex] = 0;
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        /*for (int i = 0; i < numInputs; i++) {
         myVals[numMetaData + i] = featureVals[i];
         } */
        System.arraycopy(inputs, 0, myVals, numMetaData, inputs.length); //TODO DOUBLECHECK


        /*for (int i = 0; i < numParams; i++) {
         if (isParamDiscrete[i] && (paramVals[i] < 0 || paramVals[i] >= numParamValues[i])) {
         throw new IllegalArgumentException("Invalid value for this discrete parameter");
         }

         myVals[numMetaData + numFeatures + i] = paramVals[i];
         } */
        System.arraycopy(outputs, 0, myVals, numMetaData + numInputs, outputs.length);

        Instance in = new Instance(1.0, myVals);
        for (int i = 0; i < recordingMask.length; i++) {
            if (!recordingMask[i]) {
                in.setMissing(numMetaData + numInputs + i);
            } else {
                w.getDataManager().setNumExamplesPerOutput(i, w.getDataManager().getNumExamplesPerOutput(i) + 1);
                // outputInstanceCounts[i]++;
            }
        }
        in.setDataset(trainingOutputs);
        trainingOutputs.add(in);
        //setHasInstances(true);
        //fireStateChanged();
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void storeRecordingRound(int whichRound) {
        int lastIndex = w.getDataManager().getNumExamplesInRound(whichRound) - 1;
        int numExamples = w.getDataManager().getNumExamples() - 1;
        int firstIndex = numExamples - lastIndex;
        
        int storedRecordingRound = w.getSupervisedLearningManager().getRecordingRound();
        
        for ( int i = 0 ; i < lastIndex + 1 ; i++ ) {
            double[] inputs = getRecordedInputValues(firstIndex + i);
            double[] outputs = getRecordedOutputValues(firstIndex + i);
            //w.getOutputManager().randomizeAllOutputs();
            //double[] outputs = w.getOutputManager().getCurrentValues();
            
            boolean[] mask = new boolean[numOutputs];
            for( int j = 0 ; j < mask.length ; j++ ) {
                mask[j] = true;
            }
            addToStorage(inputs, outputs, mask, storedRecordingRound);
        }
    }
    
    public void deleteRecordingRound(int whichRound) {
        w.getDataManager().deleteTrainingRound(whichRound);
    }
    
    public void getCurrentValues() {
        int storedRecordingRound = w.getSupervisedLearningManager().getRecordingRound();
        double[] inputs = new double[numInputs];
        double[] outputs = w.getOutputManager().getCurrentValues();

        boolean[] mask = new boolean[numOutputs];
        for ( int j = 0 ; j < mask.length ; j++ ) {
            mask[j] = true;
        }
        
        addToStorage(inputs, outputs, mask, storedRecordingRound);
    }
    
    @Override
    public void getRecordedStream(int whichRound) {
        whichRound = w.getDataManager().getMaxRecordingRound();
        storeRecordingRound(whichRound);
        deleteRecordingRound(whichRound);        
    }
    
    public void selectRandomOutputs(int numInstances) {
        storedOutputs.delete();
        int storedRecordingRound = w.getSupervisedLearningManager().getRecordingRound();
        
        for ( int i = 0 ; i < numInstances ; i++ ) {
            double[] inputs = new double[numInputs];
            w.getOutputManager().randomizeAllOutputs();
            double[] outputs = w.getOutputManager().getCurrentValues();
            
            boolean[] mask = new boolean[numOutputs];
            for( int j = 0 ; j < mask.length ; j++ ) {
                mask[j] = true;
            }
            addToStorage(inputs, outputs, mask, storedRecordingRound);
            addToTrainingOutputs(inputs, outputs, mask, storedRecordingRound);
        }
    }
    
    public void selectPresets(int numInstances) {
        int storedRecordingRound = w.getSupervisedLearningManager().getRecordingRound();
        double[] inputs = new double[numInputs];
        double[] outputs = new double[numOutputs];
        boolean[] mask = new boolean[numOutputs];
            for( int j = 0 ; j < mask.length ; j++ ) {
                mask[j] = true;
            }
        int numPresets = storedOutputs.numInstances();
        int index;
        
        for ( int i = 0 ; i < numInstances ; i++ ) {
            if ( i >= numPresets ) {
                index = i % numPresets;
            } else {
                index = i;
            }
            
            for (int j = 0 ; j < numOutputs ; j++ ) {
                outputs[j] = storedOutputs.instance(index).value(numMetaData + numInputs + j);
            }
            addToTrainingOutputs(inputs, outputs, mask, storedRecordingRound);
        }
    }
    
    public void selectSamples(int numInstances) {
        int storedRecordingRound = w.getSupervisedLearningManager().getRecordingRound();
        
        for ( int i = 0 ; i < numInstances ; i++ ) {
            int whichSample = i+1;
//            while (whichSample > numSamples ) {
//                whichSample = whichSample - numSamples;
//            }
            
            double[] inputs = new double[numInputs];
            double[] outputs = new double[numOutputs];
            
            for ( int j = 0 ; j < outputs.length ; j++ ) {
                outputs[j] = whichSample;
            }
            
            boolean[] mask = new boolean[numOutputs];
            for ( int j = 0 ; j < mask.length ; j++ ) {
                mask[j] = true;
            }
            addToTrainingOutputs(inputs, outputs, mask, storedRecordingRound);
        }
    }
    
    public Instances getTrainingOutputs() {
        return trainingOutputs;
    }
    
    public void deleteTrainingOutputs() {
        trainingOutputs.delete();
    }
    
    @Override
    public void setAsTrainingData(int numInstances) {
        if (outputMode == OutputMode.RANDOM) {
            selectRandomOutputs(numInstances);
        } else if (outputMode == OutputMode.PRESET) {
            selectPresets(numInstances);
        } else if (outputMode == OutputMode.SAMPLES) {
            selectSamples(numInstances);
        }
    }
}

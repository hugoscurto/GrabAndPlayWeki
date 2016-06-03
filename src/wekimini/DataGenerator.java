/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekimini;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import wekimini.InputGenerator.InputMode;
import wekimini.OutputGenerator.OutputMode;

/**
 *
 * @author admin
 */
public class DataGenerator implements GeneratesTrainingData {
    
    private final Wekinator w;
    private final InputGenerator i;
    private final OutputGenerator o;
    
    private Instances trainingInputs = null;
    private Instances trainingOutputs = null;
    
    private int dummyRecordingRound;
    
    private final int numInputs;
    private final int numOutputs;
    private static final int numMetaData = 3;
    
    private String[] outputNames;
    
    private static final Logger logger = Logger.getLogger(SupervisedLearningManager.class.getName());
    
    public DataGenerator(Wekinator w) throws IOException, Exception {
        this.w = w;
        this.i = new InputGenerator(w);
        this.o = new OutputGenerator(w);
        
        this.numInputs = w.getDataManager().getNumInputs();
        this.numOutputs = w.getDataManager().getNumOutputs();
        
        initializeInstances();
    }
    
    private void initializeInstances() {
        this.trainingInputs = w.getDataManager().getTrainingDataForOutput(0, true);
        this.trainingInputs.delete();
        
        this.trainingOutputs = w.getDataManager().getTrainingDataForOutput(0, true);
        this.trainingOutputs.delete();

        this.outputNames = w.getOutputManager().getOutputGroup().getOutputNames();
        for ( int j = 1; j < this.outputNames.length; j++ ) {
            this.trainingInputs.insertAttributeAt(new Attribute(this.outputNames[j]), this.trainingInputs.numAttributes());
            this.trainingOutputs.insertAttributeAt(new Attribute(this.outputNames[j]), this.trainingOutputs.numAttributes());
        }
    }
    
    public Instances getStoredInputs() {
        return i.getStoredInputs();
    }
    
    public Instances getStoredOutputs() {
        return o.getStoredOutputs();
    }
    
    public InputMode getInputMode() {
        return i.getInputMode();
    }
    
    public OutputMode getOutputMode() {
        return o.getOutputMode();
    }
    
    public void setInputMode(InputMode inputMode) {
        i.setInputMode(inputMode);
    }
    
    public void setOutputMode(OutputMode outputMode) {
        o.setOutputMode(outputMode);
    }
    
    public void setTrainingInputs(int numInstances) {
        i.setAsTrainingData(numInstances);
    }
    
    public void setTrainingOutputs(int numInstances) {
        o.setAsTrainingData(numInstances);
    }
    
    public void deleteStoredInputs() {
        i.deleteStoredInputs();
    }
    
    public void deleteStoredOutputs() {
        o.deleteStoredOutputs();
    }
    
    public int getNumInputInstances() {
        return i.getNumInstances();
    }
    
    public int getNumOutputInstances() {
        return o.getNumInstances();
    }
    
    public void getInputStream(int whichRound) {
        whichRound = w.getDataManager().getMaxRecordingRound();
        i.getRecordedStream(whichRound);
    }
    
    public void getOutputStream(int whichRound) {
        whichRound = w.getDataManager().getMaxRecordingRound();
        o.getRecordedStream(whichRound);
    }
    
    public void getOutputCurrentValues() {
        o.getCurrentValues();
    }
    
    @Override
    public void getRecordedStream(int whichRound) {
        //nothing?
    }
    
    public double getInputValue(int index, int whichInput) {
        Instance in = trainingInputs.instance(index);
        if (in == null || in.numAttributes() < (whichInput + numMetaData)) {
            return Double.NaN;
        }
        return in.value(whichInput + numMetaData);
    }
    
    public double[] getInputValues(int index) {
        double[] inputs = new double[numInputs];
        for (int j = 0 ; j < inputs.length ; j++) {
            inputs[j] = getInputValue(index, j);
        }
        return inputs;
    }
    
    public double getOutputValue(int index, int whichOutput) {
        Instance in = trainingOutputs.instance(index);
        if (in == null || in.numAttributes() < (whichOutput + numInputs + numMetaData)) {
            return Double.NaN;
        }
        return in.value(whichOutput + numInputs + numMetaData);
    }
    
    public double[] getOutputValues(int index) {
        double[] outputs = new double[numOutputs];
        for (int j = 0 ; j < outputs.length ; j++) {
            outputs[j] = getOutputValue(index, j);
        }
        return outputs;
    }
    
    @Override
    public void setAsTrainingData(int numInstances) {
        setTrainingInputs(numInstances);
        if ( i.getInputMode() == InputMode.CLUSTERem ) {
            numInstances = i.getNumClusters();
        }
        System.out.println(numInstances);
        setTrainingOutputs(numInstances);
        
        trainingInputs = i.getTrainingInputs();
        int inputsNumInstances = trainingInputs.numInstances();
        String inputString = Integer.toString(inputsNumInstances);
        logger.log(Level.SEVERE, "Input number instances:{0}", inputString);
        
        trainingOutputs = o.getTrainingOutputs();
        int outputsNumInstances = trainingOutputs.numInstances();
        String outputString = Integer.toString(outputsNumInstances);
        logger.log(Level.SEVERE, "Output number instances:{0}", outputString);
        
        dummyRecordingRound++;
        
        for ( int j = 0 ; j < numInstances ; j++ ) {
            logger.log(Level.SEVERE, "Into the loop...");
            double[] inputs = getInputValues(j);
            logger.log(Level.SEVERE, "Input Values OK!");
            double[] outputs = getOutputValues(j);
            logger.log(Level.SEVERE, "Output Values OK!");
            
            boolean[] mask = new boolean[numOutputs];
            for ( int k = 0 ; k < mask.length ; k++ ) {
                mask[k] = true;
            }
            logger.log(Level.SEVERE, "Mask OK!");
            
            w.getDataManager().addToTraining(inputs, outputs, mask, dummyRecordingRound);
            logger.log(Level.SEVERE, "Added to Training!");
        }
        i.deleteTrainingInputs();
        o.deleteTrainingOutputs();
        trainingInputs.delete();
        trainingOutputs.delete();
    }
}
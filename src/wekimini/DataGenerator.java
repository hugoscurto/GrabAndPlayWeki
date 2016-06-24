/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekimini;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private Instances dummyInstances = null;
    private Instances tempInstances = null;
    
    private int dummyRecordingRound;
    
    private int nextID = 1;
    
    private static final int idIndex = 0;
    private static final int timestampIndex = 1;
    private static final int recordingRoundIndex = 2;
    
    private final int numInputs;
    private final int numOutputs;
    private static final int numMetaData = 3;
    
    public InputMode trainingInputMode;
    public OutputMode trainingOutputMode;
    public InputMode dummyInputMode;
    public OutputMode dummyOutputMode;
    public InputMode tempInputMode;
    public OutputMode tempOutputMode;
    
    public int numTrainingInstances;
    
    private String[] outputNames;
    
    private static final SimpleDateFormat prettyDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
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
        
        this.dummyInstances = w.getDataManager().getTrainingDataForOutput(0, true);
        this.dummyInstances.delete();
        
        this.tempInstances = w.getDataManager().getTrainingDataForOutput(0, true);
        this.tempInstances.delete();

        this.outputNames = w.getOutputManager().getOutputGroup().getOutputNames();
        for ( int j = 1; j < this.outputNames.length; j++ ) {
            this.trainingInputs.insertAttributeAt(new Attribute(this.outputNames[j]), this.trainingInputs.numAttributes());
            this.trainingOutputs.insertAttributeAt(new Attribute(this.outputNames[j]), this.trainingOutputs.numAttributes());
            this.dummyInstances.insertAttributeAt(new Attribute(this.outputNames[j]), this.dummyInstances.numAttributes());
            this.tempInstances.insertAttributeAt(new Attribute(this.outputNames[j]), this.tempInstances.numAttributes());
        }
    }
    
    private void addToTempInstances(double[] inputs, double[] outputs, boolean[] recordingMask, int recordingRound) {
        int thisId = nextID;
        nextID++;

        double myVals[] = new double[numMetaData + numInputs + numOutputs];
        myVals[idIndex] = thisId;
        myVals[recordingRoundIndex] = recordingRound;

        Date now = new Date();
        //myVals[timestampIndex] = Double.parseDouble(dateFormat.format(now)); //Error: This gives us scientific notation!

        String pretty = prettyDateFormat.format(now);
        try {
            myVals[timestampIndex] = trainingInputs.attribute(timestampIndex).parseDate(pretty);
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
        in.setDataset(tempInstances);
        tempInstances.add(in);
        //setHasInstances(true);
        //fireStateChanged();
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void addToDummyInstances(double[] inputs, double[] outputs, boolean[] recordingMask, int recordingRound) {
        int thisId = nextID;
        nextID++;

        double myVals[] = new double[numMetaData + numInputs + numOutputs];
        myVals[idIndex] = thisId;
        myVals[recordingRoundIndex] = recordingRound;

        Date now = new Date();
        //myVals[timestampIndex] = Double.parseDouble(dateFormat.format(now)); //Error: This gives us scientific notation!

        String pretty = prettyDateFormat.format(now);
        try {
            myVals[timestampIndex] = trainingInputs.attribute(timestampIndex).parseDate(pretty);
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
        in.setDataset(dummyInstances);
        dummyInstances.add(in);
        //setHasInstances(true);
        //fireStateChanged();
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void updateTrainingInfo() {
        trainingInputMode = i.getInputMode();
        trainingOutputMode = o.getOutputMode();
        numTrainingInstances = w.getDataManager().getNumExamples();
    }
    
    public void addTrainingToDummy(int numTrainingInstances) {
        for ( int n = 0 ; n < numTrainingInstances ; n++ ) {
            double[] inputs = getTrainingInputValues(n);
            logger.log(Level.SEVERE, "Input Values OK!");
            double[] outputs = getTrainingOutputValues(n);
            logger.log(Level.SEVERE, "Output Values OK!");

            boolean[] mask = new boolean[numOutputs];
            for ( int k = 0 ; k < mask.length ; k++ ) {
                mask[k] = true;
            }
            addToDummyInstances(inputs, outputs, mask, dummyRecordingRound);
        }
        dummyInputMode = trainingInputMode;
        dummyOutputMode = trainingOutputMode;
    }
    
    public void addDummyToTemp(int numDummyInstances) {
        for ( int n = 0 ; n < numDummyInstances ; n++ ) {
            double[] inputs = getDummyInputValues(n);
            logger.log(Level.SEVERE, "Input Values OK!");
            double[] outputs = getDummyOutputValues(n);
            logger.log(Level.SEVERE, "Output Values OK!");

            boolean[] mask = new boolean[numOutputs];
            for ( int k = 0 ; k < mask.length ; k++ ) {
                mask[k] = true;
            }
            addToTempInstances(inputs, outputs, mask, dummyRecordingRound);
        }
        
        tempInputMode = dummyInputMode;
        tempOutputMode = dummyOutputMode;
    }
    
    public void addTempToTraining(int numTempInstances) {
        for ( int n = 0 ; n < numTempInstances ; n++ ) {
            double[] inputs = getTempInputValues(n);
            logger.log(Level.SEVERE, "Input Values OK!");
            double[] outputs = getTempOutputValues(n);
            logger.log(Level.SEVERE, "Output Values OK!");

            boolean[] mask = new boolean[numOutputs];
            for ( int k = 0 ; k < mask.length ; k++ ) {
                mask[k] = true;
            }
            w.getDataManager().addToTraining(inputs, outputs, mask, dummyRecordingRound);
        }
        
        trainingInputMode = tempInputMode;
        i.setInputMode(trainingInputMode);
        trainingOutputMode = tempOutputMode;
        o.setOutputMode(trainingOutputMode);
    }
    
    public void swapTrainingForDummy() {
        int numDummyInstances = dummyInstances.numInstances();
        addDummyToTemp(numDummyInstances);
        dummyInstances.delete();
        
        int numTrainingInstances = w.getDataManager().getNumExamples();
        addTrainingToDummy(numTrainingInstances);
        w.getDataManager().deleteAll();
        
        int numTempInstances = tempInstances.numInstances();
        addTempToTraining(numTempInstances);
        tempInstances.delete();
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
    
    public double[] getTrainingInputValues(int index) {
        double[] inputs = new double[numInputs];
        for (int j = 0 ; j < inputs.length ; j++) {
            inputs[j] = w.getDataManager().getInputValue(index, j);
        }
        return inputs;
    }
    
    public double[] getTrainingOutputValues(int index) {
        double[] outputs = new double[numOutputs];
        for (int j = 0 ; j < outputs.length ; j++) {
            outputs[j] = w.getDataManager().getOutputValue(index, j);
        }
        return outputs;
    }
    
    public double getDummyInputValue(int index, int whichInput) {
        Instance in = dummyInstances.instance(index);
        if (in == null || in.numAttributes() < (whichInput + numMetaData)) {
            return Double.NaN;
        }
        return in.value(whichInput + numMetaData);
    }
    
    public double[] getDummyInputValues(int index) {
        double[] inputs = new double[numInputs];
        for (int j = 0 ; j < inputs.length ; j++) {
            inputs[j] = getDummyInputValue(index, j);
        }
        return inputs;
    }
    
    public double getDummyOutputValue(int index, int whichOutput) {
        Instance in = dummyInstances.instance(index);
        if (in == null || in.numAttributes() < (whichOutput + numInputs + numMetaData)) {
            return Double.NaN;
        }
        return in.value(whichOutput + numInputs + numMetaData);
    }
    
    public double[] getDummyOutputValues(int index) {
        double[] outputs = new double[numOutputs];
        for (int j = 0 ; j < outputs.length ; j++) {
            outputs[j] = getDummyOutputValue(index, j);
        }
        return outputs;
    }
    
    public double getTempInputValue(int index, int whichInput) {
        Instance in = tempInstances.instance(index);
        if (in == null || in.numAttributes() < (whichInput + numMetaData)) {
            return Double.NaN;
        }
        return in.value(whichInput + numMetaData);
    }
    
    public double[] getTempInputValues(int index) {
        double[] inputs = new double[numInputs];
        for (int j = 0 ; j < inputs.length ; j++) {
            inputs[j] = getTempInputValue(index, j);
        }
        return inputs;
    }
    
    public double getTempOutputValue(int index, int whichOutput) {
        Instance in = tempInstances.instance(index);
        if (in == null || in.numAttributes() < (whichOutput + numInputs + numMetaData)) {
            return Double.NaN;
        }
        return in.value(whichOutput + numInputs + numMetaData);
    }
    
    public double[] getTempOutputValues(int index) {
        double[] outputs = new double[numOutputs];
        for (int j = 0 ; j < outputs.length ; j++) {
            outputs[j] = getTempOutputValue(index, j);
        }
        return outputs;
    }
    
    public boolean isDummyEmpty() {
        int numDummyInstances = dummyInstances.numInstances();
        if (numDummyInstances != 0) {
            return false;
        } else {
            return true;
        }
    }
    
    @Override
    public void setAsTrainingData(int numInstances) {
        int numTrainingInstances = w.getDataManager().getNumExamples();
        if (numTrainingInstances != 0) {
            dummyInstances.delete();
            addTrainingToDummy(numTrainingInstances);
        }        
        w.getDataManager().deleteAll();
        
        updateTrainingInfo();
        
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
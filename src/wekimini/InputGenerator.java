/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekimini;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.EM;
import weka.clusterers.HierarchicalClusterer;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.Reorder;

/**
 *
 * @author admin
 */
public class InputGenerator implements GeneratesTrainingData {
    
    private final Wekinator w;
    private Instances storedInputs = null;
    public Instances dataset = null;
    public Instances clusters = null;
    public Instances trainingInputs;
    
    private InputMode inputMode = InputMode.RANDOM;
    public static final String PROP_INPUTMODE = "inputMode";
    
    private int nextID = 1;
    
    private static final int idIndex = 0;
    private static final int timestampIndex = 1;
    private static final int recordingRoundIndex = 2;
    
    private final int numInputs;
    private final int numOutputs;
    private static final int numMetaData = 3;
    
    public int numClusters;
    
    public double[] storedMin;
    public double[] storedMax;
    
    private String[] outputNames;
    
    private static final SimpleDateFormat prettyDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
    private static final Logger logger = Logger.getLogger(SupervisedLearningManager.class.getName());
    
    public static enum InputMode {
        RANDOM, CLUSTERkm, CLUSTERem, NEW
    };
    
    public InputGenerator(Wekinator w) throws IOException, Exception {
        this.w = w;
        
        this.numInputs = w.getDataManager().getNumInputs();
        this.numOutputs = w.getDataManager().getNumOutputs();
        
        this.storedMin = new double[numInputs];
        this.storedMax = new double[numInputs];
        
        initializeInstances();
    }
    
    private void initializeInstances() {
        this.storedInputs = w.getDataManager().getTrainingDataForOutput(0, true);
        this.storedInputs.delete();
        
        this.trainingInputs = w.getDataManager().getTrainingDataForOutput(0, true);
        this.trainingInputs.delete();

        this.outputNames = w.getOutputManager().getOutputGroup().getOutputNames();
        for ( int j = 1; j < this.outputNames.length; j++ ) {
            this.storedInputs.insertAttributeAt(new Attribute(this.outputNames[j]), this.storedInputs.numAttributes());
            this.trainingInputs.insertAttributeAt(new Attribute(this.outputNames[j]), this.trainingInputs.numAttributes());
        }
    }
    
    public InputMode getInputMode() {
        return inputMode;
    }
    
    public void setInputMode(InputMode inputMode) {
        InputMode oldInputMode = this.inputMode;
        this.inputMode = inputMode;
        propertyChangeSupport.firePropertyChange(PROP_INPUTMODE, oldInputMode, inputMode);
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
    
    public void deleteStoredInputs() {
        storedInputs.delete();
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
    
    public int getNumInstances() {
        return storedInputs.numInstances();
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
            myVals[timestampIndex] = storedInputs.attribute(timestampIndex).parseDate(pretty);
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
        in.setDataset(storedInputs);
        storedInputs.add(in);
        //setHasInstances(true);
        //fireStateChanged();
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public Instances getStoredInputs() {
        return storedInputs;
    }
    
    public void addToTraining(double[] inputs, double[] outputs, boolean[] recordingMask, int recordingRound) {
        
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
        in.setDataset(trainingInputs);
        trainingInputs.add(in);
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
    
    public void setStoredStats() {
        for ( int i = 0 ; i < numInputs ; i++ ) {
            int index = i + numMetaData;
            storedMin[i] = storedInputs.attributeStats(index).numericStats.min;
            storedMax[i] = storedInputs.attributeStats(index).numericStats.max;
        }
    }
    
    public void buildDataset() throws Exception {
        int storedNumAttributes = storedInputs.numAttributes();
        String storedString = Integer.toString(storedNumAttributes);
        logger.log(Level.SEVERE, "storedInputs number attributes:{0}", storedString);
        
        int storedNumInstances = storedInputs.numInstances();
        String storedString2 = Integer.toString(storedNumInstances);
        logger.log(Level.SEVERE, "storedInputs number instances:{0}", storedString2);
        
        Reorder r = new Reorder();
        int[] reordering = new int[numInputs];
        
        for ( int i = 0 ; i < numInputs ; i++ ) {
            reordering[i] = i + numMetaData;
        }
        
        r.setAttributeIndicesArray(reordering);
        r.setInputFormat(storedInputs);
        
        dataset = Filter.useFilter(storedInputs, r);
        
        int datasetNumAttributes = dataset.numAttributes();
        String datasetString = Integer.toString(datasetNumAttributes);
        logger.log(Level.SEVERE, "Dataset number attributes:{0}", datasetString);
        
        int datasetNumInstances = dataset.numInstances();
        String datasetString2 = Integer.toString(datasetNumInstances);
        logger.log(Level.SEVERE, "Dataset number instances:{0}", datasetString2);
        
    }
    
    public void addKmClustersToTraining() {
        int storedRecordingRound = w.getSupervisedLearningManager().getRecordingRound();
        numClusters = clusters.numInstances();
        
        for ( int i = 0 ; i < numClusters ; i++ ) {
            double[] inputs = getClusterInputValues(i);
            double[] outputs = new double[numOutputs];
            
            boolean[] mask = new boolean[numOutputs];
            for( int j = 0 ; j < mask.length ; j++ ) {
                mask[j] = true;
            }
            
            addToTraining(inputs, outputs, mask, storedRecordingRound);
        }
    }
    
    public void addEmClustersToTraining(double[][][] clusterAtts) {
        int storedRecordingRound = w.getSupervisedLearningManager().getRecordingRound();
        double[] inputs = new double[numInputs];
        double[] outputs = new double[numOutputs]; 
        boolean[] mask = new boolean[numOutputs];
        for( int j = 0 ; j < mask.length ; j++ ) {
            mask[j] = true;
        }
        
        for ( int i = 0 ; i < numClusters ; i++ ) {
            for ( int j = 0 ; j < numInputs ; j++ ) {
                inputs[j] = clusterAtts[i][j][0];
            }
            addToTraining(inputs, outputs, mask, storedRecordingRound);
        }
    }
    
    public double getClusterInputValue(int index, int whichInput) {
        Instance in = clusters.instance(index);
        if (in == null || in.numAttributes() < (whichInput)) {
            return Double.NaN;
        }
        return in.value(whichInput);
    }
    
    public double[] getClusterInputValues(int index) {
        double[] inputs = new double[numInputs];
        for (int j = 0 ; j < inputs.length ; j++) {
            inputs[j] = getClusterInputValue(index, j);
        }
        return inputs;
    }
    
    public int getNumClusters() {
        return numClusters;
    }
    
    
    @Override
    public void getRecordedStream(int whichRound) {
        whichRound = w.getDataManager().getMaxRecordingRound();
        storeRecordingRound(whichRound);
        deleteRecordingRound(whichRound);
    }
    
    public int getRandomInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
    
    public void selectRandomInputs(int numRandomInstances) {
        int numInstances = storedInputs.numInstances();
        for (int i = 0 ; i < numRandomInstances ; i++) {
            int randIndex = getRandomInt(0, numInstances - 1);
            Instance tempInstance = storedInputs.instance(randIndex);
            
            trainingInputs.add(tempInstance);
            //setHasInstances(true);
            //fireStateChanged();
        }
    }
    
    public void selectKmClusters(int numClusters) throws Exception {
        buildDataset();
        
        SimpleKMeans km = new SimpleKMeans();
        km.setNumClusters(numClusters);
        km.buildClusterer(dataset);
        
        clusters = km.getClusterCentroids();
        
        addKmClustersToTraining();
    }
    
    public void selectEmClusters() throws Exception { 
        String[] options = new String[2];
        options[0] = "-I";
        options[1] = "100";
        
        buildDataset();
        
        EM clusterer = new EM();
        clusterer.setOptions(options);
        clusterer.buildClusterer(dataset);
        
        ClusterEvaluation eval = new ClusterEvaluation();
        eval.setClusterer(clusterer);
        eval.evaluateClusterer(dataset);
        System.out.println(eval.clusterResultsToString());
        
        double[][][] clusterAtts = clusterer.getClusterModelsNumericAtts();
        System.out.println(Arrays.deepToString(clusterAtts));
        
        numClusters = clusterer.numberOfClusters();
        System.out.println(numClusters);
        
        addEmClustersToTraining(clusterAtts);
    }
    
    public void selectHiClusters() throws Exception { 
        buildDataset();
        
        HierarchicalClusterer clusterer = new HierarchicalClusterer();
        clusterer.setLinkType(new SelectedTag(1, HierarchicalClusterer.TAGS_LINK_TYPE));

        
        clusterer.buildClusterer(dataset);
        
        logger.log(Level.SEVERE, "EM options: ", clusterer.getOptions());
        logger.log(Level.SEVERE, "EM numCluster tip text: ", clusterer.numClustersTipText());
        logger.log(Level.SEVERE, "EM debug tip text: ", clusterer.debugTipText());
        logger.log(Level.SEVERE, "EM info: ", clusterer.globalInfo());
        
        int num = clusterer.numberOfClusters();
        String txt = Integer.toString(num);
        logger.log(Level.SEVERE, "EM numberOfClusters: ", txt);
        
        String text = Integer.toString(numClusters);
        logger.log(Level.SEVERE, "EM numClusters: ", text);
    }
    
    public void selectNewInputs(int numNewInstances) {
        setStoredStats();
        
        for ( int j = 0 ; j < numNewInstances ; j++ ) {
            Instance tempInstance = storedInputs.firstInstance();
            
            for ( int i = 0 ; i < numInputs ; i++ ) {
                int index = i + numMetaData;
                double value = storedMin[i] + ((storedMax[i] - storedMin[i])/(numNewInstances - 1))*j;
                tempInstance.setValue(index, value);
            }
            trainingInputs.add(tempInstance);
        }
    }
    
    public Instances getTrainingInputs() {
        return trainingInputs;
    }
    
    public void deleteTrainingInputs() {
        trainingInputs.delete();
    }
    
    @Override
    public void setAsTrainingData(int numInstances) {
        if (inputMode == InputMode.RANDOM) {
            selectRandomInputs(numInstances);
        } else if (inputMode == InputMode.CLUSTERkm) {
            try {
                selectKmClusters(numInstances);
            } catch (Exception ex) {
                Logger.getLogger(InputGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (inputMode == InputMode.CLUSTERem) {
            try {
                selectEmClusters();
                //selectHiClusters();
            } catch (Exception ex) {
                Logger.getLogger(InputGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (inputMode == InputMode.NEW) {
            selectNewInputs(numInstances);
        }
    }
}
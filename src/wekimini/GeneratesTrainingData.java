/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekimini;

/**
 *
 * @author admin
 */
public interface GeneratesTrainingData {
    
    public void getRecordedStream(int whichRound);
    
    public void setAsTrainingData(int numInstances);
    
}

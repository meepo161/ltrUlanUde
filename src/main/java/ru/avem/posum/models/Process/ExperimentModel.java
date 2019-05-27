package ru.avem.posum.models.Process;

public class ExperimentModel extends Thread{
    long testId = 0;
    GraphModel graphModel;
    private boolean doStop = false;
    private boolean bRun = false;
    double tick = 0;
    double tickGlobal = 0;
    long delta = 10L;
    double deltaX = 0.01;
    double range = 100;


    public synchronized void terminate() {
        this.doStop = true;
    }

    private synchronized boolean keepRunning() {
        return this.doStop == false;
    }

    public void run(){
    }

    public Boolean getRun() {
        return !bRun;
    }

    public void setGraphModel(GraphModel graphModel) {
        this.graphModel = graphModel;
    }

    public void SetTestId(long testId){
        this.DeInit();
        this.testId = testId;

    }

    public void DeInit(){
        bRun = false;
        this.terminate();
    }

    public void Init(){
        if(!this.isAlive()) {
            this.start();
        }
        doStop = false;
    }

    public void Run(){
        bRun = true;
    }
    public void SmoothStop(){
        bRun = false;
    }

    public void Stop(){
        bRun = false;
        this.terminate();
    }

    public void ChangeParam(){
        bRun = true;
        this.DeInit();
    }

    public void SavePoint(){

    }

    public void SaveWaveform(){

    }

    public void SaveProtocol(){

    }

    public void Terminate(){

            }
}

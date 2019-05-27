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
        tick = 0;
        tickGlobal = 0;
        System.out.println("Running thread!");
        while(true) {
            if(keepRunning()) {
                if(tick > range) {
                    tick = 0;
                    for (int j = 0; j < this.graphModel.getCurrentIndex(); j++) {
                        this.graphModel.clearSeries(j);
                    }
                }
                for (int j = 0; j < this.graphModel.getCurrentIndex(); j++) {
                    this.graphModel.addSeriesData(j, Math.sin(tickGlobal+(j*deltaX*delta)), tick);
                }
                tick += deltaX;
                tickGlobal += deltaX;
            }
            try {
                Thread.sleep(delta);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
        this.graphModel.setXAxis(range);
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

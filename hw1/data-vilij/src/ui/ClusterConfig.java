package ui;

public class ClusterConfig {
    private int maxIntegers = 0;
    private int updateInterval = 0;
    private boolean continuousRun = false;
    private int numClusters = 0;

    public int getNumClusters() {
        return numClusters;
    }

    public void setNumClusters(int numClusters) {
        this.numClusters = numClusters;
    }

    public ClusterConfig(int maxIntegers, int updateInterval, boolean continuousRun, int numClusters) {
        this.maxIntegers = maxIntegers;
        this.updateInterval = updateInterval;
        this.continuousRun = continuousRun;
        this.numClusters = numClusters;

    }

    public ClusterConfig(){

    }

    public int getMaxIntegers() {

        return maxIntegers;
    }

    public void setMaxIntegers(int maxIntegers) {
        this.maxIntegers = maxIntegers;
    }

    public int getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    public boolean isContinuousRun() {
        return continuousRun;
    }

    public void setContinuousRun(boolean continuousRun) {
        this.continuousRun = continuousRun;
    }
}

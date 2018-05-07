package ui;

public class ClusterConfig implements Config{
    private int maxIntegers = 0;
    private int updateInterval = 0;
    private boolean continuousRun = false;
    private int numClusters = 0;
    private boolean configSet = false;

    public boolean isConfigSet() {
        return configSet;
    }

    public void setConfigSet(boolean configSet) {
        this.configSet = configSet;
    }

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
        this.configSet = false;
    }

    public ClusterConfig(){
        this.maxIntegers = 0;
        this.updateInterval = 0;
        this.continuousRun = false;
        this.numClusters = 0;
        this.configSet = false;
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

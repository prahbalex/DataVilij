package ui;

public class testClustConfig {
    private int maxIterations = 0;
    private int updateInterval = 0;
    private boolean continuousRun = false;
    private int numClusters = 0;
    private boolean configSet = false;

    public testClustConfig() {
        this.maxIterations = 1;
        this.updateInterval = 1;
        this.continuousRun = false;
        this.numClusters = 2;
        this.configSet = false;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
        if(maxIterations < 1)
            this.maxIterations = 1;

    }

    public int getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
        if(updateInterval < 1)
            this.updateInterval = 1;

    }

    public boolean isContinuousRun() {
        return continuousRun;
    }

    public void setContinuousRun(boolean continuousRun) {
        this.continuousRun = continuousRun;
    }

    public int getNumClusters() {
        return numClusters;
    }

    public void setNumClusters(int numClusters) {
        this.numClusters = numClusters;
        if(numClusters < 2)
            this.numClusters = 2;
        if(numClusters > 4)
            this.numClusters = 4;

    }

    public boolean isConfigSet() {
        return configSet;
    }

    public void setConfigSet(boolean configSet) {
        this.configSet = configSet;
    }
}

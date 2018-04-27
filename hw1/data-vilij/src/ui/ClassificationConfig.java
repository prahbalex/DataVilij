package ui;

import vilij.components.ConfirmationDialog;

public class ClassificationConfig implements Config{
    private int maxIntegers = 0;
    private int updateInterval = 0;
    private boolean continuousRun = false;
    private boolean configSet = false;

    public ClassificationConfig(int maxIntegers, int updateInterval, boolean continuousRun) {
        this.maxIntegers = maxIntegers;
        this.updateInterval = updateInterval;
        this.continuousRun = continuousRun;
        this.configSet = false;
    }

    public ClassificationConfig(){
        this.maxIntegers = 0;
        this.updateInterval = 0;
        this.continuousRun = false;
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

    public boolean isConfigSet() {
        return configSet;
    }

    public void setConfigSet(boolean configSet) {
        this.configSet = configSet;
    }
}

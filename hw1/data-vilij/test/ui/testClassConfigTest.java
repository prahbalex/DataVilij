package ui;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class testClassConfigTest {
    /**
     * I tested this with -1 because the method should auto set it back to 1
     */
    @Test
    public void setMaxIterationsNormal() {
        testClassConfig testClassConfig = new testClassConfig();
        testClassConfig.setMaxIterations(-1);
        Assert.assertEquals(1, testClassConfig.getMaxIterations());
    }

    /**
     * For this one I passed in Integer.parseint because it is not a integer and it should break it beacuse
     * integer.paresint does not return a int
     * @throws Exception
     */
    @Test (expected = Exception.class)
    public void setMaxIterationsFail() throws Exception{
        testClassConfig testClassConfig = new testClassConfig();
        testClassConfig.setMaxIterations(Integer.parseInt("adsad"));
    }

    /**
     * For this one I passed in -1 and it auto fixes it to 1
     */
    @Test
    public void setUpdateIntervalNormal() {
        testClassConfig testClassConfig = new testClassConfig();
        testClassConfig.setUpdateInterval(-1);
        Assert.assertEquals(1, testClassConfig.getUpdateInterval());
    }

    /**
     * For this one I passed in Integer.parse int a string because it should not gracefully degrade because it does not return a real int
     * @throws Exception
     */
    @Test (expected = Exception.class)
    public void setUpdateIntervalFail() throws Exception{
        testClassConfig testClassConfig = new testClassConfig();
        testClassConfig.setUpdateInterval(Integer.parseInt("asdasd"));
    }

    /**
     * For this one I passed in true because it is a valid test
     */
    @Test
    public void setContinuousRunNormal() {
        testClassConfig testClassConfig = new testClassConfig();
        testClassConfig.setContinuousRun(true);
        Assert.assertEquals(true, testClassConfig.isContinuousRun());
    }

    /**
     * For this one I Passed in a object casted to boolean to show that it will not gracefully degrade because an obj casted to boolean
     * is not a real boolean
     * @throws Exception
     */
    @Test (expected = Exception.class)
    public void setContinuousRunFail() throws Exception{
        testClassConfig testClassConfig = new testClassConfig();
        Object o = new Object();
        testClassConfig.setContinuousRun((boolean)o);

    }
}
package ui;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class testClustConfigTest {

    /**
     * For this one i passed in -1 to show it will gracefully degrade to 1
     */
    @Test
    public void setMaxIterationsNormal() {
        testClustConfig testClustConfig = new testClustConfig();
        testClustConfig.setMaxIterations(-1);
        Assert.assertEquals(1, testClustConfig.getMaxIterations());
    }

    /**
     * For this one I passed in Integer.parseint a string to show that it will not work because
     * it will not return a int
     * @throws Exception
     */
    @Test(expected = Exception.class)
    public void setMaxIterationsFail() throws Exception{
        testClustConfig testClustConfig = new testClustConfig();
        testClustConfig.setMaxIterations(Integer.parseInt("asdasd"));
    }

    /**
     * For this one i passed in -1 to show that it will gracefully degrade to 1
     */
    @Test
    public void setUpdateIntervalNormal() {
        testClustConfig testClustConfig = new testClustConfig();
        testClustConfig.setUpdateInterval(-1);
        Assert.assertEquals(1, testClustConfig.getUpdateInterval());
    }

    /**
     * For this one I passed in Integer.parse because it is not areal int so this should not degrade gracefully
     * @throws Exception
     */
    @Test(expected = Exception.class)
    public void setUpdateIntervalFail() throws Exception{
        testClustConfig testClustConfig = new testClustConfig();
        testClustConfig.setUpdateInterval(Integer.parseInt("asdasd"));
    }

    /**
     * For this one I passed in true to show that it works for booleans
     */
    @Test
    public void setContinuousRunNormal() {
        testClustConfig testClustConfig = new testClustConfig();
        testClustConfig.setContinuousRun(true);
        Assert.assertEquals(true, testClustConfig.isContinuousRun());
    }

    /**
     * For this one I passed in an objected casted to boolean bec ause it is not a real boolean
     * this should not gracfully degrade
     * @throws Exception
     */
    @Test(expected = Exception.class)
    public void setContinuousRunFalse() throws Exception{
        testClustConfig testClustConfig = new testClustConfig();
        Object o = new Object();
        testClustConfig.setContinuousRun((boolean)o);
    }

    /**
     * For this one I passed in -2 to show that it shoulod gracefully degrade to 2 because it is less than 2
     */
    @Test
    public void setNumClustersNormal() {
        testClustConfig testClustConfig = new testClustConfig();
        testClustConfig.setNumClusters(-2);
        Assert.assertEquals(2, testClustConfig.getNumClusters());
    }

    /**
     * For this one I passed in Integer.pareint because it is not a real int and it should not gracefully degrade
     * @throws Exception
     */
    @Test(expected = Exception.class)
    public void setNumClustersFail() throws Exception{
        testClustConfig testClustConfig = new testClustConfig();
        testClustConfig.setNumClusters(Integer.parseInt("asd"));
    }

}
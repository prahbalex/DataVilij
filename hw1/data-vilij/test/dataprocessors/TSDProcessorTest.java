package dataprocessors;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class TSDProcessorTest {

    @Test
    public void processStringTestLabel() throws Exception {
        TSDProcessor tsdProcessor = new TSDProcessor();
        String s = "@1\tlabel1\t1,2";
        tsdProcessor.processString(s);
        Assert.assertEquals("label1",tsdProcessor.getDataLabels().get("@1"));
    }

    /**
     * I used double.minvalue because its the lowest possible input value
     * @throws Exception
     */
    @Test
    public void processStringTestLowerBoundry() throws Exception {
        TSDProcessor tsdProcessor = new TSDProcessor();
        String s = "@1\tlabel1\t" + Double.MIN_VALUE + "," +Double.MIN_VALUE;
        tsdProcessor.processString(s);
        Assert.assertEquals(Double.MIN_VALUE,tsdProcessor.getDataPoints().get("@1").getX(),.000001);
    }

    /**
     * I used double.max_value because its the highest possible input value
     * @throws Exception
     */
    @Test
    public void processStringTestUpperBoundry() throws Exception {
        TSDProcessor tsdProcessor = new TSDProcessor();
        String s = "@1\tlabel1\t" + Double.MAX_VALUE + "," +Double.MAX_VALUE;
        tsdProcessor.processString(s);
        Assert.assertEquals(Double.MAX_VALUE,tsdProcessor.getDataPoints().get("@1").getX(),.000001);
    }

}
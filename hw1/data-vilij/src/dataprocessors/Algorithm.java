package dataprocessors;

import vilij.templates.ApplicationTemplate;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This interface provides a way to run an algorithm
 * on a thread as a {@link java.lang.Runnable} object.
 *
 * @author Ritwik Banerjee
 */
public interface Algorithm extends Runnable {

    int getMaxIterations();

    int getUpdateInterval();

    boolean tocontinue();

    void setApplicationTemplate(ApplicationTemplate applicationTemplate);

    void setMaxIterations(int maxIterations);

    void setUpdateInterval(int updateInterval);

    void setTocontinue(boolean tocontinue);

}

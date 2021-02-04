import com.sun.tools.javac.Main;

import javax.security.auth.callback.Callback;
import java.util.Date;

import static java.lang.Thread.sleep;


public class Timer{

    private MainForm.Callback tickCallback;
    private Thread thread;
    private int seconds;

    public Timer(MainForm.Callback c, int s){
        seconds = s;
        tickCallback = c;
    }

    public void start() {
        thread = new Thread(() -> {
            while (true) {
                try {
                    sleep(seconds * 1000L);
                } catch (InterruptedException e) {
                    return;
                }
                tickCallback.function();
            }
        });
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }
}

package gorskima.altair8800.device;

import gorskima.altair8800.io.InputListener;

public abstract class ListenableDevice {

    private InputListener listener;

    public void setListener(final InputListener listener) {
        this.listener = listener;
    }

    protected void notifyListener() {
        if (listener != null) {
            listener.notifyInputAvailable();
        }
    }

}

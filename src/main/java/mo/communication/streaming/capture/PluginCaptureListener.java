/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.communication.streaming.capture;

import java.util.EventListener;
import java.util.HashMap;

/**
 *
 * @author carlo
 */
public interface PluginCaptureListener  extends EventListener{
    void onDataReceived(Object obj,CaptureEvent e);
    void setInitConfiguration(Object obj, CaptureConfig cc);
}

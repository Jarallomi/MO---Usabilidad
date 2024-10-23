/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.communication.streaming.capture;

import java.util.List;

public interface PluginCaptureSender{
    public void subscribeListener(PluginCaptureListener c); //agrega a la clase connection para que esta escuche al plugin
    public void unsubscribeListener(PluginCaptureListener c);
    
    // los siguientes son necesarios para una correcta configuración de los de visualización
    public String getCreator();
}

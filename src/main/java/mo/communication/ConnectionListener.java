/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.communication;

import java.util.EventListener;

/**
 *
 * @author carlo
 */
public interface ConnectionListener extends EventListener{
    public void onMessageReceived(Object obj, PetitionResponse pr);
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.communication;

import mo.core.plugin.ExtensionPoint;
import mo.organization.Configuration;

@ExtensionPoint
public interface CommunicationProvider1 {
    String getName();
    CommunicationConfiguration initNewConfiguration(String id);
}

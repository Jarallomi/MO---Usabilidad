/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.core.v2.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Francisco
 */
@XmlRootElement(name="configuration")
@XmlType(propOrder={"name"})
public class ConfigurationV2 {
    String name;
    
    public ConfigurationV2(){
        this(null);
    }
    public ConfigurationV2(String ConfigurationName) {
        this.name = ConfigurationName;
    }
    @XmlElement(name="name")
    public String getName() {
        return name;
    }

    public void setName(String ConfigurationName) {
        this.name = ConfigurationName;
    }
    
}

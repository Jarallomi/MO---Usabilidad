/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.core.v2.model;

import java.util.List;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Francisco
 */
@XmlRootElement(name="plugin")
@XmlType(propOrder={"name","configurations"})
public class StagePluginV2 {
    public final static Logger LOGGER = Logger.getLogger(StagePluginV2.class.getName());
    
    String name;
    List<ConfigurationV2> configurations;
    
    public StagePluginV2(String name,List<ConfigurationV2> configurations){
        this.name=name;
        this.configurations=configurations; 
    }
    public StagePluginV2(){
        this(null,null);
    }

    public void setName(String PluginName) {
        this.name = PluginName;
    }


    public void setConfigurations(List<ConfigurationV2> configurations) {
        this.configurations = configurations;
    }

    @XmlElement(name="name")
    public String getName() {
        return name;
    }

    @XmlElementWrapper(name="configurations")
    @XmlElement(name="configuration")
    public List<ConfigurationV2> getConfigurations() {
        return configurations;
    }
    public List<ConfigurationV2> addConfiguration(ConfigurationV2 configuration){
        configurations.add(configuration);
        return configurations;
    
    }
    public List<ConfigurationV2> removeConfiguration(String ConfigurationName){
        for(ConfigurationV2 configuration: configurations){
            if(configuration.getName().equals(ConfigurationName)){
                configurations.remove(configuration);
            }
        }
        return configurations;
    
    }

}

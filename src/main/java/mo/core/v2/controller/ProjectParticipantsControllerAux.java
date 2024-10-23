/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.core.v2.controller;

import com.google.inject.Inject;
import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.ImageView;
import mo.core.v2.model.Organization;
import mo.organization.Participant;
import mo.organization.StageAction;
import mo.organization.StageModule;

/**
 *
 * @author Francisco
 */
public class ProjectParticipantsControllerAux {
    private final String CONTROLLER_KEY = "controller";
    @Inject
    Injector injector;
    @Inject
    Organization model;
    Organization model2;
    List<StageAction> action = new ArrayList<>();
    
    public ProjectParticipantsControllerAux(Organization aux){
        this.model2 = aux;
    }
    
    public ImageView unLockParticipant(String id, ImageView i){
        for(Participant participant : model.getOrg().getParticipants()){
            if(participant.id.equals(id)){
                if(participant.isLocked){
                    participant.isLocked=false;
                    ImageView iAux = new javafx.scene.image.ImageView("/images/unlocked2.png");
                    i.setImage(iAux.getImage());
                    model.setParticipants(model.getOrg().getParticipants());
                    return i;
                }
                else{
                    participant.isLocked=true;
                    ImageView iAux = new javafx.scene.image.ImageView("/images/locked2.png");
                    i.setImage(iAux.getImage());
                    model.setParticipants(model.getOrg().getParticipants());
                    return i;
                }
            }
        }
        return i;
    }
    
    //Necesaria??
    public void delete(String id){
        List<Participant> participants = model.getParticipants();
        for(Participant p : participants){
            if(p.id.equals(id)){
                model.getOrg().deleteParticipant(p);
                model.getOrg().store();
                model.getParticipants().remove(p);
                break;
            }
        }
    }
    
    public List<StageAction> actions(){
        String capture = "Captura";
        String analysis = model2.getStages2().get(0).getName();
        String visu = model2.getStages3().get(0).getName();
        StageAction c = null, a = null, v = null;
        if(!model2.getOrg().getStages().isEmpty()){
            for(StageModule module : model2.getOrg().getStages()){
                if(module.getName().equals(capture)){
                    c = module.getActions().get(0);
                }
                if(module.getName().equals(analysis)){
                    a = module.getActions().get(0);
                }
                if(module.getName().equals(visu)){
                    v = module.getActions().get(0);
                }
            }
        }
        this.action.add(c);
        this.action.add(a);
        this.action.add(v);
        return action;
    }
}

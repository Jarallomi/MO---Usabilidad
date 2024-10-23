/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.analysis;

import java.io.File;
import mo.organization.Configuration;
import mo.organization.Participant;
import mo.organization.ProjectOrganization;


public interface IndividualAnalysisConfiguration extends AnalyzableConfiguration {
    
    public void initIndividualAnalysis(Participant participant); // de prueba
}

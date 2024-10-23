package mo.organization;

import bibliothek.util.xml.XAttribute;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import mo.core.filemanagement.project.Project;
import mo.core.plugin.PluginRegistry;

public class ProjectOrganization {

    public final static Logger logger = Logger.getLogger(ProjectOrganization.class.getName());

    Project project;
    List<Participant> participants;
    List<StageModule> stages;

    public ProjectOrganization(String projectPath) {
        this(new Project(projectPath));
    }

    public ProjectOrganization(Project project) {
        participants = new ArrayList<>();
        stages = new ArrayList<>();
        this.project = project;
        restore();
    }

    public void addStage(StageModule stage) {
        for (StageModule s : stages) {
            if (s.getName().equals(stage.getName())) {
                throw new IllegalArgumentException("Stage already exists");
            }
        }

        stages.add(stage);
    }
    
    public void addStageReplacingPrevious(StageModule stage) {
        ArrayList<StageModule> toDelete = new ArrayList<>();
        for (StageModule s : stages) {
            if (s.getName().equals(stage.getName())) {
                toDelete.add(s);
            }
        }

        stages.removeAll(toDelete);
        
        stages.add(stage);
    }

    public List<StageModule> getStages() {
        return stages;
    }

    public void store() {

        File orgXml = new File(project.getFolder(), "organization.xml");
        try {
            orgXml.createNewFile();
            XElement root = new XElement("organization");
            XElement ps = new XElement("participants");

            for (Participant participant : participants) {
                XElement xParticipant = new XElement("participant");
                xParticipant.addElement("id").setString(participant.id);
                xParticipant.addElement("name").setString(participant.name);
                xParticipant.addElement("notes").setString(participant.notes);
                xParticipant.addElement("folder").setString(participant.folder);
                XElement date = new XElement("date");
                Calendar c = Calendar.getInstance();
                c.setTime(participant.date);
                date.addElement("day").setInt(c.get(Calendar.DAY_OF_MONTH));
                date.addElement("month").setInt(c.get(Calendar.MONTH));
                date.addElement("year").setInt(c.get(Calendar.YEAR));
                xParticipant.addElement(date);
                XAttribute locked = new XAttribute("isLocked");
                locked.setBoolean(participant.isLocked);
                xParticipant.addAttribute(locked);
                ps.addElement(xParticipant);
            }

            root.addElement(ps);

            for (StageModule stage : stages) {
                XElement st = new XElement("stage");

                XAttribute clazz = new XAttribute("class");
                clazz.setString(stage.getClass().getName());

                st.addAttribute(clazz);

                File file = stage.toFile(project.getFolder());
                if (file != null) {
                    
                    Path projectRoot = Paths.get(project.getFolder().getAbsolutePath());
                    Path stagePath = Paths.get(file.toURI());

                    Path relative = projectRoot.relativize(stagePath);
                    System.out.println("relative to add in stage xml: " + relative.toString());
                    st.setString(relative.toString());
                    root.addElement(st);
                }
            }
            XIO.writeUTF(root, new FileOutputStream(orgXml));
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void restore() {
        File orgXml = new File(project.getFolder(), "organization.xml");
        if (orgXml.exists()) {
            try {
                XElement root = XIO.readUTF(new FileInputStream(orgXml));
                XElement participantsXelement = root.getElement("participants");
                if (participantsXelement != null) {
                    XElement[] ps = root.getElement("participants").getElements("participant");
                    for (XElement participant : ps) {
                        Participant p = new Participant();
                        p.id = participant.getElement("id").getString();
                        p.name = participant.getElement("name").getString();
                        p.notes = participant.getElement("notes").getString();
                        p.folder = participant.getElement("folder").getString();

                        SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy");
                        String day = participant.getElement("date").getElement("day").getString();
                        String month = participant.getElement("date").getElement("month").getString();
                        String year = participant.getElement("date").getElement("year").getString();
                        Date date = new Date();
                        try {
                            date = formatter.parse(day + " " + (Integer.parseInt(month) + 1) + " " + year);
                        } catch (ParseException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }
                        p.date = date;

                        if (participant.attributeExists("isLocked")) {
                            p.isLocked = participant.getAttribute("isLocked").getBoolean();
                        }

                        participants.add(p);
                    }
                }

                XElement[] st = root.getElements("stage");
                for (XElement xElement : st) {
                    try {
                        Class<?> clazz = PluginRegistry.getInstance().getClassForName(
                                xElement.getAttribute("class").getString());
                        Object o = clazz.newInstance();
                        String path = xElement.getString();
                        Method method = clazz.getDeclaredMethod("fromFile", File.class);

                        StageModule stage;

                        stage = (StageModule) method.invoke(
                                o, new File(project.getFolder(), path));
                        System.out.println(" ProjectOrganization path = " + path); // borrame linea visualization.xml
                        if (stage != null) {
                            stage.setOrganization(this);
                            addStageReplacingPrevious(stage);
                        }
                    } catch (IllegalAccessException | IllegalArgumentException |
                            InvocationTargetException | ClassNotFoundException |
                            InstantiationException | NoSuchMethodException |
                            SecurityException ex) {
                        logger.log(Level.SEVERE, null, ex);
                    }
                }

            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void addParticipant(Participant participant) {
        participants.add(participant);
    }

    public void updateParticipant(Participant p) {
        participants.stream().filter((participant) -> (participant.id.equals(p.id))).map((participant) -> {
            participant.name = p.name;
            return participant;
        }).map((participant) -> {
            participant.date = p.date;
            return participant;
        }).forEachOrdered((participant) -> {
            participant.notes = p.notes;
        });
    }

    public void deleteParticipant(Participant p) {
        Participant toDelete = null;
        for (Participant participant : participants) {
            if (participant.id.equals(p.id)) {
                toDelete = participant;
            }
        }
        if (toDelete != null) {
            participants.remove(toDelete);
        }
    }

    public File getLocation() {
        return this.project.getFolder();
    }
}

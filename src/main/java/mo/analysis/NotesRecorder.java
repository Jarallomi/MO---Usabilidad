package mo.analysis;

import java.io.File;
import mo.organization.ProjectOrganization;
import mo.organization.Participant;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import mo.analysis.NotesVisualization;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class NotesRecorder {
	private NotesAnalysisConfig config;
	private ProjectOrganization org;
	private Participant participant;
	private File stageFolder;
	private File output;
	private BufferedWriter writer;
	public NotesRecorder(File stageFolder, NotesAnalysisConfig config) {
		this.stageFolder = stageFolder;
		this.config = config;
	}

	public void createFile() {
		Date now = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss.SSS");
        String reportDate = df.format(now);

        String fileName = reportDate + "_" + config.getId();

        output = new File(stageFolder, fileName + ".txt"); 
        try {
        	output.createNewFile();
        	createWriter();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}

	public void setFile(File output) {
		this.output = output;
		createWriter();
	}

	private void createWriter() {
		try {
			this.writer = new BufferedWriter(new FileWriter(output, true));
			writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}

	public File getFile() {
		return output;
	}

	public BufferedWriter getWriter() {
		return writer;
	}

	public void writeNote(Note nota) {
            try {
                    writer.write(nota.toString());
                    writer.newLine();
                    writer.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
	}
}
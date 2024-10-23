package mo.analysis;

public class NotesVisualization {
	private String filePath;
	private String configuration;

	public NotesVisualization(String filePath, String configuration) {
		this.filePath = filePath;
		this.configuration = configuration;
	}

	public String getFilePath() {
		return this.filePath;
	}

	public String getConfiguration() {
		return this.configuration;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;

		if(other == this)
			return true;

		if(!(other instanceof NotesVisualization))
			return false;

		NotesVisualization notesVisualization = (NotesVisualization) other;
		if (!this.filePath.equals(notesVisualization.getFilePath()) || !this.configuration.equals(notesVisualization.getConfiguration())) {
			return false;
		}

		return true;
	}
}
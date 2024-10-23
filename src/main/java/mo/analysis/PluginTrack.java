package mo.analysis;

import java.awt.Font;
import java.awt.Color;
import java.util.List;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.awt.AlphaComposite;

public class PluginTrack {
	private Font font;
	private int height;
	private int width;
	private Color color;
	private List<Note> notes;
	private long start;
	private long end;
	private AlphaComposite alpha04;
	public int y1;
	public int y2;

	public PluginTrack(long start, long end, int y1, int y2) {
		this.start = start;
		this.end = end;
		this.y1 = y1;
		this.y2 = y2;
		font = new Font("Arial Unicode MS", Font.PLAIN, 9);
		color = Color.blue;
		notes = new ArrayList<>();
		alpha04 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
	}

	public void deleteNotes() {
		this.notes.clear();
	}

	public void paint(Graphics2D g2d, int x, int y, int width, int height) {
		g2d.setColor(color);
		g2d.fillRect(x,y,width,height);
	}

	public void addNote(Note note) {
		this.notes.add(note);
	}

	public void removeNote(int index) {
		this.notes.remove(index);
	}

	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}

	public void editNote(int index, String comment) {
		notes.get(index).setComment(comment);
	}

	public List<Note> getNotes() {
		return notes;
	}
}
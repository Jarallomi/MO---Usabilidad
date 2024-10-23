package mo.analysis;

import mo.visualization.Playable;
import java.util.List;
import java.util.ArrayList;
import java.util.TreeSet;
import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.NumberFormatException;

import java.util.Iterator;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

import javax.swing.SwingUtilities;

import mo.core.ui.dockables.DockableElement;
import mo.core.ui.dockables.DockablesRegistry;



public class NotesPlayer implements Playable {

    private long start;
    private long end;
    private List<NotesRecorder> notesRecorders;
    private List<TreeSet> notesForTrack;

    private final AnalysisTimePanel panel;

    private static final Logger logger = Logger.getLogger(NotesPlayer.class.getName());

    public NotesPlayer(List<NotesRecorder> notesRecorders) {
        this.notesRecorders = notesRecorders;
        notesForTrack = new ArrayList<>();

        for (NotesRecorder nr : notesRecorders) {
            notesForTrack.add(getNotes(nr));
        }

        panel = new AnalysisTimePanel(this);

        SwingUtilities.invokeLater(() -> {
                
            try {
                DockableElement e = new DockableElement();
                e.add(panel);
                DockablesRegistry.getInstance().addAppWideDockable(e);
            } catch (Exception ex) {
                logger.log(Level.INFO, null, ex);
            }
        });        
    }

    public List<TreeSet> getNotesForTrack() {
        return notesForTrack;
    }

    private TreeSet<Note> getNotes(NotesRecorder recorder) {
        TreeSet<Note> set = new TreeSet<Note>();
        BufferedReader reader = null;

        try {
            File file = recorder.getFile();
            reader = new BufferedReader(new FileReader(file));

            String line;
            String[] tokens;
            long beginTime;
            long endTime;
            String text;
            Note note;
            while ((line = reader.readLine()) != null) {
                tokens = line.split(",");

                try {
                    beginTime = Long.parseLong(tokens[0]);
                    endTime = Long.parseLong(tokens[1]);
                    text = tokens[2];
                    note = new Note(beginTime,endTime,text);
                    set.add(note);
                } catch (NumberFormatException ex) {
                    logger.log(Level.INFO, "line in the file does not conform to the format of NotesRecorder");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Iterator<Note> it = set.iterator();
        Note anotherNote;
        while(it.hasNext()) {
            anotherNote = it.next();
        }
        return set;
    }

    public void addNote(int trackIndex, Note note) {
        notesForTrack.get(trackIndex).add(note);
        saveNote(trackIndex,note);
    }

    public void saveNote(int trackIndex, Note note) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                notesRecorders.get(trackIndex).writeNote(note);
            }
        });

        thread.start();
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setEnd(long end) {
        this.end = end;
    }

	@Override
	public long getStart() {
		return start;
	}

	@Override
    public long getEnd() {
    	return end;
    }

    @Override
    public void play(long ms) {
        if (ms < start) {
            play(start);
        } else if (ms > end) {
            play(end);
        } else {
            seek(ms);
        }
    }

	@Override
    public void stop() {

    }

    @Override
    public void seek(long ms) {
        panel.setTime(ms);
    }

    @Override
    public void pause() {

    }

    @Override
    public void sync(boolean sync){
        
    }
}
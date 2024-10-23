package mo.analysis;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Color;


public class TimeRuler {
	private Font font;
	private int decStickHeight;
	private int secStickHeight;
	private long start;
	private long end;
	private int milis;
	private int labelWidth;
	private int tiempo;
	private int rulerHeight;
	private String templateLabel = "00:00:00.000";
	private String label;
	private final static String ELLAPSED_FORMAT = "%02d:%02d:%02d:%1d";

	public TimeRuler(long start, long end) {
		this.start = start;
		this.end = end;
		milis = (int) (end - start);
		font = new Font(Font.MONOSPACED, Font.PLAIN, 9);
		rulerHeight = 25;
		decStickHeight = 5;
		secStickHeight = 10;
	}

	public void paint(Graphics2D g2d, float msPerPx) {

		float pxPerMs = 1 / msPerPx;
		float pxPerSec = 1000 * pxPerMs;
		float pxPerDec = 100 * pxPerMs;
		int x;

		// paint top line
		int nDecs=getNDecs();
		int lineLength = (int) nDecs * (int) pxPerDec;
		g2d.setColor(Color.black);
		g2d.drawLine(0,0,lineLength,0);

		// paint decs sticks
		for(int i=0; i<=nDecs; i++) {
			x = i * (int) pxPerDec;
			g2d.drawLine(x,0,x,decStickHeight);
		}

		long ms;
        long s;
        long m;
        long h;
		int msAtX;

		// paint secs sticks
		for(int i=0, nSecs = getNSecs(); i<=nSecs; i++) {
			x = i * (int) pxPerSec;
			g2d.drawLine(x,0,x,secStickHeight);

			msAtX = x * (int) msPerPx;
			ms = getMiliseconds(msAtX);
            s = getSeconds(msAtX);
            m = getMinutes(msAtX);
            h = getHours(msAtX);

            label = String.format(ELLAPSED_FORMAT,h,m,s,ms);
            labelWidth = g2d.getFontMetrics().stringWidth(label);
			g2d.drawString(label, x - (labelWidth/2),rulerHeight);
		}
	}

	private long getMiliseconds(int ms) {
		return (long) (ms % 1000) / 100;
	}
	
	private long getSeconds(int ms) {
		return (long) (ms / 1000) % 60;
	}

	private long getMinutes(int ms) {
		return (long) (ms / (1000 * 60)) % 60;
	}

	private long getHours(int ms) {
		return (long) (ms / (1000 * 60 * 60)) % 24;
	}

	private int getNSecs() {
		return getNDecs()/10;
	}

	private int getNDecs() {
		return (int) Math.ceil((double) milis/(double) 100);
	}

}
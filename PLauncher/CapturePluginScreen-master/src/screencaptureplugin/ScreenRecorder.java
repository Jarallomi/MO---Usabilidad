package screencaptureplugin;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;
import java.awt.AWTException;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import mo.organization.FileDescription;
import mo.organization.Participant;
import mo.organization.ProjectOrganization;

public class ScreenRecorder {

    Participant participant;
    ProjectOrganization org;
    private ScreenCaptureConfiguration config;
    private File output;
    private File arInicio;
    private File frames;
    private String path;
    private String file_name;
    private FileOutputStream outputStream;
    private FileDescription desc;
    private long start;
    private long pause;
    private long inicio;
    private long fin;

    private static final Logger logger = Logger.getLogger(ScreenRecorder.class.getName());

    public int sleep;
    public long fps_aux;
    public int sw = 1;
    public long time;
    public int IMG_WIDTH;
    public int IMG_HEIGHT;
    public int screen_op;

    public ScreenRecorder(File stageFolder, ProjectOrganization org, Participant p, int FPS, int width, int height, int screen, ScreenCaptureConfiguration c) {
        participant = p;
        this.org = org;
        this.config = c;
        this.sleep = (int) (1000 / FPS);
        this.screen_op = screen;
        this.IMG_WIDTH = width; // Ancho directamente asignado
        this.IMG_HEIGHT = height; // Alto directamente asignado
        createFile(stageFolder);
    }

    private void createFile(File parent) {

        Date now = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss.SSS");

        String reportDate = df.format(now);

        output = new File(parent, reportDate + "_" + config.getId() + ".mp4");
        arInicio = new File(parent, reportDate + "_" + config.getId() + "-temp.txt");
        frames = new File(parent, reportDate + "_" + config.getId() + "-frames.txt");
        path = parent.getAbsolutePath();
        file_name = reportDate + "_" + config.getId();
        try {
            output.createNewFile();
            outputStream = new FileOutputStream(output);
            desc = new FileDescription(output, ScreenRecorder.class.getName());
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

    }

    private void deleteFile() {
        if (output.isFile()) {
            output.delete();
        }
        if (desc.getDescriptionFile().isFile()) {
            desc.deleteFileDescription();
        }
    }

    private class Record implements Runnable {

        @Override
        public void run() {

            IMediaWriter writer = ToolFactory.makeWriter(path + "\\" + file_name + ".mp4");
            Robot r;
            try {
                r = new Robot();
                Rectangle screensize = null;
                int count = 1;
                String devs = "";
                int x = 100000, y = 100000, h = 0, w = 0;
                for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {

                    Rectangle bounds = gd.getDefaultConfiguration().getBounds();
                    if (screen_op == 0) {
                        if (bounds.x < x) {
                            x = bounds.x;
                        }
                        if (bounds.y < y) {
                            y = bounds.y;
                        }
                        if (h < bounds.height) {
                            h = bounds.height;
                        }
                        w = w + bounds.width;
                    } else {
                        if (count == screen_op) {
                            screensize = new Rectangle((int) bounds.getMinX(),
                                    (int) bounds.getMinY(), (int) bounds.getWidth(), (int) bounds.getHeight());
                        }
                    }
                    count++;
                }
                if (screen_op == 0) { 
                    screensize = new Rectangle(x, y, w, h);

                    IMG_WIDTH = 1920;
                    IMG_HEIGHT = 1080;
                }

                writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, IMG_WIDTH, IMG_HEIGHT);
                start = System.currentTimeMillis();
                inicio = System.currentTimeMillis();
                //bw2 = new BufferedWriter(new FileWriter(frames));
                while (sw != 0) {
                    BufferedImage image = new Robot().createScreenCapture(screensize);
                    int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
                    if (screensize.width != IMG_WIDTH && screensize.height != IMG_HEIGHT) {
                        image = resizeImage(image, type);
                    }
                    image = ConverterFactory.convertToType(image, BufferedImage.TYPE_3BYTE_BGR);
                    IConverter converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P);
                    time = System.currentTimeMillis();
                    IVideoPicture frame = converter.toPicture(image, (time - start) * 1000);
                    //bw2.write(time+"\n");
                    writer.encodeVideo(0, frame);

                    while (fps_aux > System.currentTimeMillis()) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ScreenRecorder.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    fps_aux = fps_aux + sleep;
                    while (sw == 2) {
                        try {
                            Thread.sleep(2);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ScreenRecorder.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                // bw2.close();
                fin = time;
                writer.close();
                BufferedWriter bw;
                try {
                    bw = new BufferedWriter(new FileWriter(arInicio));
                    bw.write(inicio + "\n");
                    bw.write(fin + "");
                    bw.close();
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
                try {
                    arInicio.createNewFile();
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            } catch (AWTException ex) {
                Logger.getLogger(ScreenRecorder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int type) {
        BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
        g.dispose();
        return resizedImage;
    }

    public void StartRecord() {
        Thread t = new Thread(new Record());
        t.start();
    }

    public void StopRecord() {
        sw = 0;
    }

    public void PauseRecord() {
        sw = 2;
        pause = System.currentTimeMillis() - start;
    }

    public void ResumeRecod() {
        sw = 1;
        start = System.currentTimeMillis() - pause + 1000;
    }

    public void CandelRecord() {
        StopRecord();
        deleteFile();
    }
}

package mo.visualization.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class LiveHeatMap extends JPanel {

    BufferedImage map;
    Graphics2D graphics;
    double[][] data;
    int[][] dataColorIndices;
    Color[] colors;
    double min;
    double max;
    int height;
    int width;
    int updateRate = 1;
    int updateCount = 0;
    boolean maxChanged = false, minChanged = false;
    Point updatePoint;

    public LiveHeatMap(int width, int height) {
        this.width = width;
        this.height = height;
        data = new double[width][height];
        min = max = 0;
        map = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        graphics = map.createGraphics();
        colors = Gradient.GRADIENT_GREEN_YELLOW_ORANGE_RED;
        this.setDoubleBuffered(true);
        updateDataColors();
        drawData();
        repaint();
    }

    public static int randInt(int min, int max) {

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public static void main(String[] args) {
        
        LiveHeatMap m = new LiveHeatMap(100, 100);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame f = new JFrame("app");
                f.setPreferredSize(new Dimension(400, 400));
                f.setSize(400, 400);
                f.add(m);
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setVisible(true);
            }
        });

        m.updateRate = 1;
        for (int i = 0; i < 100000; i++) {
            m.update(randInt(0, 99), randInt(0, 99));
        }
        
        while(true) {
            m.update(randInt(0, 99), randInt(0, 99));
        }
    }

    public void update(int x, int y) {
        Point p = new Point(x, y);
        Rectangle r = new Rectangle(width, height);
        if (r.contains(p)) {
            data[x][y] += 5;
            if (data[x][y] > max) {
                max = data[x][y];
                updatePoint = new Point(x,y);
                maxChanged = true;
            }
            
            if (data[x][y] < min) {
                updatePoint = new Point(x,y);
                min = data[x][y];
                minChanged = true;
            }
        }
        updateCount++;
        if (updateCount % updateRate == 0 && (maxChanged || minChanged)) {
            updateMap();
            maxChanged = false;
            minChanged = false;
        }
    }

    void updateMap() {
        drawData();
        updateDataColors();
        repaint();
    }

    private void updateDataColors() {

        double range = max - min;

        // dataColorIndices is the same size as the data array
        // It stores an int index into the color array
        dataColorIndices = new int[data.length][data[0].length];

        //assign a Color to each data point
        for (int x = 0; x < data.length; x++) {
            for (int y = 0; y < data[0].length; y++) {
                double norm = (data[x][y] - min) / range; // 0 < norm < 1
                int colorIndex = (int) Math.floor(norm * (colors.length - 1));
                dataColorIndices[x][y] = colorIndex;
            }
        }
    }

    private void drawData() {
        map = new BufferedImage(data.length, data[0].length, BufferedImage.TYPE_INT_ARGB);
        graphics = map.createGraphics();

        for (int x = 0; x < data.length; x++) {
            for (int y = 0; y < data[0].length; y++) {
                graphics.setColor(colors[dataColorIndices[x][y]]);
                graphics.fillRect(x, y, 1, 1);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int width = this.getWidth();
        int height = this.getHeight();

        this.setOpaque(true);

        // clear the panel
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, width, height);

        // draw the heat map
        if (map == null) {
            // Ideally, we only call drawData in the constructor, or if we
            // change the data or gradients. We include this just to be safe.
            drawData();
        }

        // The data plot itself is drawn with 1 pixel per data point, and the
        // drawImage method scales that up to fit our current window size. This
        // is very fast, and is much faster than the previous version, which 
        // redrew the data plot each time we had to repaint the screen.
        g2d.drawImage(map,
                31, 31,
                width - 30,
                height - 30,
                0, 0,
                map.getWidth(), map.getHeight(),
                null);

        //System.out.println("yap");
//
//        // border
//        g2d.setColor(fg);
//        g2d.drawRect(30, 30, width - 60, height - 60);
//        
//        // title
//        if (drawTitle && title != null)
//        {
//            g2d.drawString(title, (width / 2) - 4 * title.length(), 20);
//        }
//
//        // axis ticks - ticks start even with the bottom left coner, end very close to end of line (might not be right on)
//        int numXTicks = (width - 60) / 50;
//        int numYTicks = (height - 60) / 50;
//
//        String label = "";
//        DecimalFormat df = new DecimalFormat("##.##");
//
//        // Y-Axis ticks
//        if (drawYTicks)
//        {
//            int yDist = (int) ((height - 60) / (double) numYTicks); //distance between ticks
//            for (int y = 0; y <= numYTicks; y++)
//            {
//                g2d.drawLine(26, height - 30 - y * yDist, 30, height - 30 - y * yDist);
//                label = df.format(((y / (double) numYTicks) * (yMax - yMin)) + yMin);
//                int labelY = height - 30 - y * yDist - 4 * label.length();
//                //to get the text to fit nicely, we need to rotate the graphics
//                g2d.rotate(Math.PI / 2);
//                g2d.drawString(label, labelY, -14);
//                g2d.rotate( -Math.PI / 2);
//            }
//        }
//
//        // Y-Axis title
//        if (drawYTitle && yAxis != null)
//        {
//            //to get the text to fit nicely, we need to rotate the graphics
//            g2d.rotate(Math.PI / 2);
//            g2d.drawString(yAxis, (height / 2) - 4 * yAxis.length(), -3);
//            g2d.rotate( -Math.PI / 2);
//        }
//
//
//        // X-Axis ticks
//        if (drawXTicks)
//        {
//            int xDist = (int) ((width - 60) / (double) numXTicks); //distance between ticks
//            for (int x = 0; x <= numXTicks; x++)
//            {
//                g2d.drawLine(30 + x * xDist, height - 30, 30 + x * xDist, height - 26);
//                label = df.format(((x / (double) numXTicks) * (xMax - xMin)) + xMin);
//                int labelX = (31 + x * xDist) - 4 * label.length();
//                g2d.drawString(label, labelX, height - 14);
//            }
//        }
//
//        // X-Axis title
//        if (drawXTitle && xAxis != null)
//        {
//            g2d.drawString(xAxis, (width / 2) - 4 * xAxis.length(), height - 3);
//        }
//
//        // Legend
//        if (drawLegend)
//        {
//            g2d.drawRect(width - 20, 30, 10, height - 60);
//            for (int y = 0; y < height - 61; y++)
//            {
//                int yStart = height - 31 - (int) Math.ceil(y * ((height - 60) / (colors.length * 1.0)));
//                yStart = height - 31 - y;
//                g2d.setColor(colors[(int) ((y / (double) (height - 60)) * (colors.length * 1.0))]);
//                g2d.fillRect(width - 19, yStart, 9, 1);
//            }
//        }
    }
}

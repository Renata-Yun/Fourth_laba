package lab_4;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class FractalExplorer extends JFrame {

    private int width, height;
    private FractalGenerator generator;

    private Rectangle2D.Double viewport;
    private JImageDisplay fractalImage;

    public FractalExplorer(int w, int h, FractalGenerator generator) {
        super("Fractal Explorer");
        this.width = w;
        this.height = h;
        this.generator = generator;

        this.viewport = generator.getInitialRange();
    }

    public void resetViewport() {
        this.setViewport(this.generator.getInitialRange());
    }

    public Rectangle2D.Double getViewport() {
        return viewport;
    }

    public void setViewport(Rectangle2D.Double viewport) {
//        if (this.viewport.equals(viewport)) {
//            return;
//        }
        this.viewport = viewport;
        drawFractal();
    }

    public void createAndShowGui() {
        JFrame frame = this;
        BorderLayout layout = new BorderLayout();
        frame.setLayout(layout);

        JImageDisplay jim = new JImageDisplay(width, height);
        this.fractalImage = jim;

        frame.add(jim,  BorderLayout.CENTER);

        JButton drawButton = new JButton("Reset");
        drawButton.addActionListener((_e) -> { this.resetViewport(); });
        frame.add(drawButton, BorderLayout.SOUTH);

        // Mouse listeners
        jim.addMouseWheelListener(ev -> {
            Rectangle2D.Double vp = this.getViewport();
            double mouseNormedX = (double) ev.getX() / (double) jim.getWidth();
            double mouseNormedY = (double) ev.getY() / (double) jim.getHeight();
            double dCenterX = ((mouseNormedX - 0.5) * 2);
            double dCenterY = ((mouseNormedY - 0.5) * 2);

            if (ev.getWheelRotation() == -1 /* zoom + */) {
                final double ZOOM_SCALE = 0.5;
                double newCenterX = vp.getCenterX() + dCenterX * vp.getWidth() / 2.0;
                double newCenterY = vp.getCenterY() + dCenterY * vp.getHeight() / 2.0;
                double vpWidth = vp.getWidth() * ZOOM_SCALE;
                double vpHeight = vp.getHeight() * ZOOM_SCALE;

                Rectangle2D.Double newVp = new Rectangle2D.Double(
                        newCenterX - vpWidth / 2.0,
                        newCenterY - vpHeight / 2.0,
                        vpWidth,
                        vpHeight
                );
                this.setViewport(newVp);
            } else {
                final double ZOOM_SCALE = 1.5;
                double newCenterX = vp.getCenterX() - dCenterX * vp.getWidth() / 2.0;
                double newCenterY = vp.getCenterY() - dCenterY * vp.getHeight() / 2.0;
                double vpWidth = vp.getWidth() * ZOOM_SCALE;
                double vpHeight = vp.getHeight() * ZOOM_SCALE;

                Rectangle2D.Double newVp = new Rectangle2D.Double(
                        newCenterX - vpWidth / 2.0,
                        newCenterY - vpHeight / 2.0,
                        vpWidth,
                        vpHeight
                );
                this.setViewport(newVp);
            }
        });

        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // first draw
        drawFractal();
    }

    private void drawFractal() {
        BufferedImage im = this.fractalImage.getImage();
        int imWidth = im.getWidth();
        int imHeight = im.getHeight();
        for (int imX = 0; imX < imWidth; imX++) {
            for (int imY = 0; imY < imHeight; imY++) {
                Rectangle2D.Double vp = this.viewport;
                double x = FractalGenerator.getCoord(vp.getX(), vp.getMaxX(), imWidth, imX);
                double y = FractalGenerator.getCoord(vp.getY(), vp.getMaxY(), imHeight, imY);

                int iters = this.generator.numIterations(x, y);
                if (iters == -1) {
                    im.setRGB(imX, imY, 0x00000000);
                } else {
                    float hue = 0.7f + (float) iters / 200f;
                    int color = Color.HSBtoRGB(hue, 1f, 1f);
                    im.setRGB(imX, imY, color);
                }
            }
        }
        this.repaint();
    }
}

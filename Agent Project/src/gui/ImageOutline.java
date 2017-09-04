package gui;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

class ImageOutline {
	
    private BufferedImage image=null;

    public Area getArea_FastHack() {
        //Assumes Black as Shape Color
        if(image==null) return null;

        Area area = new Area();
        Rectangle r;
        int y1,y2;

        for (int x=0; x<image.getWidth(); x++) {
            y1=99;
            y2=-1;
            for (int y=0; y<image.getHeight(); y++) {
                Color pixel = new Color(image.getRGB(x,y));
                //-16777216 entspricht RGB(0,0,0)
                if (pixel.getRGB()==-16777216) {
                    if(y1==99) {y1=y;y2=y;}
                    if(y>(y2+1)) {
                        r = new Rectangle(x,y1,1,y2-y1);
                        area.add(new Area(r)); 
                        y1=y;y2=y;
                    }
                    y2=y;
                }               
            }
            if((y2-y1)>=0) {
                r = new Rectangle(x,y1,1,y2-y1);
                area.add(new Area(r)); 
            }
        }
        return area;
    }

    public static boolean isIncluded(Color target, Color pixel, int tolerance) {
        int rT = target.getRed();
        int gT = target.getGreen();
        int bT = target.getBlue();
        int rP = pixel.getRed();
        int gP = pixel.getGreen();
        int bP = pixel.getBlue();
        return(
            (rP-tolerance<=rT) && (rT<=rP+tolerance) &&
            (gP-tolerance<=gT) && (gT<=gP+tolerance) &&
            (bP-tolerance<=bT) && (bT<=bP+tolerance) );
    }

    public ImageOutline(String path) {
        try {
            BufferedImage image = ImageIO.read(new File(path));
            this.image = image;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
package com.laith.sparkvr;

import java.io.IOException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Stack;
import javafx.util.Pair;
import javax.imageio.ImageIO;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class BuildingMapImageProcessor {

    private final BufferedImage buffer;
    private int width;
    private int height;

    private BufferedImage verticalLinesImage;
    private BufferedImage horizontalLinesImage;
    private ArrayList<Pair> mainPoints;
    private ArrayList<Line> lines;

    public BuildingMapImageProcessor(String path) throws IOException {
        buffer = ImageIO.read(new File(path));
        width = buffer.getWidth();
        height = buffer.getHeight();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setMainPointsInstance(ArrayList<Pair> instance) {
        mainPoints.clear();
        for (Pair point : instance) {
            mainPoints.add(point);
        }
    }

    public ArrayList<Pair> getMainPointsInstance() {
        ArrayList<Pair> instance = new ArrayList<>();
        for (Pair point : mainPoints) {
            instance.add(point);
        }
        return instance;
    }

    public ArrayList<Line> getLinesInstance() {
        ArrayList<Line> instance = new ArrayList<>();
        for (Line line : lines) {
            instance.add(line);
        }
        return instance;
    }

    public void setImage(BufferedImage img) {
        for (int j = 0; j < height; ++j) {
            for (int i = 0; i < width; ++i) {
                buffer.setRGB(i, j, img.getRGB(i, j));
            }
        }
    }

    public BufferedImage getImage() {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int j = 0; j < height; ++j) {
            for (int i = 0; i < width; ++i) {
                img.setRGB(i, j, buffer.getRGB(i, j));
            }
        }
        return img;
    }

    public void toBinaryImage(int scale) {
        //scale is from 1 (white image) to 256 (black image)

        for (int j = 0; j < height; ++j) {
            for (int i = 0; i < width; ++i) {

                int rgb = buffer.getRGB(i, j);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                if (r < scale && g < scale && b < scale) {
                    r = g = b = 0;
                } else {
                    r = g = b = 0xFF;
                }
                int v = (0xFF << 24) | (r << 16) | (g << 8) | b;
                buffer.setRGB(i, j, v);
            }
        }
    }

    private boolean valid(int i, int j) {
        return i >= 0 && i < width && j >= 0 && j < height;
    }

    private boolean erosionCheck(ArrayList<Pair> structuringElement, int i, int j) {
        for (Pair p : structuringElement) {
            int newI = i + (Integer) p.getKey();
            int newJ = j + (Integer) p.getValue();

            if (valid(newI, newJ)) {
                if (buffer.getRGB(newI, newJ) == 0xFFFFFFFF) {
                    return false;
                }
            } else {
                return false;
            }
        }

        return true;
    }

    private BufferedImage erosion(ArrayList<Pair> structuringElement) {
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int j = 0; j < height; ++j) {
            for (int i = 0; i < width; ++i) {
                if (erosionCheck(structuringElement, i, j)) {
                    newImage.setRGB(i, j, 0xFF000000);
                } else {
                    newImage.setRGB(i, j, 0xFFFFFFFF);
                }
            }
        }
        return newImage;
    }

    private BufferedImage getHorizontalLines(int pixels) {
        ArrayList<Pair> structuringElement = new ArrayList<>();

        for (int i = -pixels; i <= pixels; ++i) {
            structuringElement.add(new Pair(i, 0));
        }

        return erosion(structuringElement);
    }

    private BufferedImage getVerticalLines(int pixels) {
        ArrayList<Pair> structuringElement = new ArrayList<>();

        for (int j = -pixels; j <= pixels; ++j) {
            structuringElement.add(new Pair(0, j));
        }

        return erosion(structuringElement);
    }

    private void InitialCleaningToVerticalAndHorizontalLines(int scale) {
        verticalLinesImage = getVerticalLines(scale);
        horizontalLinesImage = getHorizontalLines(scale);
        for (int j = 0; j < height; ++j) {
            for (int i = 0; i < width; ++i) {
                //if the current pixel belongs to one of the two previous buffers paint them black
                if (verticalLinesImage.getRGB(i, j) == 0xFF000000 || horizontalLinesImage.getRGB(i, j) == 0xFF000000) {
                    buffer.setRGB(i, j, 0xFF000000);
                } //else paint them white
                else {
                    buffer.setRGB(i, j, 0xFFFFFFFF);
                }
            }
        }
    }

    private void correctCornersWithRedPaint(int scale) {
        //loop again after InitialCleaningToVerticalAndHorizontalLines
        //to correct corners by painting them red
        for (int j = 0; j < height; ++j) {
            for (int i = 0; i < width; ++i) {
                //upper-left corner
                if (j + scale < height && i + scale < width && verticalLinesImage.getRGB(i, j + scale) == 0xFF000000 && horizontalLinesImage.getRGB(i + scale, j) == 0xFF000000) {
                    for (int k = j; k <= j + scale; ++k) {
                        buffer.setRGB(i, k, 0xFF000000);
                    }
                    for (int k = i; k <= i + scale; ++k) {
                        buffer.setRGB(k, j, 0xFF000000);
                    }
                    buffer.setRGB(i, j, 0xFFFF0000);
                } //upper-right corner
                else if (j + scale < height && i - scale >= 0 && verticalLinesImage.getRGB(i, j + scale) == 0xFF000000 && horizontalLinesImage.getRGB(i - scale, j) == 0xFF000000) {
                    for (int k = j; k <= j + scale; ++k) {
                        buffer.setRGB(i, k, 0xFF000000);
                    }
                    for (int k = i - scale; k <= i; ++k) {
                        buffer.setRGB(k, j, 0xFF000000);
                    }
                    buffer.setRGB(i, j, 0xFFFF0000);
                } //down-left corner
                else if (j - scale >= 0 && i + scale < width && verticalLinesImage.getRGB(i, j - scale) == 0xFF000000 && horizontalLinesImage.getRGB(i + scale, j) == 0xFF000000) {
                    for (int k = j - scale; k <= j; ++k) {
                        buffer.setRGB(i, k, 0xFF000000);
                    }
                    for (int k = i; k <= i + scale; ++k) {
                        buffer.setRGB(k, j, 0xFF000000);
                    }
                    buffer.setRGB(i, j, 0xFFFF0000);
                } //down-right corner
                else if (j - scale >= 0 && i - scale >= 0 && verticalLinesImage.getRGB(i, j - scale) == 0xFF000000 && horizontalLinesImage.getRGB(i - scale, j) == 0xFF000000) {
                    for (int k = j - scale; k <= j; ++k) {
                        buffer.setRGB(i, k, 0xFF000000);
                    }
                    for (int k = i - scale; k <= i; ++k) {
                        buffer.setRGB(k, j, 0xFF000000);
                    }
                    buffer.setRGB(i, j, 0xFFFF0000);
                }
            }
        }
    }

    private void onePixelPerRedPointsHeap() {
        for (int j = 0; j < height; ++j) {
            for (int i = 0; i < width; ++i) {
                if (buffer.getRGB(i, j) == 0xFFFF0000) {
                    mainPoints.add(new Pair(i, j));
                    //we are using dfs to delete neighbour red pixels
                    Stack<Pair> stack = new Stack<>();
                    stack.add(new Pair(i, j));
                    buffer.setRGB(i, j, 0xFF000000);

                    while (!stack.empty()) {
                        Pair pair = stack.lastElement();
                        stack.pop();
                        int pairI = (Integer) pair.getKey();
                        int pairJ = (Integer) pair.getValue();
                        int[] addI = {0, 0, 1, -1};
                        int[] addJ = {1, -1, 0, 0};

                        for (int k = 0; k < 4; ++k) {
                            int newI = pairI + addI[k];
                            int newJ = pairJ + addJ[k];
                            if (valid(newI, newJ) && buffer.getRGB(newI, newJ) == 0xFFFF0000) {
                                buffer.setRGB(newI, newJ, 0xFF000000);
                                stack.add(new Pair(newI, newJ));
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isEndpoint(int scale, int i, int j) {
        boolean flag = true;
        if (buffer.getRGB(i, j) == 0xFFFFFFFF) {
            return false;
        }

        //endpoint is on the left
        //each endpoint must be at the edge of a line
        //this means theres a white line perpendicular on our line
        //in the (endpoint is on the left) case this white line is vertical and right to the left of the studying point
        //the length of white line is 2*scale + 1
        for (int k = j - scale; k <= j + scale; ++k) {
            if (buffer.getRGB(i - 1, k) != 0xFFFFFFFF) {
                flag = false;
            }
        }
        //the other side (right side here) must not be white
        if (buffer.getRGB(i + scale, j) == 0xFFFFFFFF
                //the rest two sides (up & down in oure case) must be white
                || buffer.getRGB(i, j - scale) != 0xFFFFFFFF
                || buffer.getRGB(i, j + scale) != 0xFFFFFFFF) {
            flag = false;
        }

        if (flag == true) {
            return true;
        }
        flag = true;

        //endpoint is on the right
        for (int k = j - scale; k <= j + scale; ++k) {
            if (buffer.getRGB(i + 1, k) != 0xFFFFFFFF) {
                flag = false;
            }
        }
        if (buffer.getRGB(i - scale, j) == 0xFFFFFFFF
                || buffer.getRGB(i, j - scale) != 0xFFFFFFFF
                || buffer.getRGB(i, j + scale) != 0xFFFFFFFF) {
            flag = false;
        }

        if (flag == true) {
            return true;
        }
        flag = true;

        //endpoint is on top
        for (int k = i - scale; k <= i + scale; ++k) {
            if (buffer.getRGB(k, j - 1) != 0xFFFFFFFF) {
                flag = false;
            }
        }
        if (buffer.getRGB(i, j + scale) == 0xFFFFFFFF
                || buffer.getRGB(i - scale, j) != 0xFFFFFFFF
                || buffer.getRGB(i + scale, j) != 0xFFFFFFFF) {
            flag = false;
        }

        if (flag == true) {
            return true;
        }
        flag = true;

        //endpoint is on bottom
        for (int k = i - scale; k <= i + scale; ++k) {
            if (buffer.getRGB(k, j + 1) != 0xFFFFFFFF) {
                flag = false;
            }
        }
        if (buffer.getRGB(i, j - scale) == 0xFFFFFFFF
                || buffer.getRGB(i - scale, j) != 0xFFFFFFFF
                || buffer.getRGB(i + scale, j) != 0xFFFFFFFF) {
            flag = false;
        }

        return flag;
    }

    private void correctEndpointsWithRedPaint(int scale) {
        for (int j = scale; j + scale < height; ++j) {
            for (int i = scale; i + scale < width; ++i) {
                if (isEndpoint(scale, i, j)) {
                    buffer.setRGB(i, j, 0xFFFF0000);
                }
            }
        }
    }

    public void createMainPoints(int scale) {
        mainPoints = new ArrayList<>();

        //first we clean image to only vertical and horisontal lines
        InitialCleaningToVerticalAndHorizontalLines(scale);
        //now we got the vertical and horizontal lines buffers filled
        //the cleaned image has vertical and horizontal lines without any corner
        //so we need to
        correctCornersWithRedPaint(scale);
        //now we got corners painted red
        //but many corners have more than one pixel painted
        //so we need to reduce that number to one pixel per corner
        onePixelPerRedPointsHeap();
        //the mainPoints list is now filled with one pixel for each corner
        //the image is only black and white again
        //but I forgot about the endpoints of the lines that are not corners
        correctEndpointsWithRedPaint(scale);
        //now we got endpoints painted red
        //but many endpoints have more than one pixel painted
        //so we need to reduce that number to one pixel per endpoint
        onePixelPerRedPointsHeap();
        //we added now one pixel for each endpoint to the mainPoints
        //the image is only black and white again
        //and mainPoints is fully calculated
    }

    public void tuneMainPoints(int scale) {
        for (int u = 0; u < mainPoints.size(); ++u) {
            int i = (Integer) mainPoints.get(u).getKey();
            int j = (Integer) mainPoints.get(u).getValue();

            sortMainPointsByKey();
            for (int v = 0; v < mainPoints.size(); ++v) {
                int nextPointI = (Integer) mainPoints.get(v).getKey();
                int nextPointJ = (Integer) mainPoints.get(v).getValue();

                if (nextPointI >= i - scale && nextPointI <= i + scale) {
                    mainPoints.remove(mainPoints.get(v));
                    mainPoints.add(new Pair(i, nextPointJ));
                    sortMainPointsByKey();
                }
            }

            sortMainPointsByValue();
            for (int v = 0; v < mainPoints.size(); ++v) {
                int nextPointI = (Integer) mainPoints.get(v).getKey();
                int nextPointJ = (Integer) mainPoints.get(v).getValue();

                if (nextPointJ >= j - scale && nextPointJ <= j + scale) {
                    mainPoints.remove(mainPoints.get(v));
                    mainPoints.add(new Pair(nextPointI, j));
                    sortMainPointsByValue();
                }
            }
        }
    }

    public void paintMainPoints(int color, int size) {
        for (Pair pixel : mainPoints) {
            int mainI = (Integer) pixel.getKey();
            int mainJ = (Integer) pixel.getValue();
            for (int j = mainJ; j > mainJ - size; --j) {
                int k = Math.abs(j - (mainJ - size + 1));
                for (int i = mainI - k; i <= mainI + k; ++i) {
                    if (valid(i, j)) {
                        buffer.setRGB(i, j, color);
                    }
                }
            }
            for (int j = mainJ; j < mainJ + size; ++j) {
                int k = Math.abs(j - (mainJ + size - 1));
                for (int i = mainI - k; i <= mainI + k; ++i) {
                    if (valid(i, j)) {
                        buffer.setRGB(i, j, color);
                    }
                }
            }
        }
    }

    private boolean thereIsLineBetween(Pair p1, Pair p2, int scale) {
        //a scan line of lenght 2*scale+1 scans the area between the two points
        //if it findes at least one non-white pixel at each scanning stage then the points are connected with a line

        int i1 = (Integer) p1.getKey();
        int j1 = (Integer) p1.getValue();
        int i2 = (Integer) p2.getKey();
        int j2 = (Integer) p2.getValue();
        if (i1 > i2) {
            int t = i1;
            i1 = i2;
            i2 = t;
        }
        if (j1 > j2) {
            int t = j1;
            j1 = j2;
            j2 = t;
        }

        if (i1 == i2) {
            for (int j = j1 + scale; j <= j2 - scale; ++j) {
                boolean flag = false;
                for (int i = i1 - scale; i <= i1 + scale; ++i) {
                    if (valid(i, j) && buffer.getRGB(i, j) != 0xFFFFFFFF) {
                        flag = true;
                    }
                }
                if (flag == false) {
                    return false;
                }
            }
            return true;
        }
        if (j1 == j2) {
            for (int i = i1 + scale; i <= i2 - scale; ++i) {
                boolean flag = false;
                for (int j = j1 - scale; j <= j1 + scale; ++j) {
                    if (valid(i, j) && buffer.getRGB(i, j) != 0xFFFFFFFF) {
                        flag = true;
                    }
                }
                if (flag == false) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private void paintLineBetween(Pair p1, Pair p2, int color, int size) {
        int i1 = (Integer) p1.getKey();
        int j1 = (Integer) p1.getValue();
        int i2 = (Integer) p2.getKey();
        int j2 = (Integer) p2.getValue();

        //making p1 the smallest in coordinates
        if (i1 > i2) {
            int t = i1;
            i1 = i2;
            i2 = t;
        }
        if (j1 > j2) {
            int t = j1;
            j1 = j2;
            j2 = t;
        }

        if (i1 == i2) {
            for (int j = j1; j <= j2; ++j) {
                for (int i = i1 - size; i <= i1 + size; ++i) {
                    if (valid(i, j)) {
                        buffer.setRGB(i, j, color);
                    }
                }
            }
        }
        if (j1 == j2) {
            for (int i = i1; i <= i2; ++i) {
                for (int j = j1 - size; j <= j1 + size; ++j) {
                    if (valid(i, j)) {
                        buffer.setRGB(i, j, color);
                    }
                }
            }
        }
    }

    private void sortMainPointsByKey() {
        //sorting the mainPoints list of pairs by first element in pairs
        Collections.sort(mainPoints, (Pair p1, Pair p2) -> {
            if (Objects.equals((Integer) p1.getKey(), (Integer) p2.getKey())) {
                return (Integer) p1.getValue() - (Integer) p2.getValue();
            }
            return (Integer) p1.getKey() - (Integer) p2.getKey();
        });
    }

    private void sortMainPointsByValue() {
        //sorting the mainPoints list of pairs by second element in pairs
        Collections.sort(mainPoints, (Pair p1, Pair p2) -> {
            if (Objects.equals(p1.getValue(), p2.getValue())) {
                return (Integer) p1.getKey() - (Integer) p2.getKey();
            }
            return (Integer) p1.getValue() - (Integer) p2.getValue();
        });
    }

    public void createLines(int scale) {
        lines = new ArrayList<>();
        ArrayList<Line> tempLines = new ArrayList<>();

        sortMainPointsByKey();
        for (int u = 0; u < mainPoints.size() - 1; ++u) {
            int v = u + 1;
            while (v != mainPoints.size()
                    && Objects.equals((Integer) mainPoints.get(u).getKey(), (Integer) mainPoints.get(v).getKey())
                    && thereIsLineBetween(mainPoints.get(u), mainPoints.get(v), scale)) {
                ++v;
            }

            tempLines.add(new Line(mainPoints.get(u), mainPoints.get(v - 1)));
            u = v - 1;
        }
        sortMainPointsByValue();
        for (int u = 0; u < mainPoints.size() - 1; ++u) {
            int v = u + 1;
            while (v != mainPoints.size()
                    && Objects.equals((Integer) mainPoints.get(u).getValue(), (Integer) mainPoints.get(v).getValue())
                    && thereIsLineBetween(mainPoints.get(u), mainPoints.get(v), scale)) {
                ++v;
            }

            tempLines.add(new Line(mainPoints.get(u), mainPoints.get(v - 1)));
            u = v - 1;
        }
        
        for(Line newLine: tempLines){
            boolean validToAdd = true;
            for(Line line: lines){
                if(line.equals(newLine)){
                    validToAdd = false;
                    break;
                }
            }
            if(validToAdd)
                lines.add(newLine);
        }
    }

    public void paintLines(int color, int size) {
        if (lines == null) {
            return;
        }
        for (Line line : lines) {
            paintLineBetween(line.getP1(), line.getP2(), color, size);
        }
    }

    public void exportImage(String path) throws IOException {
        ImageIO.write(buffer, "PNG", new File(path));
    }

    public void exportCleanImage(String path) throws IOException {
        if (lines == null) {
            return;
        }
        paintLines(0xFFFF0000, 1);
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int j = 0; j < height; ++j) {
            for (int i = 0; i < width; ++i) {
                if (buffer.getRGB(i, j) != 0xFF000000 && buffer.getRGB(i, j) != 0xFFFFFFFF) {
                    img.setRGB(i, j, 0xFF000000);
                } else {
                    img.setRGB(i, j, 0xFFFFFFFF);
                }
            }
        }
        ImageIO.write(img, "PNG", new File(path));
    }
    
    public String exportLinesJSON() throws IOException
    {
        JSONObject obj = new JSONObject();
        JSONArray lines = new JSONArray();
        for(Line line: this.lines)
        {
            JSONObject ob = new JSONObject();
            JSONArray arr1 = new JSONArray();
            arr1.add(line.getI1());
            arr1.add(line.getJ1());
            ob.put("start", arr1);
            lines.add(ob);
            JSONArray arr2 = new JSONArray();
            arr2.add(line.getI2());
            arr2.add(line.getJ2());
            ob.put("end", arr2);
            lines.add(ob);
        }
        obj.put("lines", lines);
        return obj.toJSONString();
    }
    
    public void exprortLinesJsonFile(String uri) throws IOException 
    {
        FileWriter file = new FileWriter(uri);
        file.write(this.exportLinesJSON());
        file.close();
    }
}

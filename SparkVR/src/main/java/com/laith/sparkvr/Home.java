package com.laith.sparkvr;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import static spark.Spark.*;

/**
 *
 * @author bsbos
 */
public class Home {
    public static void main(String[] args) {
        get("/walls", (req, res) -> {
            int binary = Integer.parseInt(req.queryParams("binary"));
            int mainPoints = Integer.parseInt(req.queryParams("mainpoints"));
            int tunePoints = Integer.parseInt(req.queryParams("tunepoints"));
            int createLines = Integer.parseInt(req.queryParams("createlines"));
            BuildingMapImageProcessor img = new BuildingMapImageProcessor(req.queryParams("img"));
            img.toBinaryImage(binary);
            img.createMainPoints(mainPoints);
            img.tuneMainPoints(tunePoints);
            img.createLines(createLines);
            return img.exportLinesJSON();
        });
    }
}

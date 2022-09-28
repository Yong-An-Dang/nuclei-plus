package com.jediterm.terminal.ui.settings;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.awt.*;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


@Slf4j
public class ColorScheme {
    private Color black = new Color(0x000000);          //Black
    private Color red = new Color(0xcd0000);            //Red
    private Color green = new Color(0x00cd00);          //Green
    private Color yellow = new Color(0xcdcd00);         //Yellow
    private Color blue = new Color(0x1e90ff);           //Blue
    private Color magenta = new Color(0xcd00cd);        //Magenta
    private Color cyan = new Color(0x00cdcd);           //Cyan
    private Color white = new Color(0xe5e5e5);          //White
    //Bright versions of the ISO colors
    private Color brightBlack = new Color(0x4c4c4c);    //Black
    private Color brightRed = new Color(0xff0000);      //Red
    private Color brightGreen = new Color(0x00ff00);    //Green
    private Color brightYellow = new Color(0xffff00);   //Yellow
    private Color brightBlue = new Color(0x4682b4);     //Blue
    private Color brightMagenta = new Color(0xff00ff);  //Magenta
    private Color brightCyan = new Color(0x00ffff);     //Cyan
    private Color brightWhite = new Color(0xffffff);    //White
    private Color[] colors;
    private Color backgroundColor = new Color(0xffffff);
    private Color foregroundColor = new Color(0x4d4d4c);
    private Color cursorColor = new Color(0x4d4d4c);
    private Color selectedColor = new Color(0xd6d6d6);

    public ColorScheme(String colorScheme) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("theme/" + colorScheme + ".json");
        if (inputStream == null){
            log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }
        String text = "";
        try{
            text = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }catch (Exception e){
            e.printStackTrace();
        }

        JSONObject object = JSONObject.parseObject(text);
        backgroundColor = new Color(Integer.parseInt(object.getString("background_color").replace("#", ""), 16));
        foregroundColor = new Color(Integer.parseInt(object.getString("foreground_color").replace("#", ""), 16));
        cursorColor = new Color(Integer.parseInt(object.getString("cursor_color").replace("#", ""), 16));
        selectedColor = new Color(Integer.parseInt(object.getString("selected_color").replace("#", ""), 16));

        JSONArray ansiColors = object.getJSONArray("ansi_color");
        black = new Color(Integer.parseInt(ansiColors.getString(0).replace("#", ""), 16));
        red = new Color(Integer.parseInt(ansiColors.getString(1).replace("#", ""), 16));
        green = new Color(Integer.parseInt(ansiColors.getString(2).replace("#", ""), 16));
        yellow = new Color(Integer.parseInt(ansiColors.getString(3).replace("#", ""), 16));
        blue = new Color(Integer.parseInt(ansiColors.getString(4).replace("#", ""), 16));
        magenta = new Color(Integer.parseInt(ansiColors.getString(5).replace("#", ""), 16));
        cyan = new Color(Integer.parseInt(ansiColors.getString(6).replace("#", ""), 16));
        white = new Color(Integer.parseInt(ansiColors.getString(7).replace("#", ""), 16));
        brightBlack = new Color(Integer.parseInt(ansiColors.getString(8).replace("#", ""), 16));
        brightRed = new Color(Integer.parseInt(ansiColors.getString(9).replace("#", ""), 16));
        brightGreen = new Color(Integer.parseInt(ansiColors.getString(10).replace("#", ""), 16));
        brightYellow = new Color(Integer.parseInt(ansiColors.getString(11).replace("#", ""), 16));
        brightBlue = new Color(Integer.parseInt(ansiColors.getString(12).replace("#", ""), 16));
        brightMagenta = new Color(Integer.parseInt(ansiColors.getString(13).replace("#", ""), 16));
        brightCyan = new Color(Integer.parseInt(ansiColors.getString(14).replace("#", ""), 16));
        brightWhite = new Color(Integer.parseInt(ansiColors.getString(15).replace("#", ""), 16));

        colors = new Color[]{
                black,
                red,
                green,
                yellow,
                blue,
                magenta,
                cyan,
                white,
                brightBlack,
                brightRed,
                brightGreen,
                brightYellow,
                brightBlue,
                brightMagenta,
                brightCyan,
                brightWhite
        };
    }

    public Color getBlack() {
        return black;
    }

    public void setBlack(Color black) {
        this.black = black;
    }

    public Color getRed() {
        return red;
    }

    public void setRed(Color red) {
        this.red = red;
    }

    public Color getGreen() {
        return green;
    }

    public void setGreen(Color green) {
        this.green = green;
    }

    public Color getYellow() {
        return yellow;
    }

    public void setYellow(Color yellow) {
        this.yellow = yellow;
    }

    public Color getBlue() {
        return blue;
    }

    public void setBlue(Color blue) {
        this.blue = blue;
    }

    public Color getMagenta() {
        return magenta;
    }

    public void setMagenta(Color magenta) {
        this.magenta = magenta;
    }

    public Color getCyan() {
        return cyan;
    }

    public void setCyan(Color cyan) {
        this.cyan = cyan;
    }

    public Color getWhite() {
        return white;
    }

    public void setWhite(Color white) {
        this.white = white;
    }

    public Color getBrightBlack() {
        return brightBlack;
    }

    public void setBrightBlack(Color brightBlack) {
        this.brightBlack = brightBlack;
    }

    public Color getBrightRed() {
        return brightRed;
    }

    public void setBrightRed(Color brightRed) {
        this.brightRed = brightRed;
    }

    public Color getBrightGreen() {
        return brightGreen;
    }

    public void setBrightGreen(Color brightGreen) {
        this.brightGreen = brightGreen;
    }

    public Color getBrightYellow() {
        return brightYellow;
    }

    public void setBrightYellow(Color brightYellow) {
        this.brightYellow = brightYellow;
    }

    public Color getBrightBlue() {
        return brightBlue;
    }

    public void setBrightBlue(Color brightBlue) {
        this.brightBlue = brightBlue;
    }

    public Color getBrightMagenta() {
        return brightMagenta;
    }

    public void setBrightMagenta(Color brightMagenta) {
        this.brightMagenta = brightMagenta;
    }

    public Color getBrightCyan() {
        return brightCyan;
    }

    public void setBrightCyan(Color brightCyan) {
        this.brightCyan = brightCyan;
    }

    public Color getBrightWhite() {
        return brightWhite;
    }

    public void setBrightWhite(Color brightWhite) {
        this.brightWhite = brightWhite;
    }

    public Color[] getColors() {
        return colors;
    }

    public void setColors(Color[] colors) {
        this.colors = colors;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public Color getCursorColor() {
        return cursorColor;
    }

    public void setCursorColor(Color cursorColor) {
        this.cursorColor = cursorColor;
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
    }
}

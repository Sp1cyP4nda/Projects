package com.sp1cyp4nda;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class Initialize {
    public static void main(String[] args) {
        String resourcePackPath = "resourcepacks/mctg/";
        new File(resourcePackPath + "assets/card/items").mkdirs();
        new File(resourcePackPath + "assets/card/models/item").mkdirs();
        new File(resourcePackPath + "assets/card/textures/item").mkdirs();
        new File(resourcePackPath + "pack.mcmeta");
        try {
            FileWriter file = new FileWriter(resourcePackPath + "pack.mcmeta");
            file.write("{\r\n" +
                    "    \"pack\": {\r\n" +
                    "      \"pack_format\": 42,\r\n" +
                    "      \"description\": \"Minecraft the Gathering\"\r\n" +
                    "    }\r\n" +
                    "}"
            );
            file.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        String dataPackPath = args[0] + "/mctg_functions/";
        new File(dataPackPath + "data/mctg/function/cards").mkdirs();
        new File(dataPackPath + "pack.mcmeta");
        try {
            FileWriter file = new FileWriter(dataPackPath + "pack.mcmeta");
            file.write("{\r\n" +
                    "    \"pack\": {\r\n" +
                    "      \"pack_format\": 48,\r\n" +
                    "      \"description\": \"Minecraft the Gathering\"\r\n" +
                    "    }\r\n" +
                    "}"
            );
            file.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        new File(dataPackPath + "/logs").mkdirs();

        // Download card back textures file
        try {
            // Download the textures file
            URL imageURL = new URL("https://static.wikia.nocookie.net/mtgsalvation_gamepedia/images/f/f8/Magic_card_back.jpg/revision/latest?cb=20140813141013");
            BufferedImage image = ImageIO.read(imageURL);
            File imageFile = new File(resourcePackPath + "assets/card/textures/item/card_back.png");
            ImageIO.write(image,"png",imageFile);
            System.out.println("Successfully downloaded file: " + imageFile);

            // Calculate new dimensions
            BufferedImage oldImage = ImageIO.read(imageFile);
            int oldHeight = oldImage.getHeight();
            int oldWidth  = oldImage.getWidth();
            int newHeight = 200;
            int newWidth  = (int) ((double) oldWidth / oldHeight * newHeight);

            // Resize the textures file
            BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = newImage.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.drawImage(oldImage, 0, 0, newWidth, newHeight, null);
            graphics.dispose();

            // Save the new file
            ImageIO.write(newImage, "png", imageFile);
            System.out.println("Image resized successfully");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}

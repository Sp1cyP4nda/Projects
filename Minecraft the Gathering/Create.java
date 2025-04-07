package com.sp1cyp4nda;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

public class Create {
    String material    = " ";
    String search      = " ";
    String name        = " ";
    String filename    = " ";
    String mana_cost   = " ";
    String type_line   = " ";
    String oracle_text = " ";
    String image_url   = " ";
    String filePath = " ";

    // Getters
    public String getMaterial() {
        return material;
    }
    public String getSearch() {
        return search;
    }
    public String getName() {
        return name;
    }
    public String getFileName() {
        return filename;
    }
    public String getManaCost() {
        return mana_cost;
    }
    public String getType() {
        return type_line;
    }
    public String getOracleText() {
        return oracle_text;
    }
    public String getImageUrl() {
        return image_url;
    }
    public String getFilePath() {
        return filePath;
    }

    // Setters
    public void setMaterial(String newMaterial) {
        this.material = newMaterial;
    }public void setSearch(String newSearch) {
        this.search = newSearch
            .replace(",","")
            .replace(" ","+")
            .toLowerCase();
    }
    public void setName(String newName) {
        this.name = newName;
    }
    public void setFileName(String newFileName) {
        this.filename = newFileName;
    }
    public void setManaCost(String newManaCost) {
        this.mana_cost = newManaCost;
    }
    public void setType(String newType) {
        this.type_line = newType;
    }
    public void setOracleText(String newOracleText) {
        this.oracle_text = newOracleText;
    }
    public void setImageURL(String newImageURL) {
        this.image_url = newImageURL;
    }
    public void setLogFilePath(String newPath) {
        this.filePath = newPath;
    }

    // Create logger
    private static final Logger logger = Logger.getLogger(Create.class.getName());

    private static void setupLogger(String logFilePath) {
        try {
            FileHandler fileHandler = new FileHandler(logFilePath, true);
            fileHandler.setFormatter(new SimpleFormatter());
            Logger rootLogger = Logger.getLogger("");
            rootLogger.addHandler(fileHandler);
            rootLogger.setLevel(Level.INFO);
        } catch (IOException e) {
            System.err.println("Failed to set up logger: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            // Set path\to\resources
            String resourcePATH = "resourcepacks/mctg/";
            String datapackPATH = args[2] + "/mctg_functions/";

            // Create query
            Create search = new Create();
            search.setSearch(args[0]);

            // Setup logger for new query
            setupLogger(datapackPATH + "logs/" + search.getSearch() + ".log");
            logger.info("New query: " + search.getSearch());

            URL url = new URL("https://api.scryfall.com/cards/named?fuzzy=" + search.getSearch());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                System.out.println("Error code " + responseCode + ". Quitting.");
                logger.info("Error code " + responseCode + ". Quitting.");
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                logger.info("Card created successfully");               

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                String json = response.toString();
                Map<String, String> extractedData = extractJsonValues(json, "name", "mana_cost", "type_line", "oracle_text", "image_uris");

                // Create the card
                Create card = new Create();
                String material = (args.length == 1 || args[1].isBlank()) ? "paper" : args[1];
                card.setMaterial(material); // Check to see if this works
                card.setName(extractedData.get("name")
                    .replace(",", "")
                    .replace(" "," "));
                card.setFileName(extractedData.get("name")
                    .replace(",", "")
                    .replace(" ", "_")
                    .toLowerCase());
                card.setManaCost(extractedData.get("mana_cost"));
                card.setType(extractedData.get("type_line"));
                card.setOracleText(extractedData.get("oracle_text"));
                card.setImageURL(extractedData.get("image_uris"));

                //Setup card logger
                setupLogger(datapackPATH + "logs/" + card.getFileName() + ".log");
                logger.info("New query: " + card.getFileName());

                // Create the Backend Files
                createFile(resourcePATH + "assets/card/items/", card.getFileName(), "json");
                createFile(resourcePATH + "assets/card/models/item/", card.getFileName(), "json");

                // Write to the Backend Files
                writeToFile(resourcePATH + "assets/card/items/" + card.getFileName() + ".json", 
                    "{\r\n" +
                    "    \"model\": {\r\n" +
                    "        \"type\": \"minecraft:model\",\r\n" +
                    "        \"model\": \"card:item/" +
                    card.getFileName() +
                    "\"\r\n" +
                    "    }\r\n" +
                    "}"
                );
                writeToFile(resourcePATH + "assets/card/models/item/" + card.getFileName() + ".json",
                    "{\r\n" +
                    "    \"parent\": \"minecraft:item/handheld\",\r\n" +
                    "    \"textures\": {\r\n" +
                    "      \"layer0\": \"card:item/card_back\",\r\n" +
                    "      \"layer1\": \"card:item/" +
                    card.getFileName() +
                    "\",\r\n" +
                    "      \"layer2\": \"minecraft:block/oak_log\"\r\n" +
                    "    },\r\n" +
                    "    \"elements\": [\r\n" +
                    "        {\r\n" +
                    "          \"from\": [\r\n" +
                    "            0,\r\n" +
                    "            0,\r\n" +
                    "            7.85\r\n" +
                    "          ],\r\n" +
                    "          \"to\": [\r\n" +
                    "            16,\r\n" +
                    "            16,\r\n" +
                    "            8\r\n" +
                    "          ],\r\n" +
                    "          \"faces\":{\r\n" +
                    "            \"south\": {\r\n" +
                    "              \"texture\": \"#layer1\"\r\n" +
                    "            },\r\n" +
                    "            \"north\": {\r\n" +
                    "              \"texture\": \"#layer0\"\r\n" +
                    "            },\r\n" +
                    "            \"up\": {\r\n" +
                    "              \"texture\": \"#layer2\"\r\n" +
                    "            },\r\n" +
                    "            \"down\": {\r\n" +
                    "              \"texture\": \"#layer2\"\r\n" +
                    "            },\r\n" +
                    "            \"west\": {\r\n" +
                    "              \"texture\": \"#layer2\"\r\n" +
                    "            },\r\n" +
                    "            \"east\": {\r\n" +
                    "              \"texture\": \"#layer2\"\r\n" +
                    "            }\r\n" +
                    "          }\r\n" +
                    "        }\r\n" +
                    "    ],\r\n" +
                    "    \"display\": {\r\n" +
                    "      \"thirdperson_righthand\": {\r\n" +
                    "            \"rotation\": [ 45, 0, -5 ],\r\n" +
                    "            \"translation\": [ 0, 6, -1 ],\r\n" +
                    "            \"scale\": [ 0.75, 1, 1 ]\r\n" +
                    "        },\r\n" +
                    "        \"thirdperson_lefthand\": {\r\n" +
                    "            \"rotation\": [ 45, 0, -5 ],\r\n" +
                    "            \"translation\": [ 0, 6, -1 ],\r\n" +
                    "            \"scale\": [ 0.75, 1, 1 ]\r\n" +
                    "        },\r\n" +
                    "        \"firstperson_righthand\": {\r\n" +
                    "            \"rotation\": [ 0, 0, 5 ],\r\n" +
                    "            \"translation\": [ 10, 0, -10 ],\r\n" +
                    "            \"scale\": [ 1.25, 1.25, 1.25 ]\r\n" +
                    "        },\r\n" +
                    "        \"firstperson_lefthand\": {\r\n" +
                    "            \"rotation\": [ 0, 0, 5 ],\r\n" +
                    "            \"translation\": [ 10, 0, -10 ],\r\n" +
                    "            \"scale\": [ 1.25, 1.25, 1.25 ]\r\n" +
                    "        },\r\n" +
                    "        \"gui\": {\r\n" +
                    "            \"rotation\": [ -15, 20, -5 ],\r\n" +
                    "            \"translation\": [ 0, 0, 0 ],\r\n" +
                    "            \"scale\": [ 0.65, 0.85, 0.85 ]\r\n" +
                    "        }, \r\n" +
                    "        \"fixed\": {\r\n" +
                    "            \"rotation\": [ 0, -180, 0 ],\r\n" +
                    "            \"translation\": [ 0, 0, 0 ],\r\n" +
                    "            \"scale\": [ 0.75, 1.15, 1.15 ]\r\n" +
                    "        },\r\n" +
                    "        \"ground\": {\r\n" +
                    "            \"rotation\": [ 0, 0, 0 ],\r\n" +
                    "            \"translation\": [ 0, 2.5, 0 ],\r\n" +
                    "            \"scale\": [ 0.35, 0.55, 0.55 ]\r\n" +
                    "        }\r\n" +
                    "  }\r\n" +
                    "}"
                );

                // Create the textures file
                try {
                    // Download the textures file
                    URL imageURL = new URL(card.getImageUrl());
                    BufferedImage image = ImageIO.read(imageURL);
                    File imageFile = new File(resourcePATH + "assets/card/textures/item/" + card.getFileName() + ".png");
                    ImageIO.write(image,"png",imageFile);
                    System.out.println("Successfully downloaded file: " + imageFile);

                    // Calculate new dimensions
                    BufferedImage oldImage = ImageIO.read(imageFile);
                    int oldHeight = oldImage.getHeight();
                    int oldWidth  = oldImage.getWidth();
                    int newHeight = oldHeight - 4;
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

                // Create trigger mcfunction
                String mcfunctionPATH = "data/mctg/function/cards/";
                createFile(datapackPATH + mcfunctionPATH, card.getFileName(), "mcfunction");
                writeToFile(datapackPATH + mcfunctionPATH + card.getFileName() + ".mcfunction",
                    "execute as @p " +
                        "if items entity @p weapon.mainhand minecraft:" +
                        card.getMaterial() +
                        " run item replace entity @p weapon.mainhand " +
                        "with minecraft:" +
                        card.getMaterial() +
                        "[minecraft:custom_name=\"" +
                        card.getName() +
                        "\",lore=[\"{\'color\':\'gold\',\'text\':\'" +
                        card.getManaCost() +
                        "\'}\",\"{\'color\':\'aqua\',\'text\':\'" +
                        card.getType() +
                        "\'}\",\"{\'text\':\'" +
                        card.getOracleText() +
                        "\'}\"],minecraft:item_model=\"card:" +
                        card.getFileName() +
                        "\"]"
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Extract JSON Values
    public static Map<String, String> extractJsonValues(String json, String... keys) {
        Map<String, String> values = new HashMap<>();

        for (String key : keys) {
            // Updated regex to handle multi-line strings
            Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"((?:\\\\\"|[^\"])*)\"", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(json);

            // Extract Image URIs
            if (key.equals("image_uris")) {
                String extractedURI = extractURIs(json);

                values.put(key, extractedURI);
            }

            if (matcher.find()) {
                String extractedText = matcher.group(1)
                        // .replace("\\\"", "\"") // Handle escaped double quotes
                        .replace("'","\\\\'") // Handle escaped single quotes
                        .replace("\\n", "\'}\",\"{\'text\':\'\'}\",\"{\'text\':\'") // Handle newlines
                        .replace("\u2014", "-"); // Fix em dash encoding issue

                // Format Oracle Text
                if (key.equals("oracle_text")) {
                    extractedText = formatOracleText(extractedText, 65);
                }

                values.put(key, extractedText);
            }
        }

        return values;
    }

    // Format Oracle Text
    public static String formatOracleText(String text, int maxLineLength) {
        StringBuilder formattedText = new StringBuilder();
        int lastBreak = 0;

        while (lastBreak + maxLineLength < text.length()) {
            int nextBreak = text.lastIndexOf(" ", lastBreak + maxLineLength);
            if (nextBreak == -1) break; // No whitespace found, avoid infinite loop

            formattedText.append(text, lastBreak, nextBreak).append("\'}\",\"{\'text\':\'");
            lastBreak = nextBreak + 1; // Move past the whitespace
        }

        // Append remaining text
        formattedText.append(text.substring(lastBreak)
            // .replace("\"","\\\"")
        );

        return formattedText.toString();
    }

    // Extract URI
    public static String extractURIs(String json) {
        Pattern pattern = Pattern.compile("\"small\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);

        return matcher.find() ? matcher.group(1) : "No small image found";
    }

    // File Creation
    public static void createFile(String path, String filename, String type) {
        try {
          File newFile = new File(path + filename + "." + type);
          if (newFile.createNewFile()) {
            System.out.println("File created: " + newFile.getName());
          } else {
            System.out.println("The file " + filename + " already exists.");
          }
        } catch (IOException e) {
          System.out.println("An error occurred.");
          e.printStackTrace();
        }
    }

    // Write to File
    public static void writeToFile(String filepath, String contents) {
        try {
            FileWriter file = new FileWriter(filepath);
            file.write(contents);
            file.close();
            System.out.println("Successfully wrote to file: " + filepath);
        } catch (IOException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
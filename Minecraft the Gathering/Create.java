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
    String material = " ";
    String search = " ";
    String name = " ";
    String filename = " ";
    String mana_cost = " ";
    String type_line = " ";
    String oracle_text = " ";
    String image_url = " ";
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
    }

    public void setSearch(String newSearch) {
        this.search = newSearch
                .replace(",", "")
                .replace(" ", "+")
                .replace("\'", "%27")
                .toLowerCase();
    }

    public void setName(String newName) {
        this.name = newName
                .replace(",", "")
                .replace("\\\\'", "'")
                .replace(" ", " ");
    }

    public void setFileName(String newFileName) {
        this.filename = newFileName
                .replace(",", "")
                .replace("'", "")
                .replace("\\\\", "")
                .replace("!", "")
                .replace("?", "")
                // .replace("/ ","") // Can minecraft:custom_name handle / or |
                .replace("/", "l") // Can minecraft:custom_name handle // or ||
                .replace(" ", "_")
                .toLowerCase();
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

    // Extract JSON Values
    public static Map<String, String> extractJsonValues(String json, String... keys) {
        Map<String, String> values = new HashMap<>();
        // String[] faces = null;
        for (String key : keys) {
            if (Pattern.matches(key, "card_faces")) {
                Pattern facesPattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(\\[.*?\\}\\])", Pattern.DOTALL);
                Matcher facesMatcher = facesPattern.matcher(json);

                if (facesMatcher.find()) {
                    String extractedText = facesMatcher.group(1)
                            .replace("\u2014", "-");
                    values.put(key, extractedText);
                    logger.info("Faces values: " + values);
                }
            } else {
                Pattern regularPattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"((?:\\\\\"|[^\"])*)\"",
                        Pattern.DOTALL);
                Matcher regularMatcher = regularPattern.matcher(json);
                if (key.equals("image_uris")) {
                    String extractedURI = extractURIs(json);
                    logger.info("URI == " + extractedURI);
                    values.put(key, extractedURI);
                }
                if (regularMatcher.find()) {
                    String extractedText = regularMatcher.group(1)
                            .replace("'", "\\\\'")
                            .replace("\u2014", "-");
                    if (key.equals("oracle_text")) {
                        extractedText = formatOracleText(extractedText, 65);
                    }
                    values.put(key, extractedText);
                    logger.info("Other values: " + values);
                }
            }
        }
        logger.info("Values: " + values);
        return values;
    }

    // Format Oracle Text
    public static String formatOracleText(String text, int maxLineLength) {
        StringBuilder formattedText = new StringBuilder();
        int lastBreak = 0;

        while (lastBreak + maxLineLength < text.length()) {
            int nextBreak = text.lastIndexOf(" ", lastBreak + maxLineLength);
            if (nextBreak == -1)
                break; // No whitespace found, avoid infinite loop

            formattedText.append(text, lastBreak, nextBreak).append("\'}\",\"{\'text\':\'");
            lastBreak = nextBreak + 1; // Move past the whitespace
        }

        // Append remaining text
        formattedText.append(text.substring(lastBreak)
        // .replace("\"","\\\"")
        );

        logger.info("Oracle Text: " + formattedText);
        return formattedText
                .toString()
                // .replace("'","\\\\'") // Handle escaped single quotes
                .replace("\\n", "\'}\",\"{\'text\':\'\'}\",\"{\'text\':\'"); // Handle newlines;
    }

    // Extract URI
    public static String extractURIs(String json) {
        // logger.info("Test 1: " + json);
        Pattern pattern = Pattern.compile("\"small\"\\s*:\\s*\"([^\"]+)\"");
        // logger.info("Test 2: " + json);
        Matcher matcher = pattern.matcher(json);
        logger.info("Test URIs: " + matcher);
        return matcher.find() ? matcher.group(1) : "No small image found";
    }

    // File Creation
    public static void createFile(String path, String filename, String type) {
        try {
            File newFile = new File(path + filename + "." + type);
            if (newFile.createNewFile()) {
                logger.info("File created: " + newFile.getName());
            } else {
                logger.info("The file " + filename + " already exists.");
            }
        } catch (IOException e) {
            logger.info("An error occurred. Could not create " + filename);
            e.printStackTrace();
        }
    }

    // Write to File
    public static void writeToFile(String filepath, String contents) {
        try {
            FileWriter file = new FileWriter(filepath);
            file.write(contents);
            file.close();
            logger.info("Successfully wrote to file: " + filepath);
        } catch (IOException e) {
            logger.info("An error occurred. Could not write to file.");
            e.printStackTrace();
        }
    }

    // Make the backend files
    public static void makeItemFiles(String path, String filename, String imgURL) {
        createFile(path + "assets/card/items/", filename, "json");
        createFile(path + "assets/card/models/item/", filename, "json");

        // Write to the Backend Files
        writeToFile(path + "assets/card/items/" + filename + ".json",
                "{\r\n" +
                        "    \"model\": {\r\n" +
                        "        \"type\": \"minecraft:model\",\r\n" +
                        "        \"model\": \"card:item/" +
                        filename +
                        "\"\r\n" +
                        "    }\r\n" +
                        "}");
        writeToFile(path + "assets/card/models/item/" + filename + ".json",
                "{\r\n" +
                        "    \"parent\": \"minecraft:item/handheld\",\r\n" +
                        "    \"textures\": {\r\n" +
                        "      \"layer0\": \"card:item/card_back\",\r\n" +
                        "      \"layer1\": \"card:item/" +
                        filename +
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
                        "}");
        // Create the textures file
        try {
            // Download the textures file
            URL imageURL = new URL(imgURL);
            BufferedImage image = ImageIO.read(imageURL);
            File imageFile = new File(path + "assets/card/textures/item/" + filename + ".png");
            ImageIO.write(image, "png", imageFile);
            logger.info("Successfully downloaded file: " + imageFile);

            // Calculate new dimensions
            BufferedImage oldImage = ImageIO.read(imageFile);
            int oldHeight = oldImage.getHeight();
            int oldWidth = oldImage.getWidth();
            int newHeight = oldHeight - 4;
            int newWidth = (int) ((double) oldWidth / oldHeight * newHeight);

            // Resize the textures file
            BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = newImage.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.drawImage(oldImage, 0, 0, newWidth, newHeight, null);
            graphics.dispose();

            // Save the new file
            ImageIO.write(newImage, "png", imageFile);
            logger.info("Image resized successfully");
        } catch (IOException e) {
            logger.info("An error occurred. Could not create image file.");
            e.printStackTrace();
        }
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
            logger.info("\n\nNew query: " + search.getSearch());

            URL url = new URL("https://api.scryfall.com/cards/named?fuzzy=" + search.getSearch());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                logger.info("Error code " + responseCode + ". Quitting.");
                logger.info("Error code " + responseCode + ". Quitting.");
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                // logger.info("Card found: " + search.getSearch());

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                String json = response.toString();
                // logger.info(json);
                Map<String, String> checkLayout = extractJsonValues(json, "layout");

                // Set global variables
                String card_layout = checkLayout.get("layout");
                String material = args[1];
                String mcfunctionPATH = "data/mctg/function/cards/";
                logger.info("\n\nOutside conditionals; Card layout == " + card_layout + " " + material);

                // Create the card, depending on card_layout
                if (Pattern.matches(card_layout, "split") ||
                        Pattern.matches(card_layout, "flip") ||
                        Pattern.matches(card_layout, "adventure")) {
                    logger.info("Inside layout: " + card_layout + " " + material);
                    Map<String, String> extractedURI = extractJsonValues(json, "image_uris");
                    Map<String, String> extractedData = extractJsonValues(json, "card_faces");

                    logger.info("Successfully extracted data:\n" + extractedURI + "\n" + extractedData);

                    // Separate the cards
                    String[] card = extractedData.get("card_faces")
                            .replace("\n", "")
                            .replace("[{", "")
                            .replace("}]", "")
                            .split("},");
                    logger.info("Cards:\n" + card);

                    Map<String, String> cardOne = extractJsonValues(card[0],
                            "name",
                            "mana_cost",
                            "type_line",
                            "oracle_text");
                    Map<String, String> cardTwo = extractJsonValues(card[1],
                            "name",
                            "mana_cost",
                            "type_line",
                            "oracle_text");
                    logger.info("Cards created\n" + cardOne + "\n" + cardTwo);

                    // Create the cards
                    Create card1 = new Create();
                    card1.setMaterial(material);
                    card1.setName(cardOne.get("name"));
                    card1.setFileName(cardOne.get("name"));
                    card1.setManaCost(cardOne.get("mana_cost"));
                    card1.setType(cardOne.get("type_line"));
                    card1.setOracleText(cardOne.get("oracle_text"));
                    card1.setImageURL(extractedURI.get("image_uris"));
                    logger.info("First card created successfully.");

                    Create card2 = new Create();
                    card2.setMaterial(material);
                    card2.setName(cardTwo.get("name"));
                    card2.setFileName(cardTwo.get("name"));
                    card2.setManaCost(cardTwo.get("mana_cost"));
                    card2.setType(cardTwo.get("type_line"));
                    card2.setOracleText(cardTwo.get("oracle_text"));
                    card2.setImageURL(extractedURI.get("image_uris"));
                    logger.info("Second card created successfully.");

                    logger.info("Card filename: " + card1.getFileName() + " // " + card2.getFileName());
                    logger.info("Card name: " + card1.getName() + " // " + card2.getName());
                    logger.info("Card cost: " + card1.getManaCost() + " // " + card2.getManaCost());
                    logger.info("Card type: " + card1.getType() + " // " + card2.getType());
                    logger.info("Card ability: " + card1.getOracleText() + " // " + card2.getOracleText());
                    logger.info("Image url: " + card1.getImageUrl() + " // " + card2.getImageUrl());

                    // Make the backend files
                    makeItemFiles(resourcePATH, card1.getFileName(), card1.getImageUrl());
                    createFile(datapackPATH + mcfunctionPATH, card1.getFileName(), "mcfunction");
                    writeToFile(datapackPATH + mcfunctionPATH + card1.getFileName() + ".mcfunction",
                            "execute as @p " +
                                    "if items entity @p weapon.mainhand minecraft:" +
                                    card1.getMaterial() +
                                    " run item replace entity @p weapon.mainhand " +
                                    "with minecraft:" +
                                    card1.getMaterial() +
                                    "[minecraft:custom_name=\"" +
                                    card1.getName() +
                                    "\",lore=[\"{\'color\':\'gold\',\'text\':\'" +
                                    card1.getManaCost() +
                                    "\'}\",\"{\'color\':\'aqua\',\'text\':\'" +
                                    card1.getType() +
                                    "\'}\",\"{\'text\':\'" +
                                    card1.getOracleText() +
                                    "\'}\",\"{\'text\':\'\'}\",\"{\'color\':\'white\',\'text\':\'" +
                                    card2.getName() +
                                    "\'}\",\"{\'color\':\'gold\',\'text\':\'" +
                                    card2.getManaCost() +
                                    "\'}\",\"{\'color\':\'aqua\',\'text\':\'" +
                                    card2.getType() +
                                    "\'}\",\"{\'text\':\'" +
                                    card2.getOracleText() +
                                    "\'}\"],minecraft:item_model=\"card:" +
                                    card1.getFileName() +
                                    "\"]");

                } else if (Pattern.matches(card_layout, "transform") ||
                        Pattern.matches(card_layout, "modal_dfc") ||
                        Pattern.matches(card_layout, "reversible_card")) {
                    logger.info("Inside layout: " + card_layout + " " + material);
                    // Double faced cards
                    Map<String, String> extractedData = extractJsonValues(json, "card_faces");

                    logger.info("Data extracted successfully: " + extractedData);

                    // This separates the card faces
                    String[] faces = extractedData.get("card_faces")
                            .replace("\n", "")
                            .replace("[{", "")
                            .replace("}]", "")
                            .split("},");
                    // String[] faces = null;

                    logger.info("Card faces: " + faces);

                    Map<String, String> frontFace = extractJsonValues(faces[0],
                            "name",
                            "mana_cost",
                            "type_line",
                            "oracle_text",
                            "image_uris");
                    logger.info("Card front: " + frontFace);

                    Map<String, String> backFace = extractJsonValues(faces[1],
                            "name",
                            "mana_cost", // I don't think transform cards have a mana_cost for the backFace
                            "type_line",
                            "oracle_text",
                            "image_uris");
                    logger.info("Card back: " + backFace);

                    // Create the front of the card
                    Create front = new Create();
                    front.setMaterial(material);
                    front.setName(frontFace.get("name"));
                    front.setFileName(frontFace.get("name"));
                    front.setManaCost(frontFace.get("mana_cost"));
                    front.setType(frontFace.get("type_line"));
                    front.setOracleText(frontFace.get("oracle_text"));
                    front.setImageURL(frontFace.get("image_uris"));

                    logger.info("Card front created successfully.");

                    // Create the back of the card
                    Create back = new Create();
                    back.setMaterial(material);
                    back.setName(backFace.get("name"));
                    back.setFileName(backFace.get("name"));
                    back.setManaCost(backFace.get("mana_cost"));
                    // if (Pattern.matches(back.getManaCost(),"")) {
                    // back.setManaCost("Transforms from " + front.getName());
                    // }
                    // if (Pattern.matches(front.getManaCost(),"")) {
                    // front.setManaCost("Turn over for " + back.getName());
                    // }
                    back.setType(backFace.get("type_line"));
                    back.setOracleText(backFace.get("oracle_text"));
                    back.setImageURL(backFace.get("image_uris"));

                    logger.info("Card filename: " + front.getFileName() + " // " + back.getFileName());
                    logger.info("Card name: " + front.getName() + " // " + back.getName());
                    logger.info("Card cost: " + front.getManaCost() + " // " + back.getManaCost());
                    logger.info("Card type: " + front.getType() + " // " + back.getType());
                    logger.info("Card ability: " + front.getOracleText() + " // " + back.getOracleText());
                    logger.info("Image url: " + front.getImageUrl() + " // " + back.getImageUrl());

                    // Create the Backend Files
                    makeItemFiles(resourcePATH, front.getFileName(), front.getImageUrl());
                    makeItemFiles(resourcePATH, back.getFileName(), back.getImageUrl());

                    logger.info("Backend files created successfully.");

                    // Create trigger mcfunction
                    createFile(datapackPATH + mcfunctionPATH, front.getFileName(), "mcfunction");
                    writeToFile(datapackPATH + mcfunctionPATH + front.getFileName() + ".mcfunction",
                            "execute as @p " +
                                    "if items entity @p weapon.mainhand minecraft:" +
                                    front.getMaterial() +
                                    " run item replace entity @p weapon.mainhand " +
                                    "with minecraft:" +
                                    front.getMaterial() +
                                    "[minecraft:custom_name=\"" +
                                    front.getName() +
                                    "\",lore=[\"{\'color\':\'gold\',\'text\':\'" +
                                    front.getManaCost() +
                                    "\'}\",\"{\'color\':\'aqua\',\'text\':\'" +
                                    front.getType() +
                                    "\'}\",\"{\'text\':\'" +
                                    front.getOracleText() +
                                    "\'}\"],minecraft:item_model=\"card:" +
                                    front.getFileName() +
                                    "\"]\n" +

                                    "execute as @p " +
                                    "if items entity @p weapon.mainhand minecraft:" +
                                    back.getMaterial() +
                                    " run give @p " +
                                    "minecraft:" +
                                    back.getMaterial() +
                                    "[minecraft:custom_name=\"" +
                                    back.getName() +
                                    "\",lore=[\"{\'color\':\'gold\',\'text\':\'" +
                                    back.getManaCost() +
                                    "\'}\",\"{\'color\':\'aqua\',\'text\':\'" +
                                    back.getType() +
                                    "\'}\",\"{\'text\':\'" +
                                    back.getOracleText() +
                                    "\'}\"],minecraft:item_model=\"card:" +
                                    back.getFileName() +
                                    "\"]");

                    logger.info("mcfunction file created successfully.");
                } else {
                    logger.info("Inside layout: " + card_layout + " " + material);
                    Map<String, String> extractedData = extractJsonValues(json,
                            "name",
                            "mana_cost",
                            "type_line",
                            "image_uris",
                            "oracle_text");

                    // Create the card
                    Create card = new Create();
                    // String material = (args.length == 1 || args[1].isBlank()) ? "paper" :
                    // args[1]; // Because I added args[2], this line is completely superfluous
                    card.setMaterial(material);
                    card.setName(extractedData.get("name"));
                    card.setFileName(extractedData.get("name"));
                    card.setManaCost(extractedData.get("mana_cost"));
                    card.setType(extractedData.get("type_line"));
                    card.setOracleText(extractedData.get("oracle_text"));
                    card.setImageURL(extractedData.get("image_uris"));

                    logger.info("Card filename: " + card.getFileName());
                    logger.info("Card name: " + card.getName());
                    logger.info("Card cost: " + card.getManaCost());
                    logger.info("Card type: " + card.getType());
                    logger.info("Card ability: " + card.getOracleText());
                    logger.info("Image url: " + card.getImageUrl());

                    // Create the Backend Files
                    makeItemFiles(resourcePATH, card.getFileName(), card.getImageUrl());

                    // Create trigger mcfunction
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
                                    "\"]");
                } // I should do an entry for "meld" layouts to show "related cards"

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
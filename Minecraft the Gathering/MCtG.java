package com.sp1cyp4nda;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.ContainerLootComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
// import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.WorldSavePath;

public class MCtG implements ModInitializer {
      public static final String MOD_ID = "mctg";
      public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

      @Override
      public void onInitialize() {
            LOGGER.info("MCtG mod initializing...");

            CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
                  registerCommands(dispatcher);
            });
      }

      public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
            dispatcher.register(
                        literal("mctg")
                                    .then(literal("help")
                                                .executes(ctx -> {
                                                      ctx.getSource().sendFeedback(
                                                                  () -> Text
                                                                              .literal("\nMod created and maintained by Sp1cyP4nda\n"
                                                                                          +
                                                                                          "This mod is protected under CC BY-NC-ND 4.0\n"
                                                                                          +
                                                                                          "Respectful feedback, ideas, and collaboration are always welcome!\n"
                                                                                          +
                                                                                          "Reach me on reddit and other Socials @Sp1cyP4nda\n"
                                                                                          +
                                                                                          "Or by email: mctg@sp1cyp4nda.addy.io\n\n"
                                                                                          +

                                                                                          "If you're experiencing an issue, please let me know.\n"
                                                                                          +
                                                                                          "You can tag me on Reddit, email me, or open an issue on GitHub.\n"
                                                                                          +
                                                                                          "Please include logs when reporting bugs:\n"
                                                                                          +
                                                                                          "Logs folder: <world_name>/datapacks/mctg_functions/logs\n\n"
                                                                                          +
                                                                                          "Special thanks to Scryfall for its incredible Magic: The Gathering card database\n\n"
                                                                                          +
                                                                                          "Commands:\n" +
                                                                                          "/mctg help\n Display this message\n\n"
                                                                                          +

                                                                                          "/mctg initialize \n"
                                                                                          +
                                                                                          " Required on first install or update (must be run by server ops)\n\n"
                                                                                          +

                                                                                          "/mctg create <card_name>\n"
                                                                                          +
                                                                                          " Generates a Magic card item based on <card_name>\n"
                                                                                          +
                                                                                          "  • Omit punctuation\n" +
                                                                                          "  • Use underscores, dashes, or no spaces at all\n"
                                                                                          +
                                                                                          "  NOTE: On servers, see the download page for resource pack setup instructions\n\n"
                                                                                          +

                                                                                          "/mctg shuffle\n" +
                                                                                          "  Shuffles a bundle (deck) held in your main hand\n"
                                                                                          +
                                                                                          "  • Bundle can only contain front-facing cards\n"
                                                                                          +
                                                                                          "  • Max 99 cards per bundle\n"
                                                                                          +
                                                                                          "  • For Commander: exclude your commander card\n"),
                                                                  false);
                                                      return 1;
                                                }))
                                    .then(literal("initialize")
                                                .requires(source -> source.hasPermissionLevel(4))
                                                .executes(ctx -> {
                                                      // ServerPlayerEntity player = ctx.getSource().getPlayer();
                                                      // ItemStack mainHand = player.getMainHandStack();

                                                      // final String contentsString;
                                                      // ContainerComponent shulkerContents = null;
                                                      // NbtList contentsList = new NbtList();

                                                      // if (mainHand.getItem().toString().contains("shulker")) {
                                                      // shulkerContents = mainHand
                                                      // .get(DataComponentTypes.CONTAINER);
                                                      // for (int slot = 0; slot < shulkerContents.asMap(); slot++) {

                                                      // }
                                                      // }

                                                      // if (shulkerContents == null) {
                                                      // contentsString = "null";
                                                      // } else {
                                                      // contentsString = shulkerContents.toString();
                                                      // }

                                                      // ctx.getSource().sendFeedback(
                                                      // () -> Text.literal(
                                                      // "mainHand: [" + contentsString + "]"),
                                                      // false);

                                                      Path worldPath = ctx.getSource().getServer()
                                                                  .getSavePath(WorldSavePath.ROOT)
                                                                  .normalize();

                                                      Initialize.main(new String[] {
                                                                  worldPath + "/datapacks",
                                                                  // contentsString
                                                      });

                                                      ctx.getSource().sendFeedback(
                                                                  () -> Text.literal("MCtG directories initialized."),
                                                                  false);
                                                      ctx.getSource().getServer()
                                                                  .getCommandManager()
                                                                  .executeWithPrefix(
                                                                              ctx.getSource(), "reload");

                                                      return 1;

                                                }))
                                    .then(literal("create")
                                                .then(argument("card_name", StringArgumentType.word())
                                                            .executes(ctx -> {
                                                                  String newCard = StringArgumentType
                                                                              .getString(ctx, "card_name");
                                                                  String cardName = newCard
                                                                              .replace(",", "")
                                                                              .replace("'", "")
                                                                              .replace("\\\\", "")
                                                                              .replace("!", "")
                                                                              .replace("?", "")
                                                                              .replace("/", "l")
                                                                              .replace(" ", "_")
                                                                              .replace("-", "_")
                                                                              .toLowerCase();
                                                                  // Check if giving deck_bundle or welcome_package
                                                                  if (Pattern.matches(cardName, "deck_bundle") ||
                                                                              Pattern.matches(cardName,
                                                                                          "welcome_package")) {
                                                                        ctx.getSource().getServer()
                                                                                    .getCommandManager()
                                                                                    .executeWithPrefix(
                                                                                                ctx.getSource(),
                                                                                                "function mctg:cards/"
                                                                                                            + cardName.replace(
                                                                                                                        "-",
                                                                                                                        "_"));
                                                                        return 0;
                                                                  }
                                                                  // Get player mainHand item
                                                                  ServerPlayerEntity player = ctx.getSource()
                                                                              .getPlayer();
                                                                  ItemStack mainHand = player.getMainHandStack();
                                                                  ItemStack offHand = player.getOffHandStack();
                                                                  // Check if player is holding paper
                                                                  if (!mainHand.toString()
                                                                              .matches("^1 minecraft:paper")) {
                                                                        ctx.getSource().sendFeedback(() -> Text
                                                                                    .literal("\nYou must be holding 1 paper in your main hand."),
                                                                                    false);
                                                                        return 0;
                                                                  }
                                                                  // Declare file paths
                                                                  Path worldPath = ctx.getSource()
                                                                              .getServer()
                                                                              .getSavePath(WorldSavePath.ROOT)
                                                                              .normalize();
                                                                  String datapackPath = worldPath.toString()
                                                                              + "/datapacks";
                                                                  String resourcePath = "resourcepacks/mctg/";

                                                                  // Get Card info
                                                                  String cardInfo = Create.getCardInfo(cardName);
                                                                  // Start by checking the card's layout
                                                                  String checkLayout = Create
                                                                              .extractJsonValues(cardInfo, "layout")
                                                                              .get("layout")
                                                                              .toString();

                                                                  Create card = new Create();
                                                                  Create otherCard = new Create();
                                                                  Create back = new Create();
                                                                  if (Pattern.compile("split|flip|adventure")
                                                                              .matcher(checkLayout).matches()) {
                                                                        // Get the card faces
                                                                        Map<String, String> extractedData = Create
                                                                                    .extractJsonValues(cardInfo,
                                                                                                "card_faces");

                                                                        // Separate the card faces :: card and back
                                                                        String[] faces = extractedData.get("card_faces")
                                                                                    .replace("\n", "")
                                                                                    .replace("[{", "")
                                                                                    .replace("}]", "")
                                                                                    .split("},");

                                                                        // Create the first card
                                                                        card.setName(
                                                                                    Create.extractJsonValues(faces[0],
                                                                                                "name")
                                                                                                .get("name")
                                                                                                .toString());
                                                                        card.setFileName(card.getName());
                                                                        card.setManaCost(
                                                                                    Create.extractJsonValues(faces[0],
                                                                                                "mana_cost")
                                                                                                .get("mana_cost")
                                                                                                .toString());
                                                                        card.setType(
                                                                                    Create.extractJsonValues(faces[0],
                                                                                                "type_line")
                                                                                                .get("type_line")
                                                                                                .toString());
                                                                        card.setOracleText(
                                                                                    Create.extractJsonValues(faces[0],
                                                                                                "oracle_text")
                                                                                                .get("oracle_text")
                                                                                                .toString());
                                                                        card.setImageURL(
                                                                                    Create.extractJsonValues(faces[0],
                                                                                                "image_uris")
                                                                                                .get("image_uris")
                                                                                                .toString());
                                                                        // Create the second card
                                                                        otherCard.setName(
                                                                                    Create.extractJsonValues(faces[1],
                                                                                                "name")
                                                                                                .get("name")
                                                                                                .toString());
                                                                        otherCard.setFileName(otherCard.getName());
                                                                        otherCard.setManaCost(
                                                                                    Create.extractJsonValues(faces[1],
                                                                                                "mana_cost")
                                                                                                .get("mana_cost")
                                                                                                .toString());
                                                                        otherCard.setType(
                                                                                    Create.extractJsonValues(faces[1],
                                                                                                "type_line")
                                                                                                .get("type_line")
                                                                                                .toString());
                                                                        otherCard.setOracleText(
                                                                                    Create.extractJsonValues(faces[1],
                                                                                                "oracle_text")
                                                                                                .get("oracle_text")
                                                                                                .toString());
                                                                        otherCard.setImageURL(
                                                                                    Create.extractJsonValues(faces[1],
                                                                                                "image_uris")
                                                                                                .get("image_uris")
                                                                                                .toString());
                                                                  } else if (Pattern.compile(
                                                                              "transform|modal_dfc|reversible_card")
                                                                              .matcher(checkLayout).matches()) {

                                                                        // Check if offHand has 1 paper
                                                                        if (!offHand.toString()
                                                                                    .matches("^1 minecraft:paper")) {
                                                                              ctx.getSource().sendFeedback(() -> Text
                                                                                          .literal("This card requires 1 paper in your off hand"),
                                                                                          false);
                                                                              return 0;
                                                                        }

                                                                        // Get the card faces
                                                                        Map<String, String> extractedData = Create
                                                                                    .extractJsonValues(cardInfo,
                                                                                                "card_faces");

                                                                        // Separate the card faces :: card and back
                                                                        String[] faces = extractedData.get("card_faces")
                                                                                    .replace("\n", "")
                                                                                    .replace("[{", "")
                                                                                    .replace("}]", "")
                                                                                    .split("}},");

                                                                        card.setName(
                                                                                    Create.extractJsonValues(faces[0],
                                                                                                "name")
                                                                                                .get("name")
                                                                                                .toString());
                                                                        card.setFileName(card.getName());
                                                                        card.setManaCost(
                                                                                    Create.extractJsonValues(faces[0],
                                                                                                "mana_cost")
                                                                                                .get("mana_cost")
                                                                                                .toString());
                                                                        card.setType(
                                                                                    Create.extractJsonValues(faces[0],
                                                                                                "type_line")
                                                                                                .get("type_line")
                                                                                                .toString());
                                                                        card.setOracleText(
                                                                                    Create.extractJsonValues(faces[0],
                                                                                                "oracle_text")
                                                                                                .get("oracle_text")
                                                                                                .toString());
                                                                        card.setImageURL(
                                                                                    Create.extractJsonValues(faces[0],
                                                                                                "image_uris")
                                                                                                .get("image_uris")
                                                                                                .toString());
                                                                        // Create the card back
                                                                        back.setName(
                                                                                    Create.extractJsonValues(faces[1],
                                                                                                "name")
                                                                                                .get("name")
                                                                                                .toString());
                                                                        back.setFileName(back.getName());
                                                                        back.setManaCost(
                                                                                    Create.extractJsonValues(faces[1],
                                                                                                "mana_cost")
                                                                                                .get("mana_cost")
                                                                                                .toString());
                                                                        back.setType(
                                                                                    Create.extractJsonValues(faces[1],
                                                                                                "type_line")
                                                                                                .get("type_line")
                                                                                                .toString());
                                                                        back.setOracleText(
                                                                                    Create.extractJsonValues(faces[1],
                                                                                                "oracle_text")
                                                                                                .get("oracle_text")
                                                                                                .toString());
                                                                        back.setImageURL(
                                                                                    Create.extractJsonValues(faces[1],
                                                                                                "image_uris")
                                                                                                .get("image_uris")
                                                                                                .toString());
                                                                  } else {
                                                                        card.setName(
                                                                                    Create.extractJsonValues(cardInfo,
                                                                                                "name")
                                                                                                .get("name")
                                                                                                .toString());
                                                                        card.setFileName(card.getName());
                                                                        card.setManaCost(
                                                                                    Create.extractJsonValues(cardInfo,
                                                                                                "mana_cost")
                                                                                                .get("mana_cost")
                                                                                                .toString());
                                                                        card.setType(
                                                                                    Create.extractJsonValues(cardInfo,
                                                                                                "type_line")
                                                                                                .get("type_line")
                                                                                                .toString());
                                                                        card.setOracleText(
                                                                                    Create.extractJsonValues(cardInfo,
                                                                                                "oracle_text")
                                                                                                .get("oracle_text")
                                                                                                .toString());
                                                                        card.setImageURL(
                                                                                    Create.extractJsonValues(cardInfo,
                                                                                                "image_uris")
                                                                                                .get("image_uris")
                                                                                                .toString());
                                                                  }
                                                                  // Set card lore
                                                                  List<Text> cardLore = new ArrayList<>();
                                                                  // Set lore - mana cost
                                                                  cardLore.add(
                                                                              Text.literal(
                                                                                          card.getManaCost())
                                                                                          .styled(style -> style
                                                                                                      .withItalic(false)
                                                                                                      .withColor(Formatting.GOLD)));
                                                                  // Set lore - type
                                                                  cardLore.add(
                                                                              Text.literal(
                                                                                          card.getType())
                                                                                          .styled(style -> style
                                                                                                      .withItalic(false)
                                                                                                      .withColor(Formatting.AQUA)));
                                                                  // Set lore - oracle text
                                                                  String[] oracleText = card.getOracleText()
                                                                              .split("\\\\n");
                                                                  for (String eachLine : oracleText) {
                                                                        cardLore.add(Text.literal(eachLine)
                                                                                    .styled(style -> style
                                                                                                .withItalic(false)));
                                                                  }
                                                                  // Set lore - otherCard layout == split, etc
                                                                  if (Pattern.compile("split|flip|adventure")
                                                                              .matcher(checkLayout).matches()) {
                                                                        cardLore.add(
                                                                                    Text.literal(
                                                                                                otherCard.getName())
                                                                                                .styled(style -> style
                                                                                                            .withItalic(false)
                                                                                                            .withColor(Formatting.WHITE)));
                                                                        cardLore.add(
                                                                                    Text.literal(
                                                                                                otherCard.getManaCost())
                                                                                                .styled(style -> style
                                                                                                            .withItalic(false)
                                                                                                            .withColor(Formatting.GOLD)));
                                                                        // Set lore - type
                                                                        cardLore.add(
                                                                                    Text.literal(
                                                                                                otherCard.getType())
                                                                                                .styled(style -> style
                                                                                                            .withItalic(false)
                                                                                                            .withColor(Formatting.AQUA)));
                                                                        // Set lore - oracle text
                                                                        String[] otherOracleText = otherCard
                                                                                    .getOracleText()
                                                                                    .split("\\\\n");
                                                                        for (String eachLine : otherOracleText) {
                                                                              cardLore.add(Text.literal(eachLine)
                                                                                          .styled(style -> style
                                                                                                      .withItalic(false)));
                                                                        }
                                                                  }

                                                                  // Make backend files
                                                                  Create.makeItemFiles(resourcePath,
                                                                              card.getFileName(),
                                                                              card.getImageUrl());

                                                                  // Set card data
                                                                  mainHand.set(DataComponentTypes.CUSTOM_NAME,
                                                                              Text.literal(card.getName())
                                                                                          .styled(style -> style
                                                                                                      .withItalic(false)));
                                                                  // Set card lore
                                                                  mainHand.set(DataComponentTypes.LORE,
                                                                              new LoreComponent(cardLore));
                                                                  // Generate and set card UUID
                                                                  NbtCompound uuidGen = new NbtCompound();
                                                                  uuidGen.putString("uuid",
                                                                              UUID.randomUUID().toString());
                                                                  mainHand.set(DataComponentTypes.CUSTOM_DATA,
                                                                              NbtComponent.of(uuidGen));
                                                                  // Set card item_model
                                                                  mainHand.set(DataComponentTypes.ITEM_MODEL,
                                                                              Identifier.of("card:"
                                                                                          + card.getFileName()));
                                                                  // Set stack size to 99
                                                                  mainHand.set(DataComponentTypes.MAX_STACK_SIZE,
                                                                              99);

                                                                  if (Pattern.compile(
                                                                              "transform|modal_dfc|reversible_card")
                                                                              .matcher(checkLayout).matches()) {
                                                                        // Set card lore
                                                                        List<Text> backLore = new ArrayList<>();
                                                                        // Set lore - mana cost
                                                                        backLore.add(
                                                                                    Text.literal("Transforms from " +
                                                                                                card.getName())
                                                                                                .styled(style -> style
                                                                                                            .withItalic(false)
                                                                                                            .withColor(Formatting.GOLD)));
                                                                        // Set lore - type
                                                                        backLore.add(
                                                                                    Text.literal(
                                                                                                back.getType())
                                                                                                .styled(style -> style
                                                                                                            .withItalic(false)
                                                                                                            .withColor(Formatting.AQUA)));
                                                                        // Set lore - oracle text
                                                                        String[] oracleTextBack = back.getOracleText()
                                                                                    .split("\\\\n");
                                                                        for (String eachLine : oracleTextBack) {
                                                                              backLore.add(Text.literal(eachLine)
                                                                                          .styled(style -> style
                                                                                                      .withItalic(false)));
                                                                        }

                                                                        // Make backend files
                                                                        Create.makeItemFiles(resourcePath,
                                                                                    back.getFileName(),
                                                                                    back.getImageUrl());

                                                                        // Set back data
                                                                        offHand.set(DataComponentTypes.CUSTOM_NAME,
                                                                                    Text.literal(back.getName())
                                                                                                .styled(style -> style
                                                                                                            .withItalic(false)));
                                                                        // Set back lore
                                                                        offHand.set(DataComponentTypes.LORE,
                                                                                    new LoreComponent(backLore));
                                                                        // Generate and set back UUID
                                                                        NbtCompound uuidGenBack = new NbtCompound();
                                                                        uuidGenBack.putString("uuid",
                                                                                    UUID.randomUUID().toString());
                                                                        offHand.set(DataComponentTypes.CUSTOM_DATA,
                                                                                    NbtComponent.of(uuidGenBack));
                                                                        // Set back item_model
                                                                        offHand.set(DataComponentTypes.ITEM_MODEL,
                                                                                    Identifier.of("card:"
                                                                                                + back.getFileName()));
                                                                        // Set stack size to 99
                                                                        offHand.set(DataComponentTypes.MAX_STACK_SIZE,
                                                                                    1);
                                                                  }

                                                                  return 1;
                                                            })))
                                    .then(literal("shuffle")
                                                .executes(ctx -> {
                                                      ServerPlayerEntity player = ctx.getSource()
                                                                  .getPlayer();

                                                      // Get contents from bundle
                                                      ItemStack bundle = player.getMainHandStack();

                                                      // Check if player holding a bundle
                                                      if (!bundle.toString().contains("bundle")) {
                                                            ctx.getSource().sendFeedback(() -> Text
                                                                        .literal("You must be holding a bundle"
                                                                                    + bundle),
                                                                        false);
                                                            return 0;
                                                      }

                                                      BundleContentsComponent bundle_contents = bundle
                                                                  .get(DataComponentTypes.BUNDLE_CONTENTS);

                                                      // Copy the items into a list
                                                      List<ItemStack> items = new ArrayList<>();
                                                      bundle_contents.stream().forEach(entry -> items
                                                                  .add(entry.copy()));

                                                      // Shuffle the items
                                                      Collections.shuffle(items);

                                                      // Replace the bundle's contents
                                                      BundleContentsComponent bundle_shuffled = new BundleContentsComponent(
                                                                  items);
                                                      bundle.set(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP,
                                                                  Unit.INSTANCE);
                                                      bundle.set(DataComponentTypes.BUNDLE_CONTENTS,
                                                                  bundle_shuffled);

                                                      ctx.getSource().sendFeedback(() -> Text
                                                                  .literal("Bundle Shuffled!"),
                                                                  false);

                                                      return 1;
                                                })));

            // This deletes unused files
            //
            //
            // .then(literal("cle
            // .executes(ctx -> {
            // Path delete_all = worldPath.toString() +
            // "/datapacks/mctg_f
            // delete iteratively
            // delete_a
            // ret
            //

      }
}

package com.sp1cyp4nda;

import java.nio.file.Path;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Unit;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import java.util.UUID;
import net.minecraft.item.Items;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

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
                                       .literal("\nMod created and maintained by Sp1cyP4nda\n"+
                                             "This mod is protected under CC BY-NC-ND 4.0\n" +
                                             "Respectful feedback, ideas, and collaboration are always welcome!\n"+
                                             "Reach me on reddit and other Socials @Sp1cyP4nda\n"+
                                             "Or by email: mctg@sp1cyp4nda.addy.io\n\n"+

                                             "If you're experiencing an issue, please let me know.\n" +
                                             "You can tag me on Reddit, email me, or open an issue on GitHub.\n" +
                                             "Please include logs when reporting bugs:\n" +
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
                                             " Generates a Magic card item based on <card_name>\n" +
                                             "  • Omit punctuation\n" +
                                             "  • Use underscores, dashes, or no spaces at all\n"
                                             +
                                             "  NOTE: On servers, see the download page for resource pack setup instructions\n\n"
                                             +

                                             "/mctg shuffle\n" +
                                             "  Shuffles a bundle (deck) held in your main hand\n" +
                                             "  • Bundle can only contain front-facing cards\n" +
                                             "  • Max 99 cards per bundle\n" +
                                             "  • For Commander: exclude your commander card\n"
),
                                 false);
                           return 1;
                        }))
                  .then(literal("initialize")
                        .requires(source -> source.hasPermissionLevel(4))
                        .executes(ctx -> {
                           Path worldPath = ctx.getSource().getServer()
                                 .getSavePath(WorldSavePath.ROOT)
                                 .normalize();
                           Initialize.main(new String[] {
                                 worldPath + "/datapacks" });
                           ctx.getSource().sendFeedback(() -> Text.literal(
                                 "MCtG directories initialized."),
                                 false);
                           return 1;
                        }))
                  .then(literal("create")
                        .then(argument("card_name", StringArgumentType.word())
                              .executes(ctx -> {
                                 String card = StringArgumentType
                                       .getString(ctx, "card_name");
                                 String card_name = card.replace(
                                       ",", "")
                                       .replace("'", "")
                                       .replace("\\\\", "")
                                       .replace("!", "")
                                       .replace("?", "")

                                       .replace("/", "l")
                                       .replace(" ", "_")
                                       .replace("-", "_")
                                       .toLowerCase();
                                 String material = "paper"; // This is fine to not be used. I think technically, this
                                                            // line isn't needed
                                 Path worldPath = ctx.getSource()
                                       .getServer()
                                       .getSavePath(WorldSavePath.ROOT)
                                       .normalize();
                                 Create.main(new String[] {
                                       card_name,
                                       material,
                                       worldPath.toString()
                                             + "/datapacks" });
                                 ctx.getSource().sendFeedback(
                                       () -> Text
                                             .literal("Card created: "
                                                   + card_name),
                                       false);
                                 ctx.getSource().getServer()
                                       .getCommandManager()
                                       .executeWithPrefix(
                                             ctx.getSource(),
                                             "reload");
                                 ctx.getSource().getServer()
                                       .getCommandManager()
                                       .executeWithPrefix(
                                             ctx.getSource(),
                                             "function mctg:cards/"
                                                   + card_name.replace(
                                                         "-",
                                                         "_"));

                                 // Delay file deletion by 20
                                 // ticks = 1 second
                                 TaskScheduler.schedule(40,
                                       () -> {
                                          File delete_card = new File(
                                                worldPath.toString()
                                                      + "/datapacks/mctg_functions/data/mctg/function/cards/"
                                                      + card_name
                                                      + ".mcfunction");
                                          if (delete_card.exists()) {
                                             boolean success = delete_card
                                                   .delete();
                                             if (success) {
                                                MCtG.LOGGER.info(
                                                      "Deleted card file: "
                                                            + delete_card
                                                                  .getAbsolutePath());
                                             } else {
                                                MCtG.LOGGER
                                                      .warn("Failed to delete card file: "
                                                            + delete_card
                                                                  .getAbsolutePath());
                                             }
                                          }
                                       });

                                 return 1;
                              })))
                  .then(literal("shuffle")
                        .executes(ctx -> {
                           ServerPlayerEntity player = ctx.getSource()
                                 .getPlayer();

                           // Get contents from bundle
                           ItemStack bundle = player.getMainHandStack();

                           // Check if player holding a bundle
                           if (bundle == null) {
                              ctx.getSource().sendFeedback(() -> Text
                                    .literal("You must be holding a bundle"),
                                    false);
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

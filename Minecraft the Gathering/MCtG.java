package com.sp1cyp4nda;

import java.nio.file.Path;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.World;

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
                        ctx.getSource().sendFeedback(() ->
                            Text.literal("Usage:\n" +
                                "\nReplace <card_name> with the name of the" +
                                "name of the desired card. This doesn't have" +
                                "to be exact.\n" +
                                "\nThe material argument is optional." +
                                "It defaults to paper when left blank\n" +
                                "\nAll spaces in arguments should be separated\n" +
                                "by underscores.\n\n" +
                                "/mctg create <card_name> [material]\n" +
                                "/mctg initialize (must be ops)"
                            ),
                            false
                        );
                        return 1;
                    }))
                .then(literal("initialize")
                    .requires(source -> source.hasPermissionLevel(4))
                    .executes(ctx -> {
                        Path worldName = ctx.getSource().getServer().getSavePath(WorldSavePath.ROOT).normalize();
                        Initialize.main(new String[]{worldName + "/datapacks"});
                        ctx.getSource().sendFeedback(() -> Text.literal("MCtG directories initialized."), false);
                        return 1;
                    }))
                .then(literal("create")
                    .then(argument("card_name", StringArgumentType.word())
                        .executes(ctx -> {
                            String name = StringArgumentType.getString(ctx, "card_name");
                            String material = "paper"; // This is fine to not be used. I think technically, this line isn't needed
                            Path worldName = ctx.getSource().getServer().getSavePath(WorldSavePath.ROOT).normalize();
                            Create.main(new String[]{name,material,worldName.toString() + "/datapacks"});
                            ctx.getSource().sendFeedback(() -> Text.literal("Card '" + name + "' created with material:paper"), false);
                            return 1;
                        })
                        .then(argument("material", StringArgumentType.word())
                            .executes(ctx -> {
                                String name = StringArgumentType.getString(ctx, "card_name");
                                String material = StringArgumentType.getString(ctx, "material");
                                Path worldName = ctx.getSource().getServer().getSavePath(WorldSavePath.ROOT).normalize();
                                Create.main(new String[]{name,material,worldName.toString() + "/datapacks"});
                                ctx.getSource().sendFeedback(() -> Text.literal("Card '" + name + "' created with material:" + material), false);
                                return 1;
                            }))))
        );
    }
}

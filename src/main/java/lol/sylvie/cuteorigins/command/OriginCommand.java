package lol.sylvie.cuteorigins.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.gui.OriginGui;
import lol.sylvie.cuteorigins.origin.Origin;
import lol.sylvie.cuteorigins.state.StateManager;
import lol.sylvie.cuteorigins.util.OriginRegistries;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.LocateCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class OriginCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal(CuteOrigins.MOD_ID)
                .then(literal("gui")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(OriginCommand::executeGuiCommand))
                .then(literal("set")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(argument("player", EntityArgumentType.player())
                                .then(argument("origin", IdentifierArgumentType.identifier())
                                        .executes(OriginCommand::executeSetOriginCommand))))
                .then(literal("binds")
                        .executes(OriginCommand::executeBindsCommand)));
    }

    private static int executeGuiCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        OriginGui.openPicker(context.getSource().getPlayerOrThrow());
        return 1;
    }

    private static final DynamicCommandExceptionType ORIGIN_NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType((id) -> Text.stringifiedTranslatable("commands.origin.set.not_found", id));

    private static int executeSetOriginCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        Identifier originId = IdentifierArgumentType.getIdentifier(context, "origin");

        Origin origin = OriginRegistries.ORIGIN_REGISTRY.getOrigin(originId);
        if (origin == null) throw ORIGIN_NOT_FOUND_EXCEPTION.create(originId);

        StateManager.getPlayerState(player).setOrigin(player, origin);
        context.getSource().sendFeedback(() -> Text.translatable("commands.origin.set.success", player.getGameProfile().name(), origin.getName()), true);
        return 1;
    }

    private static int executeBindsCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try {
            OriginGui.openBinds(context.getSource().getPlayerOrThrow());
        } catch (Exception e) {
            context.getSource().sendFeedback(() -> Text.literal(e.getMessage()).formatted(Formatting.RED), false);
        }

        return 1;
    }
}

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
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class OriginCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(literal(CuteOrigins.MOD_ID)
                .then(literal("gui")
                        .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_ADMIN))
                        .executes(OriginCommand::executeGuiCommand))
                .then(literal("set")
                        .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_ADMIN))
                        .then(argument("player", EntityArgument.player())
                                .then(argument("origin", IdentifierArgument.id())
                                        .executes(OriginCommand::executeSetOriginCommand))))
                .then(literal("binds")
                        .executes(OriginCommand::executeBindsCommand)));
    }

    private static int executeGuiCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        OriginGui.openPicker(context.getSource().getPlayerOrException());
        return 1;
    }

    private static final DynamicCommandExceptionType ORIGIN_NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType((id) -> Component.translatableEscape("commands.origin.set.not_found", id));

    private static int executeSetOriginCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        Identifier originId = IdentifierArgument.getId(context, "origin");

        Origin origin = OriginRegistries.ORIGIN_REGISTRY.getOrigin(originId);
        if (origin == null) throw ORIGIN_NOT_FOUND_EXCEPTION.create(originId);

        StateManager.getPlayerState(player).setOrigin(player, origin);
        context.getSource().sendSuccess(() -> Component.translatable("commands.origin.set.success", player.getGameProfile().name(), origin.getName()), true);
        return 1;
    }

    private static int executeBindsCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        try {
            OriginGui.openBinds(context.getSource().getPlayerOrException());
        } catch (Exception e) {
            context.getSource().sendSuccess(() -> Component.literal(e.getMessage()).withStyle(ChatFormatting.RED), false);
        }

        return 1;
    }
}

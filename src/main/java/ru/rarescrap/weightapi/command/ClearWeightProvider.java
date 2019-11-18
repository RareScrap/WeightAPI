package ru.rarescrap.weightapi.command;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentTranslation;
import ru.rarescrap.weightapi.WeightRegistry;

public class ClearWeightProvider extends CommandBase {
    @Override
    public String getCommandName() {
        return "clearweightprovider";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "commands.weightprovider.usage.clear";
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] args) {
        if (args.length > 2) throw new WrongUsageException(this.getCommandUsage(commandSender));

        WeightRegistry.clearProvider(commandSender.getEntityWorld());

        boolean informPlayers = args.length == 2 ? Boolean.parseBoolean(args[1]) : false;
        if (informPlayers) {
            FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager()
                    .sendChatMsg(new ChatComponentTranslation("commands.weightprovider.success.clear"));
        } else {
            func_152373_a(commandSender, this, "commands.weightprovider.success.clear");
        }
    }

    /** Пакет, отключающий систему веса на клинте */
    public static class Message implements IMessage {
        @Override public void fromBytes(ByteBuf buf) {}
        @Override public void toBytes(ByteBuf buf) {}
    }

    public static class MessageHandler implements IMessageHandler<Message, IMessage> {
        @Override
        public IMessage onMessage(Message msg, MessageContext ctx) {
            WeightRegistry.applyToClient(null);
            return null;
        }
    }
}
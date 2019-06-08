package ru.rarescrap.weightapi.command;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentTranslation;
import ru.rarescrap.weightapi.WeightRegistry;

import java.util.List;

public class SetWeightProvider extends CommandBase {
    @Override
    public String getCommandName() {
        return "setweightprovider";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "commands.weightprovider.usage.set";
    }

    @Override
    public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
        return p_71516_2_.length == 1 ? getListOfStringsMatchingLastWord(p_71516_2_, WeightRegistry.getProvidersNames()) : (p_71516_2_.length == 2 ? getListOfStringsMatchingLastWord(p_71516_2_, new String[] {"true", "false"}): null);
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] args) {
        if (args.length > 2) throw new WrongUsageException(this.getCommandUsage(commandSender));

        if (WeightRegistry.getWeightProvider(args[0]) != null) {
            WeightRegistry.activateWeightProvider(args[0]);
        } else {
            throw new CommandException("commands.weightprovider.failure.set.notFound", args[0]);
        }

        boolean informPlayers = args.length == 2 ? Boolean.parseBoolean(args[1]) : false;
        if (informPlayers) {
            FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager()
                    .sendChatMsg(new ChatComponentTranslation("commands.weightprovider.success.set"));
        } else {
            // TODO: или commandSender.addChatMessage()?
            func_152373_a(commandSender, this, "commands.weightprovider.success.set");
        }
    }
}

package ru.rarescrap.weightapi.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import ru.rarescrap.weightapi.WeightRegistry;

public class GetActiveWeightProvider extends CommandBase {
    @Override
    public String getCommandName() {
        return "getactiveweightprovider";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "commands.weightprovider.usage.getActive";
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] args) {
        if (args.length != 0) throw new WrongUsageException(this.getCommandUsage(commandSender));

        if (WeightRegistry.getActiveWeightProvider() == null) {
            func_152373_a(commandSender, this, "commands.weightprovider.failure.getActive");
        } else {
            // TODO: или commandSender.addChatMessage()?
            func_152373_a(commandSender, this, "commands.weightprovider.success.getActive", WeightRegistry.getActiveProviderName());
        }
    }
}

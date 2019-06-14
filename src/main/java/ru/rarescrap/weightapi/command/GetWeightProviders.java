package ru.rarescrap.weightapi.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import ru.rarescrap.weightapi.WeightRegistry;

public class GetWeightProviders extends CommandBase {
    @Override
    public String getCommandName() {
        return "getweightproviders";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "commands.weightprovider.usage.getAll"; // Хоть команда и без аргументов, но именно эта строка показывается в /help
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] args) {
        if (args.length != 0) throw new WrongUsageException(this.getCommandUsage(commandSender));

        if (WeightRegistry.getProvidersNames().length == 0) {
            func_152373_a(commandSender, this, "commands.weightprovider.failure.getAll");
        } else {
            StringBuffer buffer = new StringBuffer(); // А вот потому что нету String.join() в java6
            for (String providesName : WeightRegistry.getProvidersNames()) {
                buffer.append(providesName).append(", ");
            }
            buffer.delete(buffer.length()-2, buffer.length()); // Удаляем последний разделитель

            // TODO: или commandSender.addChatMessage()?
            func_152373_a(commandSender, this, "commands.weightprovider.success.getAll", buffer.toString());
        }
    }
}

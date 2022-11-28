package net.wurstclient.forge.commands.stevebot;

import net.minecraft.util.math.BlockPos;
import net.wurstclient.forge.Command;
import stevebot.commands.StevebotCommands;
import stevebot.data.blockpos.BaseBlockPos;
import stevebot.data.blocks.BlockUtils;
import stevebot.pathfinding.PathHandler;
import stevebot.pathfinding.goal.ExactGoal;

import java.io.IOException;

public class SBCmd extends Command {
    public SBCmd() {
        super("sb", "Commands for SteveBot", ".sb <>");
    }
    @Override
    public void call(String[] args) throws CmdException, IOException {
        PathHandler pathHandler = StevebotCommands.thePathHandler;
        BaseBlockPos from = new BaseBlockPos(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ));
        if (!args[0].startsWith("minecraft")) {
            pathHandler.createPath(from, new ExactGoal(new BaseBlockPos(new BlockPos(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2])))), true, true);
        } else {
            //Block
            final BaseBlockPos posBlock = BlockUtils.findNearest(BlockUtils.getBlockLibrary().getBlockByName(args[0]), from, 219, 219);
            pathHandler.createPath(from, new ExactGoal(posBlock), true, true);
        }
    }
}

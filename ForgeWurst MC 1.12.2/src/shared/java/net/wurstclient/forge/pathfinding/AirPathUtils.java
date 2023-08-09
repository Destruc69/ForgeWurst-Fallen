package net.wurstclient.forge.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.forge.hacks.pathing.PathfinderModule;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.KeyBindingUtils;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class AirPathUtils {

    public static double xTargA = 0;
    public static double yTargA = 0;
    public static double zTargA = 0;

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static ArrayList<BlockPos> createPath(BlockPos start, BlockPos target, boolean debug) {
        if (mc.player.getDistance(target.getX(), target.getY(), target.getZ()) < mc.gameSettings.renderDistanceChunks * 16) {
            int numNodesVisited = 0;
            int numNodesConsidered = 0;
            int maxOpenListSize = 0;

            BlockPos finalTarget1 = target;
            PriorityQueue<BlockPos> openList = new PriorityQueue<>(Comparator.comparingDouble(pos -> getDistance(pos, finalTarget1)));
            HashMap<BlockPos, BlockPos> cameFrom = new HashMap<>();
            HashMap<BlockPos, Double> gScore = new HashMap<>();
            openList.add(start);
            gScore.put(start, 0.0);

            while (!openList.isEmpty()) {
                BlockPos current = openList.poll();

                numNodesVisited++;
                maxOpenListSize = Math.max(maxOpenListSize, openList.size());

                if (current.equals(target)) {
                    // Reconstruct the path
                    ArrayList<BlockPos> path = new ArrayList<>();
                    path.add(current);
                    while (cameFrom.containsKey(current)) {
                        current = cameFrom.get(current);
                        path.add(0, current);
                    }

                    if (debug) {
                        ChatUtils.message("Path found!");
                        ChatUtils.message("Number of nodes visited: " + numNodesVisited);
                        ChatUtils.message("Number of nodes considered: " + numNodesConsidered);
                        ChatUtils.message("Maximum size of open list: " + maxOpenListSize);
                        ChatUtils.message("Length of path: " + path.size());
                    } else {

                    }

                    return path;
                }

                for (BlockPos neighbor : getNeighbors(current)) {
                    double tentativeGScore = gScore.get(current) + getDistance(current, neighbor);
                    if (tentativeGScore < gScore.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                        cameFrom.put(neighbor, current);
                        gScore.put(neighbor, tentativeGScore);
                        if (!openList.contains(neighbor)) {
                            openList.add(neighbor);
                            numNodesConsidered++;
                        }
                    }
                }
            }
            return new ArrayList<>();
        } else {
            int numNodesVisited = 0;
            int numNodesConsidered = 0;
            int maxOpenListSize = 0;

            target = getClosestSolidBlock(target);
            BlockPos finalTarget = target;
            PriorityQueue<BlockPos> openList = new PriorityQueue<>(Comparator.comparingDouble(pos -> getDistance(pos, finalTarget)));
            HashMap<BlockPos, BlockPos> cameFrom = new HashMap<>();
            HashMap<BlockPos, Double> gScore = new HashMap<>();
            openList.add(start);
            gScore.put(start, 0.0);

            while (!openList.isEmpty()) {
                BlockPos current = openList.poll();

                numNodesVisited++;
                maxOpenListSize = Math.max(maxOpenListSize, openList.size());

                if (current.equals(target)) {
                    // Reconstruct the path
                    ArrayList<BlockPos> path = new ArrayList<>();
                    path.add(current);
                    while (cameFrom.containsKey(current)) {
                        current = cameFrom.get(current);
                        path.add(0, current);
                    }

                    if (debug) {
                        ChatUtils.message("Path found!");
                        ChatUtils.message("Number of nodes visited: " + numNodesVisited);
                        ChatUtils.message("Number of nodes considered: " + numNodesConsidered);
                        ChatUtils.message("Maximum size of open list: " + maxOpenListSize);
                        ChatUtils.message("Length of path: " + path.size());
                    } else {

                    }

                    return path;
                }

                for (BlockPos neighbor : getNeighbors(current)) {
                    double tentativeGScore = gScore.get(current) + getDistance(current, neighbor);
                    if (tentativeGScore < gScore.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                        cameFrom.put(neighbor, current);
                        gScore.put(neighbor, tentativeGScore);
                        if (!openList.contains(neighbor)) {
                            openList.add(neighbor);
                            numNodesConsidered++;
                        }
                    }
                }
            }
            return new ArrayList<>();
        }
    }

    private static ArrayList<BlockPos> getNeighbors(BlockPos pos) {
        ArrayList<BlockPos> neighbors = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }
                    BlockPos neighbor = pos.add(x, y, z);
                    if (isWalkable(neighbor)) {
                        neighbors.add(neighbor);
                    }
                }
            }
        }
        return neighbors;
    }

    public static boolean isWalkable(BlockPos pos) {
        assert pos != null;

        return mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR);
    }

    public static BlockPos getClosestSolidBlock(BlockPos targetPos) {
        int renderDistanceChunks = mc.gameSettings.renderDistanceChunks;

        assert targetPos != null;

        double closestDistance = Double.MAX_VALUE;
        BlockPos closestBlock = null;
        for (int x = mc.player.chunkCoordX - renderDistanceChunks; x <= mc.player.chunkCoordX + renderDistanceChunks; x++) {
            for (int z = mc.player.chunkCoordZ - renderDistanceChunks; z <= mc.player.chunkCoordZ + renderDistanceChunks; z++) {
                for (int y = 0; y <= 256; y++) {
                    BlockPos blockPos = new BlockPos(x * 16, y, z * 16);
                    double distance = blockPos.distanceSq(targetPos);
                    if (distance < closestDistance && isBlockAboveAir(blockPos) && !isBlockAboveAir(blockPos.up()) && isBlockAboveAir(blockPos.up(2))) {
                        closestDistance = distance;
                        closestBlock = blockPos;
                    }
                }
            }
        }
        assert closestBlock != null;
        return closestBlock;
    }

    private static boolean isBlockAboveAir(BlockPos pos) {
        Block block = mc.world.getBlockState(pos).getBlock();
        return block.equals(Blocks.AIR);
    }

    private static double getDistance(BlockPos a, BlockPos b) {
        assert a != null;
        assert b != null;
        return Math.sqrt(a.distanceSq(b));
    }

    private static double smoothYaw = 0;
    private static double smoothPitch = 0;

    public static double[] getYawAndPitchForPath(BlockPos playerPos, ArrayList<BlockPos> path, double smoothFactor) {
        double[] yawAndPitch = new double[]{0, 0};

        assert playerPos != null;
        assert path != null;

        if (!path.isEmpty()) {
            // Find closest block and calculate yaw and pitch
            int closestBlockIndex = 0;
            double closestBlockDistance = Double.POSITIVE_INFINITY;
            for (int i = 0; i < path.size(); i++) {
                double distance = playerPos.distanceSq(path.get(i));
                if (distance < closestBlockDistance) {
                    closestBlockDistance = distance;
                    closestBlockIndex = i;
                }
            }

            BlockPos closestBlock = path.get(closestBlockIndex);
            BlockPos nextBlock;
            if (closestBlockIndex == path.size() - 1) {
                nextBlock = closestBlock;
            } else {
                nextBlock = path.get(closestBlockIndex + 1);
            }

            double xDiff = nextBlock.getX() + 0.5 - playerPos.getX();
            double zDiff = nextBlock.getZ() + 0.5 - playerPos.getZ();
            double yDiff = nextBlock.getY() + 0.5 - (playerPos.getY() + 1.0);
            double distanceXZ = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(zDiff, 2));
            double targetYaw = Math.toDegrees(Math.atan2(zDiff, xDiff)) - 90;
            double targetPitch = Math.toDegrees(Math.atan2(-yDiff, distanceXZ));

            double diffYaw = MathHelper.wrapDegrees(targetYaw - smoothYaw);
            double diffPitch = MathHelper.wrapDegrees(targetPitch - smoothPitch);

            // Smooth the values using exponential moving average
            double SMOOTHING_FACTOR = smoothFactor;
            smoothYaw += SMOOTHING_FACTOR * diffYaw;
            smoothPitch += SMOOTHING_FACTOR * diffPitch;

            yawAndPitch[0] = smoothYaw;
            yawAndPitch[1] = smoothPitch;

            return yawAndPitch;
        } else {
        }
        return yawAndPitch;
    }

    public static void render(boolean tesla, ArrayList<BlockPos> blockPosArrayList, int lineWidth, float pathRed, float pathGreen, float pathBlue) {
        if (!tesla) {
            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayer player = mc.player;
            ArrayList<BlockPos> path = blockPosArrayList; // Replace getPath() with the method that returns the ArrayList of BlockPos

            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glLineWidth(lineWidth);
            GL11.glBegin(GL11.GL_LINES);
            GL11.glColor4f(pathRed, pathGreen, pathBlue, 1.0f);

            for (int i = 0; i < path.size() - 1; i++) {
                BlockPos start = path.get(i);
                BlockPos end = path.get(i + 1);
                double startX = start.getX() + 0.5 - player.posX;
                double startY = start.getY() + 1.5 - player.posY;
                double startZ = start.getZ() + 0.5 - player.posZ;
                double endX = end.getX() + 0.5 - player.posX;
                double endY = end.getY() + 1.5 - player.posY;
                double endZ = end.getZ() + 0.5 - player.posZ;
                GL11.glVertex3d(startX, startY, startZ);
                GL11.glVertex3d(endX, endY, endZ);
            }
            GL11.glEnd();
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glPopMatrix();
        } else if (tesla) {
            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayer player = mc.player;
            ArrayList<BlockPos> path = blockPosArrayList; // Replace getPath() with the method that returns the ArrayList of BlockPos

            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glLineWidth(lineWidth);

            // Calculate the player's direction vector
            Vec3d lookVec = player.getLook(1.0f);
            double lookX = lookVec.x;
            double lookZ = lookVec.z;

            // Calculate the offset vector perpendicular to the player's direction vector
            double offsetX = -lookZ;
            double offsetY = 0.0;
            double offsetZ = lookX;

            // Normalize the offset vector
            double offsetLength = Math.sqrt(offsetX * offsetX + offsetZ * offsetZ);
            offsetX /= offsetLength;
            offsetZ /= offsetLength;

            // Draw the two lines
            GL11.glBegin(GL11.GL_LINES);
            GL11.glColor4f(pathRed, pathGreen, pathBlue, 1.0f);

            for (int i = 0; i < path.size() - 1; i++) {
                BlockPos start = path.get(i);
                BlockPos end = path.get(i + 1);
                double startX = start.getX() + 0.5 - player.posX;
                double startY = start.getY() + 1.5 - player.posY;
                double startZ = start.getZ() + 0.5 - player.posZ;
                double endX = end.getX() + 0.5 - player.posX;
                double endY = end.getY() + 1.5 - player.posY;
                double endZ = end.getZ() + 0.5 - player.posZ;

                // Calculate the start and end points for the left line
                double leftStartX = startX + offsetX;
                double leftStartY = startY + offsetY;
                double leftStartZ = startZ + offsetZ;
                double leftEndX = endX + offsetX;
                double leftEndY = endY + offsetY;
                double leftEndZ = endZ + offsetZ;

                // Calculate the start and end points for the right line
                double rightStartX = startX - offsetX;
                double rightStartY = startY - offsetY;
                double rightStartZ = startZ - offsetZ;
                double rightEndX = endX - offsetX;
                double rightEndY = endY - offsetY;
                double rightEndZ = endZ - offsetZ;

                // Draw the left line
                GL11.glVertex3d(leftStartX, leftStartY, leftStartZ);
                GL11.glVertex3d(leftEndX, leftEndY, leftEndZ);

                // Draw the right line
                GL11.glVertex3d(rightStartX, rightStartY, rightStartZ);
                GL11.glVertex3d(rightEndX, rightEndY, rightEndZ);
            }

            GL11.glEnd();
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glPopMatrix();
        }
    }

    public static void movementsEngage(boolean safetyPlus) {
        KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, true);
        KeyBindingUtils.setPressed(mc.gameSettings.keyBindRight, false);
        KeyBindingUtils.setPressed(mc.gameSettings.keyBindBack, false);
        KeyBindingUtils.setPressed(mc.gameSettings.keyBindLeft, false);
        KeyBindingUtils.setPressed(mc.gameSettings.keyBindJump, mc.player.isInWater() || mc.player.collidedHorizontally);
        KeyBindingUtils.setPressed(mc.gameSettings.keyBindSprint, isYawStable(mc.player.rotationYaw));
        KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, PathfinderModule.safetyPlus.isChecked() && AirPathUtils.isPlayerOnCliff(mc.player) && safetyPlus);
    }

    public static void resetMovements() {
        KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, false);
        KeyBindingUtils.setPressed(mc.gameSettings.keyBindJump, false);
        KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, false);
        KeyBindingUtils.setPressed(mc.gameSettings.keyBindRight, false);
        KeyBindingUtils.setPressed(mc.gameSettings.keyBindBack, false);
        KeyBindingUtils.setPressed(mc.gameSettings.keyBindLeft, false);
    }


    public static boolean isPlayerOnCliff(EntityPlayer player) {
        BlockPos playerPos = new BlockPos(player.posX, player.posY, player.posZ);
        int distanceToFall = getDistanceToFall(playerPos);

        return distanceToFall > 1;
    }

    private static int getDistanceToFall(BlockPos position) {
        int distance = 0;
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(position);

        // Check downward until a non-air block is found
        while (mutablePos.getY() > 0) {
            mutablePos = (BlockPos.MutableBlockPos) mutablePos.down();
            if (isSolidBlock(mutablePos)) {
                break;
            }
            distance++;
        }

        return distance;
    }

    private static boolean isSolidBlock(BlockPos position) {
        return !mc.world.getBlockState(position).getBlock().equals(Blocks.AIR);
    }

    public static boolean isOnPath(ArrayList<BlockPos> blockPosArrayList) {
        try {
            boolean onPath = false;
            int range = 2; // Check blocks 2 blocks away from the player in all directions

            for (int x = -range; x <= range; x++) {
                for (int y = -range; y <= range; y++) {
                    for (int z = -range; z <= range; z++) {
                        BlockPos blockPos = new BlockPos(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z);
                        if (blockPosArrayList.contains(blockPos)) {
                            onPath = true;
                            break; // Break out of the loops once a block on the path is found
                        }
                    }
                    if (onPath) {
                        break; // Break out of the loops once a block on the path is found
                    }
                }
                if (onPath) {
                    break; // Break out of the loops once a block on the path is found
                }
            }
            return onPath;
        } catch (Exception ignored) {
        }
        return false;
    }

    private static double prevYaw = 0;

    public static boolean isYawStable(double yaw) {
        boolean isStable = Math.round(Math.abs(yaw - prevYaw)) < 0.01; // set a threshold value for stable yaw
        prevYaw = Math.round(yaw);
        return isStable;
    }

    public static boolean isEntityMoving(Entity entity) {
        double prevPosX = entity.prevPosX;
        double prevPosY = entity.prevPosY;
        double prevPosZ = entity.prevPosZ;

        double currentPosX = entity.posX;
        double currentPosY = entity.posY;
        double currentPosZ = entity.posZ;

        double deltaX = currentPosX - prevPosX;
        double deltaY = currentPosY - prevPosY;
        double deltaZ = currentPosZ - prevPosZ;

        double EPSILON = 0.001;
        return Math.abs(deltaX) > EPSILON || Math.abs(deltaY) > EPSILON || Math.abs(deltaZ) > EPSILON;
    }

    public static String calculateETA(ArrayList<BlockPos> path) {
        double etaInSeconds = path.size() / 4.956; // 4.956 is the average of walking speed and sprinting speed

        int minutes = (int) Math.floor(etaInSeconds / 60);
        int seconds = (int) Math.round(etaInSeconds % 60);

        if (minutes == 0 && seconds == 0) {
            return null;
        } else {
            return String.format("%d minutes, %d seconds", minutes, seconds);
        }
    }
}

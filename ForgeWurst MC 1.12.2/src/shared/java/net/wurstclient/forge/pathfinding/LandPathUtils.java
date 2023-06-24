package net.wurstclient.forge.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.wurstclient.forge.utils.ChatUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class LandPathUtils {

    public static double xTargA = 0;
    public static double yTargA = 0;
    public static double zTargA = 0;

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static ArrayList<BlockPos> createPath(BlockPos start, BlockPos target, boolean debug) {
        if (mc.player.onGround) {
            int numNodesVisited = 0;
            int numNodesConsidered = 0;
            int maxOpenListSize = 0;

            assert start != null;
            assert target != null;

            if (start.getDistance(target.getX(), target.getY(), target.getZ()) < mc.gameSettings.renderDistanceChunks * 16) {
                PriorityQueue<BlockPos> openList = new PriorityQueue<>(Comparator.comparingDouble(pos -> getDistance(pos, target)));
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
            } else {
                BlockPos blockPos = getClosestSolidBlock(target);
                PriorityQueue<BlockPos> openList = new PriorityQueue<>(Comparator.comparingDouble(pos -> getDistance(pos, blockPos)));
                HashMap<BlockPos, BlockPos> cameFrom = new HashMap<>();
                HashMap<BlockPos, Double> gScore = new HashMap<>();
                openList.add(start);
                gScore.put(start, 0.0);

                assert blockPos != null;

                xTargA = blockPos.getX();
                yTargA = blockPos.getY();
                zTargA = blockPos.getZ();

                while (!openList.isEmpty()) {
                    BlockPos current = openList.poll();

                    numNodesVisited++;
                    maxOpenListSize = Math.max(maxOpenListSize, openList.size());

                    if (current.equals(blockPos)) {
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
            }
            return new ArrayList<>();
        }
        return new ArrayList<>();
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
                    if (distance < closestDistance && !isBlockAboveAir(blockPos) && isBlockAboveAir(blockPos.up()) && isBlockAboveAir(blockPos.up(2))) {
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

    private static ArrayList<BlockPos> getNeighbors(BlockPos pos) {
        ArrayList<BlockPos> neighbors = new ArrayList<>();
        assert pos != null;
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

    private static double getDistance(BlockPos a, BlockPos b) {
        assert a != null;
        assert b != null;
        return Math.sqrt(a.distanceSq(b));
    }

    public static boolean isWalkable(BlockPos pos) {
        assert pos != null;

        return !mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR);
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
            ChatUtils.error("Error, path is empty, contact dev!");
        }
        return yawAndPitch;
    }
}

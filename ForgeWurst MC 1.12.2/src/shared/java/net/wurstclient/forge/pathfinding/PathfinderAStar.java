package net.wurstclient.forge.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.forge.clickgui.Radar;
import net.wurstclient.forge.hacks.pathing.PathfinderModule;
import net.wurstclient.forge.utils.BlockUtils;
import net.wurstclient.forge.utils.RenderUtils;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class PathfinderAStar {
    private BlockPos startVec3;
    private BlockPos endVec3;
    private ArrayList<BlockPos> path = new ArrayList<BlockPos>();
    private ArrayList<Hub> hubs = new ArrayList<Hub>();
    private ArrayList<Hub> hubsToWork = new ArrayList<Hub>();
    private double minDistanceSquared = 9;
    private boolean nearest = true;

    private static boolean air;

    private static final Minecraft mc = Minecraft.getMinecraft();

    private static BlockPos[] flatCardinalDirections = {
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1),

            new BlockPos(1, -1, 0),
            new BlockPos(-1, -1, 0),
            new BlockPos(0, -1, 1),
            new BlockPos(0, -1, -1),

            new BlockPos(1, 1, 0),
            new BlockPos(-1, 1, 0),
            new BlockPos(0, 1, 1),
            new BlockPos(0, 1, -1),
    };

    public PathfinderAStar(BlockPos startVec3, BlockPos endVec3, boolean air) {
        this.startVec3 = new BlockPos(MathHelper.floor(startVec3.getX()), MathHelper.floor(startVec3.getY()), MathHelper.floor(startVec3.getZ()));
        this.endVec3 = new BlockPos(MathHelper.floor(endVec3.getX()), MathHelper.floor(endVec3.getY()), MathHelper.floor(endVec3.getZ()));

        this.air = air;
    }

    public ArrayList<BlockPos> getPath() {
        return path;
    }

    public void compute() {
        compute(PathfinderModule.loops.getValueI(), PathfinderModule.depth.getValueI());
    }

    public void compute(int loops, int depth) {
        path.clear();
        hubsToWork.clear();
        ArrayList<BlockPos> initPath = new ArrayList<BlockPos>();
        initPath.add(startVec3);
        hubsToWork.add(new Hub(startVec3, null, initPath, startVec3.distanceSq(endVec3), 0, 0));
        search:
        for (int i = 0; i < loops; i++) {
            Collections.sort(hubsToWork, new CompareHub());
            int j = 0;
            if (hubsToWork.size() == 0) {
                break;
            }
            for (Hub hub : new ArrayList<Hub>(hubsToWork)) {
                j++;
                if (j > depth) {
                    break;
                } else {
                    hubsToWork.remove(hub);
                    hubs.add(hub);

                    for (BlockPos direction : flatCardinalDirections) {
                        BlockPos loc = new BlockPos(MathHelper.floor(hub.getLoc().add(direction).getX()), MathHelper.floor(hub.getLoc().add(direction).getY()), MathHelper.floor(hub.getLoc().add(direction).getZ()));
                        if (!air ? checkPositionValidity(loc, false) : checkPositionValidity(loc, true)) {
                            if (addHub(hub, loc, 0)) {
                                break search;
                            }
                        }
                    }

                    BlockPos loc1 = new BlockPos(MathHelper.floor(hub.getLoc().add(0, 1, 0).getX()), MathHelper.floor(hub.getLoc().add(0, 1, 0).getY()), MathHelper.floor(hub.getLoc().add(0, 1, 0).getZ()));
                    if (!air ? checkPositionValidity(loc1, false) : checkPositionValidity(loc1, true)) {
                        if (addHub(hub, loc1, 0)) {
                            break search;
                        }
                    }

                    BlockPos loc2 = new BlockPos(MathHelper.floor(hub.getLoc().add(0, -1, 0).getX()), MathHelper.floor(hub.getLoc().add(0, -1, 0).getY()), MathHelper.floor(hub.getLoc().add(0, -1, 0).getZ()));
                    if (!air ? checkPositionValidity(loc2, false) : checkPositionValidity(loc2, true)) {
                        if (addHub(hub, loc2, 0)) {
                            break search;
                        }
                    }
                }
            }
        }
        if (nearest) {
            Collections.sort(hubs, new CompareHub());
            path = hubs.get(0).getPath();
        }
    }

    public static boolean checkPositionValidity(BlockPos loc, boolean checkGround) {
        return checkPositionValidity((int) loc.getX(), (int) loc.getY(), (int) loc.getZ(), checkGround);
    }

    public static boolean checkPositionValidity(int x, int y, int z, boolean checkGround) {
        BlockPos block1 = new BlockPos(x, y, z);
        BlockPos block2 = new BlockPos(x, y + 1, z);
        BlockPos block3 = new BlockPos(x, y - 1, z);
        if (air) {
            return mc.world.getBlockState(block1).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(block2).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(block3).getBlock().equals(Blocks.AIR);
        } else {
            return !isBlockSolid(block1) && !isBlockSolid(block2) && (isBlockSolid(block3) || !checkGround) && isSafeToWalkOn(block3);
        }
    }

    private static boolean isBlockSolid(BlockPos block) {
        return !mc.world.getBlockState(block).getBlock().equals(Blocks.AIR);
    }

    private static boolean isSafeToWalkOn(BlockPos block) {
        return !mc.world.getBlockState(block).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(block.add(0, 1, 0)).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(block.add(0, 2, 0)).getBlock().equals(Blocks.AIR);
    }

    public Hub isHubExisting(BlockPos loc) {
        for (Hub hub : hubs) {
            if (hub.getLoc().equals(loc)) {
                return hub;
            }
        }
        for (Hub hub : hubsToWork) {
            if (hub.getLoc().equals(loc)) {
                return hub;
            }
        }
        return null;
    }

    public boolean addHub(Hub parent, BlockPos loc, double cost) {
        Hub existingHub = isHubExisting(loc);
        double totalCost = cost;
        if (parent != null) {
            totalCost += parent.getTotalCost();
        }
        if (existingHub == null) {
            if ((loc.getX() == endVec3.getX() && loc.getY() == endVec3.getY() && loc.getZ() == endVec3.getZ()) || (minDistanceSquared != 0 && loc.distanceSq(endVec3) <= minDistanceSquared)) {
                path.clear();
                path = parent.getPath();
                path.add(loc);
                return true;
            } else {
                ArrayList<BlockPos> path = new ArrayList<BlockPos>(parent.getPath());
                path.add(loc);
                hubsToWork.add(new Hub(loc, parent, path, loc.distanceSq(endVec3), cost, totalCost));
            }
        } else if (existingHub.getCost() > cost) {
            ArrayList<BlockPos> path = new ArrayList<BlockPos>(parent.getPath());
            path.add(loc);
            existingHub.setLoc(loc);
            existingHub.setParent(parent);
            existingHub.setPath(path);
            existingHub.setSquareDistanceToFromTarget(loc.distanceSq(endVec3));
            existingHub.setCost(cost);
            existingHub.setTotalCost(totalCost);
        }
        return false;
    }

    private class Hub {
        private BlockPos loc = null;
        private Hub parent = null;
        private ArrayList<BlockPos> path;
        private double squareDistanceToFromTarget;
        private double cost;
        private double totalCost;

        public Hub(BlockPos loc, Hub parent, ArrayList<BlockPos> path, double squareDistanceToFromTarget, double cost, double totalCost) {
            this.loc = loc;
            this.parent = parent;
            this.path = path;
            this.squareDistanceToFromTarget = squareDistanceToFromTarget;
            this.cost = cost;
            this.totalCost = totalCost;
        }

        public BlockPos getLoc() {
            return loc;
        }

        public Hub getParent() {
            return parent;
        }

        public ArrayList<BlockPos> getPath() {
            return path;
        }

        public double getSquareDistanceToFromTarget() {
            return squareDistanceToFromTarget;
        }

        public double getCost() {
            return cost;
        }

        public void setLoc(BlockPos loc) {
            this.loc = loc;
        }

        public void setParent(Hub parent) {
            this.parent = parent;
        }

        public void setPath(ArrayList<BlockPos> path) {
            this.path = path;
        }

        public void setSquareDistanceToFromTarget(double squareDistanceToFromTarget) {
            this.squareDistanceToFromTarget = squareDistanceToFromTarget;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }

        public double getTotalCost() {
            return totalCost;
        }

        public void setTotalCost(double totalCost) {
            this.totalCost = totalCost;
        }
    }

    public class CompareHub implements Comparator<Hub> {
        @Override
        public int compare(Hub o1, Hub o2) {
            return (int) (
                    (o1.getSquareDistanceToFromTarget() + o1.getTotalCost()) - (o2.getSquareDistanceToFromTarget() + o2.getTotalCost())
            );
        }
    }

    public static void render(ArrayList<BlockPos> blockPosArrayList, int lineWidth, float pathRed, float pathGreen, float pathBlue) {
        // GL settings
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(2);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        GL11.glPushMatrix();
        GL11.glTranslated(-TileEntityRendererDispatcher.staticPlayerX,
                -TileEntityRendererDispatcher.staticPlayerY,
                -TileEntityRendererDispatcher.staticPlayerZ);

        for (int i = 0; i < blockPosArrayList.size() - 1; i++) {
            GL11.glColor4f(pathRed, pathGreen, pathBlue, 1F);
            GL11.glBegin(GL11.GL_LINE);
            RenderUtils.drawArrow(new Vec3d(blockPosArrayList.get(i).getX() + 0.5, blockPosArrayList.get(i).getY(), blockPosArrayList.get(i).getZ() + 0.5), new Vec3d(blockPosArrayList.get(i + 1).getX() + 0.5, blockPosArrayList.get(i + 1).getY(), blockPosArrayList.get(i + 1).getZ() + 0.5));
            GL11.glEnd();
        }

        GL11.glColor4f(pathRed, pathGreen, pathBlue, 1F);
        GL11.glBegin(GL11.GL_LINE);
        RenderUtils.drawNode(BlockUtils.getBoundingBox(blockPosArrayList.get(blockPosArrayList.size() - 1)));
        GL11.glEnd();

        GL11.glPopMatrix();

        // GL resets
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        Radar.HIGHLIGHTED_POSITIONS = blockPosArrayList;
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

        double EPSILON = 0.1;
        return Math.abs(deltaX) > EPSILON || Math.abs(deltaY) > EPSILON || Math.abs(deltaZ) > EPSILON;
    }

    public static double[] calculateMotion(ArrayList<BlockPos> path, double rotationYaw, double speed) {
        double playerX = mc.player.posX;
        double playerZ = mc.player.posZ;
        double velocityX = mc.player.motionX;
        double velocityZ = mc.player.motionZ;

        rotationYaw = Math.toRadians(rotationYaw);

        int closestBlockIndex = 0;
        double closestBlockDistance = Double.POSITIVE_INFINITY;

        for (int i = 0; i < path.size(); i++) {
            double distance = mc.player.getDistanceSq(path.get(i));
            if (distance < closestBlockDistance) {
                closestBlockDistance = distance;
                closestBlockIndex = i;
            }
        }

        BlockPos closestBlock = path.get(closestBlockIndex);

        // Ensure we don't exceed the array size when accessing nextBlock
        BlockPos nextBlock = (closestBlockIndex == path.size() - 1) ? closestBlock : path.get(closestBlockIndex + 1);

        // Adjust delta values based on player's velocity

        double deltaX = nextBlock.getX() + 0.5 - playerX + velocityX;
        double deltaZ = nextBlock.getZ() + 0.5 - playerZ + velocityZ;

        // Calculate the target rotationYaw based on angle difference
        double targetAngle = Math.atan2(deltaZ, deltaX);
        double playerAngle = Math.toRadians(rotationYaw);

        // Smoothly adjust the player's rotationYaw
        double angleDifference = targetAngle - playerAngle;
        if (angleDifference > Math.PI) {
            angleDifference -= 2 * Math.PI;
        } else if (angleDifference < -Math.PI) {
            angleDifference += 2 * Math.PI;
        }
        rotationYaw = Math.toDegrees(playerAngle + angleDifference);

        // Calculate the distance to the target position
        double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        // Calculate the motion components (motionX and motionZ)
        double motionX = Math.cos(angleDifference) * distance;
        double motionZ = Math.sin(angleDifference) * distance;

        // Apply motion limits based on speed
        double motionMagnitude = Math.sqrt(motionX * motionX + motionZ * motionZ);
        if (motionMagnitude > speed) {
            motionX *= speed / motionMagnitude;
            motionZ *= speed / motionMagnitude;
        }

        return new double[]{motionX, motionZ};
    }

    public static BlockPos getTargetPositionInPathArray(ArrayList<BlockPos> path) {
        int closestBlockIndex = 0;
        double closestBlockDistance = Double.POSITIVE_INFINITY;
        for (int i = 0; i < path.size(); i++) {
            double distance = mc.player.getDistanceSq(path.get(i));
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

        return nextBlock;
    }

    public static BlockPos findNearestReachableBlock(Block targetBlock) {
        EntityPlayer entityPlayer = mc.player;
        int maxRadius = mc.gameSettings.renderDistanceChunks * 15; // Set the maximum search radius
        BlockPos playerPos = entityPlayer.getPosition();
        Queue<BlockPos> queue = new ArrayDeque<>();
        boolean[][][] visited = new boolean[maxRadius * 2 + 1][256][maxRadius * 2 + 1];

        queue.add(playerPos);

        while (!queue.isEmpty()) {
            BlockPos currentPos = queue.poll();

            if (ibra(currentPos, entityPlayer) && mc.world.getBlockState(currentPos).getBlock().equals(targetBlock)) {
                return currentPos; // Return the position if a matching block is found and it's reachable.
            }

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        BlockPos neighborPos = currentPos.add(x, y, z);
                        if (Math.abs(neighborPos.getX() - playerPos.getX()) <= maxRadius &&
                                neighborPos.getY() >= 0 && neighborPos.getY() <= 255 &&
                                Math.abs(neighborPos.getZ() - playerPos.getZ()) <= maxRadius &&
                                !visited[neighborPos.getX() - playerPos.getX() + maxRadius][neighborPos.getY()][neighborPos.getZ() - playerPos.getZ() + maxRadius]) {
                            queue.add(neighborPos);
                            visited[neighborPos.getX() - playerPos.getX() + maxRadius][neighborPos.getY()][neighborPos.getZ() - playerPos.getZ() + maxRadius] = true;
                        }
                    }
                }
            }
        }

        return null; // Return null if no matching block is found within the search radius.
    }

    // Check if a block is reachable (assuming no block needs to be mined)
    public static boolean isBlockReachable(BlockPos blockPos, EntityPlayer entityPlayer) {
        PathfinderAStar pathfinderAStar;
        pathfinderAStar = new PathfinderAStar(entityPlayer.getPosition(), blockPos, air);
        pathfinderAStar.compute();
        return pathfinderAStar.getPath().size() > 0;
    }


    // Check if a block is reachable (assuming no block needs to be mined)
    private static boolean ibra(BlockPos blockPos, EntityPlayer entityPlayer) {
        PathfinderAStar pathfinderAStar;
        pathfinderAStar = new PathfinderAStar(entityPlayer.getPosition(), blockPos, air);
        pathfinderAStar.compute(1, 1); // Its so resource intensive we must use 1 loop 1 depth.
        return pathfinderAStar.getPath().size() > 0;
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
}

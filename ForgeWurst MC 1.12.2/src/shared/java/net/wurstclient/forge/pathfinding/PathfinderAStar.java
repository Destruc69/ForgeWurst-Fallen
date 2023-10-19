package net.wurstclient.forge.pathfinding;

import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.forge.hacks.pathing.AutoPilot;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class PathfinderAStar {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private BlockPos startVec3;
    private BlockPos endVec3;
    private ArrayList<BlockPos> path = new ArrayList<BlockPos>();
    private ArrayList<Hub> hubs = new ArrayList<Hub>();
    private ArrayList<Hub> hubsToWork = new ArrayList<Hub>();
    private double minDistanceSquared = 9;
    private boolean nearest = true;
    private static boolean air;

    private static BlockPos[] flatCardinalDirections = {
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1)
    };

    public PathfinderAStar(BlockPos startVec3, BlockPos endVec3, boolean air) {
        this.startVec3 = startVec3.add(0, 0, 0);
        this.endVec3 = endVec3.add(0, 0, 0);
        this.air = air;
    }

    public ArrayList<BlockPos> getPath() {
        return path;
    }

    public void compute() {
        compute(1000, 4);
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
                        BlockPos loc = hub.getLoc().add(direction);
                        if (checkPositionValidity(loc, false)) {
                            if (addHub(hub, loc, 0)) {
                                break search;
                            }
                        }
                    }

                    BlockPos loc1 = hub.getLoc().add(0, 1, 0);
                    if (checkPositionValidity(loc1, false)) {
                        if (addHub(hub, loc1, 0)) {
                            break search;
                        }
                    }

                    BlockPos loc2 = hub.getLoc().add(0, -1, 0);
                    if (checkPositionValidity(loc2, false)) {
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
        if (!air) {
            BlockPos block1 = new BlockPos(x, y, z);
            BlockPos block2 = new BlockPos(x, y + 1, z);
            BlockPos block3 = new BlockPos(x, y - 1, z);
            return !isBlockSolid(block1) && !isBlockSolid(block2) && (isBlockSolid(block3) || !checkGround) && isSafeToWalkOn(block3);
        } else {
            BlockPos block1 = new BlockPos(x, y, z);
            BlockPos block2 = new BlockPos(x, y + 1, z);
            BlockPos block3 = new BlockPos(x, y - 1, z);

            // For air pathfinding, you want to allow movement through empty spaces (air blocks).
            // So, return true if block1 and block2 are air blocks, and you may want to avoid checking ground.
            return !isBlockSolid(block1) && !isBlockSolid(block2) && (isBlockSolid(block3) || !checkGround) && isSafeToWalkOn(block3);
        }
    }

    private static boolean isBlockSolid(BlockPos block) {
        return Minecraft.getMinecraft().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).isFullBlock() ||
                (Minecraft.getMinecraft().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockSlab) ||
                (Minecraft.getMinecraft().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockStairs)||
                (Minecraft.getMinecraft().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockCactus)||
                (Minecraft.getMinecraft().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockChest)||
                (Minecraft.getMinecraft().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockEnderChest)||
                (Minecraft.getMinecraft().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockSkull)||
                (Minecraft.getMinecraft().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockPane)||
                (Minecraft.getMinecraft().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockFence)||
                (Minecraft.getMinecraft().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockWall)||
                (Minecraft.getMinecraft().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockGlass)||
                (Minecraft.getMinecraft().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockPistonBase)||
                (Minecraft.getMinecraft().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockPistonExtension)||
                (Minecraft.getMinecraft().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockPistonMoving)||
                (Minecraft.getMinecraft().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockStainedGlass)||
                (Minecraft.getMinecraft().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockTrapDoor);
    }

    private static boolean isSafeToWalkOn(BlockPos block) {
        return !(Minecraft.getMinecraft().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())) instanceof BlockFence) &&
                !(Minecraft.getMinecraft().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())) instanceof BlockWall);
    }

    public Hub isHubExisting(BlockPos loc) {
        for (Hub hub : hubs) {
            if (hub.getLoc().getX() == loc.getX() && hub.getLoc().getY() == loc.getY() && hub.getLoc().getZ() == loc.getZ()) {
                return hub;
            }
        }
        for (Hub hub : hubsToWork) {
            if (hub.getLoc().getX() == loc.getX() && hub.getLoc().getY() == loc.getY() && hub.getLoc().getZ() == loc.getZ()) {
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
                ArrayList<BlockPos> path = new ArrayList<BlockPos>((Collection<? extends BlockPos>) parent.getPath());
                path.add(loc);
                hubsToWork.add(new Hub(loc, parent, path, loc.distanceSq(endVec3), cost, totalCost));
            }
        } else if (existingHub.getCost() > cost) {
            ArrayList<BlockPos> path = new ArrayList<>(parent.getPath());
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
        double etaInSeconds = path.size() / AutoPilot.baseSpeed.getValue();

        int minutes = (int) Math.floor(etaInSeconds / 60);
        int seconds = (int) Math.round(etaInSeconds % 60);

        if (minutes == 0 && seconds == 0) {
            return null;
        } else {
            return String.format("%d minutes, %d seconds", minutes, seconds);
        }
    }

    public static double[] calculateMotion(ArrayList<BlockPos> path, double rotationYaw, boolean sprint) {
        // Player's current position (assumed values for demonstration)
        double playerX = mc.player.lastTickPosX;
        double playerZ = mc.player.lastTickPosZ;

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
        BlockPos nextBlock;
        if (closestBlockIndex == path.size() - 1) {
            nextBlock = closestBlock;
        } else {
            nextBlock = path.get(closestBlockIndex + 1);
        }

        BlockPos targetPos = nextBlock;

        // Calculate the distance to the target position
        double deltaX = targetPos.getX() + 0.5 - playerX; // Adding 0.5 to target center of the block
        double deltaZ = targetPos.getZ() + 0.5 - playerZ; // Adding 0.5 to target center of the block

        // Calculate the angle between the player's rotation and the target position
        double targetAngle = Math.atan2(deltaZ, deltaX);
        double playerAngle = Math.toRadians(rotationYaw);

        // Calculate the angle difference between player's rotation and target position
        double angleDifference = targetAngle - playerAngle;

        // Calculate the distance to the target position
        double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        // Calculate the motion components (motionX and motionZ)
        double motionX = Math.cos(angleDifference) * distance;
        double motionZ = Math.sin(angleDifference) * distance;

        // Apply motion limits based on sprinting
        double maxMotion = sprint ? 0.26 : 0.2;
        double motionMagnitude = Math.sqrt(motionX * motionX + motionZ * motionZ);
        if (motionMagnitude > maxMotion) {
            motionX *= maxMotion / motionMagnitude;
            motionZ *= maxMotion / motionMagnitude;
        }

        return new double[]{motionX, motionZ};
    }

    // Given a block type, find the nearest reachable block position of that type
    public static BlockPos findNearestReachableBlock(Block targetBlock) {
        Minecraft mc = Minecraft.getMinecraft();
        Vec3d playerPos = mc.player.getPositionVector();
        double closestDistance = Double.MAX_VALUE;
        BlockPos closestBlockPos = null;

        int a = mc.gameSettings.renderDistanceChunks * 15;
        for (int x = -a; x <= a; x++) {
            for (int y = 0; y <= 256; y++) {
                for (int z = -a; z <= a; z++) {
                    BlockPos currentPos = new BlockPos(playerPos.x + x, y, playerPos.z + z);
                    if (mc.world.getBlockState(currentPos).getBlock() == targetBlock) {
                        double distance = playerPos.distanceTo(new Vec3d(new BlockPos(currentPos.getX(), currentPos.getY(), currentPos.getZ()).getX(), new BlockPos(currentPos.getX(), currentPos.getY(), currentPos.getZ()).getY(), new BlockPos(currentPos.getX(), currentPos.getY(), currentPos.getZ()).getZ()));

                        if (distance < closestDistance && isBlockReachable(currentPos, mc.player)) {
                            closestDistance = distance;
                            closestBlockPos = currentPos;
                        }
                    }
                }
            }
        }

        return closestBlockPos;
    }

    // Check if a block is reachable (assuming no block needs to be mined)
    public static boolean isBlockReachable(BlockPos blockPos, EntityPlayer entityPlayer) {
        PathfinderAStarAIR pathfinderAStar;
        pathfinderAStar = new PathfinderAStarAIR(entityPlayer.getPosition(), blockPos);
        pathfinderAStar.compute();
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

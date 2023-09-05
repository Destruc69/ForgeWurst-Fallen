package net.wurstclient.forge.pathfinding;

import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TestPathfinderAStar {
    private BlockPos startBlockPos;
    private BlockPos endBlockPos;
    private ArrayList<BlockPos> path = new ArrayList<BlockPos>();
    private ArrayList<Hub> hubs = new ArrayList<Hub>();
    private ArrayList<Hub> hubsToWork = new ArrayList<Hub>();
    private double minDistanceSquared = 9;
    private boolean nearest = true;

    private static BlockPos[] flatCardinalDirections = {
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1)
    };

    public TestPathfinderAStar(BlockPos startBlockPos, BlockPos endBlockPos) {
        this.startBlockPos = startBlockPos;
        this.endBlockPos = endBlockPos;
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
        initPath.add(startBlockPos);
        hubsToWork.add(new Hub(startBlockPos, null, initPath, startBlockPos.distanceSq(endBlockPos), 0, 0));
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

                    BlockPos loc1 = hub.getLoc().up();
                    if (checkPositionValidity(loc1, false)) {
                        if (addHub(hub, loc1, 0)) {
                            break search;
                        }
                    }

                    BlockPos loc2 = hub.getLoc().down();
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
        return checkPositionValidity(loc.getX(), loc.getY(), loc.getZ(), checkGround);
    }

    public static boolean checkPositionValidity(int x, int y, int z, boolean checkGround) {
        BlockPos block1 = new BlockPos(x, y, z);
        BlockPos block2 = new BlockPos(x, y + 1, z);
        BlockPos block3 = new BlockPos(x, y - 1, z);
        return !isBlockSolid(block1) && !isBlockSolid(block2) && (isBlockSolid(block3) || !checkGround) && isSafeToWalkOn(block3);
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
            if ((loc.equals(endBlockPos) || (minDistanceSquared != 0 && loc.distanceSq(endBlockPos) <= minDistanceSquared))) {
                path.clear();
                path = parent.getPath();
                path.add(loc);
                return true;
            } else {
                ArrayList<BlockPos> path = new ArrayList<BlockPos>(parent.getPath());
                path.add(loc);
                hubsToWork.add(new Hub(loc, parent, path, loc.distanceSq(endBlockPos), cost, totalCost));
            }
        } else if (existingHub.getCost() > cost) {
            ArrayList<BlockPos> path = new ArrayList<BlockPos>(parent.getPath());
            path.add(loc);
            existingHub.setLoc(loc);
            existingHub.setParent(parent);
            existingHub.setPath(path);
            existingHub.setSquareDistanceToFromTarget(loc.distanceSq(endBlockPos));
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
}
package net.wurstclient.forge.other.customs.entities;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.hacks.pathing.PathfinderModule;
import net.wurstclient.forge.pathfinding.LandPathUtils;

import java.util.ArrayList;

public class DummyEntity extends EntityOtherPlayerMP {

    private ArrayList<BlockPos> blockPosArrayList;

    public DummyEntity(double x, double y, double z, GameProfile gameProfile, boolean alex) {
        super(Minecraft.getMinecraft().world, gameProfile);
        if (alex) {
            setAlexSkin();
        } else {
            setSteveSkin();
        }
        getDataManager().set(EntityPlayer.PLAYER_MODEL_FLAG, WMinecraft
                .getPlayer().getDataManager().get(EntityPlayer.PLAYER_MODEL_FLAG));
        Minecraft.getMinecraft().world.addEntityToWorld(getEntityId(), this);
        this.setPosition(x, y, z);
    }

    public void moveTowards(BlockPos blockPos) {
        try {
            if (Minecraft.getMinecraft().player.ticksExisted % 20 == 0) {
                blockPosArrayList = LandPathUtils.createPath(this.getPosition().add(0, -1, 0), blockPos, false);
            }
            if (blockPosArrayList.size() > 0) {
                moveForward(true);
            }
            double[] toLook = LandPathUtils.getYawAndPitchForPath(this.getPosition().add(0, -1, 0), blockPosArrayList, PathfinderModule.smoothingFactor.getValue());
            this.rotationYaw = (float) toLook[0];
            this.rotationYawHead = (float) toLook[0];
            this.rotationPitch = (float) toLook[1];
        } catch (Exception ignored) {
        }
    }

    public void moveForward(boolean sprint) {
        // Calculate the motion values based on the entity's yaw angle
        double radians = Math.toRadians(rotationYaw);
        double motionX = -Math.sin(radians) * (sprint ? 0.2 : 0.1);
        double motionZ = Math.cos(radians) * (sprint ? 0.2 : 0.1);

        // Set the player's new position by adding the motion values to the current position
        setPosition(posX + motionX, posY, posZ + motionZ);

        IBlockState ibs = this.world.getBlockState(this.getPosition());
        if (!(ibs.getBlock().equals(Blocks.AIR)) && !(ibs.getBlock().isPassable(this.world, this.getPosition()))) {
            this.posY = this.posY + 1;
        } else if (this.world.getBlockState(this.getPosition().add(0, -1, 0)).getBlock().equals(Blocks.AIR)) {
            this.posY = this.posY - 1;
        }

        this.limbSwingAmount = sprint ? 1f : 0.5f;
    }


    public void setSteveSkin() {
        setSkin("textures/entity/steve.png");
    }

    public void setAlexSkin() {
        setSkin("textures/entity/alex.png");
    }

    private void setSkin(String skinTexture) {
        // Get the player profile of the entity
        GameProfile gameProfile = getGameProfile();

        // Remove existing skin properties
        gameProfile.getProperties().removeAll("textures");

        // Create a new skin property with the given texture
        Property skinProperty = new Property("textures", skinTexture);
        gameProfile.getProperties().put("textures", skinProperty);
    }
}
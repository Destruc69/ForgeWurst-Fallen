package net.wurstclient.forge;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.wurstclient.forge.compatibility.WHackList;
import net.wurstclient.forge.hacks.ClickGuiHack;
import net.wurstclient.forge.hacks.combat.*;
import net.wurstclient.forge.hacks.hud.CoordinatesModule;
import net.wurstclient.forge.hacks.hud.DataFuelModule;
import net.wurstclient.forge.hacks.hud.EntityListModule;
import net.wurstclient.forge.hacks.movement.*;
import net.wurstclient.forge.hacks.pathing.*;
import net.wurstclient.forge.hacks.player.*;
import net.wurstclient.forge.hacks.render.*;
import net.wurstclient.forge.hacks.world.*;
import net.wurstclient.forge.hacks.world.FriendsModule;
import net.wurstclient.forge.settings.Setting;
import net.wurstclient.forge.utils.JsonUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;

public final class HackList extends WHackList
{
	public final RadarHack radarHack = register(new RadarHack());
	public final NoFall noFall = register(new NoFall());
	public final Speed speed = register(new Speed());
	public final Step step = register(new Step());
	public final Gadgetry gadgetry = register(new Gadgetry());
	public final AntiForge antiForge = register(new AntiForge());
	public final AutoClutch autoClutch = register(new AutoClutch());
	public final Arson arson = register(new Arson());
	public final AutoGrief autoGrief = register(new AutoGrief());
	public final AutoTNT autoTNT = register(new AutoTNT());
	public final AutoMount autoMount = register(new AutoMount());
	public final DataFuelModule dataFuelModule = register(new DataFuelModule());
	public final PacketLimiter packetLimiter = register(new PacketLimiter());
	public final AutoJoinModule autoJoinModule = register(new AutoJoinModule());
	public final AutoMine autoMine = register(new AutoMine());
	public final AutoPilot autoPilot = register(new AutoPilot());
	public final HideMyAss hideMyAss = register(new HideMyAss());
	public final ServerCrasher serverCrasher = register(new ServerCrasher());
	public final AutoClicker autoClicker = register(new AutoClicker());
	public final Giant giant = register(new Giant());
	public final DamageHack damageHack = register(new DamageHack());
	public final ChunkKeeper chunkKeeper = register(new ChunkKeeper());
	public final SoundEventLogger soundEventLogger = register(new SoundEventLogger());
	public final ShulkerViewer shulkerViewer = register(new ShulkerViewer());
	public final AutoTotem autoTotem = register(new AutoTotem());
	public final AutoAttack autoAttack = register(new AutoAttack());
	public final FOV rotations = register(new FOV());
	public final PathfinderModule pathfinderModule = register(new PathfinderModule());
	public final Debuger debuger = register(new Debuger());
	public final FastFall fastFall = register(new FastFall());
	public final Hitboxes hitboxes = register(new Hitboxes());
	public final Discord discord = register(new Discord());
	public final NoKnockBack noKnockBack = register(new NoKnockBack());
	public final Criticals criticals = register(new Criticals());
	public final CrystalAuraHack crystalAuraHack = register(new CrystalAuraHack());
	public final Nuker nuker = register(new Nuker());
	public final Test test = register(new Test());
	public final FriendsModule friendsModule = register(new FriendsModule());
	public final IllegalSlot illegalSlot = register(new IllegalSlot());
	public final NoRender noRender = register(new NoRender());
	public final NewChunks newChunks = register(new NewChunks());
	public final Breadcrumbs breadcrumbs = register(new Breadcrumbs());
	public final AntiLag antiLag = register(new AntiLag());
	public final BaseFinder baseFinder = register(new BaseFinder());
	public final Cheststealer cheststealer = register(new Cheststealer());
	public final InvMove invMove = register(new InvMove());
	public final Phase phase = register(new Phase());
	public final TargetStrafe targetStrafe = register(new TargetStrafe());
	public final Aimbot aimbot = register(new Aimbot());
	public final AntiCooldown antiCooldown = register(new AntiCooldown());
	public final AntiBot antiBot = register(new AntiBot());
	public final PositionESP positionESP = register(new PositionESP());
	public final WallESP wallESP = register(new WallESP());
	public final AntiBlockLag antiBlockLag = register(new AntiBlockLag());
	public final AntiHunger antiHunger = register(new AntiHunger());
	public final Jesus jesus = register(new Jesus());
	public final AntiFog antiFog = register(new AntiFog());
	public final AntiWeather antiWeather = register(new AntiWeather());
	public final AntiKick antiKick = register(new AntiKick());
	public final FakePlayer fakePlayer = register(new FakePlayer());
	public final NoSound noSound = register(new NoSound());
	public final AutoTool autoTool = register(new AutoTool());
	public final FreeCam freeCam = register(new FreeCam());
	public final Flight flight = register(new Flight());
	public final AntiCollide antiCollide = register(new AntiCollide());
	public final FastBow fastBow = register(new FastBow());
	public final WaypointsModule pointer = register(new WaypointsModule());
	public final Stability stability = register(new Stability());
	public final CPvPViewer cPvPViewer = register(new CPvPViewer());
	public final Killaura killaura = register(new Killaura());
	public final Scaffold scaffold = register(new Scaffold());
	public final Timerr timerr = register(new Timerr());
	public final LongJump longJump = register(new LongJump());
	public final NameTags nameTags = register(new NameTags());
	public final NoHurtCam noHurtCam = register(new NoHurtCam());
	public final ReachPlus reachPlus = register(new ReachPlus());
	public final FastBreak fastBreak = register(new FastBreak());
	public final FastPlace fastPlace = register(new FastPlace());
	public final Tunneler tunneler = register(new Tunneler());
	public final Freeze freeze = register(new Freeze());
	public final MoreInv moreInv = register(new MoreInv());
	public final AutoEat autoEat = register(new AutoEat());
	public final CoordinatesModule coordinatesModule = register(new CoordinatesModule());
	public final HighJump highJump = register(new HighJump());
	public final AutoLeave autoLeave = register(new AutoLeave());
	public final EntityFlight entityFlight = register(new EntityFlight());
	public final EntitySpeed entitySpeed = register(new EntitySpeed());
	public final HoleESP holeESP = register(new HoleESP());
	public final ElytraFlight elytraFlight = register(new ElytraFlight());
	public final AntiAFK antiAFK = register(new AntiAFK());
	public final HackerDedector hackerDedector = register(new HackerDedector());
	public final Animations animations = register(new Animations());
	public final Disabler disabler = register(new Disabler());
	public final AutoSneak autoSneak = register(new AutoSneak());
	public final AutoMove autoWalk = register(new AutoMove());
	public final LiquidJump liquidJump = register(new LiquidJump());
	public final NoSlowDown noSlowDown = register(new NoSlowDown());
	public final ClickGuiHack clickGuiHack = register(new ClickGuiHack());
	public final AntiSpamHack antiSpamHack = register(new AntiSpamHack());
	public final AutoArmorHack autoArmorHack = register(new AutoArmorHack());
	public final AutoFarmHack autoFarmHack = register(new AutoFarmHack());
	public final AutoFishHack autoFishHack = register(new AutoFishHack());
	public final AutoSprintHack autoSprintHack = register(new AutoSprintHack());
	public final AutoSwimHack autoSwimHack = register(new AutoSwimHack());
	public final BlinkHack blinkHack = register(new BlinkHack());
	public final ChestEspHack chestEspHack = register(new ChestEspHack());
	public final FastLadderHack fastLadderHack = register(new FastLadderHack());
	public final FullbrightHack fullbrightHack = register(new FullbrightHack());
	public final GlideHack glideHack = register(new GlideHack());
	public final ItemEspHack itemEspHack = register(new ItemEspHack());
	public final MobEspHack mobEspHack = register(new MobEspHack());
	public final MobSpawnEspHack mobSpawnEspHack = register(new MobSpawnEspHack());
	public final NoWebHack noWebHack = register(new NoWebHack());
	public final PlayerEspHack playerEspHack = register(new PlayerEspHack());
	public final SpiderHack spiderHack = register(new SpiderHack());
	public final XRayHack xRayHack = register(new XRayHack());
	public final FakeHackers fakeHackers = register(new FakeHackers());
	public final AntiVoid antiVoid = register(new AntiVoid());
	public final YawLock yawLock = register(new YawLock());
	public final AntiSwing antiSwing = register(new AntiSwing());
	public final Follow follow = register(new Follow());
	public final EntityListModule entityListModule = register(new EntityListModule());
	public final Surround surround = register(new Surround());
	public final GodMode godMode = register(new GodMode());
	
	private final Path enabledHacksFile;
	private final Path settingsFile;
	private boolean disableSaving;
	
	public HackList(Path enabledHacksFile, Path settingsFile)
	{
		this.enabledHacksFile = enabledHacksFile;
		this.settingsFile = settingsFile;
	}
	
	public void loadEnabledHacks()
	{
		JsonArray json;
		try(BufferedReader reader = Files.newBufferedReader(enabledHacksFile))
		{
			json = JsonUtils.jsonParser.parse(reader).getAsJsonArray();
			
		}catch(NoSuchFileException e)
		{
			saveEnabledHacks();
			return;
			
		}catch(Exception e)
		{
			System.out
				.println("Failed to load " + enabledHacksFile.getFileName());
			e.printStackTrace();
			
			saveEnabledHacks();
			return;
		}
		
		disableSaving = true;
		for(JsonElement e : json)
		{
			if(!e.isJsonPrimitive() || !e.getAsJsonPrimitive().isString())
				continue;
			
			Hack hack = get(e.getAsString());
			if(hack == null || !hack.isStateSaved())
				continue;
			
			hack.setEnabled(true);
		}
		disableSaving = false;
		
		saveEnabledHacks();
	}
	
	public void saveEnabledHacks()
	{
		if(disableSaving)
			return;
		
		JsonArray enabledHacks = new JsonArray();
		for(Hack hack : getRegistry())
			if(hack.isEnabled() && hack.isStateSaved())
				enabledHacks.add(new JsonPrimitive(hack.getName()));
			
		try(BufferedWriter writer = Files.newBufferedWriter(enabledHacksFile))
		{
			JsonUtils.prettyGson.toJson(enabledHacks, writer);
			
		}catch(IOException e)
		{
			System.out
				.println("Failed to save " + enabledHacksFile.getFileName());
			e.printStackTrace();
		}
	}
	
	public void loadSettings()
	{
		JsonObject json;
		try(BufferedReader reader = Files.newBufferedReader(settingsFile))
		{
			json = JsonUtils.jsonParser.parse(reader).getAsJsonObject();
			
		}catch(NoSuchFileException e)
		{
			saveSettings();
			return;
			
		}catch(Exception e)
		{
			System.out.println("Failed to load " + settingsFile.getFileName());
			e.printStackTrace();
			
			saveSettings();
			return;
		}
		
		disableSaving = true;
		for(Entry<String, JsonElement> e : json.entrySet())
		{
			if(!e.getValue().isJsonObject())
				continue;
			
			Hack hack = get(e.getKey());
			if(hack == null)
				continue;
			
			Map<String, Setting> settings = hack.getSettings();
			for(Entry<String, JsonElement> e2 : e.getValue().getAsJsonObject()
				.entrySet())
			{
				String key = e2.getKey().toLowerCase();
				if(!settings.containsKey(key))
					continue;
				
				settings.get(key).fromJson(e2.getValue());
			}
		}
		disableSaving = false;
		
		saveSettings();
	}
	
	public void saveSettings()
	{
		if(disableSaving)
			return;
		
		JsonObject json = new JsonObject();
		for(Hack hack : getRegistry())
		{
			if(hack.getSettings().isEmpty())
				continue;
			
			JsonObject settings = new JsonObject();
			for(Setting setting : hack.getSettings().values())
				settings.add(setting.getName(), setting.toJson());
			
			json.add(hack.getName(), settings);
		}
		
		try(BufferedWriter writer = Files.newBufferedWriter(settingsFile))
		{
			JsonUtils.prettyGson.toJson(json, writer);
			
		}catch(IOException e)
		{
			System.out.println("Failed to save " + settingsFile.getFileName());
			e.printStackTrace();
		}
	}
}

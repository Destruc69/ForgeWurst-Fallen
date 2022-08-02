package net.wurstclient.forge;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.wurstclient.forge.compatibility.WHackList;
import net.wurstclient.forge.hacks.*;
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
	public final FastFall fastFall = register(new FastFall());
	public final Hitboxes hitboxes = register(new Hitboxes());
	public final Discord discord = register(new Discord());
	public final NoKnockBack noKnockBack = register(new NoKnockBack());
	public final Anchor anchor = register(new Anchor());
	public final Criticals criticals = register(new Criticals());
	public final AutoCrystal autoCrystal = register(new AutoCrystal());
	public final Nuker nuker = register(new Nuker());
	public final Test test = register(new Test());
	public final Cheststealer cheststealer = register(new Cheststealer());
	public final InvMove invMove = register(new InvMove());
	public final Phase phase = register(new Phase());
	public final AntiCooldown antiCooldown = register(new AntiCooldown());
	public final AntiBot antiBot = register(new AntiBot());
	public final TerrianNavv terrianNav = register(new TerrianNavv());
	public final Pointer pointer = register(new Pointer());
	public final PositionESP positionESP = register(new PositionESP());
	public final WallESP wallESP = register(new WallESP());
	public final HighwayBuilder highwayBuilder = register(new HighwayBuilder());
	public final AntiBlockLag antiBlockLag = register(new AntiBlockLag());
	public final AntiHunger antiHunger = register(new AntiHunger());
	public final Jesus jesus = register(new Jesus());
	public final HighwayNav highwayNav = register(new HighwayNav());
	public final Console console = register(new Console());
	public final AntiFog antiFog = register(new AntiFog());
	public final AntiWeather antiWeather = register(new AntiWeather());
	public final AntiKick antiKick = register(new AntiKick());
	public final FakePlayer fakePlayer = register(new FakePlayer());
	public final NoSound noSound = register(new NoSound());
	public final AutoTool autoTool = register(new AutoTool());
	public final FreeCam freeCam = register(new FreeCam());
	public final Flight flight = register(new Flight());
	public final Stability stability = register(new Stability());
	public final CPvPViewer cPvPViewer = register(new CPvPViewer());
	public final Killaura killaura = register(new Killaura());
	public final Scaffold scaffold = register(new Scaffold());
	public final Timerr timerr = register(new Timerr());
	public final LongJump longJump = register(new LongJump());
	public final BedWars bedWars = register(new BedWars());
	public final NameTags nameTags = register(new NameTags());
	public final NoHurtCam noHurtCam = register(new NoHurtCam());
	public final ReachPlus reachPlus = register(new ReachPlus());
	public final FastBreak fastBreak = register(new FastBreak());
	public final FastPlace fastPlace = register(new FastPlace());
	public final Tunneler tunneler = register(new Tunneler());
	public final PacketCanceler packetCanceler = register(new PacketCanceler());
	public final PacketSender packetSender = register(new PacketSender());
	public final EntityFlight entityFlight = register(new EntityFlight());
	public final EntitySpeed entitySpeed = register(new EntitySpeed());
	public final NCPFly packetFly = register(new NCPFly());
	public final ElytraFlight elytraFlight = register(new ElytraFlight());
	public final AntiAFK antiAFK = register(new AntiAFK());
	public final HackerDedector hackerDedector = register(new HackerDedector());
	public final Animations animations = register(new Animations());
	public final SkywarsCamper skywarsCamper = register(new SkywarsCamper());
	public final Disabler disabler = register(new Disabler());
	public final AutoSneak autoSneak = register(new AutoSneak());
	public final AutoWalk autoWalk = register(new AutoWalk());
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
	public final MobSpawnEspHack mobSpawnEspHack =
			register(new MobSpawnEspHack());
	public final NoWebHack noWebHack = register(new NoWebHack());
	public final PlayerEspHack playerEspHack = register(new PlayerEspHack());
	public final SpiderHack spiderHack = register(new SpiderHack());
	public final XRayHack xRayHack = register(new XRayHack());
	public final FakeHackers fakeHackers = register(new FakeHackers());
	public final AntiVoid antiVoid = register(new AntiVoid());
	public final YawLock yawLock = register(new YawLock());
	public final AntiSwing antiSwing = register(new AntiSwing());
	public final Follow follow = register(new Follow());
	
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

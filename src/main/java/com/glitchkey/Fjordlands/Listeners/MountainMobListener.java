/*
 * Copyright (c) 2013-2016 Sean Porter <glitchkey@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.glitchkey.fjordlands.listeners;

//* IMPORTS: JDK/JRE
	import java.util.ArrayList;
	import java.util.Arrays;
	import java.util.List;
	import java.util.Random;
//* IMPORTS: BUKKIT
	import org.bukkit.Bukkit;
	import org.bukkit.DyeColor;
	import org.bukkit.enchantments.Enchantment;
	import org.bukkit.entity.Ageable;
	import org.bukkit.entity.EntityType;
	import org.bukkit.entity.LivingEntity;
	import org.bukkit.entity.Sheep;
	import org.bukkit.entity.Skeleton;
	import org.bukkit.entity.Skeleton.SkeletonType;
	import org.bukkit.entity.Zombie;
	import org.bukkit.event.Listener;
	import org.bukkit.event.entity.CreatureSpawnEvent;
	import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
	import org.bukkit.event.EventHandler;
	import org.bukkit.event.EventPriority;
	import org.bukkit.event.HandlerList;
	import org.bukkit.inventory.EntityEquipment;
	import org.bukkit.inventory.ItemStack;
	import org.bukkit.inventory.meta.ItemMeta;
	import org.bukkit.Location;
	import org.bukkit.Material;
	import org.bukkit.material.Tree;
	import org.bukkit.TreeSpecies;
//* IMPORTS: PANDORA
	//* NOT NEEDED
//* IMPORTS: FJORDLANDS
	import com.glitchkey.fjordlands.datatypes.SpawnedMob;
	import com.glitchkey.fjordlands.FjordlandsPlugin;
//* IMPORTS: OTHER
	//* NOT NEEDED

public final class MountainMobListener implements Listener {
	private final FjordlandsPlugin plugin;
	private List<SpawnedMob> mobs;
	private List<Enchantment> helmet;
	private List<Enchantment> chestplate;
	private List<Enchantment> leggings;
	private List<Enchantment> boots;
	private List<Enchantment> tool;
	private List<Enchantment> sword;
	private List<Enchantment> bow;
	private List<String> firstNames;
	private List<String> sFirstNames;
	private List<String> lastNames;
	private Random rand = new Random();

	public MountainMobListener(final FjordlandsPlugin plugin) {
		this.plugin = plugin;
		this.helmet = new ArrayList<Enchantment>();
		this.helmet.add(Enchantment.PROTECTION_ENVIRONMENTAL);
		this.helmet.add(Enchantment.PROTECTION_FIRE);
		this.helmet.add(Enchantment.PROTECTION_EXPLOSIONS);
		this.helmet.add(Enchantment.PROTECTION_PROJECTILE);
		this.helmet.add(Enchantment.OXYGEN);
		this.helmet.add(Enchantment.WATER_WORKER);
		this.helmet.add(Enchantment.THORNS);
		this.helmet.add(Enchantment.DURABILITY);
		this.chestplate = new ArrayList<Enchantment>();
		this.chestplate.add(Enchantment.PROTECTION_ENVIRONMENTAL);
		this.chestplate.add(Enchantment.PROTECTION_FIRE);
		this.chestplate.add(Enchantment.PROTECTION_EXPLOSIONS);
		this.chestplate.add(Enchantment.PROTECTION_PROJECTILE);
		this.chestplate.add(Enchantment.THORNS);
		this.chestplate.add(Enchantment.DURABILITY);
		this.leggings = new ArrayList<Enchantment>();
		this.leggings.add(Enchantment.PROTECTION_ENVIRONMENTAL);
		this.leggings.add(Enchantment.PROTECTION_FIRE);
		this.leggings.add(Enchantment.PROTECTION_EXPLOSIONS);
		this.leggings.add(Enchantment.PROTECTION_PROJECTILE);
		this.leggings.add(Enchantment.THORNS);
		this.leggings.add(Enchantment.DURABILITY);
		this.boots = new ArrayList<Enchantment>();
		this.boots.add(Enchantment.PROTECTION_ENVIRONMENTAL);
		this.boots.add(Enchantment.PROTECTION_FIRE);
		this.boots.add(Enchantment.PROTECTION_FALL);
		this.boots.add(Enchantment.PROTECTION_EXPLOSIONS);
		this.boots.add(Enchantment.PROTECTION_PROJECTILE);
		this.boots.add(Enchantment.THORNS);
		this.boots.add(Enchantment.DURABILITY);
		this.tool = new ArrayList<Enchantment>();
		this.tool.add(Enchantment.DIG_SPEED);
		this.tool.add(Enchantment.SILK_TOUCH);
		this.tool.add(Enchantment.LOOT_BONUS_BLOCKS);
		this.tool.add(Enchantment.DURABILITY);
		this.sword = new ArrayList<Enchantment>();
		this.sword.add(Enchantment.DAMAGE_ALL);
		this.sword.add(Enchantment.DAMAGE_UNDEAD);
		this.sword.add(Enchantment.DAMAGE_ARTHROPODS);
		this.sword.add(Enchantment.KNOCKBACK);
		this.sword.add(Enchantment.FIRE_ASPECT);
		this.sword.add(Enchantment.LOOT_BONUS_MOBS);
		this.sword.add(Enchantment.DURABILITY);
		this.bow = new ArrayList<Enchantment>();
		this.bow.add(Enchantment.ARROW_DAMAGE);
		this.bow.add(Enchantment.ARROW_KNOCKBACK);
		this.bow.add(Enchantment.ARROW_FIRE);
		this.bow.add(Enchantment.ARROW_INFINITE);
		this.bow.add(Enchantment.DURABILITY);
		String[] nameArray = {"Abraham", "Abram", "Adam", "Adolf",
			"Adolv", "Agnar", "Aksel", "Albert", "Albin",
			"Albrecht", "Albreckt", "Albrekt", "Alexander",
			"Alfred", "Alf", "Alvar", "Amund", "Anthon", "Anton",
			"Armann", "Arne", "Arthur", "Axel", "Baard", "Bendt",
			"Bengt", "Bent", "Bernhard", "Bjarne", "Bjorn", "Bodil",
			"Carl", "Carsten", "Christen", "Christian",
			"Christoffer", "Daniel", "David", "Einar", "Elmer",
			"Erick", "Eric", "Erik", "Frederik", "Gerhard", "Goran",
			"Gulbrand", "Gunnar", "Gunvald", "Gustaf", "Haakon",
			"Hakon", "Harald", "Heimir", "Henning", "Henrik",
			"Herbert", "Herman", "Hilmar", "Holger", "Ib", "Inge",
			"Inger", "Jacob", "Jakob", "Jan", "Jesper", "Johan",
			"John", "Jokum", "Jon", "Jorgen", "Karl", "Karsten",
			"Kasper", "Kjeld", "Kjell", "Knud", "Knut", "Kristen",
			"Kristian", "Kristoffer", "Leif", "Leonard", "Ludvig",
			"Martin", "Matt", "Mikael", "Mikkel", "Morten", "Niel",
			"Nikolaj", "Norman", "Ole", "Oliver", "Olof", "Otto",
			"Ove", "Palle", "Patrik", "Peder", "Per", "Peter",
			"Petter", "Poul", "Preben", "Ragnvald", "Robert",
			"Rupert", "Sigurd", "Simon", "Sina", "Soren", "Steen",
			"Stefan", "Steffen", "Stein", "Stig", "Sven", "Svend", 
			"Ulf", "Verner", "Victor", "Walter"};
		this.firstNames = new ArrayList(Arrays.asList(nameArray));
		String[] nameArray2 = {"Ander", "Andrea", "Clae", "Clau",
			"Clemen", "Han", "Jen", "Jona", "Jon", "Kla", "Klau",
			"Lar", "Laurit", "Mad", "Magnu", "Marku", "Mathia",
			"Matthia", "Matt", "Nikla", "Nil", "Rasmu", "Toma",
			"Troel"};
		this.sFirstNames = new ArrayList(Arrays.asList(nameArray2));
		String[] nameArray3 = {"Abel", "Ahlberg", "Ahlgren", "Ahlstrom",
			"Akerman", "Almstedt", "Arud", "Bager", "Bengtsdotter",
			"Berg", "Bergfalk", "Bergman", "Bergstrom", "Bjork",
			"Bjorkman", "Blom", "Blomgren", "Borg", "Byquist",
			"Bystrom", "Dahl", "Dalgaard", "Dam", "Doctor", "Ek",
			"Eklund", "Eld", "Engberg", "Engman", "Engstrom",
			"Falk", "Fisker", "Frank", "Frisk", "Giese", "Grahn",
			"Hagebak", "Hall", "Hallman", "Haugen", "Hjort",
			"Holmstrom", "Holst", "Holt", "Horn", "Hult", "Hummel",
			"Kron", "Lager", "Landvik", "Lang", "Lange",
			"Langenberg", "Lindberg", "Lindgren", "Lindholm",
			"Lindquist", "Lindstrom", "Ljung", "Ljungborg",
			"Ljunggren", "Ljungman", "Ljungstrand", "Lofgren",
			"Losnedahl", "Lund", "Lundgren", "Lundquist", "Lykke",
			"Mardh", "Moller", "Munson", "Naess", "Nass", "Ness",
			"Niequist", "Nordskov", "Norling", "Norup", "Nylund",
			"Nystrom", "Olander", "Olhouser", "Olofsdotter",
			"Olsen", "Olsson", "Olvirsson", "Oman", "Omdahl",
			"Ostberg", "Oster", "Ostergaard", "Ostergard",
			"Pilkvist", "Randrup", "Rapp", "Rask", "Raske",
			"REENBERG", "Riber", "Rolvsson", "Rundstrom", "Salomon",
			"Skjeggestad", "Skovgaard", "Solberg", "Spillum",
			"Stenberg", "Stendahl", "Stenger", "Storstrand",
			"Strand", "Sunden", "Swenhaugen", "Tennfjord",
			"Thorirsson", "Thorn", "Thorsen", "Tjader", "Toov",
			"Vang", "Vilhjalmsson", "Vinter", "Voll", "Vollan",
			"Wang", "Westerberg", "Winter", "Wolf", "Wolff"};
		this.lastNames = new ArrayList(Arrays.asList(nameArray3));
		mobs = new ArrayList<SpawnedMob>();

		registerEvents();
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onCreatureSpawn(final CreatureSpawnEvent event) {
		LivingEntity entity = event.getEntity();

		if (entity.getWorld() != plugin.world)
			return;

		switch (event.getSpawnReason()) {
			case CHUNK_GEN:
			case DEFAULT:
			case NATURAL:
				event.setCancelled(true);
				break;

			case VILLAGE_INVASION:
				entity.setCustomName("Devourer");
				entity.setCustomNameVisible(false);
				entity.setRemoveWhenFarAway(true);
				entity.setMaxHealth(35);
				entity.setHealth(35);
				return;
			default:
				return;
		}

		switch (entity.getType()) {
			case SQUID:
			case BAT:
				event.setCancelled(false);
				return;
			case BLAZE:
			case CAVE_SPIDER:
			case CREEPER:
			case ENDER_DRAGON:
			case ENDERMAN:
			case GHAST:
			case GIANT:
			case GUARDIAN:
			case MAGMA_CUBE:
			case PIG_ZOMBIE:
			case SHULKER:
			case SILVERFISH:
			case SKELETON:
			case SLIME:
			case SPIDER:
			case WITCH:
			case WITHER:
			case ZOMBIE:
				spawnHostiles(entity.getLocation());
				break;
			case CHICKEN:
			case COW:
			case HORSE:
			case IRON_GOLEM:
			case MUSHROOM_COW:
			case OCELOT:
			case PIG:
			case POLAR_BEAR:
			case RABBIT:
			case SHEEP:
			case VILLAGER:
			case WOLF:
				spawnPassives(entity.getLocation());
			default:
				return;
		}

		spawnMobs();
	}

	private void spawnMobs() {
		List<SpawnedMob> mobs = getMobs();

		for (SpawnedMob mob : mobs) {
			Location loc = new Location(plugin.world, mob.x, mob.y, mob.z);
			loc.add(0.5D, 0D, 0.5D);
			EntityType type = mob.type;
			LivingEntity entity = (LivingEntity) plugin.world.spawnEntity(loc, type);

			if (entity == null)
				continue;

			entity.setCustomName(mob.name);
			entity.setCustomNameVisible(mob.isNameShown);
			entity.setRemoveWhenFarAway(mob.canDespawn);
			entity.setMaxHealth(mob.maxHealth);
			entity.setHealth(mob.maxHealth);

			switch (type) {
				case SHEEP:
					((Sheep) entity).setColor(mob.color);
				case WOLF:
					if (mob.isBaby) ((Ageable) entity).setBaby();
			}

			if (type == EntityType.ZOMBIE) {
				((Zombie) entity).setVillager(mob.isVillager);
				((Zombie) entity).setBaby(mob.isVillager);
				equipSpawnedMob(entity, mob);
			}
			else if (type == EntityType.SKELETON) {
				if (mob.isWither) {
					((Skeleton) entity).setSkeletonType(
					SkeletonType.WITHER);
				}
				equipSpawnedMob(entity, mob);
			}
		}
	}

	private void equipSpawnedMob(LivingEntity entity, SpawnedMob mob) {
		EntityEquipment inv = entity.getEquipment();
		inv.setHelmet(mob.helmet);
		inv.setChestplate(mob.chestplate);
		inv.setLeggings(mob.leggings);
		inv.setBoots(mob.boots);
		inv.setItemInHand(mob.held);
		inv.setHelmetDropChance(0F);
		inv.setChestplateDropChance(0F);
		inv.setLeggingsDropChance(0F);
		inv.setBootsDropChance(0F);
		inv.setItemInHandDropChance(0F);
	}

	public List<SpawnedMob> getMobs() {
		List<SpawnedMob> result = new ArrayList<SpawnedMob>(mobs);
		mobs.clear();
		return result;
	}

	private void spawnHostiles(Location loc) {
		EntityType mobType;
		int dice = rand.nextInt(100);

		if (dice < 40) {
			spawnPassives(loc);
			return;
		}

		dice = rand.nextInt(10);

		if (dice < 5)
			mobType = EntityType.SKELETON;
		else
			mobType = EntityType.ZOMBIE;

		SpawnedMob mob = new SpawnedMob();
		mob.type = mobType;
		mob.name = getName();
		mob.isNameShown = false;
		mob.canDespawn = true;
		mob.maxHealth = 40;
		mob.x = loc.getBlockX();
		mob.y = loc.getBlockY();
		mob.z = loc.getBlockZ();

		if (mobType == EntityType.SKELETON) {
			if (rand.nextInt(10) < 2)
				mob.isWither = true;
		}
		else if (mobType == EntityType.ZOMBIE) {
			if (rand.nextInt(10) < 2)
				mob.isBaby = true;
			if (rand.nextInt(10) == 0)
				mob.isVillager = true;
		}
		equipMob(mob, mob.name);
		mobs.add(mob);
	}

	private void spawnPassives(Location loc) {

		EntityType mobType = EntityType.SHEEP;
		int dice = rand.nextInt(10);

		if (dice < 2)
			mobType = EntityType.WOLF;

		SpawnedMob mob = new SpawnedMob();
		mob.type = mobType;
		mob.isNameShown = false;
		mob.canDespawn = false;
		mob.maxHealth = 32;
		mob.x = loc.getBlockX();
		mob.y = loc.getBlockY();
		mob.z = loc.getBlockZ();

		if (mobType == EntityType.SHEEP) {
			mob.name = "Grey Troender";

			dice = rand.nextInt(100);

			if (dice < 30)
				mob.color = DyeColor.WHITE;
			else if (dice < 90)
				mob.color = DyeColor.SILVER;
			else if (dice < 97)
				mob.color = DyeColor.GRAY;
			else
				mob.color = DyeColor.BLACK;

			if (rand.nextInt(10) < 2)
				mob.isBaby = true;
		}
		else {
			mob.name = "Winter Wolf";

			if (rand.nextInt(10) < 2)
				mob.isBaby = true;
		}
		mobs.add(mob);
	}

	private void equipMob(SpawnedMob mob, String name) {
		mob.helmet = getHelmet(name);
		mob.chestplate = getChestplate(name);
		mob.leggings = getLeggings(name);
		mob.boots = getBoots(name);
		mob.held = getHeldItem(name);
	}

	private ItemStack getHelmet(String name) {
		int dice = rand.nextInt(100);

		if (dice < 90)
			return null;
		else if (dice < 93)
			return getHelmetBlock();

		dice = rand.nextInt(100);
		ItemStack result = null;

		if (dice < 70)
			result = new ItemStack(Material.LEATHER_HELMET, 1);
		else if (dice < 85)
			result = new ItemStack(Material.GOLD_HELMET, 1);
		else if (dice < 93)
			result = new ItemStack(Material.IRON_HELMET, 1);
		else if (dice < 97)
			result = new ItemStack(Material.DIAMOND_HELMET, 1);
		else
			result = new ItemStack(Material.CHAINMAIL_HELMET, 1);

		result.setDurability((short) (rand.nextInt(
			result.getDurability() + 1)));
		dice = rand.nextInt(100);

		if (dice < 85)
			return nameGear(result, name, "Helmet");

		for (Enchantment ench: helmet) {
			if (rand.nextInt(3) != 0)
				continue;

			int level = getEnchantLevel(ench.getMaxLevel());
			result.addUnsafeEnchantment(ench, level);
		}

		return nameGear(result, name, "Helmet");
	}

	private ItemStack getHelmetBlock() {
		int dice = rand.nextInt(100);

		if (dice < 25)
			return (new ItemStack(Material.GLASS, 1));
		else if (dice < 95)
			return (new ItemStack(Material.ICE, 1));
		else
			return (new ItemStack(Material.PUMPKIN, 1));
	}

	private ItemStack getChestplate(String name) {
		int dice = rand.nextInt(100);

		if (dice < 90)
			return null;

		dice = rand.nextInt(100);
		ItemStack result = null;
		String type = "Chestplate";

		if (dice < 70)
			result = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
		else if (dice < 85)
			result = new ItemStack(Material.GOLD_CHESTPLATE, 1);
		else if (dice < 93)
			result = new ItemStack(Material.IRON_CHESTPLATE, 1);
		else if (dice < 97)
			result = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
		else {
			result = new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1);
			type = "Chainmail";
		}

		result.setDurability((short) (rand.nextInt(
			result.getDurability() + 1)));
		dice = rand.nextInt(100);

		if (dice < 85)
			return nameGear(result, name, type);

		for (Enchantment ench: chestplate) {
			if (rand.nextInt(3) != 0)
				continue;

			int level = getEnchantLevel(ench.getMaxLevel());
			result.addUnsafeEnchantment(ench, level);
		}

		return nameGear(result, name, type);
	}

	private ItemStack getLeggings(String name) {
		int dice = rand.nextInt(100);

		if (dice < 90)
			return null;

		dice = rand.nextInt(100);
		ItemStack result = null;

		if (dice < 70)
			result = new ItemStack(Material.LEATHER_LEGGINGS, 1);
		else if (dice < 85)
			result = new ItemStack(Material.GOLD_LEGGINGS, 1);
		else if (dice < 93)
			result = new ItemStack(Material.IRON_LEGGINGS, 1);
		else if (dice < 97)
			result = new ItemStack(Material.DIAMOND_LEGGINGS, 1);
		else
			result = new ItemStack(Material.CHAINMAIL_LEGGINGS, 1);

		result.setDurability((short) (rand.nextInt(
			result.getDurability() + 1)));
		dice = rand.nextInt(100);

		if (dice < 85)
			return nameGear(result, name, "Leggings");

		for (Enchantment ench: leggings) {
			if (rand.nextInt(3) != 0)
				continue;

			int level = getEnchantLevel(ench.getMaxLevel());
			result.addUnsafeEnchantment(ench, level);
		}

		return nameGear(result, name, "Leggings");
	}

	private ItemStack getBoots(String name) {
		int dice = rand.nextInt(100);

		if (dice < 90)
			return null;

		dice = rand.nextInt(100);
		ItemStack result = null;

		if (dice < 70)
			result = new ItemStack(Material.LEATHER_BOOTS, 1);
		else if (dice < 85)
			result = new ItemStack(Material.GOLD_BOOTS, 1);
		else if (dice < 93)
			result = new ItemStack(Material.IRON_BOOTS, 1);
		else if (dice < 97)
			result = new ItemStack(Material.DIAMOND_BOOTS, 1);
		else
			result = new ItemStack(Material.CHAINMAIL_BOOTS, 1);

		result.setDurability((short) (rand.nextInt(
			result.getDurability() + 1)));
		dice = rand.nextInt(100);

		if (dice < 85)
			return nameGear(result, name, "Boots");

		for (Enchantment ench: boots) {
			if (rand.nextInt(3) != 0)
				continue;

			int level = getEnchantLevel(ench.getMaxLevel());
			result.addUnsafeEnchantment(ench, level);
		}

		return nameGear(result, name, "Boots");
	}

	private ItemStack getHeldItem(String name) {
		int dice = rand.nextInt(3);

		if (dice < 1)
			return getMeleeWeapon(name);
		else if (dice < 2) 
			return getBow(name);
		else
			return getTool(name);
	}

	private ItemStack getMeleeWeapon(String name) {
		int dice = rand.nextInt(100);

		if (dice < 90)
			return null;

		dice = rand.nextInt(100);
		ItemStack result = null;
		String type = "Sword";

		if (dice < 30)
			result = new ItemStack(Material.WOOD_SWORD, 1);
		else if (dice < 60) {
			result = new ItemStack(Material.WOOD_AXE, 1);
			type = "Axe";
		} else if (dice < 68)
			result = new ItemStack(Material.STONE_SWORD, 1);
		else if (dice < 76) {
			result = new ItemStack(Material.STONE_AXE, 1);
			type = "Axe";
		} 
		else if (dice < 82)
			result = new ItemStack(Material.GOLD_SWORD, 1);
		else if (dice < 88) {
			result = new ItemStack(Material.GOLD_AXE, 1);
			type = "Axe";
		} 
		else if (dice < 92)
			result = new ItemStack(Material.IRON_SWORD, 1);
		else if (dice < 96) {
			result = new ItemStack(Material.IRON_AXE, 1);
			type = "Axe";
		} 
		else if (dice < 98)
			result = new ItemStack(Material.DIAMOND_SWORD, 1);
		else {
			result = new ItemStack(Material.DIAMOND_AXE, 1);
			type = "Axe";
		} 

		result.setDurability((short) (rand.nextInt(
			result.getDurability() + 1)));
		dice = rand.nextInt(100);

		if (dice < 85)
			return nameGear(result, name, type);

		for (Enchantment ench: sword) {
			if (rand.nextInt(3) != 0)
				continue;

			int level = getEnchantLevel(ench.getMaxLevel());
			result.addUnsafeEnchantment(ench, level);
		}

		return nameGear(result, name, type);
	}

	private ItemStack getBow(String name) {
		int dice = rand.nextInt(100);

		if (dice < 90)
			return null;

		ItemStack result = new ItemStack(Material.BOW, 1);

		result.setDurability((short) (rand.nextInt(
			result.getDurability() + 1)));
		dice = rand.nextInt(100);

		if (dice < 85)
			return nameGear(result, name, "Bow");

		for (Enchantment ench: bow) {
			if (rand.nextInt(3) != 0)
				continue;

			int level = getEnchantLevel(ench.getMaxLevel());
			result.addUnsafeEnchantment(ench, level);
		}

		return nameGear(result, name, "Bow");
	}

	private ItemStack getTool(String name) {
		int dice = rand.nextInt(5);

		if (dice < 1)
			return getShovel(name);
		else if (dice < 2) 
			return getPickaxe(name);
		else if (dice < 3)
			return getAxe(name);
		else if (dice < 4)
			return getHoe(name);
		else
			return getOtherTool(name);
	}

	private ItemStack getShovel(String name) {
		int dice = rand.nextInt(100);

		if (dice < 90)
			return null;

		dice = rand.nextInt(100);
		ItemStack result = null;

		if (dice < 60)
			result = new ItemStack(Material.WOOD_SPADE, 1);
		else if (dice < 76) 
			result = new ItemStack(Material.STONE_SPADE, 1);
		else if (dice < 88)
			result = new ItemStack(Material.GOLD_SPADE, 1);
		else if (dice < 96)
			result = new ItemStack(Material.IRON_SPADE, 1);
		else
			result = new ItemStack(Material.DIAMOND_SPADE, 1);

		result.setDurability((short) (rand.nextInt(
			result.getDurability() + 1)));
		dice = rand.nextInt(100);

		if (dice < 85)
			return nameGear(result, name, "Shovel");

		boolean silk = false;

		for (Enchantment ench: tool) {
			if (rand.nextInt(3) != 0)
				continue;
			if (silk && ench == Enchantment.LOOT_BONUS_BLOCKS)
				continue;

			int level = getEnchantLevel(ench.getMaxLevel());
			result.addUnsafeEnchantment(ench, level);

			if (ench == Enchantment.SILK_TOUCH)
				silk = true;
		}

		return nameGear(result, name, "Shovel");
	}

	private ItemStack getPickaxe(String name) {
		int dice = rand.nextInt(100);

		if (dice < 90)
			return null;

		dice = rand.nextInt(100);
		ItemStack result = null;

		if (dice < 60)
			result = new ItemStack(Material.WOOD_PICKAXE, 1);
		else if (dice < 76) 
			result = new ItemStack(Material.STONE_PICKAXE, 1);
		else if (dice < 88)
			result = new ItemStack(Material.GOLD_PICKAXE, 1);
		else if (dice < 96)
			result = new ItemStack(Material.IRON_PICKAXE, 1);
		else
			result = new ItemStack(Material.DIAMOND_PICKAXE, 1);

		result.setDurability((short) (rand.nextInt(
			result.getDurability() + 1)));
		dice = rand.nextInt(100);

		if (dice < 85)
			return nameGear(result, name, "Pickaxe");

		boolean silk = false;

		for (Enchantment ench: tool) {
			if (rand.nextInt(3) != 0)
				continue;
			if (silk && ench == Enchantment.LOOT_BONUS_BLOCKS)
				continue;

			int level = getEnchantLevel(ench.getMaxLevel());
			result.addUnsafeEnchantment(ench, level);

			if (ench == Enchantment.SILK_TOUCH)
				silk = true;
		}

		return nameGear(result, name, "Pickaxe");
	}

	private ItemStack getAxe(String name) {
		int dice = rand.nextInt(100);

		if (dice < 90)
			return null;

		dice = rand.nextInt(100);
		ItemStack result = null;

		if (dice < 60)
			result = new ItemStack(Material.WOOD_AXE, 1);
		else if (dice < 76) 
			result = new ItemStack(Material.STONE_AXE, 1);
		else if (dice < 88)
			result = new ItemStack(Material.GOLD_AXE, 1);
		else if (dice < 96)
			result = new ItemStack(Material.IRON_AXE, 1);
		else
			result = new ItemStack(Material.DIAMOND_AXE, 1);

		result.setDurability((short) (rand.nextInt(
			result.getDurability() + 1)));
		dice = rand.nextInt(100);

		if (dice < 85)
			return nameGear(result, name, "Axe");

		boolean silk = false;

		for (Enchantment ench: tool) {
			if (rand.nextInt(3) != 0)
				continue;
			if (silk && ench == Enchantment.LOOT_BONUS_BLOCKS)
				continue;

			int level = getEnchantLevel(ench.getMaxLevel());
			result.addUnsafeEnchantment(ench, level);

			if (ench == Enchantment.SILK_TOUCH)
				silk = true;
		}

		return nameGear(result, name, "Axe");
	}

	private ItemStack getHoe(String name) {
		int dice = rand.nextInt(100);

		if (dice < 90)
			return null;

		dice = rand.nextInt(100);
		ItemStack result = null;

		if (dice < 60)
			result = new ItemStack(Material.WOOD_HOE, 1);
		else if (dice < 76) 
			result = new ItemStack(Material.STONE_HOE, 1);
		else if (dice < 88)
			result = new ItemStack(Material.GOLD_HOE, 1);
		else if (dice < 96)
			result = new ItemStack(Material.IRON_HOE, 1);
		else
			result = new ItemStack(Material.DIAMOND_HOE, 1);

		result.setDurability((short) (rand.nextInt(
			result.getDurability() + 1)));
		dice = rand.nextInt(100);

		return nameGear(result, name, "Hoe");
	}

	private ItemStack getOtherTool(String name) {
		int dice = rand.nextInt(5);

		if (dice == 0)
			return null;

		dice = rand.nextInt(11);
		ItemStack result = null;
		String type = "";

		if (dice < 1) {
			result = new ItemStack(Material.FLINT_AND_STEEL, 1);
			type = "Firestarter";
		} else if (dice < 2) {
			result = new ItemStack(Material.STICK, 1);
			type = "Stick";
		} else if (dice < 3) {
			result = new ItemStack(Material.ARROW, 1);
			type = "Arrow";
		} else if (dice < 4) {
			result = new ItemStack(Material.BOWL, 1);
			type = "Bowl";
		} else if (dice < 5) {
			result = new ItemStack(Material.SHEARS, 1);
			type = "Shears";
		} else if (dice < 6) {
			result = new ItemStack(Material.GLASS_BOTTLE, 1);
			type = "Bottle";
		} else if (dice < 7) {
			result = (new Tree(TreeSpecies.GENERIC)).toItemStack(1);
			result.setType(Material.SAPLING);
			type = "Family Tree";
		} else if (dice < 8) {
			result = (new Tree(TreeSpecies.REDWOOD)).toItemStack(1);
			result.setType(Material.SAPLING);
			type = "Family Tree";
		} else if (dice < 9) {
			result = (new Tree(TreeSpecies.BIRCH)).toItemStack(1);
			result.setType(Material.SAPLING);
			type = "Family Tree";
		} else if (dice < 10) {
			result = (new Tree(TreeSpecies.JUNGLE)).toItemStack(1);
			result.setType(Material.SAPLING);
			type = "Family Tree";
		} else {
			result = new ItemStack(Material.FISHING_ROD, 1);
			type = "Rod";
		}

		return nameGear(result, name, type);
	}

	private int getEnchantLevel(int maximum) {
		for (int i = 1; i < maximum; i++) {
			if (rand.nextInt(100) < 70)
				return i;
		}

		return maximum;
	}

	private ItemStack nameGear(ItemStack item, String name, String type)
	{
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name + "'s " + type);
		item.setItemMeta(meta);
		return item;
	}

	private String getName() {
		String result = "";

		if (rand.nextInt(5) == 0) {
			result += sFirstNames.get(rand.nextInt(
				sFirstNames.size())) + "s ";
		}
		else {
			result += firstNames.get(rand.nextInt(
				firstNames.size())) + " ";
		}

		if (rand.nextInt(10) < 7) {
			result += firstNames.get(rand.nextInt(
				firstNames.size()));

			if (rand.nextInt(5) == 0)
				result += "sen";
			else if (rand.nextInt(3) == 0)
				result += "sson";
			else
				result += "son";
		}
		else if (rand.nextInt(5) == 0) {
			result += sFirstNames.get(rand.nextInt(
				sFirstNames.size()));

			if (rand.nextInt(5) == 0)
				result += "sen";
			else if (rand.nextInt(3) == 0)
				result += "sson";
			else
				result += "son";
		}
		else {
			result += lastNames.get(rand.nextInt(lastNames.size()));
		}

		return result;
	}

	public void registerEvents() {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public void unregisterEvents() {
		HandlerList.unregisterAll(this);
	}
}
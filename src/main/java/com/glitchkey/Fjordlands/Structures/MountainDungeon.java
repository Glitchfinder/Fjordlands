/*
 * Copyright (c) 2012-2016 Sean Porter <glitchkey@gmail.com>
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

package com.glitchkey.fjordlands.structures;

//* IMPORTS: JDK/JRE
	import java.util.Random;
//* IMPORTS: BUKKIT
	import org.bukkit.block.Block;
	import org.bukkit.block.BlockState;
	import org.bukkit.block.Chest;
	import org.bukkit.block.CreatureSpawner;
	import org.bukkit.DyeColor;
	import org.bukkit.entity.EntityType;
	import org.bukkit.inventory.Inventory;
	import org.bukkit.inventory.ItemStack;
	import org.bukkit.Location;
	import org.bukkit.Material;
	import org.bukkit.material.Dye;
	import org.bukkit.material.MaterialData;
	import org.bukkit.material.Sapling;
	import org.bukkit.plugin.Plugin;
	import org.bukkit.TreeSpecies;
	import org.bukkit.World;
//* IMPORTS: PANDORA
	import org.pandora.PandoraWorldGenerator;
//* IMPORTS: FJORDLANDS
    //* NOT NEEDED
//* IMPORTS: OTHER
	//* NOT NEEDED

public class MountainDungeon extends PandoraWorldGenerator
{
	public MountainDungeon(Plugin plugin, boolean notifyOnBlockChanges) {
		super(plugin, notifyOnBlockChanges, false);

		for (int data = 0; data < 16; data++) {
			addToBlacklist(22, (byte) data);
			addToBlacklist(44, (byte) data);
			addToBlacklist(52, (byte) data);
			addToBlacklist(54, (byte) data);
			addToBlacklist(98, (byte) data);
			addToBlacklist(155, (byte) data);
			addToBlacklist(156, (byte) data);
		}

		
	}

	public boolean generate(World w, Random r, int x, int y,
		int z)
	{
		int chunkX = x >> 4;
		int chunkZ = z >> 4;
		int baseX = chunkX << 4;
		int baseZ = chunkZ << 4;
		Location start = new Location(w, x, y, z);

		int baseY = getBaseY(w, x, z, false) + 11;

		if (baseY <= 10)
			return false;

		int bottomY = baseY;

		for(int cx = baseX; cx < (baseX + 16); cx++) {
			for(int cz = baseZ; cz < (baseZ + 16); cz++) {
				int checkY = getBaseY(w, cx, cz, true);
				bottomY = Math.min(checkY, bottomY);

				if (bottomY < 0)
					return false;
			}
		}

		generateBaseExterior(w, start, baseX, baseY, baseZ, bottomY);
		generateBaseInterior(w, start, r, baseX, baseY, baseZ, bottomY);
		generateArches(w, start, baseX, baseY + 1, baseZ);
		generateCorners(w, start, baseX, baseY, baseZ);
		generateCap(w, start, baseX, baseY, baseZ);
		generateAir(w, start, baseX, baseY + 1, baseZ);
		generatePlatform(w, start, baseX, baseY + 1, baseZ);

		Location l1 = new Location(w, baseX + 7, baseY + 2, z + 7);
		Location l2 = new Location(w, baseX + 7, baseY + 2, z + 8);
		Location l3 = new Location(w, baseX + 8, baseY + 2, z + 8);
		Location l4 = new Location(w, baseX + 8, baseY + 2, z + 7);

		addBlock(start, l1, 52, 0);
		addBlock(start, l2, 52, 0);
		addBlock(start, l3, 52, 0);
		addBlock(start, l4, 52, 0);

		boolean success = placeBlocks(start, true);

		if (success) {
			setSpawner(l1, r);
			setSpawner(l2, r);
			setSpawner(l3, r);
			setSpawner(l4, r);
			l1 = new Location(w, baseX + 6, baseY + 1, z + 7);
			l2 = new Location(w, baseX + 7, baseY + 1, z + 9);
			l3 = new Location(w, baseX + 8, baseY + 1, z + 6);
			l4 = new Location(w, baseX + 9, baseY + 1, z + 8);
			setLoot(l1, r);
			setLoot(l2, r);
			setLoot(l3, r);
			setLoot(l4, r);
		}

		return success;
	}

	private void setSpawner(Location location, Random r) {
		EntityType type = EntityType.SHEEP;

		if (r.nextInt(10) < 8) {
			int rand = r.nextInt(100);

			if (rand < 28)
				type = EntityType.PIG;
			else if (rand < 56)
				type = EntityType.COW;
			else if (rand < 84)
				type = EntityType.CHICKEN;
			else if (rand < 94)
				type = EntityType.OCELOT;
			else
				type = EntityType.MUSHROOM_COW;
		}
		else {
			int rand = r.nextInt(100);

			if (rand < 28)
				type = EntityType.CAVE_SPIDER;
			else if (rand < 56)
				type = EntityType.SPIDER;
			else if (rand < 84)
				type = EntityType.ENDERMAN;
			else if (rand < 94)
				type = EntityType.SLIME;
			else
				type = EntityType.CREEPER;
		}

		BlockState b = location.getBlock().getState();
		((CreatureSpawner) b).setSpawnedType(type);
	}

	private void setLoot(Location location, Random r) {
		Chest c = (Chest) location.getBlock().getState();

		addTreasure(c.getBlockInventory(), r);
		addPlants(c.getBlockInventory(), r);
	}

	private void addTreasure(Inventory i, Random r) {
		ItemStack item = new ItemStack(Material.AIR);

		if (r.nextInt(100) < 11) {
			item = new ItemStack(Material.SPONGE);
			item.setAmount(r.nextInt(11) + 2);
			i.addItem(item);
		}
		if (r.nextInt(100) < 3) {
			item = new ItemStack(Material.LAPIS_BLOCK);
			item.setAmount(r.nextInt(5) + 1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 33) {
			item = new ItemStack(Material.WEB);
			item.setAmount(r.nextInt(12) + 3);
			i.addItem(item);
		}
		if (r.nextInt(100) < 3) {
			item = new ItemStack(Material.GOLD_BLOCK);
			item.setAmount(r.nextInt(3) + 1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 4) {
			item = new ItemStack(Material.IRON_BLOCK);
			item.setAmount(r.nextInt(4) + 2);
			i.addItem(item);
		}
		if (r.nextInt(100) < 16) {
			item = new ItemStack(Material.MOSSY_COBBLESTONE);
			item.setAmount(r.nextInt(20) + 5);
			i.addItem(item);
		}
		if (r.nextInt(100) < 4) {
			item = new ItemStack(Material.OBSIDIAN);
			item.setAmount(r.nextInt(6) + 2);
			i.addItem(item);
		}
		if (r.nextInt(100) < 10) {
			item = new ItemStack(Material.CHEST);
			item.setAmount(r.nextInt(2) + 1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 2) {
			item = new ItemStack(Material.DIAMOND_BLOCK);
			item.setAmount(r.nextInt(3) + 1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 17) {
			item = new ItemStack(Material.CLAY);
			item.setAmount(r.nextInt(11) + 3);
			i.addItem(item);
		}
		if (r.nextInt(100) < 6) {
			item = new ItemStack(Material.MYCEL);
			item.setAmount(r.nextInt(5) + 1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 2) {
			item = new ItemStack(Material.EMERALD_BLOCK);
			item.setAmount(r.nextInt(3) + 1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 17) {
			item = new ItemStack(Material.COAL);
			item.setAmount(r.nextInt(15) + 2);
			i.addItem(item);
		}
		if (r.nextInt(100) < 10) {
			item = new ItemStack(Material.DIAMOND);
			item.setAmount(r.nextInt(6) + 2);
			i.addItem(item);
		}
		if (r.nextInt(100) < 13) {
			item = new ItemStack(Material.IRON_INGOT);
			item.setAmount(r.nextInt(10) + 2);
			i.addItem(item);
		}
		if (r.nextInt(100) < 12) {
			item = new ItemStack(Material.GOLD_INGOT);
			item.setAmount(r.nextInt(8) + 1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 16) {
			item = new ItemStack(Material.SADDLE);
			item.setAmount(1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 21) {
			item = new ItemStack(Material.CLAY_BALL);
			item.setAmount(r.nextInt(30) + 5);
			i.addItem(item);
		}
		if (r.nextInt(100) < 19) {
			item = new ItemStack(Material.PAPER);
			item.setAmount(r.nextInt(12) + 2);
			i.addItem(item);
		}
		if (r.nextInt(100) < 11) {
			item = new ItemStack(Material.SLIME_BALL);
			item.setAmount(r.nextInt(6) + 2);
			i.addItem(item);
		}
		if (r.nextInt(100) < 15) {
			item = new ItemStack(Material.EGG);
			item.setAmount(r.nextInt(7) + 6);
			i.addItem(item);
		}
		if (r.nextInt(100) < 36) {
			item = new ItemStack(Material.GLOWSTONE_DUST);
			item.setAmount(r.nextInt(50) + 10);
			i.addItem(item);
		}
		if (r.nextInt(100) < 14) {
			item = new ItemStack(Material.ENDER_PEARL);
			item.setAmount(r.nextInt(4) + 2);
			i.addItem(item);
		}
		if (r.nextInt(100) < 18) {
			item = new ItemStack(Material.GOLD_NUGGET);
			item.setAmount(r.nextInt(22) + 4);
			i.addItem(item);
		}
		if (r.nextInt(100) < 11) {
			item = new ItemStack(Material.EMERALD);
			item.setAmount(r.nextInt(6) + 1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 1) {
			item = new ItemStack(Material.NETHER_STAR);
			item.setAmount(1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 11) {
			item = new ItemStack(Material.IRON_BARDING);
			item.setAmount(1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 8) {
			item = new ItemStack(Material.GOLD_BARDING);
			item.setAmount(1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 4) {
			item = new ItemStack(Material.DIAMOND_BARDING);
			item.setAmount(1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 28) {
			item = new ItemStack(Material.NAME_TAG);
			item.setAmount(1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 5) {
			item = new ItemStack(Material.ELYTRA);
			item.setAmount(1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 4) {
			item = new ItemStack(Material.GREEN_RECORD);
			item.setAmount(1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 4) {
			item = new ItemStack(Material.GOLD_RECORD);
			item.setAmount(1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 4) {
			item = new ItemStack(Material.GREEN_RECORD);
			item.setAmount(1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 4) {
			item = new ItemStack(Material.RECORD_3);
			item.setAmount(1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 4) {
			item = new ItemStack(Material.RECORD_4);
			item.setAmount(1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 4) {
			item = new ItemStack(Material.RECORD_5);
			item.setAmount(1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 4) {
			item = new ItemStack(Material.RECORD_6);
			item.setAmount(1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 4) {
			item = new ItemStack(Material.RECORD_7);
			item.setAmount(1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 4) {
			item = new ItemStack(Material.RECORD_8);
			item.setAmount(1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 4) {
			item = new ItemStack(Material.RECORD_9);
			item.setAmount(1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 4) {
			item = new ItemStack(Material.RECORD_10);
			item.setAmount(1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 4) {
			item = new ItemStack(Material.RECORD_11);
			item.setAmount(1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 4) {
			item = new ItemStack(Material.RECORD_12);
			item.setAmount(1);
			i.addItem(item);
		}
	}

	private void addPlants(Inventory i, Random r) {
		ItemStack item = new ItemStack(Material.AIR);

		if (r.nextInt(100) < 10) {
			item = new ItemStack(Material.SEEDS);
			item.setAmount(r.nextInt(3) + 2);
			i.addItem(item);
		}
		if (r.nextInt(100) < 13) {
			item = new ItemStack(Material.SUGAR_CANE);
			item.setAmount(r.nextInt(2) + 1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 11) {
			item = new ItemStack(Material.PUMPKIN_SEEDS);
			item.setAmount(r.nextInt(3) + 1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 13) {
			item = new ItemStack(Material.MELON_SEEDS);
			item.setAmount(r.nextInt(3) + 2);
			i.addItem(item);
		}
		if (r.nextInt(100) < 35) {
			item = new ItemStack(Material.CARROT_ITEM);
			item.setAmount(r.nextInt(5) + 1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 31) {
			item = new ItemStack(Material.POTATO_ITEM);
			item.setAmount(r.nextInt(4) + 2);
			i.addItem(item);
		}
		if (r.nextInt(100) < 23) {
			item = new ItemStack(Material.BEETROOT_SEEDS);
			item.setAmount(r.nextInt(4) + 1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 18) {
			Sapling s = new Sapling(TreeSpecies.GENERIC);
			item = s.toItemStack(1);
			item.setAmount(r.nextInt(2) + 1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 9) {
			Sapling s = new Sapling(TreeSpecies.ACACIA);
			item = s.toItemStack(1);
			item.setAmount(r.nextInt(2) + 1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 17) {
			Sapling s = new Sapling(TreeSpecies.BIRCH);
			item = s.toItemStack(1);
			item.setAmount(r.nextInt(2) + 1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 11) {
			Sapling s = new Sapling(TreeSpecies.DARK_OAK);
			item = s.toItemStack(1);
			item.setAmount(r.nextInt(2) + 1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 13) {
			Sapling s = new Sapling(TreeSpecies.JUNGLE);
			item = s.toItemStack(1);
			item.setAmount(r.nextInt(2) + 1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 17) {
			item = new ItemStack(Material.RED_MUSHROOM);
			item.setAmount(r.nextInt(4) + 2);
			i.addItem(item);
		}
		if (r.nextInt(100) < 17) {
			item = new ItemStack(Material.BROWN_MUSHROOM);
			item.setAmount(r.nextInt(4) + 2);
			i.addItem(item);
		}
		if (r.nextInt(100) < 11) {
			item = new ItemStack(Material.CACTUS);
			item.setAmount(r.nextInt(3) + 1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 9) {
			item = new ItemStack(Material.VINE);
			item.setAmount(r.nextInt(2) + 1);
			i.addItem(item);
		}
		if (r.nextInt(100) < 23) {
			item = new ItemStack(Material.WATER_LILY);
			item.setAmount(r.nextInt(16) + 2);
			i.addItem(item);
		}
		if (r.nextInt(100) < 14) {
			item = new ItemStack(Material.INK_SACK);
			item.setData((MaterialData) new Dye(DyeColor.BROWN));
			item.setAmount(r.nextInt(5) + 1);
			i.addItem(item);
		}
	}

	private void generateBaseExterior(World w, Location start, int x, int y,
		int z, int bottomY)
	{
		generateBaseColumn(w, start, x,      y, z + 6 , bottomY);
		generateBaseColumn(w, start, x,      y, z + 9 , bottomY);
		generateBaseColumn(w, start, x + 15, y, z + 6 , bottomY);
		generateBaseColumn(w, start, x + 15, y, z + 9 , bottomY);
		generateBaseColumn(w, start, x + 6 , y, z     , bottomY);
		generateBaseColumn(w, start, x + 9 , y, z     , bottomY);
		generateBaseColumn(w, start, x + 6 , y, z + 15, bottomY);
		generateBaseColumn(w, start, x + 9 , y, z + 15, bottomY);
		generateBaseColumn(w, start, x + 1 , y, z + 4 , bottomY);
		generateBaseColumn(w, start, x + 1 , y, z + 11, bottomY);
		generateBaseColumn(w, start, x + 14, y, z + 4 , bottomY);
		generateBaseColumn(w, start, x + 14, y, z + 11, bottomY);
		generateBaseColumn(w, start, x + 4 , y, z + 1 , bottomY);
		generateBaseColumn(w, start, x + 11, y, z + 1 , bottomY);
		generateBaseColumn(w, start, x + 4 , y, z + 14, bottomY);
		generateBaseColumn(w, start, x + 11, y, z + 14, bottomY);

		generateBaseColumnFill(w, start, x     , y, z + 7 , bottomY, 3);
		generateBaseColumnFill(w, start, x     , y, z + 8 , bottomY, 3);
		generateBaseColumnFill(w, start, x + 15, y, z + 7 , bottomY, 3);
		generateBaseColumnFill(w, start, x + 15, y, z + 8 , bottomY, 3);
		generateBaseColumnFill(w, start, x + 1 , y, z + 5 , bottomY, 3);
		generateBaseColumnFill(w, start, x + 1 , y, z + 10, bottomY, 3);
		generateBaseColumnFill(w, start, x + 14, y, z + 5 , bottomY, 3);
		generateBaseColumnFill(w, start, x + 14, y, z + 10, bottomY, 3);

		generateBaseColumnFill(w, start, x + 7 , y, z     , bottomY, 4);
		generateBaseColumnFill(w, start, x + 8 , y, z     , bottomY, 4);
		generateBaseColumnFill(w, start, x + 7 , y, z + 15, bottomY, 4);
		generateBaseColumnFill(w, start, x + 8 , y, z + 15, bottomY, 4);
		generateBaseColumnFill(w, start, x + 5 , y, z + 1 , bottomY, 4);
		generateBaseColumnFill(w, start, x + 10, y, z + 1 , bottomY, 4);
		generateBaseColumnFill(w, start, x + 5 , y, z + 14, bottomY, 4);
		generateBaseColumnFill(w, start, x + 10, y, z + 14, bottomY, 4);

		generateBaseColumnFill(w, start, x + 2 , y, z + 3 , bottomY, 1);
		generateBaseColumnFill(w, start, x + 2 , y, z + 12, bottomY, 1);
		generateBaseColumnFill(w, start, x + 3 , y, z + 2 , bottomY, 1);
		generateBaseColumnFill(w, start, x + 3 , y, z + 13, bottomY, 1);
		generateBaseColumnFill(w, start, x + 12, y, z + 2 , bottomY, 1);
		generateBaseColumnFill(w, start, x + 12, y, z + 13, bottomY, 1);
		generateBaseColumnFill(w, start, x + 13, y, z + 3 , bottomY, 1);
		generateBaseColumnFill(w, start, x + 13, y, z + 12, bottomY, 1);
	}

	private void generateBaseInterior(World w, Location s, Random r, int x,
		int y, int z, int bY)
	{
		for (int cy = (y - 1); cy >= bY; cy -= 3) {
			for (int yMod = 0; yMod < 3; yMod++) {
				int ty = cy - yMod;
				generateLineX(w, s, r, x + 6, ty, z + 1 , 4 );
				generateLineX(w, s, r, x + 6, ty, z + 14, 4 );
				generateLineX(w, s, r, x + 4, ty, z + 2 , 8 );
				generateLineX(w, s, r, x + 4, ty, z + 13, 8 );
				generateLineX(w, s, r, x + 3, ty, z + 3 , 10);
				generateLineX(w, s, r, x + 3, ty, z + 12, 10);
				generateLineX(w, s, r, x + 2, ty, z + 4 , 12);
				generateLineX(w, s, r, x + 2, ty, z + 11, 12);
				generateLineX(w, s, r, x + 2, ty, z + 5 , 12);
				generateLineX(w, s, r, x + 2, ty, z + 10, 12);
				generateLineX(w, s, r, x + 1, ty, z + 6 , 14);
				generateLineX(w, s, r, x + 1, ty, z + 9 , 14);
				generateLineX(w, s, r, x + 1, ty, z + 7 , 14);
				generateLineX(w, s, r, x + 1, ty, z + 8 , 14);
			}
		}

		generateSLineX(w, s, x + 6, y, z + 1 , 4 , 98, 3);
		generateSLineX(w, s, x + 6, y, z + 14, 4 , 98, 3);
		generateSLineX(w, s, x + 4, y, z + 2 , 8 , 98, 3);
		generateSLineX(w, s, x + 4, y, z + 13, 8 , 98, 3);
		generateSLineX(w, s, x + 3, y, z + 3 , 10, 98, 3);
		generateSLineX(w, s, x + 3, y, z + 12, 10, 98, 3);

		generateSLineZ(w, s, x + 1 , y, z + 6, 4, 98, 3);
		generateSLineZ(w, s, x + 14, y, z + 6, 4, 98, 3);
		generateSLineZ(w, s, x + 2 , y, z + 4, 8, 98, 3);
		generateSLineZ(w, s, x + 13, y, z + 4, 8, 98, 3);

		generateSLineX(w, s, x + 4, y, z + 3 , 8 , 22, 0);
		generateSLineX(w, s, x + 4, y, z + 12, 8 , 22, 0);
		generateSLineX(w, s, x + 3, y, z + 4 , 10, 22, 0);
		generateSLineX(w, s, x + 3, y, z + 11, 10, 22, 0);
		generateSLineX(w, s, x + 3, y, z + 5 , 10, 22, 0);
		generateSLineX(w, s, x + 3, y, z + 10, 10, 22, 0);

		generateSLineX(w, s, x + 2, y, z + 6, 12, 98, 3);
		generateSLineX(w, s, x + 2, y, z + 9, 12, 98, 3);
		generateSLineZ(w, s, x + 6, y, z + 2, 12, 98, 3);
		generateSLineZ(w, s, x + 9, y, z + 2, 12, 98, 3);

		generateLineX(w, s, r, x + 2, y, z + 7, 12);
		generateLineX(w, s, r, x + 2, y, z + 8, 12);
		generateLineZ(w, s, r, x + 7, y, z + 2, 12);
		generateLineZ(w, s, r, x + 8, y, z + 2, 12);

		addBlock(s, w, x + 4 , y, z + 4 , 98, 3);
		addBlock(s, w, x + 4 , y, z + 11, 98, 3);
		addBlock(s, w, x + 11, y, z + 4 , 98, 3);
		addBlock(s, w, x + 11, y, z + 11, 98, 3);
	}

	private void generateArches(World world, Location start, int x, int y,
		int z)
	{
		generateArch(world, start, x + 6, y    , z     , 2, 3, false);
		generateArch(world, start, x + 6, y + 5, z + 1 , 2, 3, true );
		generateArch(world, start, x + 6, y + 9, z + 2 , 1, 3, true );
		generateArch(world, start, x + 6, y    , z + 15, 2, 2, false);
		generateArch(world, start, x + 6, y + 5, z + 14, 2, 2, true );
		generateArch(world, start, x + 6, y + 9, z + 13, 1, 2, true );

		generateArch(world, start, x     , y    , z + 6, 2, 1, false);
		generateArch(world, start, x + 1 , y + 5, z + 6, 2, 1, true );
		generateArch(world, start, x + 2 , y + 9, z + 6, 1, 1, true );
		generateArch(world, start, x + 15, y    , z + 6, 2, 0, false);
		generateArch(world, start, x + 14, y + 5, z + 6, 2, 0, true );
		generateArch(world, start, x + 13, y + 9, z + 6, 1, 0, true );
	}

	private void generateCorners(World world, Location start, int x, int y,
		int z)
	{
		generateCornerStairs(world, start, x, y, z, 0);
		generateCornerStairs(world, start, x, y, z, 2);

		for (int i = 0; i < 4; i++) {
			int xMod = ((i % 2 == 0) ? 1 : 14);
			int zMod = ((i % 2 == 0) ? 4 : 11);
			if (i >= 2) {xMod = (15 - xMod);}
			int xm = x + xMod;
			int zm = z + zMod;
			generateLineY(world, start, xm, y + 2, zm, 2, 2);
			addBlock(start, world, xm, y + 3, zm, 155, 1);

			xMod = ((i % 2 == 0) ? 4 : 11);
			zMod = ((i % 2 == 0) ? 1 : 14);
			if (i >= 2) {xMod = (15 - xMod);}
			xm = x + xMod;
			zm = z + zMod;
			generateLineY(world, start, xm, y + 2, zm, 2, 2);
			addBlock(start, world, xm, y + 3, zm, 155, 1);

			xMod = ((i % 2 == 0) ? 3 : 12);
			zMod = ((i % 2 == 0) ? 3 : 12);
			if (i >= 2) {xMod = (15 - xMod);}
			xm = x + xMod;
			zm = z + zMod;
			addBlock(start, world, xm, y + 5, zm, 44, (0x7 | 0x8));
			generateLineY(world, start, xm, y + 7, zm, 2, 2);
			addBlock(start, world, xm, y + 8, zm, 155, 1);
			addBlock(start, world, xm, y + 9, zm, 44, 7);

			xMod = ((i % 2 == 0) ? 2 : 13);
			zMod = ((i % 2 == 0) ? 3 : 12);
			if (i >= 2) {xMod = (15 - xMod);}
			xm = x + xMod;
			zm = z + zMod;
			addBlock(start, world, xm, y + 5, zm, 44, 7);

			xMod = ((i % 2 == 0) ? 3 : 12);
			zMod = ((i % 2 == 0) ? 2 : 13);
			if (i >= 2) {xMod = (15 - xMod);}
			xm = x + xMod;
			zm = z + zMod;
			addBlock(start, world, xm, y + 5, zm, 44, 7);

			xMod = ((i % 2 == 0) ? 3 : 12);
			zMod = ((i % 2 == 0) ? 4 : 11);
			if (i >= 2) {xMod = (15 - xMod);}
			xm = x + xMod;
			zm = z + zMod;
			addBlock(start, world, xm, y + 9, zm, 155, 0);

			xMod = ((i % 2 == 0) ? 4 : 11);
			zMod = ((i % 2 == 0) ? 3 : 12);
			if (i >= 2) {xMod = (15 - xMod);}
			xm = x + xMod;
			zm = z + zMod;
			addBlock(start, world, xm, y + 9, zm, 155, 0);

			xMod = ((i % 2 == 0) ? 3 : 12);
			zMod = ((i % 2 == 0) ? 6 : 9 );
			if (i >= 2) {xMod = (15 - xMod);}
			xm = x + xMod;
			zm = z + zMod;
			addBlock(start, world, xm, y + 12, zm, 155, 0);
			addBlock(start, world, xm, y + 13, zm, 155, 2);

			xMod = ((i % 2 == 0) ? 6 : 9 );
			zMod = ((i % 2 == 0) ? 3 : 12);
			if (i >= 2) {xMod = (15 - xMod);}
			xm = x + xMod;
			zm = z + zMod;
			addBlock(start, world, xm, y + 12, zm, 155, 0);
			addBlock(start, world, xm, y + 13, zm, 155, 2);
		}
	}

	private void generateCap(World world, Location start, int x, int y,
		int z)
	{
		addBlock(start, world, x + 5 , y + 15, z + 6 , 44, 7);
		addBlock(start, world, x + 6 , y + 15, z + 5 , 44, 7);
		addBlock(start, world, x + 10, y + 15, z + 6 , 44, 7);
		addBlock(start, world, x + 9 , y + 15, z + 5 , 44, 7);
		addBlock(start, world, x + 5 , y + 15, z + 9 , 44, 7);
		addBlock(start, world, x + 6 , y + 15, z + 10, 44, 7);
		addBlock(start, world, x + 10, y + 15, z + 9 , 44, 7);
		addBlock(start, world, x + 9 , y + 15, z + 10, 44, 7);

		addBlock(start, world, x + 6, y + 16, z + 7, 44, 0x7);
		addBlock(start, world, x + 6, y + 16, z + 8, 44, 0x7);
		addBlock(start, world, x + 9, y + 16, z + 7, 44, 0x7);
		addBlock(start, world, x + 9, y + 16, z + 8, 44, 0x7);
		addBlock(start, world, x + 7, y + 16, z + 6, 44, 0x7);
		addBlock(start, world, x + 7, y + 16, z + 9, 44, 0x7);
		addBlock(start, world, x + 8, y + 16, z + 9, 44, 0x7);
		addBlock(start, world, x + 8, y + 16, z + 6, 44, 0x7);

		addBlock(start, world, x + 5 , y + 15, z + 7 , 44, 0x7 | 0x8);
		addBlock(start, world, x + 5 , y + 15, z + 8 , 44, 0x7 | 0x8);
		addBlock(start, world, x + 10, y + 15, z + 7 , 44, 0x7 | 0x8);
		addBlock(start, world, x + 10, y + 15, z + 8 , 44, 0x7 | 0x8);
		addBlock(start, world, x + 7 , y + 15, z + 5 , 44, 0x7 | 0x8);
		addBlock(start, world, x + 7 , y + 15, z + 10, 44, 0x7 | 0x8);
		addBlock(start, world, x + 8 , y + 15, z + 10, 44, 0x7 | 0x8);
		addBlock(start, world, x + 8 , y + 15, z + 5 , 44, 0x7 | 0x8);
	}

	private void generateAir(World world, Location start, int x, int y,
		int z)
	{
		generateSLineY(world, start, x     , y, z + 7 , 3);
		generateSLineY(world, start, x     , y, z + 8 , 3);
		generateSLineY(world, start, x + 15, y, z + 7 , 3);
		generateSLineY(world, start, x + 15, y, z + 8 , 3);
		generateSLineY(world, start, x + 7 , y, z     , 3);
		generateSLineY(world, start, x + 8 , y, z     , 3);
		generateSLineY(world, start, x + 7 , y, z + 15, 3);
		generateSLineY(world, start, x + 8 , y, z + 15, 3);
		generateSLineY(world, start, x + 1 , y, z + 5 , 3);
		generateSLineY(world, start, x + 1 , y, z + 6 , 3);
		generateSLineY(world, start, x + 1 , y, z + 9 , 3);
		generateSLineY(world, start, x + 1 , y, z + 10, 3);
		generateSLineY(world, start, x + 14, y, z + 5 , 3);
		generateSLineY(world, start, x + 14, y, z + 6 , 3);
		generateSLineY(world, start, x + 14, y, z + 9 , 3);
		generateSLineY(world, start, x + 14, y, z + 10, 3);
		generateSLineY(world, start, x + 5 , y, z + 1 , 3);
		generateSLineY(world, start, x + 6 , y, z + 1 , 3);
		generateSLineY(world, start, x + 9 , y, z + 1 , 3);
		generateSLineY(world, start, x + 10, y, z + 1 , 3);
		generateSLineY(world, start, x + 5 , y, z + 14, 3);
		generateSLineY(world, start, x + 6 , y, z + 14, 3);
		generateSLineY(world, start, x + 9 , y, z + 14, 3);
		generateSLineY(world, start, x + 10, y, z + 14, 3);
		generateSLineY(world, start, x + 2 , y, z + 4 , 3);
		generateSLineY(world, start, x + 2 , y, z + 11, 3);
		generateSLineY(world, start, x + 4 , y, z + 2 , 3);
		generateSLineY(world, start, x + 4 , y, z + 13, 3);
		generateSLineY(world, start, x + 11, y, z + 13, 3);
		generateSLineY(world, start, x + 11, y, z + 2 , 3);
		generateSLineY(world, start, x + 13, y, z + 11, 3);
		generateSLineY(world, start, x + 13, y, z + 4 , 3);

		generateSLineY(world, start, x + 2 , y, z + 3 , 4);
		generateSLineY(world, start, x + 2 , y, z + 12, 4);
		generateSLineY(world, start, x + 3 , y, z + 2 , 4);
		generateSLineY(world, start, x + 3 , y, z + 3 , 4);
		generateSLineY(world, start, x + 3 , y, z + 12, 4);
		generateSLineY(world, start, x + 3 , y, z + 13, 4);
		generateSLineY(world, start, x + 12, y, z + 13, 4);
		generateSLineY(world, start, x + 12, y, z + 12, 4);
		generateSLineY(world, start, x + 12, y, z + 3 , 4);
		generateSLineY(world, start, x + 12, y, z + 2 , 4);
		generateSLineY(world, start, x + 13, y, z + 12, 4);
		generateSLineY(world, start, x + 13, y, z + 3 , 4);

		generateSLineY(world, start, x + 2 , y, z + 6 , 7);
		generateSLineY(world, start, x + 2 , y, z + 9 , 7);
		generateSLineY(world, start, x + 3 , y, z + 4 , 7);
		generateSLineY(world, start, x + 3 , y, z + 11, 7);
		generateSLineY(world, start, x + 4 , y, z + 3 , 7);
		generateSLineY(world, start, x + 4 , y, z + 12, 7);
		generateSLineY(world, start, x + 6 , y, z + 2 , 7);
		generateSLineY(world, start, x + 6 , y, z + 13, 7);
		generateSLineY(world, start, x + 9 , y, z + 13, 7);
		generateSLineY(world, start, x + 9 , y, z + 2 , 7);
		generateSLineY(world, start, x + 11, y, z + 12, 7);
		generateSLineY(world, start, x + 11, y, z + 3 , 7);
		generateSLineY(world, start, x + 12, y, z + 11, 7);
		generateSLineY(world, start, x + 12, y, z + 4 , 7);
		generateSLineY(world, start, x + 13, y, z + 9 , 7);
		generateSLineY(world, start, x + 13, y, z + 6 , 7);

		generateSLineY(world, start, x + 1 , y, z + 7 , 8);
		generateSLineY(world, start, x + 1 , y, z + 8 , 8);
		generateSLineY(world, start, x + 2 , y, z + 5 , 8);
		generateSLineY(world, start, x + 2 , y, z + 10, 8);
		generateSLineY(world, start, x + 5 , y, z + 2 , 8);
		generateSLineY(world, start, x + 5 , y, z + 13, 8);
		generateSLineY(world, start, x + 7 , y, z + 1 , 8);
		generateSLineY(world, start, x + 7 , y, z + 14, 8);
		generateSLineY(world, start, x + 8 , y, z + 14, 8);
		generateSLineY(world, start, x + 8 , y, z + 1 , 8);
		generateSLineY(world, start, x + 10, y, z + 13, 8);
		generateSLineY(world, start, x + 10, y, z + 2 , 8);
		generateSLineY(world, start, x + 13, y, z + 10, 8);
		generateSLineY(world, start, x + 13, y, z + 5 , 8);
		generateSLineY(world, start, x + 14, y, z + 8 , 8);
		generateSLineY(world, start, x + 14, y, z + 7 , 8);

		generateSLineY(world, start, x + 3 , y, z + 5 , 9);
		generateSLineY(world, start, x + 3 , y, z + 10, 9);
		generateSLineY(world, start, x + 5 , y, z + 12, 9);
		generateSLineY(world, start, x + 5 , y, z + 3 , 9);
		generateSLineY(world, start, x + 10, y, z + 3 , 9);
		generateSLineY(world, start, x + 10, y, z + 12, 9);
		generateSLineY(world, start, x + 12, y, z + 10, 9);
		generateSLineY(world, start, x + 12, y, z + 5 , 9);

		generateSLineY(world, start, x + 3 , y, z + 6 , 10);
		generateSLineY(world, start, x + 3 , y, z + 9 , 10);
		generateSLineY(world, start, x + 4 , y, z + 4 , 10);
		generateSLineY(world, start, x + 4 , y, z + 11, 10);
		generateSLineY(world, start, x + 6 , y, z + 12, 10);
		generateSLineY(world, start, x + 6 , y, z + 3 , 10);
		generateSLineY(world, start, x + 9 , y, z + 3 , 10);
		generateSLineY(world, start, x + 9 , y, z + 12, 10);
		generateSLineY(world, start, x + 11, y, z + 11, 10);
		generateSLineY(world, start, x + 11, y, z + 4 , 10);
		generateSLineY(world, start, x + 12, y, z + 9 , 10);
		generateSLineY(world, start, x + 12, y, z + 6 , 10);

		generateSLineY(world, start, x + 2 , y, z + 7 , 11);
		generateSLineY(world, start, x + 2 , y, z + 8 , 11);
		generateSLineY(world, start, x + 7 , y, z + 2 , 11);
		generateSLineY(world, start, x + 7 , y, z + 13, 11);
		generateSLineY(world, start, x + 8 , y, z + 13, 11);
		generateSLineY(world, start, x + 8 , y, z + 2 , 11);
		generateSLineY(world, start, x + 13, y, z + 8 , 11);
		generateSLineY(world, start, x + 13, y, z + 7 , 11);

		generateSLineY(world, start, x + 3 , y, z + 7 , 12);
		generateSLineY(world, start, x + 3 , y, z + 8 , 12);
		generateSLineY(world, start, x + 4 , y, z + 5 , 12);
		generateSLineY(world, start, x + 4 , y, z + 10, 12);
		generateSLineY(world, start, x + 5 , y, z + 11, 12);
		generateSLineY(world, start, x + 5 , y, z + 4 , 12);
		generateSLineY(world, start, x + 7 , y, z + 3 , 12);
		generateSLineY(world, start, x + 7 , y, z + 12, 12);
		generateSLineY(world, start, x + 8 , y, z + 12, 12);
		generateSLineY(world, start, x + 8 , y, z + 3 , 12);
		generateSLineY(world, start, x + 10, y, z + 4 , 12);
		generateSLineY(world, start, x + 10, y, z + 11, 12);
		generateSLineY(world, start, x + 11, y, z + 10, 12);
		generateSLineY(world, start, x + 11, y, z + 5 , 12);
		generateSLineY(world, start, x + 12, y, z + 8 , 12);
		generateSLineY(world, start, x + 12, y, z + 7 , 12);

		generateSLineY(world, start, x + 4 , y, z + 6 , 13);
		generateSLineY(world, start, x + 4 , y, z + 9 , 13);
		generateSLineY(world, start, x + 5 , y, z + 5 , 13);
		generateSLineY(world, start, x + 5 , y, z + 10, 13);
		generateSLineY(world, start, x + 6 , y, z + 11, 13);
		generateSLineY(world, start, x + 6 , y, z + 4 , 13);
		generateSLineY(world, start, x + 9 , y, z + 4 , 13);
		generateSLineY(world, start, x + 9 , y, z + 11, 13);
		generateSLineY(world, start, x + 10, y, z + 10, 13);
		generateSLineY(world, start, x + 10, y, z + 5 , 13);
		generateSLineY(world, start, x + 11, y, z + 9 , 13);
		generateSLineY(world, start, x + 11, y, z + 6 , 13);

		generateSLineY(world, start, x + 4 , y, z + 7 , 14);
		generateSLineY(world, start, x + 4 , y, z + 8 , 14);
		generateSLineY(world, start, x + 5 , y, z + 8 , 14);
		generateSLineY(world, start, x + 5 , y, z + 7 , 14);
		generateSLineY(world, start, x + 5 , y, z + 6 , 14);
		generateSLineY(world, start, x + 5 , y, z + 9 , 14);
		generateSLineY(world, start, x + 6 , y, z + 9 , 14);
		generateSLineY(world, start, x + 6 , y, z + 6 , 14);
		generateSLineY(world, start, x + 6 , y, z + 5 , 14);
		generateSLineY(world, start, x + 6 , y, z + 10, 14);
		generateSLineY(world, start, x + 7 , y, z + 10, 14);
		generateSLineY(world, start, x + 7 , y, z + 5 , 14);
		generateSLineY(world, start, x + 7 , y, z + 4 , 14);
		generateSLineY(world, start, x + 7 , y, z + 11, 14);
		generateSLineY(world, start, x + 8 , y, z + 11, 14);
		generateSLineY(world, start, x + 8 , y, z + 4 , 14);
		generateSLineY(world, start, x + 8 , y, z + 5 , 14);
		generateSLineY(world, start, x + 8 , y, z + 10, 14);
		generateSLineY(world, start, x + 9 , y, z + 10, 14);
		generateSLineY(world, start, x + 9 , y, z + 5 , 14);
		generateSLineY(world, start, x + 9 , y, z + 6 , 14);
		generateSLineY(world, start, x + 9 , y, z + 9 , 14);
		generateSLineY(world, start, x + 10, y, z + 9 , 14);
		generateSLineY(world, start, x + 10, y, z + 6 , 14);
		generateSLineY(world, start, x + 10, y, z + 7 , 14);
		generateSLineY(world, start, x + 10, y, z + 8 , 14);
		generateSLineY(world, start, x + 11, y, z + 8 , 14);
		generateSLineY(world, start, x + 11, y, z + 7 , 14);

		generateSLineY(world, start, x + 6, y, z + 7, 15);
		generateSLineY(world, start, x + 6, y, z + 8, 15);
		generateSLineY(world, start, x + 7, y, z + 9, 15);
		generateSLineY(world, start, x + 7, y, z + 6, 15);
		generateSLineY(world, start, x + 7, y, z + 7, 15);
		generateSLineY(world, start, x + 7, y, z + 8, 15);
		generateSLineY(world, start, x + 8, y, z + 8, 15);
		generateSLineY(world, start, x + 8, y, z + 7, 15);
		generateSLineY(world, start, x + 8, y, z + 6, 15);
		generateSLineY(world, start, x + 8, y, z + 9, 15);
		generateSLineY(world, start, x + 9, y, z + 8, 15);
		generateSLineY(world, start, x + 9, y, z + 7, 15);
	}

	private void generatePlatform(World world, Location start, int x, int y,
		int z)
	{
		addBlock(start, world, x + 5 , y, z + 7 , 98, 0);
		addBlock(start, world, x + 5 , y, z + 8 , 98, 0);
		addBlock(start, world, x + 7 , y, z + 5 , 98, 0);
		addBlock(start, world, x + 7 , y, z + 10, 98, 0);
		addBlock(start, world, x + 8 , y, z + 10, 98, 0);
		addBlock(start, world, x + 8 , y, z + 5 , 98, 0);
		addBlock(start, world, x + 10, y, z + 8 , 98, 0);
		addBlock(start, world, x + 10, y, z + 7 , 98, 0);

		addBlock(start, world, x + 4 , y, z + 7 , 44, 5);
		addBlock(start, world, x + 4 , y, z + 8 , 44, 5);
		addBlock(start, world, x + 7 , y, z + 4 , 44, 5);
		addBlock(start, world, x + 7 , y, z + 11, 44, 5);
		addBlock(start, world, x + 8 , y, z + 11, 44, 5);
		addBlock(start, world, x + 8 , y, z + 4 , 44, 5);
		addBlock(start, world, x + 11, y, z + 8 , 44, 5);
		addBlock(start, world, x + 11, y, z + 7 , 44, 5);

		addBlock(start, world, x + 6, y, z + 7, 54, 5);
		addBlock(start, world, x + 6, y, z + 8, 54, 5);
		addBlock(start, world, x + 7, y, z + 9, 54, 3);
		addBlock(start, world, x + 7, y, z + 6, 54, 2);
		addBlock(start, world, x + 8, y, z + 6, 54, 2);
		addBlock(start, world, x + 8, y, z + 9, 54, 3);
		addBlock(start, world, x + 9, y, z + 8, 54, 4);
		addBlock(start, world, x + 9, y, z + 7, 54, 4);

		addBlock(start, world, x + 6, y, z + 6, 98, 3);
		addBlock(start, world, x + 6, y, z + 9, 98, 3);
		addBlock(start, world, x + 9, y, z + 9, 98, 3);
		addBlock(start, world, x + 9, y, z + 6, 98, 3);

		addBlock(start, world, x + 7, y, z + 7, 155, 1);
		addBlock(start, world, x + 7, y, z + 8, 155, 1);
		addBlock(start, world, x + 8, y, z + 8, 155, 1);
		addBlock(start, world, x + 8, y, z + 7, 155, 1);

		addBlock(start, world, x + 6, y + 1, z + 7, 44, 5);
		addBlock(start, world, x + 6, y + 1, z + 8, 44, 5);
		addBlock(start, world, x + 7, y + 1, z + 9, 44, 5);
		addBlock(start, world, x + 7, y + 1, z + 6, 44, 5);
		addBlock(start, world, x + 8, y + 1, z + 6, 44, 5);
		addBlock(start, world, x + 8, y + 1, z + 9, 44, 5);
		addBlock(start, world, x + 9, y + 1, z + 8, 44, 5);
		addBlock(start, world, x + 9, y + 1, z + 7, 44, 5);
	}

	private void generateBaseColumn(World world, Location start, int x,
		int y, int z, int maxY)
	{
		addBlock(start, world, x, y, z, 155, 1);

		for (int cy = (y - 1); cy >= maxY; cy -= 3) {
			generateLineY(world, start, x, cy, z, 2, 2);
			addBlock(start, world, x, cy - 2, z, 155, 1);
		}
	}

	private void generateBaseColumnFill(World world, Location start, int x,
		int y, int z, int maxY, int data)
	{
		addBlock(start, world, x, y, z, 155, 0);

		for (int cy = (y - 1); cy >= maxY; cy -= 3) {
			generateLineY(world, start, x, cy, z, 2, data);
			addBlock(start, world, x, cy - 2, z, 155, 0);
		}
	}

	private void generateArch(World w, Location s, int x, int y, int z,
		int ht, int dir, boolean base)
	{
		int data = ((dir >= 2) ? 0 : 2);

		generateLineY(w, s, x, y + (ht - 1), z, ht, 2);
		addBlock(s, w, x, y + ht, z, 155, 1);
		addBlock(s, w, x, y + ht + 1, z, 156, data);

		if (base) {
			addBlock(s, w, x, y - 1, z, 155, 0);
			addBlock(s, w, x, y - 2, z, 156, dir | 0x4);
		}

		int xMod = ((dir >= 2) ? 3 : 0);
		int zMod = ((dir <= 1) ? 3 : 0);
		data = ((dir >= 2) ? 1 : 3);
		int xm = x + xMod;
		int zm = z + zMod;

		generateLineY(w, s, xm, y + (ht - 1), zm, ht, 2);
		addBlock(s, w, xm, y + ht, zm, 155, 1);
		addBlock(s, w, xm, y + ht + 1, zm, 156, data);

		if (base) {
			addBlock(s, w, xm, y - 1, zm, 155, 0);
			addBlock(s, w, xm, y - 2, zm, 156, dir | 0x4);
		}

		xMod = ((dir >= 2) ? 1 : 0);
		zMod = ((dir <= 1) ? 1 : 0);
		data = ((dir >= 2) ? 1 : 3);
		xm = x + xMod;
		zm = z + zMod;

		addBlock(s, w, xm, y + ht + 2, zm, 44, 7);
		addBlock(s, w, xm, y + ht + 1, zm, 156, data | 0x4);

		xMod = ((dir >= 2) ? 2 : 0);
		zMod = ((dir <= 1) ? 2 : 0);
		data = ((dir >= 2) ? 0 : 2);
		xm = x + xMod;
		zm = z + zMod;

		addBlock(s, w, xm, y + ht + 2, zm, 44, 7);
		addBlock(s, w, xm, y + ht + 1, zm, 156, data | 0x4);
	}

	private void generateCornerStairs(World w, Location s, int x, int y,
		int z, int dir)
	{
		for (int type = 0; type < 2; type++) {
			int xMod = ((dir >= 2) ? 1 : 4);
			int zMod = ((dir >= 2) ? 4 : 1);
			if (type != 0) {xMod = (15 - xMod); zMod = (15 - zMod);}
			xMod += x;
			zMod += z;
			addBlock(s, w, xMod, y + 4, zMod, 156, dir);

			xMod = ((dir >= 2) ? 2 : 4);
			zMod = ((dir >= 2) ? 4 : 2);
			if (type != 0) {xMod = (15 - xMod); zMod = (15 - zMod);}
			xMod += x;
			zMod += z;
			addBlock(s, w, xMod, y + 9, zMod, 156, dir);

			xMod = ((dir >= 2) ? 5 : 1);
			zMod = ((dir >= 2) ? 1 : 5);
			if (type != 0) {xMod = (15 - xMod); zMod = (15 - zMod);}
			xMod += x;
			zMod += z;
			addBlock(s, w, xMod, y + 4, zMod, 156, dir);

			xMod = ((dir >= 2) ? 5 : 2);
			zMod = ((dir >= 2) ? 2 : 5);
			if (type != 0) {xMod = (15 - xMod); zMod = (15 - zMod);}
			xMod += x;
			zMod += z;
			addBlock(s, w, xMod, y + 9, zMod, 156, dir);

			xMod = ((dir >= 2) ? 6 : 3);
			zMod = ((dir >= 2) ? 3 : 6);
			if (type != 0) {xMod = (15 - xMod); zMod = (15 - zMod);}
			xMod += x;
			zMod += z;
			addBlock(s, w, xMod, y + 14, zMod, 156, dir);

			xMod = ((dir >= 2) ? 9 : 3);
			zMod = ((dir >= 2) ? 3 : 9);
			if (type != 0) {xMod = (15 - xMod); zMod = (15 - zMod);}
			xMod += x;
			zMod += z;
			addBlock(s, w, xMod, y + 14, zMod, 156, dir);

			xMod = ((dir >= 2) ? 14 : 4 );
			zMod = ((dir >= 2) ? 4  : 14);
			if (type != 0) {xMod = (15 - xMod); zMod = (15 - zMod);}
			xMod += x;
			zMod += z;
			addBlock(s, w, xMod, y + 4, zMod, 156, dir);

			xMod = ((dir >= 2) ? 13 : 4 );
			zMod = ((dir >= 2) ? 4  : 13);
			if (type != 0) {xMod = (15 - xMod); zMod = (15 - zMod);}
			xMod += x;
			zMod += z;
			addBlock(s, w, xMod, y + 9, zMod, 156, dir);

			xMod = ((dir >= 2) ? 10 : 1 );
			zMod = ((dir >= 2) ? 1  : 10);
			if (type != 0) {xMod = (15 - xMod); zMod = (15 - zMod);}
			xMod += x;
			zMod += z;
			addBlock(s, w, xMod, y + 4, zMod, 156, dir);

			xMod = ((dir >= 2) ? 10 : 2 );
			zMod = ((dir >= 2) ? 2  : 10);
			if (type != 0) {xMod = (15 - xMod); zMod = (15 - zMod);}
			xMod += x;
			zMod += z;
			addBlock(s, w, xMod, y + 9, zMod, 156, dir);

			xMod = ((dir >= 2) ? 11 : 13);
			zMod = ((dir >= 2) ? 13 : 11);
			if (type != 0) {xMod = (15 - xMod); zMod = (15 - zMod);}
			xMod += x;
			zMod += z;
			addBlock(s, w, xMod, y + 4, zMod, 156, dir | 0x4);

			xMod = ((dir >= 2) ? 4  : 13);
			zMod = ((dir >= 2) ? 13 : 4 );
			if (type != 0) {xMod = (15 - xMod); zMod = (15 - zMod);}
			xMod += x;
			zMod += z;
			addBlock(s, w, xMod, y + 4, zMod, 156, dir | 0x4);

			xMod = ((dir >= 2) ? 12 : 11);
			zMod = ((dir >= 2) ? 11 : 12);
			if (type != 0) {xMod = (15 - xMod); zMod = (15 - zMod);}
			xMod += x;
			zMod += z;
			addBlock(s, w, xMod, y + 8, zMod, 156, dir | 0x4);

			xMod = ((dir >= 2) ? 3  : 11);
			zMod = ((dir >= 2) ? 11 : 3 );
			if (type != 0) {xMod = (15 - xMod); zMod = (15 - zMod);}
			xMod += x;
			zMod += z;
			addBlock(s, w, xMod, y + 8, zMod, 156, dir | 0x4);

			xMod = ((dir >= 2) ? 9  : 12);
			zMod = ((dir >= 2) ? 12 : 9 );
			if (type != 0) {xMod = (15 - xMod); zMod = (15 - zMod);}
			xMod += x;
			zMod += z;
			addBlock(s, w, xMod, y + 11, zMod, 156, dir | 0x4);

			xMod = ((dir >= 2) ? 6  : 12);
			zMod = ((dir >= 2) ? 12 : 6 );
			if (type != 0) {xMod = (15 - xMod); zMod = (15 - zMod);}
			xMod += x;
			zMod += z;
			addBlock(s, w, xMod, y + 11, zMod, 156, dir | 0x4);

			xMod = ((dir >= 2) ? 9  : 11);
			zMod = ((dir >= 2) ? 11 : 9 );
			if (type != 0) {xMod = (15 - xMod); zMod = (15 - zMod);}
			xMod += x;
			zMod += z;
			addBlock(s, w, xMod, y + 14, zMod, 156, dir | 0x4);

			xMod = ((dir >= 2) ? 6  : 11);
			zMod = ((dir >= 2) ? 11 : 6 );
			if (type != 0) {xMod = (15 - xMod); zMod = (15 - zMod);}
			xMod += x;
			zMod += z;
			addBlock(s, w, xMod, y + 14, zMod, 156, dir | 0x4);

			dir = (dir == 2 ? 3 : 1);
		}
	}

	private void generateLineX(World world, Location start, Random random,
		int x, int y, int z, int length)
	{
		for (int cx = x; cx < (x + length); cx++) {
			addBlock(start, world, cx, y, z, 98, random.nextInt(3));
		}
	}

	private void generateLineY(World world, Location start, int x, int y,
		int z, int length, int data)
	{
		for (int cy = y; cy > (y - length); cy--) {
			addBlock(start, world, x, cy, z, 155, data);
		}
	}

	private void generateLineZ(World world, Location start, Random random,
		int x, int y, int z, int length)
	{
		for (int cz = z; cz < (z + length); cz++) {
			addBlock(start, world, x, y, cz, 98, random.nextInt(3));
		}
	}

	private void generateSLineX(World world, Location start, int x, int y,
		int z, int len, int id, int data)
	{
		for (int cx = x; cx < (x + len); cx++) {
			addBlock(start, world, cx, y, z, id, data);
		}
	}

	private void generateSLineY(World world, Location start, int x, int y,
		int z, int length)
	{
		for (int cy = y; cy < (y + length); cy++) {
			addBlock(start, world, x, cy, z, 0, 0);
		}
	}

	private void generateSLineZ(World world, Location start, int x, int y,
		int z, int len, int id, int data)
	{
		for (int cz = z; cz < (z + len); cz++) {
			addBlock(start, world, x, y, cz, id, data);
		}
	}

	private int getBaseY(World world, int x, int z, boolean ignoreExtras) {
		for (int y = 85; y > 0; y--) {
			int id = world.getBlockTypeIdAt(x, y, z);

			if (!ignoreExtras && (id == 8 || id == 9))
				return y;
			else if (!ignoreExtras && (id == 2 || id == 3))
				return y;
			else if (id == 1 || id == 16 || id == 21 || id == 56)
				return y;
			else if (id == 73 || id == 74 || id == 129)
				return y;
		}

		return -1;
	}
}
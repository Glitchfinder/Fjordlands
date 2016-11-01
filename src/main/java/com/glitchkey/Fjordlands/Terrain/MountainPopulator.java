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

package com.glitchkey.fjordlands.terrain;

//* IMPORTS: JDK/JRE
	import java.util.Random;
//* IMPORTS: BUKKIT
	import org.bukkit.block.Block;
	import org.bukkit.block.BlockFace;
	import org.bukkit.Chunk;
	import org.bukkit.DyeColor;
	import org.bukkit.entity.EntityType;
	import org.bukkit.entity.LivingEntity;
	import org.bukkit.entity.Sheep;
	import org.bukkit.Location;
	import org.bukkit.plugin.Plugin;
	import org.bukkit.World;
//* IMPORTS: PANDORA
	import org.pandora.PandoraBiomePopulator;
	import org.pandora.trees.Redwood;
	import org.pandora.trees.TallRedwood;
//* IMPORTS: FJORDLANDS
	import com.glitchkey.fjordlands.FjordlandsPlugin;
	import com.glitchkey.fjordlands.structures.MountainDungeon;
	import com.glitchkey.fjordlands.structures.MountainOre;
//* IMPORTS: OTHER
	//* NOT NEEDED

public class MountainPopulator extends PandoraBiomePopulator
{
	Redwood redwood;
	TallRedwood tallRedwood;
	MountainDungeon dungeon;
	MountainOre coal, lapis, diamond, redstone, emerald, gold, iron, gravel;
	MountainOre sand, lava;

	public MountainPopulator(FjordlandsPlugin plugin)
	{
		Plugin p = (Plugin) plugin;
		minTemperature = -1.0F;
		maxTemperature = 101.0F;
		minHumidity = -1.0F;
		maxHumidity = 101.0F;
		redwood = new Redwood(p, false);
		tallRedwood = new TallRedwood(p, false);
		dungeon  = new MountainDungeon(p, false);
		coal     = new MountainOre(p, false, 16, 3D, 5.5D, 1D, 2.3D);
		lapis    = new MountainOre(p, false, 21, 2D, 3D, 1D, 1.5D);
		diamond  = new MountainOre(p, false, 56, 1.5D, 3D, 0.5D, 1.2D);
		redstone = new MountainOre(p, false, 73, 3D, 5D, 1D, 2D);
		emerald  = new MountainOre(p, false, 129, 1.5D, 3.5D, 1D, 2D);
		iron     = new MountainOre(p, false, 15, 3D, 3.5D, 1.2D, 2.5D);
		gold     = new MountainOre(p, false, 14, 2D, 3.5D, 1.2D, 2.1D);
		gravel   = new MountainOre(p, false, 13, 3.5D, 6D, 1D, 2.5D);
		sand     = new MountainOre(p, false, 12, 4D, 5.5D, 1D, 2.8D);
		lava     = new MountainOre(p, false, 11, 5D, 8D, 1.2D, 3.1D);
	}

	public void populate(World w, Random r, Chunk source, int x,
		int z)
	{
		int chunkX = source.getX();
		int chunkZ = source.getZ();
		int soil = this.getTopSoilY(w, x, z, chunkX, chunkZ);
		int base = soil + 1;
		boolean space = (soil != -1);
		boolean tree = false;

		generateOre(w, r, x, z);

		if ((chunkX << 4) == x && (chunkZ << 4) == z) {
			Random rand = getRandom(w, x, z);

			if(rand.nextInt(100) <= 5 && rand.nextInt(100) <= 20) {
				if (dungeon.place(w, r, x, 0, z))
					return;
			}
		}

		int id = w.getBlockTypeIdAt(x, (soil + 1), z);

		if (space && r.nextInt(30) == 0 && id == 0) {
			if (r.nextInt(3) == 0)
				tree = tallRedwood.place(w, r, x, base, z);
			else
				tree = redwood.place(w, r, x, base, z);
		}

		if (r.nextInt(30) == 0) {
			for (int attempt = 0; attempt < 8; attempt++) {
				int cx = x + r.nextInt(8) - r.nextInt(8);
				int cz = z + r.nextInt(8) - r.nextInt(8);
				int chX = cx >> 4;
				int chZ = cz >> 4;

				int cy = getTopSoilY(w, cx, cz, chX, chZ) + 1;

				if (cy <= 0)
					continue;

				if (w.getBlockTypeIdAt(cx, cy, cz) != 0)
					continue;

				id = w.getBlockTypeIdAt(cx, cy - 1, cz);

				if (id != 3 && id != 2)
					continue;

				Block block = w.getBlockAt(cx, cy, cz);

				if (block.getLightLevel() < 8)
					continue;

				block.setTypeIdAndData(31, (byte) 1, true);
			}
		}

		if (r.nextInt(28672) == 0) {
			for (int attempt = 0; attempt < 24; attempt++) {
				int cx = x + r.nextInt(8) - r.nextInt(8);
				int cz = z + r.nextInt(8) - r.nextInt(8);
				int chX = cx >> 4;
				int chZ = cz >> 4;

				int cy = getTopSoilY(w, cx, cz, chX, chZ) + 1;

				if (cy <= 0)
					continue;

				if (w.getBlockTypeIdAt(cx, cy, cz) != 0)
					continue;

				id = w.getBlockTypeIdAt(cx, cy - 1, cz);

				if (id != 3 && id != 2)
					continue;

				Block block = w.getBlockAt(cx, cy, cz);
				block.setTypeIdAndData(86, (byte) 4, true);
			}
		}

		if (r.nextInt(32768) == 0) {
			for (int attempt = 0; attempt < 6; attempt++) {
				int cx = x + r.nextInt(8) - r.nextInt(8);
				int cz = z + r.nextInt(8) - r.nextInt(8);
				int chX = cx >> 4;
				int chZ = cz >> 4;

				int cy = getTopSoilY(w, cx, cz, chX, chZ) + 1;

				if (cy <= 0)
					continue;

				if (w.getBlockTypeIdAt(cx, cy, cz) != 0)
					continue;

				id = w.getBlockTypeIdAt(cx, cy - 1, cz);

				if (id != 3 && id != 2)
					continue;

				Block block = w.getBlockAt(cx, cy, cz);
				block.setTypeIdAndData(103, (byte) 0, true);
			}
		}

		if (r.nextInt(768) == 0) {
			int flower = ((r.nextInt(4) == 0) ? 37 : 38);

			for (int attempt = 0; attempt < 12; attempt++) {
				int cx = x + r.nextInt(8) - r.nextInt(8);
				int cz = z + r.nextInt(8) - r.nextInt(8);
				int chX = cx >> 4;
				int chZ = cz >> 4;

				int cy = getTopSoilY(w, cx, cz, chX, chZ) + 1;

				if (cy <= 0)
					continue;

				if (w.getBlockTypeIdAt(cx, cy, cz) != 0)
					continue;

				id = w.getBlockTypeIdAt(cx, cy - 1, cz);

				if (id != 3 && id != 2)
					continue;

				Block block = w.getBlockAt(cx, cy, cz);

				if (block.getLightLevel() < 8)
					continue;

				block.setTypeIdAndData(flower, (byte) 0, true);
			}
		}

		if (r.nextInt(768) == 0) {
			int mushroom = ((r.nextInt(4) == 0) ? 39 : 40);

			for (int attempt = 0; attempt < 12; attempt++) {
				int cx = x + r.nextInt(8) - r.nextInt(8);
				int cz = z + r.nextInt(8) - r.nextInt(8);
				int chX = cx >> 4;
				int chZ = cz >> 4;

				int cy = getTopSoilY(w, cx, cz, chX, chZ) + 1;

				if (cy <= 0)
					continue;

				if (w.getBlockTypeIdAt(cx, cy, cz) != 0)
					continue;

				id = w.getBlockTypeIdAt(cx, cy - 1, cz);

				if (id != 3 && id != 2)
					continue;

				Block b = w.getBlockAt(cx, cy, cz);

				if (b.getLightLevel() > 12)
					continue;

				b.setTypeIdAndData(mushroom, (byte) 0, true);
			}
		}

		if (r.nextInt(256) == 0) {
			for (int attempt = 0; attempt < 24; attempt++) {
				int cx = x + r.nextInt(8) - r.nextInt(8);
				int cz = z + r.nextInt(8) - r.nextInt(8);
				int chX = cx >> 4;
				int chZ = cz >> 4;
				int cy = 39;

				boolean con1 = w.isChunkLoaded(chX, chZ);
				boolean con2 = w.loadChunk(chX, chZ, false);

				if (!con1 && !con2)
					continue;

				if (w.getBlockTypeIdAt(cx, cy, cz) != 0)
					continue;

				id = w.getBlockTypeIdAt(cx, cy - 1, cz);

				if (id != 3 && id != 2 && id != 12)
					continue;

				int y = cy - 1;

				int id1 = w.getBlockTypeIdAt(cx + 1, y, cz    );
				int id2 = w.getBlockTypeIdAt(cx - 1, y, cz    );
				int id3 = w.getBlockTypeIdAt(cx    , y, cz + 1);
				int id4 = w.getBlockTypeIdAt(cx    , y, cz - 1);

				if (checkWater(id1, id2, id3, id4))
					continue;

				Block b = w.getBlockAt(cx, cy, cz);

				for (int i = 0; i < 7; i++) {
					b.setTypeIdAndData(83, (byte) 0, true);
					b = b.getRelative(0, 1, 0);

					if (r.nextBoolean())
						break;
				}
			}
		}

		if (r.nextInt(160) == 0) {
			for (int attempt = 0; attempt < 6; attempt++) {
				int cx = x + r.nextInt(8) - r.nextInt(8);
				int cz = z + r.nextInt(8) - r.nextInt(8);
				int chX = cx >> 4;
				int chZ = cz >> 4;
				int cy = 39;

				boolean con1 = w.isChunkLoaded(chX, chZ);
				boolean con2 = w.loadChunk(chX, chZ, false);

				if (!con1 && !con2)
					continue;

				if (w.getBlockTypeIdAt(cx, cy, cz) != 0)
					continue;

				id = w.getBlockTypeIdAt(cx, cy - 1, cz);

				if (id != 8 && id != 9)
					continue;

				Block block = w.getBlockAt(cx, cy, cz);

				block.setTypeIdAndData(111, (byte) 0, true);
			}
		}

		if (space && r.nextInt(800) == 0) {
			EntityType type = EntityType.SHEEP;

			if (r.nextInt(8) == 0)
				type = EntityType.WOLF;

			int spawned = 0;
			int pack = ((type == EntityType.WOLF) ? 8 : 4);

			for (int att = 0; att < 12 && spawned < pack; att++) {
				int cx = x + r.nextInt(8) - r.nextInt(8);
				int cz = z + r.nextInt(8) - r.nextInt(8);
				int chX = cx >> 4;
				int chZ = cz >> 4;

				int cy = this.getTopSoilY(w, cx, cz, chX, chZ) + 1;

				if (cy <= 0)
					continue;

				id = w.getBlockTypeIdAt(cx, cy, cz);

				if(id == 8 || id == 9)
					continue;

				Location l;
				l = new Location(w, cx + 0.5D, cy, cz + 0.5D);

				LivingEntity entity;
				entity = (LivingEntity) w.spawnEntity(l, type);

				if(entity == null)
					continue;

				spawned++;

				if (type == EntityType.SHEEP) {
					handleSheep(r, (Sheep) entity);
				}
				else {
					entity.setCustomName("Winter Wolf");
					entity.setCustomNameVisible(false);
					entity.setRemoveWhenFarAway(false);
					entity.setMaxHealth(16);
					entity.setHealth(16);
				}
			}
		}
	}

	private boolean checkWater(int id1, int id2, int id3, int id4) {
		if (id1 != 8 && id2 != 8 && id3 != 8 && id4 != 8) {
			if (id1 != 9 && id2 != 9 && id3 != 9 && id4 != 9) {
				return false;
			}
		}

		return true;
	}

	private void handleSheep(Random r, Sheep sheep) {
		sheep.setCustomName("Grey Troender");
		sheep.setCustomNameVisible(false);
		sheep.setRemoveWhenFarAway(false);
		sheep.setMaxHealth(16);
		sheep.setHealth(16);

		int rand = r.nextInt(100);

		if (rand < 30)
			sheep.setColor(DyeColor.WHITE);
		else if (rand < 90)
			sheep.setColor(DyeColor.SILVER);
		else if (rand < 97)
			sheep.setColor(DyeColor.GRAY);
		else
			sheep.setColor(DyeColor.BLACK);
	}

	private void generateOre(World w, Random rand, int x, int z) {
		if(rand.nextInt(100) < 2) {
			coal.place(w, rand, x, rand.nextInt(55) + 10, z);
		}

		if(rand.nextInt(100) < 1) {
			lapis.place(w, rand, x, rand.nextInt(50) + 10, z);
		}

		if(rand.nextInt(10000) < 78) {
			redstone.place(w, rand, x, rand.nextInt(30) + 10, z);
		}

		if(rand.nextInt(10000) < 39) {
			diamond.place(w, rand, x, rand.nextInt(30) + 10, z);
		}

		if(rand.nextInt(100000) < 78) {
			emerald.place(w, rand, x, rand.nextInt(25) + 10, z);
		}

		if(rand.nextInt(5000) < 78) {
			iron.place(w, rand, x, rand.nextInt(30) + 10, z);
		}

		if(rand.nextInt(7000) < 78) {
			gold.place(w, rand, x, rand.nextInt(30) + 10, z);
		}

		if(rand.nextInt(100) < 1) {
			gravel.place(w, rand, x, rand.nextInt(50) + 10, z);
		}

		if(rand.nextInt(100) < 1) {
			sand.place(w, rand, x, rand.nextInt(50) + 10, z);
		}

		if(rand.nextInt(11000) < 78) {
			lava.place(w, rand, x, rand.nextInt(10) + 10, z);
		}
	}

	public int getTopSoilY(World w, int x, int z, int chunkX, int chunkZ) {
		return this.getTopSoilY(w, x, 236, z, chunkX, chunkZ);
	}

	public int getTopSoilY(World w, int x, int maxY, int z, int chunkX,
		int chunkZ)
	{
		if (!w.isChunkLoaded(chunkX, chunkZ)) {
			if (!w.loadChunk(chunkX, chunkZ, false)) {
				return -1;
			}
		}

		for (int y = maxY; y >= 0; y--) {
			Block b = w.getBlockAt(x, y, z);
			int id = b.getTypeId();

			if (id == 8 || id == 9)
				return -1;

			boolean solid;
			solid = b.getRelative(BlockFace.UP).getType().isSolid();
			if ((id == 2 || id == 3) && !solid)
				return y;
		}

		return -1;
	}

	public int getTopLeafY(World w, int x, int z, int chunkX, int chunkZ) {
		return this.getTopLeafY(w, x, 96, z, chunkX, chunkZ);
	}

	public int getTopLeafY(World w, int x, int maxY, int z, int chunkX,
		int chunkZ)
	{
		if (!w.isChunkLoaded(chunkX, chunkZ)) {
			if (!w.loadChunk(chunkX, chunkZ, false)) {
				return -1;
			}
		}

		for (int y = maxY; y >= 0; y--) {
			int id = w.getBlockTypeIdAt(x, y, z);
			boolean top = (w.getBlockTypeIdAt(x, y + 1, z) == 0);

			if (id == 18 && top)
				return y;
		}

		return -1;
	}

	private Random getRandom(World w, long x, long z) {
		long seed = (x * 341873128712L + z * 132897987541L);
		seed = seed ^ w.getSeed();
		return new Random(seed);
	}
}
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
	import org.bukkit.block.Biome;
	import org.bukkit.generator.ChunkGenerator.BiomeGrid;
	import org.bukkit.World;
//* IMPORTS: PANDORA
	import org.pandora.EmptyBiomeGrid;
	import org.pandora.PandoraBiome;
//* IMPORTS: FJORDLANDS
	//* NOT NEEDED
//* IMPORTS: OTHER
	//* NOT NEEDED

public class MountainGenerator extends PandoraBiome
{
	public MountainGenerator()
	{
		this.minTemperature = -1.0F;
		this.maxTemperature = 101.0F;
		this.minHumidity = -1.0F;
		this.maxHumidity = 101.0F;
	}

	public short[] generateExtSections(World w, Random random, int xPos,
	int zPos, BiomeGrid biomes)
	{
		short[] result = new short[w.getMaxHeight()];

		int x = Math.abs(xPos % 16);
		int z = Math.abs(zPos % 16);

		int stone = 235;
		int bRock = 5;

		bRock += (int) getNoise(w, xPos, zPos, 1D, 1D, 2, 4D, 0.06D);

		biomes.setBiome(x, z, Biome.EXTREME_HILLS);

		double column = getNoise(w, xPos, zPos, 1D, 1D, 2, 32D,  0.06D);
		double height = getNoise(w, xPos, zPos, 1D, 1D, 2, 12D,  0.04D);
		double str    = getNoise(w, xPos, zPos, 1D, 1D, 2, 50D,  0.12D);
		double value  = getNoise(w, xPos, zPos, 1D, 1D, 2, 100D, 0.06D);

		height *= ((column / 4D) + 24D);
		double mod = (height * ((str + 50D) / 100D));
		double modifier = (mod * ((value / 200D) + 0.5D));

		int yMod = -1;
		double dist = -1D;
		double noise = getNoise(w, xPos, zPos, 1D, 1D, 2, 450D, 0.004D);
		noise += 100D;

		for (int y = 235; y >= 0; y--) {
			short id;
			int cy = y;
			int yCap = 200;

			id = this.getId(bRock, stone, y, result, noise,
				modifier, cy, yCap);

			result[y] = id;
		}

		biomes.setBiome(x, z, Biome.EXTREME_HILLS);

		return result;
	}

	public int adjustModifier(World w, int x, int y, int z, int yMod,
		double dist)
	{
		if (w == null)
			return y;

		if (dist < 0D || yMod <= 0)
			return y;

		int newY = (int) lerp(y, yMod, dist);
		return  y + (y - newY);
	}

	public short getId(int bedrock, int stone, int y, short[] result,
		double noise, double modifier, int yMod, int yCap)
	{
		short id = 0;

		if(y <= bedrock)
			id = 7;
		else if(y <= stone) {
			short temp;
			temp = result[y + 1];

			if(temp == 0)
				id = 2;
			else if(temp == 2 || temp == 9)
				id = 3;
			else
				id = 1;
		}

		id = checkColumn(id, yMod, noise, modifier, yCap);

		if(id == 0 && y <= 38)
			id = 9;

		return id;
	}

	public short checkColumn(short id, int y, double noise, double modifier,
		int cap)
	{
		double height = (235D - ((double) y)) + modifier;

		if ((noise < height) && (y > cap)) {
			double percent = (((double) y) - 200D) / 35D;
			double hTarget = ((((double) y) - 200D) / 4D) + height;
			double target = lerp(height, hTarget, percent);
			noise = lerp(noise, target, percent);
		}
		else if (noise >= height && y < 15)
			return id;
		else if (noise >= height && y < 30) {
			double target = ((noise - (noise - height)) - 1D);
			double hTarget = (15D - (((double) y) - 15D)) / 15D;
			noise = lerp(noise, target, hTarget);
		}


		if (noise >= height)
			return 0;

		return id;
	}

	double lerp(double start, double end, double percent) {
		return (start + (percent * (end - start)));
	}

	private Random getRandom(long x, long z) {
		long seed = (x * 341873128712L + z * 132897987541L) ^ 1575463L;
		return new Random(seed);
	}

	private double getNoise(World world, double x, double y, double z,
		int octaves, double frequency, double amplitude)
	{
		return getNoiseGenerator(world).noise(x, y, z, octaves,
			frequency, amplitude, true);
	}

}
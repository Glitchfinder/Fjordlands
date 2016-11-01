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
	import org.bukkit.Location;
	import org.bukkit.plugin.Plugin;
	import org.bukkit.World;
//* IMPORTS: PANDORA
	import org.pandora.PandoraWorldGenerator;
//* IMPORTS: FJORDLANDS
	//* NOT NEEDED
//* IMPORTS: OTHER
	//* NOT NEEDED

public class MountainOre extends PandoraWorldGenerator
{
	private int id;
	private double minWidth, maxWidth, minD, maxD;
	private double widthDiff, distanceDiff;

	public MountainOre(
		Plugin plugin,
		boolean notifyOnBlockChanges,
		int id,
		double minWidth,
		double maxWidth,
		double minDistance,
		double maxDistance)
	{
		super(plugin, notifyOnBlockChanges, true);

		this.id = id;
		this.minWidth = minWidth;
		this.maxWidth = maxWidth;
		this.minD = minDistance;
		this.maxD = maxDistance;

		this.widthDiff = maxWidth - minWidth;
		this.distanceDiff = maxDistance - minDistance;

		for (int data = 0; data < 16; data++) {
			addToBlacklist(1, (byte) data);
		}
	}

	public boolean generate(World world, Random random, int x, int y,
		int z)
	{
		// Use the range variables to generate an offset
		int xDiff = (int) ((random.nextDouble() * distanceDiff) + minD);
		int yDiff = (int) ((random.nextDouble() * distanceDiff) + minD);
		int zDiff = (int) ((random.nextDouble() * distanceDiff) + minD);

		// Randomly invert the offset directions
		if (random.nextBoolean())
			xDiff *= -1;
		if (random.nextBoolean())
			yDiff *= -1;
		if (random.nextBoolean())
			zDiff *= -1;

		// Get the offset coordinates
		int x2 = x + xDiff;
		int y2 = y + yDiff;
		int z2 = z + zDiff;

		// Find the lowest and highest values to use for looping
		int lowX  = Math.min(x, x2);
		int lowY  = Math.min(y, y2);
		int lowZ  = Math.min(z, z2);
		int highX = Math.max(x, x2);
		int highY = Math.max(y, y2);
		int highZ = Math.max(z, z2);

		// Calculate the size to use for the spheres
		double cWidth = (random.nextDouble() * widthDiff) + minWidth;
		int width = (int) Math.round(cWidth);

		// Create location variables for easier distance calculations
		Location b1 = new Location(world, x, y, z);
		Location b2 = new Location(world, x2, y2, z2);
		Location c1;

		// Determine the outer bounds of the looping
		int minX = lowX  - width;
		int minY = lowY  - width;
		int minZ = lowZ  - width;
		int maxX = highX + width;
		int maxY = highY + width;
		int maxZ = highZ + width;

		// Loop through the entire region
		for (int cx = minX; cx <= maxX; cx++) {
			for (int cz = minZ; cz <= maxZ; cz++) {
				// Skip if the block is invalid for some reason
				if (!isChunkValid(world, cx, cz))
					continue;

				for (int cy = minY; cy <= maxY; cy++) {
					// Get the location of the current block
					c1 = new Location(world, cx, cy, cz);

					// Check the block
					checkBlock(b1, b2, c1, cWidth);
				}
			}
		}

		// Attempt to place the blocks, and return the result
		return placeBlocks(b1, true);
	}

	private void checkBlock(Location b1, Location b2, Location c1,
		double width)
	{
		// Skip if the block is too close to the original point
		if (b1.distance(c1) < width)
			return;
		// Skip if the block is too far from the offset point
		else if (b2.distance(c1) > width)
			return;

		// Get the actual block
		Block block = c1.getBlock();

		// Skip if the block is not one this generator replaces
		if (!isInBlacklist(block))
			return;

		// Add this block to the list of blocks to place
		addBlock(b1, block, id, (byte) 0);
	}

	public boolean isChunkValid(World world, int x, int z) {
		x = x >> 4; // Chunk X
		z = z >> 4; // Chunk Z

		// If the chunk is not loaded, and does not exist
		if (!world.isChunkLoaded(x, z) && !world.loadChunk(x, z, false))
			return false;

		return true;
	}
}
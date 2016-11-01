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

package com.glitchkey.fjordlands;

//* IMPORTS: JDK/JRE
	//* NOT NEEDED
//* IMPORTS: BUKKIT
	import org.bukkit.generator.BlockPopulator;
	import org.bukkit.generator.ChunkGenerator;
	import org.bukkit.plugin.java.JavaPlugin;
	import org.bukkit.World;
	import org.bukkit.World.Environment;
	import org.bukkit.WorldCreator;
	import org.bukkit.WorldType;
//* IMPORTS: PANDORA
	import org.pandora.PandoraBiome;
	import org.pandora.PandoraBiomePopulator;
	import org.pandora.PandoraGenerator;
	import org.pandora.PandoraPopulator;
//* IMPORTS: FJORDLANDS
	import com.glitchkey.fjordlands.listeners.MountainListener;
	import com.glitchkey.fjordlands.listeners.MountainMobListener;
	import com.glitchkey.fjordlands.terrain.MountainGenerator;
	import com.glitchkey.fjordlands.terrain.MountainPopulator;
//* IMPORTS: OTHER
	//* NOT NEEDED

public class FjordlandsPlugin extends JavaPlugin
{
	public static FjordlandsPlugin plugin;
	public World world = null;
	public MountainListener listener;
	public MountainMobListener mobListener;

	public void onDisable() {
		this.listener.unregisterEvents();
		this.mobListener.unregisterEvents();
	}

	public void onEnable()
	{
		plugin = this;
		this.listener = new MountainListener(this);
		this.mobListener = new MountainMobListener(this);

		this.createWorld();
	}

	public void createWorld()
	{

		WorldCreator worldCreator = new WorldCreator("Fjordlands");
		worldCreator = worldCreator.environment(Environment.NORMAL);
		worldCreator = worldCreator.type(WorldType.NORMAL);
		PandoraGenerator generator = new PandoraGenerator();
		PandoraBiome glaciers = (PandoraBiome) (new MountainGenerator());
		generator.setDefaultBiome(glaciers);
		PandoraPopulator populator = new PandoraPopulator();
		PandoraBiomePopulator glacierPop = (PandoraBiomePopulator) (new MountainPopulator(this));
		populator.setDefaultBiome(glacierPop);
		generator.addPopulator((BlockPopulator) populator);
		worldCreator = worldCreator.generator((ChunkGenerator) generator);
		this.world = worldCreator.createWorld();
	}
}

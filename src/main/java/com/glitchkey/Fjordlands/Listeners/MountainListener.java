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

package com.glitchkey.fjordlands.listeners;

//* IMPORTS: JDK/JRE
	import java.util.ArrayList;
	import java.util.List;
	import java.util.Random;
//* IMPORTS: BUKKIT
	import org.bukkit.Bukkit;
	import org.bukkit.entity.LivingEntity;
	import org.bukkit.entity.Player;
	import org.bukkit.entity.Skeleton;
	import org.bukkit.entity.Zombie;
	import org.bukkit.event.entity.EntityDeathEvent;
	import org.bukkit.event.EventHandler;
	import org.bukkit.event.EventPriority;
	import org.bukkit.event.HandlerList;
	import org.bukkit.event.Listener;
	import org.bukkit.GameMode;
	import org.bukkit.inventory.EntityEquipment;
	import org.bukkit.inventory.ItemStack;
	import org.bukkit.inventory.meta.ItemMeta;
	import org.bukkit.Material;
//* IMPORTS: PANDORA
	//* NOT NEEDED
//* IMPORTS: FJORDLANDS
	import com.glitchkey.fjordlands.FjordlandsPlugin;
//* IMPORTS: OTHER
	//* NOT NEEDED

public final class MountainListener implements Listener {
	private final FjordlandsPlugin plugin;
	private Random rand;

	public MountainListener(final FjordlandsPlugin plugin) {
		this.plugin = plugin;
		registerEvents();
		rand = new Random();
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onCreatureDeath(final EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();

		if (entity.getWorld() != plugin.world)
			return;
		else if (entity.getKiller() != null) {
			Player p = entity.getKiller();
			if(p.getGameMode() == GameMode.CREATIVE)
				return;
		}

		switch (entity.getType()) {
			case ZOMBIE: zombieDeath(event, (Zombie) entity); break;
			case SKELETON:
				skeletonDeath(event, (Skeleton) entity);
				break;
		}

		List<ItemStack> toRemove = new ArrayList<ItemStack>();

		for (ItemStack drop : event.getDrops()) {
			if (drop.getAmount() <= 0)
				toRemove.add(drop);
		}

		event.getDrops().removeAll(toRemove);
	}

	private void zombieDeath(EntityDeathEvent event, Zombie entity) {
		ensureDrops(event, entity);
	}

	private void skeletonDeath(EntityDeathEvent event, Skeleton entity) {
		ensureDrops(event, entity);
	}

	private void ensureDrops(EntityDeathEvent event, LivingEntity entity) {
		EntityEquipment gear = entity.getEquipment();
		List<ItemStack> drops = event.getDrops();
		List<ItemStack> newDrops = new ArrayList<ItemStack>();
		newDrops.add(gear.getItemInHand());
		newDrops.add(gear.getHelmet());
		newDrops.add(gear.getChestplate());
		newDrops.add(gear.getLeggings());
		newDrops.add(gear.getBoots());

		Random rand = new Random();

		for (ItemStack equip : newDrops) {
			if (!drops.contains(equip)) {
				Material type = equip.getType();
				if (type.getMaxDurability() > 0)
					equip.setDurability((short) (rand.nextInt(type.getMaxDurability()) + 1));
				drops.add(equip);
			}
		}
	}

	private void addDrop(List<ItemStack> drops, Material type, String name,
		int min, int max)
	{
		int count = rand.nextInt((max + 1) - min) + min;
		ItemStack drop = new ItemStack(type, count);
		ItemMeta meta = drop.getItemMeta();
		meta.setDisplayName(name);
		drop.setItemMeta(meta);
		drops.add(drop);
	}

	public void registerEvents() {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public void unregisterEvents() {
		HandlerList.unregisterAll(this);
	}
}
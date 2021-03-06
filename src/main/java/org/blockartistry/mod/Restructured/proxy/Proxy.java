/*
 * This file is part of Restructured, licensed under the MIT License (MIT).
 *
 * Copyright (c) OreCruncher
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.blockartistry.mod.Restructured.proxy;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import org.blockartistry.mod.Restructured.ModOptions;
import org.blockartistry.mod.Restructured.assets.Assets;
import org.blockartistry.mod.Restructured.world.MobControl;
import org.blockartistry.mod.Restructured.world.TerrainEventBusHandler;
import org.blockartistry.mod.Restructured.world.themes.BlockThemes;
import org.blockartistry.mod.Restructured.VersionCheck;

public class Proxy {

	public void preInit(final FMLPreInitializationEvent event) {
		// Register early to give the background process a good amount
		// of time to get the mod version data
		VersionCheck.register();
	}

	public void init(final FMLInitializationEvent event) {
		// Hook to patch up any village generation code
		TerrainEventBusHandler.initialize();
	}

	public void postInit(final FMLPostInitializationEvent event) {
		
		Assets.initialize();
		
		// Patch up the village biome list with the configured
		// settings.  We need to create a new map because
		// the one that is there is immutable.
		final int[] additional = ModOptions.getAdditionalVillageBiomes();
		if(additional.length > 0) {
			final List<BiomeGenBase> newList = new ArrayList<BiomeGenBase>();
			newList.addAll(MapGenVillage.villageSpawnBiomes);
			
			for(final int biomeId: additional) {
				final BiomeGenBase biome = BiomeGenBase.getBiome(biomeId);
				if(biome != null)
					newList.add(biome);
			}
			MapGenVillage.villageSpawnBiomes = newList;
		}
		
		// Initialize themes
		if(ModOptions.getEnableTheming())
			BlockThemes.initialize();

		MobControl.initialize();
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {

	}
}

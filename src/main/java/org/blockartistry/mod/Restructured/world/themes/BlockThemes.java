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

package org.blockartistry.mod.Restructured.world.themes;

import java.util.HashMap;
import java.util.Map;

import org.blockartistry.mod.Restructured.util.SelectedBlock;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenMutated;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.BiomeEvent;

public final class BlockThemes {

	private static final HashMap<BiomeGenBase, HashMap<Block, SelectedBlock>> themes = new HashMap<BiomeGenBase, HashMap<Block, SelectedBlock>>();
	private static final int KEEP_META = -1;
	private static final SelectedBlock AIR = new SelectedBlock(Blocks.air);

	protected static void register(final BiomeGenBase biome, final HashMap<Block, SelectedBlock> theme) {
		// Register biome
		themes.put(biome, theme);

		// Check for mutation
		final BiomeGenBase mutation = BiomeGenBase.getBiome(biome.biomeID + 128);
		if (mutation instanceof BiomeGenMutated)
			themes.put(mutation, theme);
	}

	protected static Block biomeBlockReplace(final BiomeGenBase biome, final SelectedBlock block) {
		final Map<Block, SelectedBlock> replacements = themes.get(biome);
		if(replacements == null)
			return block.getBlock();
		final SelectedBlock code = replacements.get(block.getBlock());
		return code != null ? code.getBlock() : block.getBlock();
	}

	protected static int biomeMetaReplace(final BiomeGenBase biome, final SelectedBlock block) {
		final Map<Block, SelectedBlock> replacements = themes.get(biome);
		if(replacements == null)
			return block.getMeta();

		final SelectedBlock code = replacements.get(block.getBlock());
		if(code == null || code.getMeta() == KEEP_META)
			return block.getMeta();
		
		int replace = code.getMeta();

		// Preserve slab orientation
		if (block.isSlab())
			replace |= (block.getMeta() & 8);
		// Preserve log orientation
		else if (block.isLog())
			replace |= (block.getMeta() & 12);

		return replace;
	}

	protected static SelectedBlock scrubEggs(final SelectedBlock block) {

		if (block.getBlock() != Blocks.monster_egg)
			return block;

		Block b = null;
		int meta = 0;
		switch (block.getMeta()) {
		case 0:
			b = Blocks.stone;
			meta = 0;
			break;
		case 1:
			b = Blocks.cobblestone;
			meta = 0;
			break;
		case 2:
			b = Blocks.stonebrick;
			meta = 0;
			break;
		case 3:
			b = Blocks.stonebrick;
			meta = 1;
			break;
		case 4:
			b = Blocks.stonebrick;
			meta = 2;
		case 5:
			b = Blocks.stonebrick;
			meta = 3;
			break;
		default:
			;
		}

		return new SelectedBlock(b, meta);
	}

	public static SelectedBlock findReplacement(final BiomeGenBase biome, SelectedBlock block, final boolean scrubEggs) {

		if (scrubEggs)
			block = scrubEggs(block);

		Block theBlock = block.getBlock();
		int meta = block.getMeta();

		final BiomeEvent.GetVillageBlockID event1 = new BiomeEvent.GetVillageBlockID(biome, theBlock, meta);
		MinecraftForge.TERRAIN_GEN_BUS.post(event1);
		if (event1.getResult() == Result.DENY)
			theBlock = event1.replacement;

		final BiomeEvent.GetVillageBlockMeta event2 = new BiomeEvent.GetVillageBlockMeta(biome, block.getBlock(),
				block.getMeta());
		MinecraftForge.TERRAIN_GEN_BUS.post(event2);
		if (event2.getResult() == Result.DENY)
			meta = event2.replacement;

		return new SelectedBlock(theBlock, meta);
	}

	private static Block findReplacementBlock(final BiomeGenBase biome, SelectedBlock block, final boolean scrubEggs) {

		if (scrubEggs)
			block = scrubEggs(block);

		return biomeBlockReplace(biome, block);
	}

	private static int findReplacementMeta(final BiomeGenBase biome, SelectedBlock block, final boolean scrubEggs) {

		if (scrubEggs)
			block = scrubEggs(block);

		return biomeMetaReplace(biome, block);
	}

	public static void initialize() {
		
		// Beaches
		HashMap<Block, SelectedBlock> mappings = new HashMap<Block, SelectedBlock>();
		mappings.put(Blocks.dirt, new SelectedBlock(Blocks.sand, 0));
		mappings.put(Blocks.grass, new SelectedBlock(Blocks.sand, 0));
		mappings.put(Blocks.red_flower, new SelectedBlock(Blocks.deadbush, 0));
		mappings.put(Blocks.yellow_flower, new SelectedBlock(Blocks.deadbush, 0));
		mappings.put(Blocks.red_mushroom, AIR);
		mappings.put(Blocks.brown_mushroom, AIR);
		mappings.put(Blocks.double_plant, AIR);
		mappings.put(Blocks.tallgrass, AIR);
		register(BiomeGenBase.beach, mappings);
		register(BiomeGenBase.coldBeach, mappings);

		mappings = new HashMap<Block, SelectedBlock>();
		mappings.put(Blocks.dirt, new SelectedBlock(Blocks.stone, 0));
		mappings.put(Blocks.grass, new SelectedBlock(Blocks.stone, 0));
		mappings.put(Blocks.red_flower, new SelectedBlock(Blocks.red_mushroom, 0));
		mappings.put(Blocks.yellow_flower, new SelectedBlock(Blocks.brown_mushroom, 0));
		mappings.put(Blocks.double_plant, AIR);
		mappings.put(Blocks.tallgrass, AIR);
		register(BiomeGenBase.stoneBeach, mappings);

		// Birch Forest
		mappings = new HashMap<Block, SelectedBlock>();
		mappings.put(Blocks.log, new SelectedBlock(Blocks.log, 2));
		mappings.put(Blocks.log2, new SelectedBlock(Blocks.log, 2));
		mappings.put(Blocks.planks, new SelectedBlock(Blocks.planks, 2));
		mappings.put(Blocks.spruce_stairs, new SelectedBlock(Blocks.birch_stairs, KEEP_META));
		mappings.put(Blocks.oak_stairs, new SelectedBlock(Blocks.birch_stairs, KEEP_META));
		mappings.put(Blocks.dark_oak_stairs, new SelectedBlock(Blocks.birch_stairs, KEEP_META));
		mappings.put(Blocks.jungle_stairs, new SelectedBlock(Blocks.birch_stairs, KEEP_META));
		mappings.put(Blocks.acacia_stairs, new SelectedBlock(Blocks.birch_stairs, KEEP_META));
		mappings.put(Blocks.wooden_slab, new SelectedBlock(Blocks.wooden_slab, 2));
		mappings.put(Blocks.double_wooden_slab, new SelectedBlock(Blocks.double_wooden_slab, 2));
		register(BiomeGenBase.birchForest, mappings);
		register(BiomeGenBase.birchForestHills, mappings);

		// Desert
		mappings = new HashMap<Block, SelectedBlock>();
		mappings.put(Blocks.log, new SelectedBlock(Blocks.sandstone, 0));
		mappings.put(Blocks.log2, new SelectedBlock(Blocks.sandstone, 0));
		mappings.put(Blocks.cobblestone, new SelectedBlock(Blocks.sandstone, 0));
		mappings.put(Blocks.planks, new SelectedBlock(Blocks.sandstone, 2));
		mappings.put(Blocks.oak_stairs, new SelectedBlock(Blocks.sandstone_stairs, KEEP_META));
		mappings.put(Blocks.stone_stairs, new SelectedBlock(Blocks.sandstone_stairs, KEEP_META));
		mappings.put(Blocks.spruce_stairs, new SelectedBlock(Blocks.sandstone_stairs, KEEP_META));
		mappings.put(Blocks.birch_stairs, new SelectedBlock(Blocks.sandstone_stairs, KEEP_META));
		mappings.put(Blocks.dark_oak_stairs, new SelectedBlock(Blocks.sandstone_stairs, KEEP_META));
		mappings.put(Blocks.acacia_stairs, new SelectedBlock(Blocks.sandstone_stairs, KEEP_META));
		mappings.put(Blocks.gravel, new SelectedBlock(Blocks.sandstone, KEEP_META));
		mappings.put(Blocks.wooden_slab, new SelectedBlock(Blocks.stone_slab, 1));
		mappings.put(Blocks.stone_slab, new SelectedBlock(Blocks.stone_slab, 1));
		mappings.put(Blocks.double_wooden_slab, new SelectedBlock(Blocks.sandstone, 0));
		mappings.put(Blocks.double_stone_slab, new SelectedBlock(Blocks.sandstone, 0));
		mappings.put(Blocks.dirt, new SelectedBlock(Blocks.sand, 0));
		mappings.put(Blocks.grass, new SelectedBlock(Blocks.sand, 0));
		mappings.put(Blocks.red_flower, new SelectedBlock(Blocks.deadbush, 0));
		mappings.put(Blocks.yellow_flower, new SelectedBlock(Blocks.deadbush, 0));
		mappings.put(Blocks.red_mushroom, AIR);
		mappings.put(Blocks.brown_mushroom, AIR);
		mappings.put(Blocks.double_plant, AIR);
		mappings.put(Blocks.tallgrass, AIR);
		register(BiomeGenBase.desert, mappings);
		register(BiomeGenBase.desertHills, mappings);

		// Forest
		mappings = new HashMap<Block, SelectedBlock>();
		mappings.put(Blocks.log, new SelectedBlock(Blocks.log, 0));
		mappings.put(Blocks.log2, new SelectedBlock(Blocks.log, 0));
		mappings.put(Blocks.planks, new SelectedBlock(Blocks.planks, 0));
		mappings.put(Blocks.spruce_stairs, new SelectedBlock(Blocks.oak_stairs, KEEP_META));
		mappings.put(Blocks.birch_stairs, new SelectedBlock(Blocks.oak_stairs, KEEP_META));
		mappings.put(Blocks.dark_oak_stairs, new SelectedBlock(Blocks.oak_stairs, KEEP_META));
		mappings.put(Blocks.jungle_stairs, new SelectedBlock(Blocks.oak_stairs, KEEP_META));
		mappings.put(Blocks.acacia_stairs, new SelectedBlock(Blocks.oak_stairs, KEEP_META));
		mappings.put(Blocks.wooden_slab, new SelectedBlock(Blocks.wooden_slab, 0));
		mappings.put(Blocks.double_wooden_slab, new SelectedBlock(Blocks.double_wooden_slab, 0));
		register(BiomeGenBase.forest, mappings);
		register(BiomeGenBase.forestHills, mappings);

		// Jungle
		mappings = new HashMap<Block, SelectedBlock>();
		mappings.put(Blocks.log, new SelectedBlock(Blocks.log, 3));
		mappings.put(Blocks.log2, new SelectedBlock(Blocks.log, 3));
		mappings.put(Blocks.planks, new SelectedBlock(Blocks.planks, 3));
		mappings.put(Blocks.spruce_stairs, new SelectedBlock(Blocks.jungle_stairs, KEEP_META));
		mappings.put(Blocks.oak_stairs, new SelectedBlock(Blocks.jungle_stairs, KEEP_META));
		mappings.put(Blocks.dark_oak_stairs, new SelectedBlock(Blocks.jungle_stairs, KEEP_META));
		mappings.put(Blocks.birch_stairs, new SelectedBlock(Blocks.jungle_stairs, KEEP_META));
		mappings.put(Blocks.acacia_stairs, new SelectedBlock(Blocks.jungle_stairs, KEEP_META));
		mappings.put(Blocks.wooden_slab, new SelectedBlock(Blocks.wooden_slab, 3));
		mappings.put(Blocks.double_wooden_slab, new SelectedBlock(Blocks.double_wooden_slab, 3));
		register(BiomeGenBase.jungle, mappings);
		register(BiomeGenBase.jungleEdge, mappings);
		register(BiomeGenBase.jungleHills, mappings);
		
		// Roofed Forest (Dark Oak)
		mappings = new HashMap<Block, SelectedBlock>();
		mappings.put(Blocks.log, new SelectedBlock(Blocks.log2, 1));
		mappings.put(Blocks.log2, new SelectedBlock(Blocks.log2, 1));
		mappings.put(Blocks.planks, new SelectedBlock(Blocks.planks, 5));
		mappings.put(Blocks.spruce_stairs, new SelectedBlock(Blocks.dark_oak_stairs, KEEP_META));
		mappings.put(Blocks.oak_stairs, new SelectedBlock(Blocks.dark_oak_stairs, KEEP_META));
		mappings.put(Blocks.birch_stairs, new SelectedBlock(Blocks.dark_oak_stairs, KEEP_META));
		mappings.put(Blocks.jungle_stairs, new SelectedBlock(Blocks.dark_oak_stairs, KEEP_META));
		mappings.put(Blocks.acacia_stairs, new SelectedBlock(Blocks.dark_oak_stairs, KEEP_META));
		mappings.put(Blocks.wooden_slab, new SelectedBlock(Blocks.wooden_slab, 5));
		mappings.put(Blocks.double_wooden_slab, new SelectedBlock(Blocks.double_wooden_slab, 5));
		register(BiomeGenBase.roofedForest, mappings);

		// Savanna
		mappings = new HashMap<Block, SelectedBlock>();
		mappings.put(Blocks.log, new SelectedBlock(Blocks.log2, 0));
		mappings.put(Blocks.log2, new SelectedBlock(Blocks.log2, 0));
		mappings.put(Blocks.planks, new SelectedBlock(Blocks.planks, 4));
		mappings.put(Blocks.spruce_stairs, new SelectedBlock(Blocks.acacia_stairs, KEEP_META));
		mappings.put(Blocks.oak_stairs, new SelectedBlock(Blocks.acacia_stairs, KEEP_META));
		mappings.put(Blocks.dark_oak_stairs, new SelectedBlock(Blocks.acacia_stairs, KEEP_META));
		mappings.put(Blocks.jungle_stairs, new SelectedBlock(Blocks.acacia_stairs, KEEP_META));
		mappings.put(Blocks.birch_stairs, new SelectedBlock(Blocks.acacia_stairs, KEEP_META));
		mappings.put(Blocks.wooden_slab, new SelectedBlock(Blocks.wooden_slab, 4));
		mappings.put(Blocks.double_wooden_slab, new SelectedBlock(Blocks.double_wooden_slab, 4));
		register(BiomeGenBase.savanna, mappings);
		register(BiomeGenBase.savannaPlateau, mappings);

		// Taiga
		mappings = new HashMap<Block, SelectedBlock>();
		mappings.put(Blocks.log, new SelectedBlock(Blocks.log, 1));
		mappings.put(Blocks.log2, new SelectedBlock(Blocks.log, 1));
		mappings.put(Blocks.planks, new SelectedBlock(Blocks.planks, 1));
		mappings.put(Blocks.oak_stairs, new SelectedBlock(Blocks.spruce_stairs, KEEP_META));
		mappings.put(Blocks.birch_stairs, new SelectedBlock(Blocks.spruce_stairs, KEEP_META));
		mappings.put(Blocks.dark_oak_stairs, new SelectedBlock(Blocks.spruce_stairs, KEEP_META));
		mappings.put(Blocks.jungle_stairs, new SelectedBlock(Blocks.spruce_stairs, KEEP_META));
		mappings.put(Blocks.acacia_stairs, new SelectedBlock(Blocks.spruce_stairs, KEEP_META));
		mappings.put(Blocks.wooden_slab, new SelectedBlock(Blocks.wooden_slab, 1));
		mappings.put(Blocks.double_wooden_slab, new SelectedBlock(Blocks.double_wooden_slab, 1));
		register(BiomeGenBase.coldTaiga, mappings);
		register(BiomeGenBase.coldTaigaHills, mappings);
		register(BiomeGenBase.megaTaiga, mappings);
		register(BiomeGenBase.megaTaigaHills, mappings);
		register(BiomeGenBase.taiga, mappings);
		register(BiomeGenBase.taigaHills, mappings);

		// Mushroom
		mappings = new HashMap<Block, SelectedBlock>();
		mappings.put(Blocks.grass, new SelectedBlock(Blocks.mycelium));
		mappings.put(Blocks.log, new SelectedBlock(Blocks.red_mushroom_block));
		mappings.put(Blocks.log2, new SelectedBlock(Blocks.red_mushroom_block));
		mappings.put(Blocks.planks, new SelectedBlock(Blocks.brown_mushroom_block));
		mappings.put(Blocks.red_flower, new SelectedBlock(Blocks.red_mushroom));
		mappings.put(Blocks.yellow_flower, new SelectedBlock(Blocks.brown_mushroom, 0));
		mappings.put(Blocks.double_plant, AIR);
		mappings.put(Blocks.tallgrass, AIR);
		register(BiomeGenBase.mushroomIsland, mappings);
		register(BiomeGenBase.mushroomIslandShore, mappings);

		// Hook for block replacement
		MinecraftForge.TERRAIN_GEN_BUS.register(new BlockThemes());
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void blockReplaceEvent(final BiomeEvent.GetVillageBlockID event) {
		if(event.getResult() == Result.DENY)
			return;
		final Block replace = findReplacementBlock(event.biome, new SelectedBlock(event.original, event.type), true);
		if(replace != event.original) {
			event.replacement = replace;
			event.setResult(Result.DENY);
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOW)
	public void blockMetaReplaceEvent(final BiomeEvent.GetVillageBlockMeta event) {
		if(event.getResult() == Result.DENY)
			return;
		final int replace = findReplacementMeta(event.biome, new SelectedBlock(event.original, event.type), true);
		if(replace != event.type) {
			event.replacement = replace;
			event.setResult(Result.DENY);
		}
	}
}

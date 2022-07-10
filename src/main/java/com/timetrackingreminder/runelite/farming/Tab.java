package com.timetrackingreminder.runelite.farming;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;

@RequiredArgsConstructor
@Getter
public enum Tab
{
	OVERVIEW("Overview", ItemID.OLD_NOTES),
	CLOCK("Timers & Stopwatches", ItemID.WATCH),
	BIRD_HOUSE("Bird Houses", ItemID.OAK_BIRD_HOUSE),
	ALLOTMENT("Allotment Patches", ItemID.CABBAGE),
	FLOWER("Flower Patches", ItemID.RED_FLOWERS),
	HERB("Herb Patches", ItemID.GRIMY_RANARR_WEED),
	TREE("Tree Patches", ItemID.YEW_LOGS),
	FRUIT_TREE("Fruit Tree Patches", ItemID.PINEAPPLE),
	HOPS("Hops Patches", ItemID.BARLEY),
	BUSH("Bush Patches", ItemID.POISON_IVY_BERRIES),
	GRAPE("Grape Patches", ItemID.GRAPES),
	SPECIAL("Special Patches", ItemID.MUSHROOM),
	GIANT_COMPOST("Giant Compost Bin", ItemID.ULTRACOMPOST),
	SEAWEED("Seaweed", ItemID.GIANT_SEAWEED),
	CALQUAT("Calquat Patch", ItemID.CALQUAT_FRUIT),
	HARDWOOD("Hardwood Patches", ItemID.TEAK_LOGS),
  REDWOOD("Redwood Patch", ItemID.REDWOOD_LOGS),
	CACTUS("Cactus Patch", ItemID.POTATO_CACTUS),
	HESPORI("Hespori Patch", ItemID.TANGLEROOT),
	TIME_OFFSET("Farming Tick Offset", ItemID.WATERING_CAN);

	public static final Tab[] FARMING_TABS = {HERB, TREE, FRUIT_TREE, SPECIAL, FLOWER, ALLOTMENT, BUSH, GRAPE, HOPS};

	private final String name;
	private final int itemID;
}

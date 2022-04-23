package com.timetrackingreminder;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.timetracking.SummaryState;
import net.runelite.client.plugins.timetracking.Tab;
import net.runelite.client.plugins.timetracking.farming.FarmingTracker;
import net.runelite.client.plugins.timetracking.hunter.BirdHouseTracker;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.timetracking.TimeTrackingPlugin;

import java.lang.reflect.Field;

@Slf4j
@PluginDescriptor(
        name = "Time Tracking Reminder",
        description = "Extend the \"Time Tracking\" plugin to show an infobox when bird houses or farming patches are ready."
)
public class TimeTrackingReminderPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private TimeTrackingReminderConfig config;

    @Inject
    private ItemManager itemManager;

    @Inject
    private PluginManager pluginManager;

    @Inject
    private InfoBoxManager infoBoxManager;

    private BirdHouseTracker birdHouseTracker;
    private FarmingTracker farmingTracker;

    private TimeTrackingReminderGroup[] reminderGroups;

    @Provides
    TimeTrackingReminderConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(TimeTrackingReminderConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        log.info("Time Tracking Reminder started!");
        injectTimeTrackingPluginFields();
        initializeReminderGroups();
    }

    private void injectTimeTrackingPluginFields() {
        TimeTrackingPlugin timeTrackingPlugin = null;

        for (Plugin plugin : pluginManager.getPlugins()) {
            if (plugin.getName().equals("Time Tracking")) {
                timeTrackingPlugin = (TimeTrackingPlugin) plugin;
            }
        }

        if (timeTrackingPlugin == null) {
            String message = "[Time Tracking Reminder] Could not find \"Time Tracking\" plugin. Maybe it's not enabled?";
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", message, null);
            return;
        }

        try {
            Field field = timeTrackingPlugin.getClass().getDeclaredField("birdHouseTracker");
            field.setAccessible(true);
            birdHouseTracker = (BirdHouseTracker) field.get(timeTrackingPlugin);
            log.debug("Injected 'birdHouseTracker' via reflection");
        } catch (NoSuchFieldException e) {
            log.error("Could not find field 'birdHouseTracker' via reflection");
        } catch (IllegalAccessException e) {
            log.error("Could not access field 'birdHouseTracker' via reflection");
        }

        try {
            Field field = timeTrackingPlugin.getClass().getDeclaredField("farmingTracker");
            field.setAccessible(true);
            farmingTracker = (FarmingTracker) field.get(timeTrackingPlugin);
            log.debug("Injected 'farmingTracker' via reflection");
        } catch (NoSuchFieldException e) {
            log.error("Could not find field 'farmingTracker' via reflection");
        } catch (IllegalAccessException e) {
            log.error("Could not access field 'farmingTracker' via reflection");
        }
    }

    private void initializeReminderGroups() {
        reminderGroups = new TimeTrackingReminderGroup[]{
                new TimeTrackingReminderGroup(
                        this,
                        infoBoxManager,
                        itemManager,
                        "Bird Houses",
                        21515, // Oak bird house
                        () -> config.birdHouses() && birdHouseTracker.getSummary() != SummaryState.IN_PROGRESS
                ),
                new TimeTrackingReminderGroup(
                        this,
                        infoBoxManager,
                        itemManager,
                        "Herb Patches",
                        207, // Grimy ranarr weed
                        () -> config.herbPatches() && farmingTracker.getSummary(Tab.HERB) != SummaryState.IN_PROGRESS
                ),
                new TimeTrackingReminderGroup(
                        this,
                        infoBoxManager,
                        itemManager,
                        "Tree Patches",
                        1515, // Yew logs
                        () -> config.treePatches() && farmingTracker.getSummary(Tab.TREE) != SummaryState.IN_PROGRESS
                ),
                new TimeTrackingReminderGroup(
                        this,
                        infoBoxManager,
                        itemManager,
                        "Fruit Tree Patches",
                        2114, // Pineapple
                        () -> config.fruitTreePatches() && farmingTracker.getSummary(Tab.FRUIT_TREE) != SummaryState.IN_PROGRESS
                )
        };
    }

    @Override
    protected void shutDown() throws Exception {
        log.info("Time Tracking Reminder stopped!");

        for (TimeTrackingReminderGroup reminderGroup : reminderGroups) {
            reminderGroup.hideInfoBox();
        }
    }

    @Subscribe
    public void onGameTick(GameTick t) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        for (TimeTrackingReminderGroup reminderGroup : reminderGroups) {
            reminderGroup.onGameTick();
        }
    }
}

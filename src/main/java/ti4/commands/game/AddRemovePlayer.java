package ti4.commands.game;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import ti4.helpers.Constants;
import ti4.map.Map;
import ti4.map.MapManager;
import ti4.map.MapSaveLoadManager;
import ti4.message.MessageHelper;

abstract public class AddRemovePlayer extends GameSubcommandData {

    public AddRemovePlayer(@NotNull String name, @NotNull String description) {
        super(name, description);
        addOptions(new OptionData(OptionType.USER, Constants.PLAYER1, "Player @playerName").setRequired(true));
        addOptions(new OptionData(OptionType.USER, Constants.PLAYER2, "Player @playerName"));
        addOptions(new OptionData(OptionType.USER, Constants.PLAYER3, "Player @playerName"));
        addOptions(new OptionData(OptionType.USER, Constants.PLAYER4, "Player @playerName"));
        addOptions(new OptionData(OptionType.USER, Constants.PLAYER5, "Player @playerName"));
        addOptions(new OptionData(OptionType.USER, Constants.PLAYER6, "Player @playerName"));
        addOptions(new OptionData(OptionType.USER, Constants.PLAYER7, "Player @playerName"));
        addOptions(new OptionData(OptionType.USER, Constants.PLAYER8, "Player @playerName"));
        addOptions(new OptionData(OptionType.STRING, Constants.GAME_NAME, "Game name"));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping gameOption = event.getOption(Constants.GAME_NAME);
        User callerUser = event.getUser();
        String mapName;
        if (gameOption != null) {
            mapName = event.getOptions().get(0).getAsString();
            if (!MapManager.getInstance().getMapList().containsKey(mapName)) {
                MessageHelper.sendMessageToChannel(event.getChannel(), "Game with such name does not exist, use `/help list_games`");
                return;
            }
        } else {
            Map userActiveMap = MapManager.getInstance().getUserActiveMap(callerUser.getId());
            if (userActiveMap == null){
                MessageHelper.sendMessageToChannel(event.getChannel(), "Specify game or set active Game");
                return;
            }
            mapName = userActiveMap.getName();
        }
        MapManager mapManager = MapManager.getInstance();
        Map activeMap = mapManager.getMap(mapName);
        if (!activeMap.isMapOpen()) {
            MessageHelper.sendMessageToChannel(event.getChannel(), "Game is not open. Can only add/remove players when game status is 'open'.");
            return;
        }

        User user = event.getUser();
        action(event, activeMap, user);
        MapSaveLoadManager.saveMap(activeMap, event);
        MessageHelper.replyToMessage(event, getResponseMessage(activeMap, user));
    }
    abstract protected String getResponseMessage(Map activeMap, User user);

    abstract protected void action(SlashCommandInteractionEvent event, Map activeMap, User user);
}
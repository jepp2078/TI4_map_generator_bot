package ti4.commands.planet;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ti4.helpers.Constants;
import ti4.helpers.Helper;
import ti4.map.Game;
import ti4.map.Player;

public class PlanetExhaustAll extends PlanetSubcommandData {
    public PlanetExhaustAll() {
        super(Constants.PLANET_EXHAUST_ALL, "Exhaust All Planets");
        addOptions(new OptionData(OptionType.USER, Constants.PLAYER, "Player for which you set stats"));
        addOptions(new OptionData(OptionType.STRING, Constants.FACTION_COLOR, "Faction or Color for which you set stats").setAutoComplete(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Game activeGame = getActiveGame();
        Player player = activeGame.getPlayer(getUser().getId());
        player = Helper.getGamePlayer(activeGame, player, event, null);
        player = Helper.getPlayer(activeGame, player, event);

        if (player == null) {
            sendMessage("Player could not be found");
            return;
        }

        for (String planet : player.getPlanets()) {
            player.exhaustPlanet(planet);
        }
        sendMessage(player.getRepresentation() + " exhausted all planets.");
    }
}

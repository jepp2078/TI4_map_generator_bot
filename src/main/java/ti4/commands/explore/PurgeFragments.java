package ti4.commands.explore;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ti4.helpers.ButtonHelper;
import ti4.helpers.Constants;
import ti4.helpers.Helper;
import ti4.map.Game;
import ti4.map.Player;
import ti4.generator.Mapper;

public class PurgeFragments extends ExploreSubcommandData {

	public PurgeFragments() {
		super(Constants.PURGE_FRAGMENTS, "Purge a number of relic fragments (for example, to gain a relic. Can use unknown fragments)");
		addOptions(typeOption.setRequired(true),
				new OptionData(OptionType.INTEGER, Constants.COUNT, "Number of fragments to purge (default 3, use this for NRA or black market forgery)"));
		addOptions(new OptionData(OptionType.STRING, Constants.FACTION_COLOR, "Faction or Color").setAutoComplete(true));
		addOptions(new OptionData(OptionType.BOOLEAN, Constants.ALSO_DRAW_RELIC, "'true' to also draw a relic"));
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		Game activeGame = getActiveGame();
		Player activePlayer = activeGame.getPlayer(getUser().getId());
		activePlayer = Helper.getGamePlayer(activeGame, activePlayer, event, null);
		activePlayer = Helper.getPlayer(activeGame, activePlayer, event);
		if (activePlayer == null){
			sendMessage("Player not found in game.");
			return;
		}
		String color = event.getOption(Constants.TRAIT).getAsString();
		OptionMapping countOption = event.getOption(Constants.COUNT);
		int count = 3;
		if (countOption != null) {
			count = countOption.getAsInt();
		}
		List<String> fragmentsToPurge = new ArrayList<>();
		List<String> unknowns = new ArrayList<>();
		ArrayList<String> playerFragments = activePlayer.getFragments();
		for (String id : playerFragments) {
			String[] cardInfo = Mapper.getExploreRepresentation(id).split(";");
			if (cardInfo[1].equalsIgnoreCase(color)) {
				fragmentsToPurge.add(id);
			} else if (cardInfo[1].equalsIgnoreCase(Constants.FRONTIER)) {
				unknowns.add(id);
			}
		}

		while (fragmentsToPurge.size() > count) {
			fragmentsToPurge.remove(0);
		}

		while (fragmentsToPurge.size() < count) {
			if (unknowns.size() == 0) {
				sendMessage("Not enough fragments. Note that default count is 3.");
				return;
			}
			fragmentsToPurge.add(unknowns.remove(0));
		}

		for (String id : fragmentsToPurge) {
			activePlayer.removeFragment(id);
			activeGame.setNumberOfPurgedFragments(activeGame.getNumberOfPurgedFragments() + 1);
		}

		Player lanefirPlayer = activeGame.getPlayers().values().stream().filter(
				p -> p.getLeaderIDs().contains("lanefircommander") && !p.hasLeaderUnlocked("lanefircommander")
			).findFirst().orElse(null);

		if (lanefirPlayer != null){
			ButtonHelper.commanderUnlockCheck(activePlayer, activeGame, "lanefir", event);
		}
		String message = activePlayer.getRepresentation() + " purged fragments: " + fragmentsToPurge;
		sendMessage(message);
		
		if(activePlayer.hasTech("dslaner")){
			activePlayer.setAtsCount(activePlayer.getAtsCount()+1);
			sendMessage(activePlayer.getRepresentation() + " Put 1 commodity on ATS Armaments");
		}

		OptionMapping drawRelicOption = event.getOption(Constants.ALSO_DRAW_RELIC);
		if (drawRelicOption != null) {
			if (drawRelicOption.getAsBoolean()) {
				DrawRelic.drawRelicAndNotify(activePlayer, event, activeGame);
			}
		}
	}

}

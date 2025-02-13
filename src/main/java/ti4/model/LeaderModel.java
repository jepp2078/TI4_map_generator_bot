package ti4.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.apache.commons.lang3.StringUtils;
import ti4.generator.Mapper;
import ti4.helpers.Constants;
import ti4.helpers.Emojis;
import ti4.model.Source.ComponentSource;

@Data
public class LeaderModel implements ModelInterface, EmbeddableModel {
    private String ID;
    private String type;
    private String faction;
    private String name;
    private String shortName;
    private String title;
    private String abilityName;
    private String abilityWindow;
    private String abilityText;
    private String unlockCondition;
    private String flavourText;
    private String emoji;
    private ComponentSource source;
    private List<String> searchTags = new ArrayList<>();

    @Override
    public boolean isValid() {
        return ID != null
            && type != null
            && faction != null
            && name != null
            && title != null
            && abilityWindow != null
            && abilityText != null
            && unlockCondition != null
            && source != null;
    }

    @Override
    public String getAlias() {
        return getID();
    }

    public String getShortName() {
        return Optional.ofNullable(shortName).orElse(getName());
    }

    public String getLeaderEmoji() {
        return Optional.ofNullable(getEmoji()).orElse(Emojis.getEmojiFromDiscord(getID()));
    }

    public Optional<String> getAbilityName() {
        return Optional.ofNullable(abilityName);
    }

    public Optional<String> getFlavourText() {
        return Optional.ofNullable(flavourText);
    }

    public String getRepresentation(boolean includeTitle, boolean includeAbility, boolean includeUnlockCondition) {
        StringBuilder representation = new StringBuilder();
        representation.append(getLeaderEmoji()).append(" **").append(getName()).append("**");

        if (includeTitle) representation.append(": ").append(getTitle()); //add title
        if (includeAbility && Constants.HERO.equals(getType())) representation.append(" - ").append("__**").append(getAbilityName()).append("**__"); //add hero ability name
        if (includeAbility) representation.append(" - *").append(getAbilityWindow()).append("* ").append(getAbilityText()); //add ability
        if (includeUnlockCondition) representation.append(" *Unlock:* ").append(getUnlockCondition());

        return representation.toString();
    }

    public MessageEmbed getRepresentationEmbed() {
        return getRepresentationEmbed(false, true, false, false);
    }

    public MessageEmbed getRepresentationEmbed(boolean includeID, boolean includeFactionType, boolean showUnlockConditions, boolean includeFlavourText) {
        EmbedBuilder eb = new EmbedBuilder();

        //TITLE
        String title = getLeaderEmoji() +
            " __**" + getName() + "**__" + " - " + getTitle() +
            getSource().emoji();
        eb.setTitle(title);

        Emoji emoji = Emoji.fromFormatted(getLeaderEmoji());
        if (emoji instanceof CustomEmoji customEmoji) {
            eb.setThumbnail(customEmoji.getImageUrl());
        }

        //DESCRIPTION
        StringBuilder description = new StringBuilder();
        if (includeFactionType) {
            FactionModel faction = Mapper.getFaction(getFaction());
            if (faction != null) {
                description.append(Emojis.getFactionIconFromDiscord(faction.getAlias())).append(" ").append(faction.getFactionName()).append(" ");
            } else {
                description.append(Emojis.getFactionIconFromDiscord(getFaction())).append(" ").append(getFaction());
            }
            description.append(" ").append(StringUtils.capitalize(getType()));
        }
        if (showUnlockConditions && !"agent".equals(getType())) description.append("\n*Unlock: ").append(getUnlockCondition()).append("*");
        eb.setDescription(description.toString());

        //FIELDS
        eb.addField(getAbilityName().orElse(" "), "**" + getAbilityWindow() + "**\n> " + getAbilityText(), false);
        if (includeFlavourText && getFlavourText().isPresent()) eb.addField(" ", "*" + getFlavourText() + "*", false);

        //FOOTER
        StringBuilder footer = new StringBuilder();
        if (includeID) footer.append("ID: ").append(getAlias()).append("    Source: ").append(getSource());
        eb.setFooter(footer.toString());

        eb.setColor(Color.black);
        return eb.build();
    }

    public boolean search(String searchString) {
        if (searchString == null) return true;
        searchString = searchString.toLowerCase();
        return getID().toLowerCase().contains(searchString)
            || getName().toLowerCase().contains(searchString)
            || getTitle().toLowerCase().contains(searchString)
            || getAbilityName().orElse("").toLowerCase().contains(searchString)
            || getAbilityWindow().toLowerCase().contains(searchString)
            || getAbilityText().toLowerCase().contains(searchString)
            || getUnlockCondition().toLowerCase().contains(searchString)
            || getAutoCompleteName().toLowerCase().contains(searchString)
            || getSearchTags().contains(searchString);
    }

    public String getAutoCompleteName() {
        return getName() + " (" + getFaction() + " " + getType() + ")";
    }

}

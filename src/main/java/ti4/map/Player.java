package ti4.map;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.requests.restaction.ThreadChannelAction;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import ti4.MapGenerator;
import ti4.generator.Mapper;
import ti4.helpers.AliasHandler;
import ti4.helpers.Constants;
import ti4.message.BotLogger;
import ti4.model.FactionModel;
import ti4.model.PublicObjectiveModel;
import ti4.model.TechnologyModel;
import ti4.model.UnitModel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Map.Entry;
import java.util.*;

public class Player {

    private String userID;
    private String userName;

    private boolean passed = false;
    private boolean readyToPassBag= false;
    private boolean searchWarrant = false;
    private boolean isDummy = false;

    private String faction;
    private Faction faction_;
    //abilities
    //factiontech
    //home

    @Getter @Setter
    private String playerStatsAnchorPosition = null;
    private String allianceMembers = "";
    private String color;
    private String autoCompleteRepresentation = null;

    private int tacticalCC = 3;
    private int fleetCC = 3;
    private int strategicCC = 2;

    private int tg = 0;
    private int commodities = 0;
    private int commoditiesTotal = 0;
    private int stasisInfantry = 0;

    private Set<Integer> followedSCs = new HashSet<>();

    private LinkedHashMap<String, Integer> actionCards = new LinkedHashMap<>();
    private LinkedHashMap<String, Integer> trapCards = new LinkedHashMap<>();
    private LinkedHashMap<String, String> trapCardsPlanets = new LinkedHashMap<>();
    private LinkedHashMap<String, Integer> secrets = new LinkedHashMap<>();
    private LinkedHashMap<String, Integer> secretsScored = new LinkedHashMap<>();
    private LinkedHashMap<String, Integer> promissoryNotes = new LinkedHashMap<>();
    private HashSet<String> abilities = new HashSet<>();
    private HashSet<String> exhaustedAbilities = new HashSet<>();
    private HashSet<String> promissoryNotesOwned = new HashSet<>();
    private HashSet<String> unitsOwned = new HashSet<>();
    private List<String> promissoryNotesInPlayArea = new ArrayList<>();
    private List<String> techs = new ArrayList<>();
    private List<String> frankenBagPersonal = new ArrayList<>();
    private List<String> frankenBagToPass = new ArrayList<>();
    private List<String> exhaustedTechs = new ArrayList<>();
    private List<String> planets = new ArrayList<>();
    private List<String> exhaustedPlanets = new ArrayList<>();
    private List<String> exhaustedPlanetsAbilities = new ArrayList<>();
    private List<String> mahactCC = new ArrayList<>();

    @JsonProperty("leaders")
    private List<Leader> leaders = new ArrayList<>();

    private Map<String, Integer> debt_tokens = new LinkedHashMap<>(); //colour, count
    private HashMap<String, String> fow_seenTiles = new HashMap<>();
    private HashMap<String, Integer> unitCaps = new HashMap<>();
    private HashMap<String, String> fow_customLabels = new HashMap<>();
    private String fowFogFilter = null;
    private boolean fogInitialized = false;

    @Nullable
    private String roleIDForCommunity = null;
    @Nullable
    private String privateChannelID = null;
    @Nullable
    private String cardsInfoThreadID = null;

    private int crf = 0;
    private int hrf = 0;
    private int irf = 0;
    private int vrf = 0;
    private ArrayList<String> fragments = new ArrayList<>();
    private List<String> relics = new ArrayList<>();
    private List<String> exhaustedRelics = new ArrayList<>();
    private LinkedHashSet<Integer> SCs = new LinkedHashSet<>();

    //BENTOR CONGLOMERATE ABILITY "Ancient Blueprints"
    private boolean hasFoundCulFrag = false;
    private boolean hasFoundHazFrag = false;
    private boolean hasFoundIndFrag = false;
    private boolean hasFoundUnkFrag = false;

    // Statistics
    private int numberOfTurns = 0;
    private long totalTimeSpent = 0;

    private Tile nomboxTile = new Tile("nombox", "nombox");

    public Player() {
    }

    public Player(@JsonProperty("userID") String userID,
                  @JsonProperty("userName") String userName) {
        this.userID = userID;
        this.userName = userName;
    }

    public Tile getNomboxTile() {
        return nomboxTile;
    }

    public List<String> getMahactCC() {
        return mahactCC;
    }

    public void setMahactCC(List<String> mahactCC) {
        this.mahactCC = mahactCC;
    }

    public void addMahactCC(String cc) {
        if (!mahactCC.contains(cc)) {
            mahactCC.add(cc);
        }
    }

    public void removeMahactCC(String cc) {
         mahactCC.remove(cc);
    }

    public String getRoleIDForCommunity() {
        return roleIDForCommunity;
    }

    public void setRoleIDForCommunity(String roleIDForCommunity) {
        this.roleIDForCommunity = roleIDForCommunity;
    }

    @Nullable @JsonIgnore
    public Role getRoleForCommunity() {
        try {
            return MapGenerator.jda.getRoleById(getRoleIDForCommunity());
        } catch (Exception e) {
            // BotLogger.log("Could not retrieve MainGameChannel for " + getName(), e);
        }
        return null;
    }

    public String getPrivateChannelID() {
        return privateChannelID;
    }

    public void setPrivateChannelID(String privateChannelID) {
        this.privateChannelID = privateChannelID;
    }

    @Nullable @JsonIgnore
    public MessageChannel getPrivateChannel() {
        try {
            return MapGenerator.jda.getTextChannelById(getPrivateChannelID());
        } catch (Exception e) {
            // BotLogger.log("Could not retrieve privateChannel for " + getName(), e);
        }
        return null;
    }

    public String getCardsInfoThreadID() {
        return cardsInfoThreadID;
    }

    public boolean hasPDS2Tech() {
        if(getTechs().contains("ht2") ||getTechs().contains("pds2") ||getTechs().contains("dsgledpds") || getTechs().contains("dsmirvpds"))
        {
            return true; 
        }
        return false;
    }
    public boolean hasWarsunTech() {
        if(getTechs().contains("pws2") ||getTechs().contains("dsrohdws") ||getTechs().contains("ws") || getFaction().equalsIgnoreCase("muaat"))
        {
            return true; 
        }
        return false;
    }
    public boolean hasFF2Tech() {
        if(getTechs().contains("ff2") ||getTechs().contains("hcf2") ||getTechs().contains("dsflorff") ||getTechs().contains("dslizhff"))
        {
            return true; 
        }
        return false;
    }

    public void setCardsInfoThreadID(String cardsInfoThreadID) {
        this.cardsInfoThreadID = cardsInfoThreadID;
    }

    @JsonIgnore
    public ThreadChannel getCardsInfoThread(ti4.map.Map activeMap) {
        TextChannel actionsChannel = (TextChannel) activeMap.getMainGameChannel();
        if (activeMap.isFoWMode() || activeMap.isCommunityMode()) actionsChannel = (TextChannel) getPrivateChannel();
        if (actionsChannel == null) {
            BotLogger.log("`Helper.getPlayerCardsInfoThread`: actionsChannel is null for game: " + activeMap.getName());
            return null;
        }

        String threadName = Constants.CARDS_INFO_THREAD_PREFIX + activeMap.getName() + "-" + getUserName().replaceAll("/", "");
        if (activeMap.isFoWMode()) {
            threadName = activeMap.getName() + "-" + "cards-info-"+ getUserName().replaceAll("/", "") + "-private";
        }

        //ATTEMPT TO FIND BY ID
        String cardsInfoThreadID = getCardsInfoThreadID();
        try {
            if (cardsInfoThreadID != null && !cardsInfoThreadID.isBlank() && !cardsInfoThreadID.isEmpty() && !cardsInfoThreadID.equals("null")) {
                List<ThreadChannel> threadChannels = actionsChannel.getThreadChannels();
                if (threadChannels == null) return null;

                ThreadChannel threadChannel = MapGenerator.jda.getThreadChannelById(cardsInfoThreadID);
                if (threadChannel != null) return threadChannel;

                // SEARCH FOR EXISTING OPEN THREAD
                for (ThreadChannel threadChannel_ : threadChannels) {
                    if (threadChannel_.getId().equals(cardsInfoThreadID)) {
                        setCardsInfoThreadID(threadChannel_.getId());
                        return threadChannel_;
                    }
                }

                // SEARCH FOR EXISTING CLOSED/ARCHIVED THREAD
                List<ThreadChannel> hiddenThreadChannels = actionsChannel.retrieveArchivedPrivateThreadChannels().complete();
                for (ThreadChannel threadChannel_ : hiddenThreadChannels) {
                    if (threadChannel_.getId().equals(cardsInfoThreadID)) {
                        setCardsInfoThreadID(threadChannel_.getId());
                        return threadChannel_;
                    }
                }
            }
        } catch (Exception e) {
            BotLogger.log("`Player.getCardsInfoThread`: Could not find existing Cards Info thead using ID: " + cardsInfoThreadID + " for potential thread name: " + threadName, e);
        }

        //ATTEMPT TO FIND BY NAME
        try {
            if (cardsInfoThreadID != null && !cardsInfoThreadID.isBlank() && !cardsInfoThreadID.isEmpty() && !cardsInfoThreadID.equals("null")) {
                List<ThreadChannel> threadChannels = actionsChannel.getThreadChannels();
                if (threadChannels == null) return null;

                ThreadChannel threadChannel = MapGenerator.jda.getThreadChannelById(cardsInfoThreadID);
                if (threadChannel != null) return threadChannel;

                // SEARCH FOR EXISTING OPEN THREAD
                for (ThreadChannel threadChannel_ : threadChannels) {
                    if (threadChannel_.getName().equals(threadName)) {
                        setCardsInfoThreadID(threadChannel_.getId());
                        return threadChannel_;
                    }
                }

                // SEARCH FOR EXISTING CLOSED/ARCHIVED THREAD
                List<ThreadChannel> hiddenThreadChannels = actionsChannel.retrieveArchivedPrivateThreadChannels().complete();
                for (ThreadChannel threadChannel_ : hiddenThreadChannels) {
                    if (threadChannel_.getName().equals(threadName)) {
                        setCardsInfoThreadID(threadChannel_.getId());
                        return threadChannel_;
                    }
                }
            }
        } catch (Exception e) {
            BotLogger.log("`Player.getCardsInfoThread`: Could not find existing Cards Info thead using name: " + threadName, e);
        }

        // CREATE NEW THREAD
        //Make card info thread a public thread in community mode
        boolean isPrivateChannel = (!activeMap.isCommunityMode() && !activeMap.isFoWMode());
        if(activeMap.getName().contains("pbd100") || activeMap.getName().contains("pbd500")){
            isPrivateChannel = true;
        }
        ThreadChannelAction threadAction = actionsChannel.createThreadChannel(threadName, isPrivateChannel);
        threadAction.setAutoArchiveDuration(ThreadChannel.AutoArchiveDuration.TIME_24_HOURS);
        if (isPrivateChannel) {
            threadAction.setInvitable(false);
        }
        ThreadChannel threadChannel = threadAction.complete();
        setCardsInfoThreadID(threadChannel.getId());
        return threadChannel;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }
     public boolean isReadyToPassBag() {
        return readyToPassBag;
    }

    public void setReadyToPassBag(boolean passed) {
        readyToPassBag= passed;
    }

    public HashSet<String> getAbilities() {
        return abilities;
    }

    public void setAbilities(HashSet<String> abilities) {
        this.abilities = abilities;
    }

    /**
     * @param abilityID The ID of the ability - does not check if valid
     */
    public void addAbility(String abilityID) {
        abilities.add(abilityID);
    }

    public void removeAbility(String abilityID) {
        abilities.remove(abilityID);
    }

    public boolean hasAbility(String ability) {
        return getAbilities().contains(ability);
    }

    public HashSet<String> getExhaustedAbilities() {
        return exhaustedAbilities;
    }

    public void setExhaustedAbilities(HashSet<String> exhaustedAbilities) {
        this.exhaustedAbilities = exhaustedAbilities;
    }

    public boolean addExhaustedAbility(String ability) {
        return exhaustedAbilities.add(ability);
    }

    public boolean removeExhaustedAbility(String ability) {
        return exhaustedAbilities.remove(ability);
    }

    public void clearExhaustedAbilities() {
        exhaustedAbilities.clear();
    }

    public int getUnitCap(String unit) {
        if (unitCaps.get(unit) == null) {
            return 0;
        }
        return unitCaps.get(unit);
    }

    public HashMap<String, Integer> getUnitCaps() {
        return unitCaps;
    }

    public void setUnitCap(String unit, int cap) {
        unitCaps.put(unit, cap);
    }

    public LinkedHashMap<String, Integer> getActionCards() {
        return actionCards;
    }

    public LinkedHashMap<String, Integer> getTrapCards() {
        return trapCards;
    }

    public LinkedHashMap<String, String> getTrapCardsPlanets() {
        return trapCardsPlanets;
    }

    public HashSet<String> getPromissoryNotesOwned() {
        return promissoryNotesOwned;
    }

    public void setPromissoryNotesOwned(HashSet<String> promissoryNotesOwned) {
        this.promissoryNotesOwned = promissoryNotesOwned;
    }

    public boolean ownsPromissoryNote(String promissoryNoteID) {
        return promissoryNotesOwned.contains(promissoryNoteID);
    }

    public boolean removeOwnedPromissoryNoteByID(String promissoryNoteID) {
        return promissoryNotesOwned.remove(promissoryNoteID);
    }

    public boolean addOwnedPromissoryNoteByID(String promissoryNoteID) {
        return promissoryNotesOwned.add(promissoryNoteID);
    }

    public LinkedHashMap<String, Integer> getPromissoryNotes() {
        return promissoryNotes;
    }

    public List<String> getPromissoryNotesInPlayArea() {
        return promissoryNotesInPlayArea;
    }

    public HashSet<String> getUnitsOwned() {
        return unitsOwned;
    }

    public void setUnitsOwned(HashSet<String> unitsOwned) {
        this.unitsOwned = unitsOwned;
    }

    public boolean ownsUnit(String unitID) {
        return unitsOwned.contains(unitID);
    }

    public boolean removeOwnedUnitByID(String unitID) {
        return unitsOwned.remove(unitID);
    }

    public boolean addOwnedUnitByID(String unitID) {
        return unitsOwned.add(unitID);
    }

    public UnitModel getUnitByType(String unitType) {
        return getUnitsOwned().stream()
                .map(unitID -> Mapper.getUnit(unitID))
                .filter(unit -> unitType.equalsIgnoreCase(unit.getBaseType()))
                .findFirst()
                .orElse(null);
    }

    public List<UnitModel> getUnitsByAsyncID(String asyncID) {
        return getUnitsOwned().stream()
                .map(unitID -> Mapper.getUnit(unitID))
                .filter(unit -> asyncID.equalsIgnoreCase(unit.getAsyncId()))
                .toList();
    }

    public UnitModel getUnitByID(String unitID) {
        return Mapper.getUnit(unitID);
    }

    public String checkUnitsOwned() {
        for (int count : getUnitsOwnedByBaseType().values()) {
            if (count > 1) {
                String message = "> Warning - Player: " + getUserName() + " has more than one of the same unit type.\n> Unit Counts: `" + getUnitsOwnedByBaseType() + "`\n> Units Owned: `" + getUnitsOwned() + "`";
                BotLogger.log(message);
                return message;
            }
        }
        return null;
    }

    public Map<String, Integer> getUnitsOwnedByBaseType() {
        Map<String, Integer> unitCount = new HashMap<>();
        for (String unitID : getUnitsOwned()) {
            UnitModel unitModel = Mapper.getUnit(unitID);
            unitCount.merge(unitModel.getBaseType(), 1, (oldValue, newValue) -> oldValue + 1);
        }
        return unitCount;
    }

    public void setActionCard(String id) {
        Collection<Integer> values = actionCards.values();
        int identifier = new Random().nextInt(1000);
        while (values.contains(identifier)) {
            identifier = new Random().nextInt(1000);
        }
        actionCards.put(id, identifier);
    }

    public void setTrapCard(String id) {
        Collection<Integer> values = trapCards.values();
        int identifier = new Random().nextInt(1000);
        while (values.contains(identifier)) {
            identifier = new Random().nextInt(1000);
        }
        trapCards.put(id, identifier);
    }

    public void setTrapCardPlanet(String id, String planet) {
        trapCardsPlanets.put(id, planet);
    }

    public void setPromissoryNote(String id) {
        Collection<Integer> values = promissoryNotes.values();
        int identifier = new Random().nextInt(100);
        while (values.contains(identifier)) {
            identifier = new Random().nextInt(100);
        }
        promissoryNotes.put(id, identifier);
    }

    public void clearPromissoryNotes() {
        promissoryNotes.clear();
    }

    public void setPromissoryNotesInPlayArea(String id) {
        if (!promissoryNotesInPlayArea.contains(id)) {
            promissoryNotesInPlayArea.add(id);
        }
    }

    @JsonSetter
    public void setPromissoryNotesInPlayArea(List<String> promissoryNotesInPlayArea) {
        this.promissoryNotesInPlayArea = promissoryNotesInPlayArea;
    }

    public void setPromissoryNotes(LinkedHashMap<String, Integer> promissoryNotes) {
        this.promissoryNotes = promissoryNotes;
    }

    public void removePromissoryNotesInPlayArea(String id) {
        promissoryNotesInPlayArea.remove(id);
    }

    public void setActionCard(String id, Integer identifier) {
        actionCards.put(id, identifier);
    }

    public void setTrapCard(String id, Integer identifier) {
        trapCards.put(id, identifier);
    }

    public void setPromissoryNote(String id, Integer identifier) {
        promissoryNotes.put(id, identifier);
    }

    public void removeActionCard(Integer identifier) {
        String idToRemove = "";
        for (Map.Entry<String, Integer> so : actionCards.entrySet()) {
            if (so.getValue().equals(identifier)) {
                idToRemove = so.getKey();
                break;
            }
        }
        actionCards.remove(idToRemove);
    }

    public void removePromissoryNote(Integer identifier) {
        String idToRemove = "";
        for (Map.Entry<String, Integer> so : promissoryNotes.entrySet()) {
            if (so.getValue().equals(identifier)) {
                idToRemove = so.getKey();
                break;
            }
        }
        promissoryNotes.remove(idToRemove);
    }

    public void removePromissoryNote(String id) {
        promissoryNotes.remove(id);
        removePromissoryNotesInPlayArea(id);
    }

    public LinkedHashMap<String, Integer> getSecrets() {
        return secrets;
    }

    public void setSecret(String id) {

        Collection<Integer> values = secrets.values();
        int identifier = new Random().nextInt(1000);
        while (values.contains(identifier)) {
            identifier = new Random().nextInt(1000);
        }
        secrets.put(id, identifier);
    }

    public void setSecret(String id, Integer identifier) {
        secrets.put(id, identifier);
    }

    public void removeSecret(Integer identifier) {
        String idToRemove = "";
        for (Map.Entry<String, Integer> so : secrets.entrySet()) {
            if (so.getValue().equals(identifier)) {
                idToRemove = so.getKey();
                break;
            }
        }
        secrets.remove(idToRemove);
    }

    public LinkedHashMap<String, Integer> getSecretsScored() {
        return secretsScored;
    }

    public void setSecretScored(String id, ti4.map.Map activeMap) {
        Collection<Integer> values = secretsScored.values();
        List<Integer> allIDs = activeMap.getPlayers().values().stream().flatMap(player -> player.getSecretsScored().values().stream()).toList();
        int identifier = new Random().nextInt(1000);
        while (values.contains(identifier) || allIDs.contains(identifier)) {
            identifier = new Random().nextInt(1000);
        }
        secretsScored.put(id, identifier);
    }

    public void setSecretScored(String id, Integer identifier) {
        secretsScored.put(id, identifier);
    }

    public void removeSecretScored(Integer identifier) {
        String idToRemove = "";
        for (Map.Entry<String, Integer> so : secretsScored.entrySet()) {
            if (so.getValue().equals(identifier)) {
                idToRemove = so.getKey();
                break;
            }
        }
        secretsScored.remove(idToRemove);
    }


    public int getCrf() {
        return crf;
    }

    public int getIrf() {
        return irf;
    }

    public int getHrf() {
        return hrf;
    }

    public int getVrf() {
        return vrf;
    }

    public ArrayList<String> getFragments() {
        return fragments;
    }

    public boolean enoughFragsForRelic(){
        boolean enough = false;
        int haz = 0;
        int ind = 0;
        int cult = 0;
        int frontier = 0;
		for (String id : fragments) {
			String[] cardInfo = Mapper.getExplore(id).split(";");
			if (cardInfo[1].equalsIgnoreCase("hazardous")) {
				haz = haz + 1;
			} else if (cardInfo[1].equalsIgnoreCase(Constants.FRONTIER)) {
				frontier = frontier+1;
			}else if (cardInfo[1].equalsIgnoreCase("industrial")) {
				ind = ind+1;
			}else if (cardInfo[1].equalsIgnoreCase("cultural")) {
				cult = cult+1;
			}
		}
        int targetToHit = 3 - frontier;
        if(hasAbility("fabrication") || getPromissoryNotes().containsKey("bmf"))
        {
            targetToHit = targetToHit-1;
        }
        if(haz >= targetToHit || cult >= targetToHit || ind >= targetToHit)
        {
            enough = true;
        }

        return enough;
    }


    public void setFragments(ArrayList<String> fragmentList) {
        fragments = fragmentList;
        updateFragments();
    }

    public void addFragment(String fragmentID) {
        fragments.add(fragmentID);
        updateFragments();
    }

    public void removeFragment(String fragmentID) {
        fragments.remove(fragmentID);
        updateFragments();
    }

    private void updateFragments() {
        crf = irf = hrf = vrf = 0;
        for (String cardID : fragments) {
            String color = Mapper.getExplore(cardID).split(";")[1].toLowerCase();
            switch (color) {
                case Constants.CULTURAL -> {
                    crf++;
                    hasFoundCulFrag = true;
                }
                case Constants.INDUSTRIAL -> {
                    irf++;
                    hasFoundIndFrag = true;
                }
                case Constants.HAZARDOUS -> {
                    hrf++;
                    hasFoundHazFrag = true;
                }
                case Constants.FRONTIER -> {
                    vrf++;
                    hasFoundUnkFrag = true;
                }
            }
        }
    }

    public void addRelic(String relicID) {
        if (!relics.contains(relicID) || Constants.ENIGMATIC_DEVICE.equals(relicID)) {
            if (relicID.equals("dynamiscore") || relicID.equals("absol_dynamiscore")){
                setCommoditiesTotal(getCommoditiesTotal() + 2);
            }
            relics.add(relicID);
        }
    }

    public void addExhaustedRelic(String relicID) {
        exhaustedRelics.add(relicID);
    }

    public void removeRelic(String relicID) {
        if (relicID.equals("dynamiscore") || relicID.equals("absol_dynamiscore")){
            setCommoditiesTotal(getCommoditiesTotal() - 2);
        }
        relics.remove(relicID);
    }

    public void removeExhaustedRelic(String relicID) {
        exhaustedRelics.remove(relicID);
    }

    public List<String> getRelics() {
        return relics;
    }

    public List<String> getExhaustedRelics() {
        return exhaustedRelics;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        User userById = MapGenerator.jda.getUserById(userID);
        if (userById != null) {
            userName = userById.getName();
            Member member = MapGenerator.guildPrimary.getMemberById(userID);
            if (member != null) userName = member.getEffectiveName();
        }
        return userName;
    }

    public String getFaction() {
        return faction;
    }

    public void setFaction(String faction) {
        this.faction = faction;
        initLeaders();
        initAbilities();
    }

    private void initAbilities() {
        HashSet<String> abilities = new HashSet<>();
        for (String ability : getFactionStartingAbilities()) {
            if (ability.isEmpty() || ability.isBlank()){
                continue;
            } else {
                abilities.add(ability);
            }
        }
        setAbilities(abilities);
        if (faction.equals(Constants.LIZHO)){
            Map<String, String> dsHandcards = Mapper.getDSHandcards();
            for (Entry<String, String> entry : dsHandcards.entrySet()) {
                String key = entry.getKey();
                if (key.endsWith(Constants.LIZHO)){
                    setTrapCard(key);
                }
            }
        }
    }

    @JsonIgnore
    public FactionModel getFactionSetupInfo() {
        if (faction == null || faction.equals("null") || faction.equals("keleres")) return null;
        FactionModel factionSetupInfo = Mapper.getFactionSetup(faction);
        if (factionSetupInfo == null) {
            BotLogger.log("Could not get faction setup info for: " + faction);
            return null;
        }
        return factionSetupInfo;
    }

    private List<String> getFactionStartingAbilities() {
        FactionModel factionSetupInfo = getFactionSetupInfo();
        if (factionSetupInfo == null) return new ArrayList<String>();
        return new ArrayList<String>(factionSetupInfo.getAbilities());
    }

    private List<String> getFactionStartingLeaders() {
        FactionModel factionSetupInfo = getFactionSetupInfo();
        if(factionSetupInfo == null) return new ArrayList<String>();
        return new ArrayList<String>(factionSetupInfo.getLeaders());
    }

    public void initLeaders() {
        leaders.clear();
        for (String leaderID : getFactionStartingLeaders()) {
            Leader leader = new Leader(leaderID);
            leaders.add(leader);
        }
    }

    @Nullable
    public Leader getLeader(String leaderIdOrType) {
        Leader leader = getLeaderByID(leaderIdOrType);
        if (leader == null) {
            return getLeaderByType(leaderIdOrType);
        }
        return leader;
    }

    @Nullable
    public Leader getLeaderByType(String leaderType) {
        for (Leader leader : leaders) {
            if (leader.getType().equals(leaderType)) {
                return leader;
            }
        }
        return null;
    }

    @Nullable
    public Leader getLeaderByID(String leaderID) {
        for (Leader leader : leaders) {
            if (leader.getId().equals(leaderID)) {
                return leader;
            }
        }
        return null;
    }

    public List<Leader> getLeaders() {
        return leaders;
    }

    public List<String> getLeaderIDs() {
        return getLeaders().stream().map(l -> l.getId()).toList();
    }

    public boolean hasLeader(String leaderID) {
        return getLeaderIDs().contains(leaderID);
    }

    public boolean hasLeaderUnlocked(String leaderID) {
        return hasLeader(leaderID) && !getLeader(leaderID).isLocked();
    }

    public void setLeaders(List<Leader> leaders) {
        this.leaders = leaders;
    }

    public boolean removeLeader(String leaderID) {
        Leader leaderToPurge = null;
        for (Leader leader : leaders) {
            if (leader.getId().equals(leaderID)) {
                leaderToPurge = leader;
                break;
            }
        }
        if (leaderToPurge == null){
            return false;
        }
        return leaders.remove(leaderToPurge);
    }

    public boolean removeLeader(Leader leader) {
        return leaders.remove(leader);
    }
    
    public void addLeader(String leaderID) {
        if (!getLeaderIDs().contains(leaderID)) {
            Leader leader = new Leader(leaderID);
            leaders.add(leader);
        }
    }

    public void addLeader(Leader leader) {
        if (!getLeaderIDs().contains(leader.getId())) {
            leaders.add(leader);
        }
    }

    public String getColor() {
        return color != null ? color : "null";
    }

    public void setColor(String color) {
        if (!color.equals("null")) {
            this.color = AliasHandler.resolveColor(color);
        }
    }
    public void addAllianceMember(String color) {
        if (!color.equals("null")) {
            this.allianceMembers = allianceMembers+color;
        }
    }
    public void setAllianceMembers(String color) {
        if (!color.equals("null")) {
            this.allianceMembers = color;
        }
    }
    public String getAllianceMembers() {
        return allianceMembers;
    }
    public void removeAllianceMember(String color) {
        if (!color.equals("null")) {
            this.allianceMembers = allianceMembers.replace(color, "");
        }
    }

    public void changeColor(String color) {
        if (!color.equals("null")) {
            this.color = AliasHandler.resolveColor(color);
        }
    }

    public void initPNs(ti4.map.Map activeMap) {
        if (activeMap != null && color != null && faction != null && Mapper.isColorValid(color) && Mapper.isFaction(faction)) {
            promissoryNotes.clear();
            List<String> promissoryNotes = Mapper.getColourFactionPromissoryNoteIDs(activeMap, color, faction);
            for (String promissoryNote : promissoryNotes) {
                if (promissoryNote.endsWith("_an") && hasAbility("hubris")) {
                    continue;
                }
                if (promissoryNote.equalsIgnoreCase("blood_pact") && !hasAbility("dark_whispers")) {
                    continue;
                }
                if (promissoryNote.endsWith("_sftt") && hasAbility("enlightenment")) {
                    continue;
                }
                setPromissoryNote(promissoryNote);
            }
        }
    }

    public int getTacticalCC() {
        return tacticalCC;
    }

    public void setTacticalCC(int tacticalCC) {
        this.tacticalCC = tacticalCC;
    }

    public int getFleetCC() {
        return fleetCC;
    }

    public void setFleetCC(int fleetCC) {
        this.fleetCC = fleetCC;
    }

    public int getStrategicCC() {
        return strategicCC;
    }

    public void setStrategicCC(int strategicCC) {
        this.strategicCC = strategicCC;
    }

    public int getTg() {
        return tg;
    }

    public int getPublicVictoryPoints(ti4.map.Map activeMap) {
        LinkedHashMap<String, List<String>> scoredPOs = activeMap.getScoredPublicObjectives();
        int vpCount = 0;
        for (Entry<String, List<String>> scoredPOEntry : scoredPOs.entrySet()) {
            if (scoredPOEntry.getValue().contains(getUserID())) {
                String poID = scoredPOEntry.getKey();
                try {
                    PublicObjectiveModel po = Mapper.getPublicObjective(poID);
                    if (po != null) {//IS A PO
                        vpCount += po.getPoints();
                    } else { //IS A CUSTOM PO
                        int frequency = Collections.frequency(scoredPOEntry.getValue(), userID);
                        int poValue = activeMap.getCustomPublicVP().getOrDefault(poID, 0);
                        vpCount += poValue * frequency;
                    }
                } catch (Exception e) {
                    BotLogger.log("`Player.getPublicVictoryPoints   map=" + activeMap.getName() + "  player=" + getUserName() + "` - error finding value of `PO_ID=" + poID, e);
                }
            }
        }

        return vpCount;
    }

    @JsonIgnore
    public int getSecretVictoryPoints(ti4.map.Map activeMap) {
        Map<String, Integer> scoredSecrets = getSecretsScored();
        for (String id : activeMap.getSoToPoList()) {
            scoredSecrets.remove(id);
        }
        return scoredSecrets.size();
    }

    @JsonIgnore
    public int getSupportForTheThroneVictoryPoints() {
        List<String> promissoryNotesInPlayArea = getPromissoryNotesInPlayArea();
        int vpCount = 0;
        for (String id : promissoryNotesInPlayArea) {
            if (id.endsWith("_sftt")) {
                vpCount++;
            }
        }
        return vpCount;
    }

    @JsonIgnore
    public int getTotalVictoryPoints(ti4.map.Map activeMap) {
        return getPublicVictoryPoints(activeMap) + getSecretVictoryPoints(activeMap) + getSupportForTheThroneVictoryPoints();
    }

    public void setTg(int tg) {
        this.tg = tg;
    }

    public void setFollowedSCs(Set<Integer> followedSCs) {
        this.followedSCs = followedSCs;
    }

    public void addFollowedSC(Integer sc) {
        this.followedSCs.add(sc);
    }

    public void removeFollowedSC(Integer sc) {
        this.followedSCs.remove(sc);
    }

    public boolean hasFollowedSC(int sc) {
        return getFollowedSCs().contains(sc);
    }

    public void clearFollowedSCs() {
        this.followedSCs.clear();
    }

    public Set<Integer> getFollowedSCs() {
        return this.followedSCs;
    }

    @JsonIgnore
    public int getAc() {
        return actionCards.size();
    }

    @JsonIgnore
    public int getPnCount() {
        return (promissoryNotes.size() - promissoryNotesInPlayArea.size());
    }

    @JsonIgnore
    public int getSo() {
        return secrets.size();
    }

    @JsonIgnore
    public int getSoScored() {
        return secretsScored.size();
    }

    public LinkedHashSet<Integer> getSCs() {
        return SCs;
    }

    public void setSCs(LinkedHashSet<Integer> SCs) {
        this.SCs = SCs;
        this.SCs.remove(0); // TEMPORARY MIGRATION TO REMOVE 0 IF PLAYER HAS IT FROM OLD SAVES
    }

    public void addSC(int sc) {
        SCs.add(sc);
    }

    public void removeSC(int sc) {
        SCs.remove(sc);
    }

    public void clearSCs() {
        SCs.clear();
    }

    public int getLowestSC() {
        try {
            return Collections.min(getSCs());
        } catch (NoSuchElementException e) {
            return 100;
        }
    }

    public int getCommodities() {
        return commodities;
    }

    public void setCommodities(int commodities) {
        this.commodities = commodities;
    }

    public List<String> getTechs() {
        return techs;
    }
    public List<String> getFrankenBagPersonal() {
        return frankenBagPersonal;
    }
    public List<String> getFrankenBagToPass() {
        return frankenBagToPass;
    }

    public boolean hasTech(String techID) {
        return techs.contains(techID);
    }

    public boolean hasTechReady(String techID) {
        return hasTech(techID) && !exhaustedTechs.contains(techID);
    }
    public List<String> getPlanets() {
        return planets;
    }
    public boolean isPlayerMemberOfAlliance(Player player2) {
        return allianceMembers.contains(player2.getFaction());
    }

    public List<String> getPlanets(ti4.map.Map activeMap) {
        List<String> newPlanets = new ArrayList<String>();
        newPlanets.addAll(planets);
        if(!allianceMembers.equalsIgnoreCase("")){
            for(Player player2 : activeMap.getRealPlayers()){
                if(getAllianceMembers().contains(player2.getFaction())){
                    newPlanets.addAll(player2.getPlanets());
                }
            }
        }
        return newPlanets;
    }

    public void setPlanets(List<String> planets) {
        this.planets = planets;
    }
    public void setFrankenBagPersonal(List<String> planets) {
        frankenBagPersonal = planets;
    }
    public void setFrankenBagToPass(List<String> planets) {
        frankenBagToPass = planets;
    }

    public List<String> getReadiedPlanets() {
        List<String> planets = new ArrayList<>(getPlanets());
        planets.removeAll(getExhaustedPlanets());
        return planets;
    }

    public List<String> getExhaustedPlanets() {
        return exhaustedPlanets;
    }

    public void setExhaustedPlanets(List<String> exhaustedPlanets) {
        this.exhaustedPlanets = exhaustedPlanets;
    }

    public List<String> getExhaustedPlanetsAbilities() {
        return exhaustedPlanetsAbilities;
    }

    public void setExhaustedPlanetsAbilities(List<String> exhaustedPlanetsAbilities) {
        this.exhaustedPlanetsAbilities = exhaustedPlanetsAbilities;
    }

    public void setTechs(List<String> techs) {
        this.techs = techs;
    }

    public void setRelics(List<String> relics) {
        this.relics = relics;
    }

    public void setExhaustedRelics(List<String> exhaustedRelics) {
        this.exhaustedRelics = exhaustedRelics;
    }

    public boolean hasRelic(String relicID) {
        return relics.contains(relicID);
    }

    public boolean hasRelicReady(String relicID) {
        return hasRelic(relicID) && !exhaustedRelics.contains(relicID);
    }

    public List<String> getExhaustedTechs() {
        return exhaustedTechs;
    }

    public void cleanExhaustedTechs() {
        exhaustedTechs.clear();
    }

    public void cleanExhaustedPlanets(boolean cleanAbilities) {
        exhaustedPlanets.clear();
        if (cleanAbilities) {
            exhaustedPlanetsAbilities.clear();
        }
    }

    public void cleanExhaustedRelics() {
        exhaustedRelics.clear();
    }

    public void setExhaustedTechs(List<String> exhaustedTechs) {
        this.exhaustedTechs = exhaustedTechs;
    }

    public void addTech(String techID) {
        if (techs.contains(techID)) {
            return;
        }
        techs.add(techID);

        doAdditionalThingsWhenAddingTech(techID);
    }
    public void addToFrankenPersonalBag(String thing) {
        if (frankenBagPersonal.contains(thing)) {
            return;
        }
        frankenBagPersonal.add(thing);
    }
    public void addToFrankenPassingBag(String thing) {
        if (frankenBagToPass.contains(thing)) {
            return;
        }
        frankenBagToPass.add(thing);
    }

    private void doAdditionalThingsWhenAddingTech(String techID) {
        // Add Custodia Vigilia when researching IIHQ
        if(techID.equalsIgnoreCase("iihq")){
            addPlanet("custodiavigilia");
            exhaustPlanet("custodiavigilia");
        }

        // Update Owned Units when Researching a Unit Upgrade
        TechnologyModel techModel = Mapper.getTech(techID);
        if (techID == null) return;

        if (Constants.UNIT_UPGRADE.equalsIgnoreCase(techModel.getType())) {
            UnitModel unitModel = Mapper.getUnitModelByTechUpgrade(techID);
            if (unitModel != null && unitModel.getUpgradesFromUnitId() != null) {
                if (getUnitsOwned().contains(unitModel.getUpgradesFromUnitId())) {
                    removeOwnedUnitByID(unitModel.getUpgradesFromUnitId());
                }
                addOwnedUnitByID(unitModel.getId());
            }
        }
    }

    public void exhaustTech(String tech) {
        if (techs.contains(tech) && !exhaustedTechs.contains(tech)) {
            exhaustedTechs.add(tech);
        }
    }

    public void refreshTech(String tech) {
        boolean isRemoved = exhaustedTechs.remove(tech);
        if (isRemoved) refreshTech(tech);
    }

    public void removeTech(String tech) {
        boolean isRemoved = techs.remove(tech);
        if (isRemoved) removeTech(tech);
        refreshTech(tech);
        //TODO: Remove unitupgrade -> fix owned units
    }
    public void removeElementFromBagToPass(String tech) {
        frankenBagToPass.remove(tech);
    }

    public void addPlanet(String planet) {
        if (!planets.contains(planet)) {
            planets.add(planet);
        }
    }

    public void exhaustPlanet(String planet) {
        if (planets.contains(planet) && !exhaustedPlanets.contains(planet)) {
            exhaustedPlanets.add(planet);
        }
    }

    public void exhaustPlanetAbility(String planet) {
        if (planets.contains(planet) && !exhaustedPlanetsAbilities.contains(planet)) {
            exhaustedPlanetsAbilities.add(planet);
        }
    }

    public void refreshPlanet(String planet) {
        boolean isRemoved = exhaustedPlanets.remove(planet);
        if (isRemoved) refreshPlanet(planet);
    }

    public void refreshPlanetAbility(String planet) {
        boolean isRemoved = exhaustedPlanetsAbilities.remove(planet);
        if (isRemoved) refreshPlanetAbility(planet);
    }

    public void removePlanet(String planet) {
        planets.remove(planet);
        refreshPlanet(planet);
        refreshPlanetAbility(planet);
    }

    public int getStasisInfantry() {
        return stasisInfantry;
    }

    public void setStasisInfantry(int stasisInfantry) {
        this.stasisInfantry = stasisInfantry;
    }

    public int getCommoditiesTotal() {
        return commoditiesTotal;
    }

    public void setCommoditiesTotal(int commoditiesTotal) {
        this.commoditiesTotal = commoditiesTotal;
    }

    public void setSearchWarrant() {
        searchWarrant = !searchWarrant;
    }

    public void setSearchWarrant(boolean value) {
        searchWarrant = value;
    }

    public boolean isSearchWarrant() {
        return searchWarrant;
    }

    public void updateFogTile(@NotNull Tile tile, String label) {
        fow_seenTiles.put(tile.getPosition(), tile.getTileID());
        if (label == null) {
            fow_customLabels.remove(tile.getPosition());
        } else {
            fow_customLabels.put(tile.getPosition(), label);
        }
    }

    public void addFogTile(String tileID, String position, String label) {
        fow_seenTiles.put(position, tileID);
        if (label != null && !label.equals(".") && !label.equals("")) {
            fow_customLabels.put(position, label);
        }
    }

    public void removeFogTile(String position) {
        fow_seenTiles.remove(position);
        fow_customLabels.remove(position);
    }

    @JsonIgnore
    public Tile buildFogTile(String position, Player player) {
        String tileID = fow_seenTiles.get(position);
        if (tileID == null) tileID = "0b";

        String label = fow_customLabels.get(position);
        if (label == null) label = "";

        return new Tile(tileID, position, player, true, label);
    }

    public HashMap<String,String> getFogTiles() {
        return fow_seenTiles;
    }

    public HashMap<String,String> getFogLabels() {
        return fow_customLabels;
    }

    public boolean hasFogInitialized() {
        return fogInitialized;
    }

    public void setFogInitialized(boolean init) {
        fogInitialized = init;
    }

    public boolean isDummy() {
        return isDummy;
    }

    public void setDummy(boolean isDummy) {
        this.isDummy = isDummy;
    }

    /**
     * @return true if the player is: not a "dummy", faction != null, color != null, & color != "null"
     */
    @JsonIgnore
    public boolean isRealPlayer() {
        return !(isDummy || faction == null || color == null || color.equals("null"));
    }

    public void setFogFilter(String preference) {
        fowFogFilter = preference;
    }

    public String getFogFilter() {
        return fowFogFilter == null ? "default" : fowFogFilter;
    }

    public void updateTurnStats(long turnTime) {
        numberOfTurns++;
        totalTimeSpent += turnTime;
    }

    public int getNumberTurns() {
        return numberOfTurns;
    }

    public void setNumberTurns(int numTurns) {
        numberOfTurns = numTurns;
    }

    public long getTotalTurnTime() {
        return totalTimeSpent;
    }

    public void setTotalTurnTime(long totalTime) {
        totalTimeSpent = totalTime;
    }

    @JsonIgnore
    public String getAutoCompleteRepresentation() {
        return getAutoCompleteRepresentation(false);
    }

    @JsonIgnore
    public String getAutoCompleteRepresentation(boolean reset) {
        if (reset || this.autoCompleteRepresentation == null) {
            String faction = getFaction();
            if (faction == null || faction == "null") {
                faction = "No Faction";
            } else {
                faction = Mapper.getFactionRepresentations().get(faction);
            }

            String color = getColor();
            if (color == null || color == "null") color = "No Color";

            String userName = getUserName();
            if (userName == null || userName.isEmpty() || userName.isBlank()) {
                userName = "No User";
            }

            String representation = color + " / " + faction + " / " + userName;
            setAutoCompleteRepresentation(representation);
            return getAutoCompleteRepresentation();
        }
        return this.autoCompleteRepresentation;
    }

    public void setAutoCompleteRepresentation(String representation) {
        this.autoCompleteRepresentation = representation;
    }

    //BENTOR CONGLOMERATE ABILITY "Ancient Blueprints"
    public boolean hasFoundCulFrag() {
        return hasFoundCulFrag;
    }

    public void setHasFoundCulFrag(boolean hasFoundCulFrag) {
        this.hasFoundCulFrag = hasFoundCulFrag;
    }

    public boolean hasFoundHazFrag() {
        return hasFoundHazFrag;
    }

    public void setHasFoundHazFrag(boolean hasFoundHazFrag) {
        this.hasFoundHazFrag = hasFoundHazFrag;
    }

    public boolean hasFoundIndFrag() {
        return hasFoundIndFrag;
    }

    public void setHasFoundIndFrag(boolean hasFoundIndFrag) {
        this.hasFoundIndFrag = hasFoundIndFrag;
    }

    public boolean hasFoundUnkFrag() {
        return hasFoundUnkFrag;
    }

    public void setHasFoundUnkFrag(boolean hasFoundUnkFrag) {
        this.hasFoundUnkFrag = hasFoundUnkFrag;
    }

    public Map<String, Integer> getDebtTokens() {
        return debt_tokens;
    }

    public void setDebtTokens(Map<String, Integer> debt_tokens) {
        this.debt_tokens = debt_tokens;
    }

    public void addDebtTokens(String tokenColour, int count) {
        if (debt_tokens.containsKey(tokenColour)) {
            debt_tokens.put(tokenColour, debt_tokens.get(tokenColour) + count);
        } else {
            debt_tokens.put(tokenColour, count);
        }
    }

    public void removeDebtTokens(String tokenColour, int count) {
        if (debt_tokens.containsKey(tokenColour)) {
            debt_tokens.put(tokenColour, Math.max(debt_tokens.get(tokenColour) - count, 0));
        }
    }

    public void clearAllDebtTokens(String tokenColour) {
        debt_tokens.remove(tokenColour);
    }

    public int getDebtTokenCount(String tokenColour) {
        if (debt_tokens.containsKey(tokenColour)) {
            return debt_tokens.get(tokenColour);
        } else {
            return 0;
        }
    }
}

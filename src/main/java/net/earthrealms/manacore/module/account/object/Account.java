package net.earthrealms.manacore.module.account.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.earthrealms.manacore.module.account.exception.InvalidPlayerException;
import net.earthrealms.manacore.utility.string.StringUtility;

public class Account {

	// Economy
	private double untradableCoins;
	private double credits;
	private double premiumCoins;
	private double shards;

	// Status
	private String displayName;
	private boolean premium;
	private boolean operator;
	private String prefix;
	private String title;
	private String suffix;
	private String nickColor;
	private String chatColor;

	// Skills
	private int grindingSkillLevel;
	private int grindingSkillXP;
	private int miningSkillLevel;
	private int miningSkillXP;
	private int farmingSkillLevel;
	private int farmingSkillXP;

	// Games
	private int barWins;
	private int barLosses;
	private int coinFlipWins;
	private int coinFlipLosses;
	private int lotteryWins;
	private int lotteryLosses;
	private int scratchOffWins;
	private int scratchOffLosses;
	private int chatGameWins;
	private int chatGameLosses;

	// Maps & Lists
	private Map<String, Long> cooldowns;
	private Map<Integer, ItemStack[]> backpacks;
	private List<String> titles;

	// Constructors
	public Account() {
		
	}
	
	public Account(Player player) {
		createProfile(player.getUniqueId());
	}

	public Account(UUID uuid) {
		createProfile(uuid);
	}

	public Account(String username) throws InvalidPlayerException {
		Player player = Bukkit.getPlayer(username);
		if (player == null) {
			throw new InvalidPlayerException(username + " is not a player!");
		}
	}

	// Create Profile
	public Account createProfile(UUID uuid) {
		
		// Economy
		untradableCoins = 0;
		credits = 25000;
		premiumCoins = 0;
		shards = 0;
		
		Player player = Bukkit.getPlayer(uuid);
		
		// Status
		displayName = player.getName();
		premium = false;
		operator = false;
		prefix = "1";
		title = "";
		suffix = "";
		nickColor = StringUtility.translateColor("&f");
		chatColor = StringUtility.translateColor("&f");
		
		// Skills
		grindingSkillLevel = 0;
		grindingSkillXP = 0;
		miningSkillLevel = 0;
		miningSkillXP = 0;
		farmingSkillLevel = 0;
		farmingSkillXP = 0;
		
		// Games
		barWins = 0;
		barLosses = 0;
		coinFlipWins = 0;
		coinFlipLosses = 0;
		lotteryWins = 0;
		lotteryLosses = 0;
		scratchOffWins = 0;
		scratchOffLosses = 0;
		
		// Maps & Lists
		cooldowns = new HashMap<String, Long>();
		backpacks = new HashMap<Integer, ItemStack[]>();
		titles = new ArrayList<String>();
		
		return this;
	}

	public double getUntradableCoins() {
		return untradableCoins;
	}

	public void setUntradableCoins(double untradableCoins) {
		this.untradableCoins = untradableCoins;
	}

	public double getCredits() {
		return credits;
	}

	public void setCredits(double credits) {
		this.credits = credits;
	}

	public double getPremiumCoins() {
		return premiumCoins;
	}

	public void setPremiumCoins(double premiumCoins) {
		this.premiumCoins = premiumCoins;
	}

	public double getShards() {
		return shards;
	}

	public void setShards(double shards) {
		this.shards = shards;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public boolean isPremium() {
		return premium;
	}

	public void setPremium(boolean premium) {
		this.premium = premium;
	}

	public boolean isOperator() {
		return operator;
	}

	public void setOperator(boolean operator) {
		this.operator = operator;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getNickColor() {
		return nickColor;
	}

	public void setNickColor(String nickColor) {
		this.nickColor = nickColor;
	}

	public String getChatColor() {
		return chatColor;
	}

	public void setChatColor(String chatColor) {
		this.chatColor = chatColor;
	}

	public int getGrindingSkillLevel() {
		return grindingSkillLevel;
	}

	public void setGrindingSkillLevel(int grindingSkillLevel) {
		this.grindingSkillLevel = grindingSkillLevel;
	}

	public int getGrindingSkillXP() {
		return grindingSkillXP;
	}

	public void setGrindingSkillXP(int grindingSkillXP) {
		this.grindingSkillXP = grindingSkillXP;
	}

	public int getMiningSkillLevel() {
		return miningSkillLevel;
	}

	public void setMiningSkillLevel(int miningSkillLevel) {
		this.miningSkillLevel = miningSkillLevel;
	}

	public int getMiningSkillXP() {
		return miningSkillXP;
	}

	public void setMiningSkillXP(int miningSkillXP) {
		this.miningSkillXP = miningSkillXP;
	}

	public int getFarmingSkillLevel() {
		return farmingSkillLevel;
	}

	public void setFarmingSkillLevel(int farmingSkillLevel) {
		this.farmingSkillLevel = farmingSkillLevel;
	}

	public int getFarmingSkillXP() {
		return farmingSkillXP;
	}

	public void setFarmingSkillXP(int farmingSkillXP) {
		this.farmingSkillXP = farmingSkillXP;
	}

	public int getBarWins() {
		return barWins;
	}

	public void setBarWins(int barWins) {
		this.barWins = barWins;
	}

	public int getBarLosses() {
		return barLosses;
	}

	public void setBarLosses(int barLosses) {
		this.barLosses = barLosses;
	}

	public int getCoinFlipWins() {
		return coinFlipWins;
	}

	public void setCoinFlipWins(int coinFlipWins) {
		this.coinFlipWins = coinFlipWins;
	}

	public int getCoinFlipLosses() {
		return coinFlipLosses;
	}

	public void setCoinFlipLosses(int coinFlipLosses) {
		this.coinFlipLosses = coinFlipLosses;
	}

	public int getLotteryWins() {
		return lotteryWins;
	}

	public void setLotteryWins(int lotteryWins) {
		this.lotteryWins = lotteryWins;
	}

	public int getLotteryLosses() {
		return lotteryLosses;
	}

	public void setLotteryLosses(int lotteryLosses) {
		this.lotteryLosses = lotteryLosses;
	}

	public int getScratchOffWins() {
		return scratchOffWins;
	}

	public void setScratchOffWins(int scratchOffWins) {
		this.scratchOffWins = scratchOffWins;
	}

	public int getScratchOffLosses() {
		return scratchOffLosses;
	}

	public void setScratchOffLosses(int scratchOffLosses) {
		this.scratchOffLosses = scratchOffLosses;
	}

	public int getChatGameWins() {
		return chatGameWins;
	}

	public void setChatGameWins(int chatGameWins) {
		this.chatGameWins = chatGameWins;
	}

	public int getChatGameLosses() {
		return chatGameLosses;
	}

	public void setChatGameLosses(int chatGameLosses) {
		this.chatGameLosses = chatGameLosses;
	}

	public Map<String, Long> getCooldowns() {
		return cooldowns;
	}

	public void setCooldowns(Map<String, Long> cooldowns) {
		this.cooldowns = cooldowns;
	}

	public Map<Integer, ItemStack[]> getBackpacks() {
		return backpacks;
	}

	public void setBackpacks(Map<Integer, ItemStack[]> backpacks) {
		this.backpacks = backpacks;
	}

	public List<String> getTitles() {
		return titles;
	}

	public void setTitles(List<String> titles) {
		this.titles = titles;
	}
	
}
package ru.yourname.clans.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class MyClanHolder implements InventoryHolder {
    private Inventory inventory;
    private final String clanName;
    public MyClanHolder(String clanName) { this.clanName = clanName; }
    public String getClanName() { return clanName; }
    public void setInventory(Inventory inventory) { this.inventory = inventory; }
    @Override public Inventory getInventory() { return inventory; }
}

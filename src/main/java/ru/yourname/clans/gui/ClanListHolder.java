package ru.yourname.clans.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ClanListHolder implements InventoryHolder {
    private Inventory inventory;
    private final int page;
    public ClanListHolder(int page) { this.page = page; }
    public int getPage() { return page; }
    public void setInventory(Inventory inventory) { this.inventory = inventory; }
    @Override public Inventory getInventory() { return inventory; }
}

package bluiscool.stickybed;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

class StickyBedEvents implements Listener {
    StickyBed stickyBed;

    StickyBedEvents(StickyBed stickyBed) {
        this.stickyBed = stickyBed;
    }

    @EventHandler
    public void tryLeaveBed(PlayerBedLeaveEvent event) {
        stickyBed.log("Test!");
        Block bed = event.getBed();
        if(bed.hasMetadata("sticky"))
        {
            stickyBed.log("sticky!!!");
    
        }
    }

    public void onBedClick(PlayerInteractEvent event) {
        if(!(event.getClickedBlock().getBlockData() instanceof org.bukkit.block.data.type.Bed))
            return;

        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        ItemStack handItem = inventory.getItemInMainHand();

        if(handItem.getType() == Material.SLIME_BALL) {
            Block bed = event.getClickedBlock();
            if(!bed.hasMetadata("sticky")) {
                event.setCancelled(true);
                bed.setMetadata("sticky", new FixedMetadataValue(stickyBed, true));
                bed.setType(Material.GREEN_BED);
                handItem.setAmount(handItem.getAmount() - 1);
                bed.getWorld().playSound(bed.getLocation(), Sound.BLOCK_SLIME_BLOCK_PLACE, 5, 1.0f);
            }
        }
    }
}

public final class StickyBed extends JavaPlugin {
    public void log(String msg) {
        this.getLogger().info(msg);
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new StickyBedEvents(this), this);
        log("Ready!");
    }

    @Override
    public void onDisable() {
    }
}

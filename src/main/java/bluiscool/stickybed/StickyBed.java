package bluiscool.stickybed;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

class StickyBedEvents implements Listener {
    StickyBed stickyBed;

    StickyBedEvents(StickyBed stickyBed) {
        this.stickyBed = stickyBed;
    }

    @EventHandler
    public void tryLeaveBed(PlayerBedLeaveEvent event) {

        Block bed = event.getBed();
        if(bed.hasMetadata("sticky"))
        {
            event.setCancelled(true);

            if(bed.getMetadata("sticky").get(0).asInt() != 1) return;

            Bed bedAsBed = (Bed)bed.getBlockData();

            Block otherBed = bed.getRelative(bedAsBed.getFacing().getOppositeFace());
            if(!(otherBed.getBlockData() instanceof org.bukkit.block.data.type.Bed))
                otherBed = bed.getRelative(bedAsBed.getFacing());

            bed.setMetadata("sticky", new FixedMetadataValue(stickyBed, 2));
            otherBed.setMetadata("sticky", new FixedMetadataValue(stickyBed, 2));

            Block finalOtherBed = otherBed;
            new BukkitRunnable() {
                int ticks = 0;
                @Override
                public void run() {
                    ticks++;

                    if(ticks == 6) {
                        event.getPlayer().teleport(bed.getLocation());
                        event.getPlayer().damage(5);

                        bed.getWorld().dropItemNaturally(bed.getLocation(), new ItemStack(bed.getType(), 1));
                        bed.setType(Material.AIR);
                        bed.removeMetadata("sticky", stickyBed);
                        finalOtherBed.removeMetadata("sticky", stickyBed);
                        this.cancel();
                        return;
                    }

                    bed.getWorld().playSound(bed.getLocation(), Sound.ENTITY_PLAYER_HURT, 5 + ticks, 1.0f);
                    bed.getWorld().playSound(bed.getLocation(), Sound.BLOCK_SLIME_BLOCK_PLACE, 5 + ticks, 1.0f);
                }

            }.runTaskTimer(stickyBed, 0, 20L);
        }
    }

    @EventHandler
    public void onBedClick(PlayerInteractEvent event) {
        if(event.getClickedBlock() == null) return;
        if(!(event.getClickedBlock().getBlockData() instanceof org.bukkit.block.data.type.Bed))
            return;

        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        ItemStack handItem = inventory.getItemInMainHand();

        if(handItem.getType() == Material.SLIME_BALL) {
            event.setCancelled(true);

            Block bed = event.getClickedBlock();
            if(!bed.hasMetadata("sticky")) {
                event.setCancelled(true);

                Bed bedAsBed = (Bed)bed.getBlockData();

                Block otherBed = bed.getRelative(bedAsBed.getFacing().getOppositeFace());
                if(!(otherBed.getBlockData() instanceof org.bukkit.block.data.type.Bed))
                    otherBed = bed.getRelative(bedAsBed.getFacing());

                bed.setMetadata("sticky", new FixedMetadataValue(stickyBed, 1));
                otherBed.setMetadata("sticky", new FixedMetadataValue(stickyBed, 1));

                handItem.setAmount(handItem.getAmount() - 1);
                bed.getWorld().playSound(bed.getLocation(), Sound.BLOCK_SLIME_BLOCK_PLACE, 5, 1.0f);
            }
        }
    }
}

public final class StickyBed extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new StickyBedEvents(this), this);
    }

    @Override
    public void onDisable() {
    }
}

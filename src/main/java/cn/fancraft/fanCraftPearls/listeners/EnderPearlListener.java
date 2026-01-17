package cn.fancraft.fanCraftPearls.listeners;

import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import cn.fancraft.fanCraftPearls.FanCraftPearls;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnderPearlListener implements Listener {

    // 存储每个玩家当前骑乘的末影珍珠
    private static final Map<UUID, EnderPearl> playerPearls = new HashMap<>();

    // 存储每个珍珠对应的任务
    private static final Map<EnderPearl, BukkitRunnable> pearlTasks = new HashMap<>();

    // 防止递归调用
    private static boolean isCreatingPearl = false;

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        // 防止递归调用
        if (isCreatingPearl) {
            return;
        }

        if (event.getEntity() instanceof EnderPearl) {
            EnderPearl pearl = (EnderPearl) event.getEntity();
            if (pearl.getShooter() instanceof Player) {
                Player player = (Player) pearl.getShooter();

                // 取消默认的末影珍珠行为
                event.setCancelled(true);

                // 如果玩家已经有骑乘的末影珍珠，则删除它
                EnderPearl oldPearl = playerPearls.get(player.getUniqueId());
                if (oldPearl != null && !oldPearl.isDead()) {
                    oldPearl.remove();
                    // 取消旧珍珠的任务
                    BukkitRunnable task = pearlTasks.remove(oldPearl);
                    if (task != null) {
                        task.cancel();
                    }
                }

                // 创建一个新的末影珍珠实体
                isCreatingPearl = true;
                EnderPearl newPearl = player.launchProjectile(EnderPearl.class, player.getLocation().getDirection());
                isCreatingPearl = false;
                
                // 根据配置设置末影珍珠的速度
                double speed = FanCraftPearls.getPearlSpeed();
                newPearl.setVelocity(newPearl.getVelocity().multiply(speed));

                // 将玩家设置为乘客
                newPearl.addPassenger(player);

                // 存储新的末影珍珠
                playerPearls.put(player.getUniqueId(), newPearl);

                // 添加任务来持续更新玩家的位置，确保玩家跟随珍珠
                BukkitRunnable task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            // 检查玩家是否在线和珍珠是否有效
                            if (player == null || !player.isOnline() || newPearl.isDead() || !newPearl.isValid()) {
                                this.cancel();
                                playerPearls.remove(player.getUniqueId());
                                pearlTasks.remove(newPearl);
                                return;
                            }

                            // 确保玩家在珍珠上
                            if (player.getVehicle() != newPearl) {
                                newPearl.addPassenger(player);
                            }
                        } catch (Exception e) {
                            this.cancel();
                            playerPearls.remove(player.getUniqueId());
                            pearlTasks.remove(newPearl);
                            e.printStackTrace();
                        }
                    }
                };

                pearlTasks.put(newPearl, task);
                task.runTaskTimer(FanCraftPearls.getInstance(), 1L, 1L);
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        // 取消所有末影珍珠传送
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        // 取消末影珍珠造成的伤害
        if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
        }
    }
}

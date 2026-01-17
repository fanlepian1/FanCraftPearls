package cn.fancraft.fanCraftPearls;

import cn.fancraft.fanCraftPearls.listeners.EnderPearlListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class FanCraftPearls extends JavaPlugin {

    private static FanCraftPearls instance;
    private static double pearlSpeed;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        // 保存默认配置文件
        saveDefaultConfig();

        // 加载配置
        loadConfig();

        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new EnderPearlListener(), this);

        getLogger().info("FanCraftPearls 插件已启用！");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("FanCraftPearls 插件已禁用！");
    }

    /**
     * 加载配置文件
     */
    private void loadConfig() {
        pearlSpeed = getConfig().getDouble("pearl.speed", 1.0);
        getLogger().info("已加载配置: 末影珍珠速度 = " + pearlSpeed);
    }

    /**
     * 重新加载配置文件
     */
    public void reloadPluginConfig() {
        reloadConfig();
        loadConfig();
    }

    public static FanCraftPearls getInstance() {
        return instance;
    }

    /**
     * 获取末影珍珠速度
     * @return 末影珍珠速度
     */
    public static double getPearlSpeed() {
        return pearlSpeed;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("fancraftpearls") || command.getName().equalsIgnoreCase("fcp")) {
            if (!sender.hasPermission("fancraftpearls.reload")) {
                sender.sendMessage("§c你没有权限使用此命令！");
                return true;
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                reloadPluginConfig();
                sender.sendMessage("§aFanCraftPearls 配置已重新加载！");
                sender.sendMessage("§e末影珍珠速度: §f" + pearlSpeed);
                return true;
            }

            sender.sendMessage("§c用法: /fancraftpearls reload");
            return true;
        }
        return false;
    }
}

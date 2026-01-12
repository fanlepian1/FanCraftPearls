package cn.fancraft.fanCraftPearls;

import cn.fancraft.fanCraftPearls.listeners.EnderPearlListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class FanCraftPearls extends JavaPlugin {

    private static FanCraftPearls instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new EnderPearlListener(), this);

        getLogger().info("FanCraftPearls 插件已启用！");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("FanCraftPearls 插件已禁用！");
    }

    public static FanCraftPearls getInstance() {
        return instance;
    }
}

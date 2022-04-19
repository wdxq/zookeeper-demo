package ink.lsq.demo.zk;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class ZkServiceRegister {

    private static final String ROOT = "/ZookeeperDemoRootPath";

    private ZooKeeper zooKeeper;

    private volatile String path;

    private volatile List<String> children;

    public void init() {
        try {
            log.info("init");
            zooKeeper = new ZooKeeper("127.0.0.1:2181", 3000, event -> {
                try {
                    if (Watcher.Event.KeeperState.SyncConnected.equals(event.getState())) {
                        if (null == zooKeeper.exists(ROOT, false)) {
                            log.info("未发现节点，执行创建。");
                            zooKeeper.create(ROOT, "".getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                        }
                        String currentPath = zooKeeper.create(ROOT + "/server", "".getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
                        log.info("创建临时节点成功 currentPath:{}", currentPath);
                        path = currentPath.substring(ROOT.length() + 1);
                        log.info("更新当前节点信息成功 path:{}", path);
                        refreshNode();
                    }
                } catch (Exception e) {
                    log.error("创建节点异常_" + e.getMessage(), e);
                }
            });
        } catch (Exception e) {
            log.error("启动失败" + e.getMessage(), e);
        }
    }

    public void destroy() {
        try {
            log.info("destroy");
            if (null != zooKeeper) {
                zooKeeper.close();
            }
        } catch (Exception e) {
            log.error("销毁失败_" + e.getMessage(), e);
        }
    }

    public boolean needRun(long taskId) {
        if (StringUtils.isBlank(path) || CollectionUtils.isEmpty(children)) {
            log.warn("节点数据为空！无法正确进行软负载均衡！");
            return false;
        }
        return (taskId % children.size()) == children.indexOf(path);
    }

    private void refreshNode() {
        try {
            children = zooKeeper.getChildren(ROOT, event -> {
                if (Watcher.Event.EventType.NodeChildrenChanged.equals(event.getType())) {
                    refreshNode();
                }
            });
            log.info("更新节点信息成功，当前节点信息:{}", JSON.toJSONString(children));
        } catch (Exception e) {
            log.error("刷新节点异常_" + e.getMessage(), e);
        }
    }

}

package ink.lsq.demo.zk;

import io.netty.util.internal.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TaskTimer extends Timer {

    @Autowired
    private ZkServiceRegister zkServiceRegister;

    @PostConstruct
    public void start() {
        this.schedule(new TimerTask() {
            @Override
            public void run() {
                long taskId = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
                boolean needRun = zkServiceRegister.needRun(taskId);
                log.info("当前节点是否需要执行该任务 taskId:{} needRun:{}", taskId, needRun);
            }
        }, TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(3));
    }

}

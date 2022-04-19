package ink.lsq.demo.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {

    @Autowired
    private ZkServiceRegister zkServiceRegister;

    @GetMapping("submitTask")
    public String submitTask(String taskId) {
        if (StringUtils.isBlank(taskId)) {
            return "taskId参数未找到！";
        }
        Long taskIdLong;
        try {
            taskIdLong = NumberUtils.createLong(taskId);
        } catch (RuntimeException e) {
            log.error("taskId参数转化异常_" + e.getMessage(), e);
            return "taskId参数转化异常";
        }
        return "改任务是否应该被当前节点运行:" + zkServiceRegister.needRun(taskIdLong);
    }

}

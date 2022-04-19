package ink.lsq.demo.zk;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@SpringBootApplication
public class ZookeeperDemoApplication implements ApplicationContextAware {

	private static ApplicationContext ac;

	public static void main(String[] args) {
		SpringApplication.run(ZookeeperDemoApplication.class, args);
		ZkServiceRegister zkServiceRegister = ac.getBean(ZkServiceRegister.class);
		zkServiceRegister.init();
		Runtime.getRuntime().addShutdownHook(new Thread(zkServiceRegister::destroy));
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ac = applicationContext;
	}
}

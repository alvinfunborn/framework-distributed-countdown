#### 基于redis的分布式倒计时框架
实现onStart,onCountdown的可取消的倒计时器, 适用于多实例微服务的倒计时业务

##### usage
1. 实现RedisOperator
2. 实现DistributedLocker, 必须是分布式锁

###### config
```$xslt
@Configuration
public class CountdownConfig {

    @Autowired
    private DistributedLocker distributedLocker;
    @Autowired
    private RedisOperator redisOperator;

    @Bean
    public Countdown countdown() {
        return new StandardCountdown(distributedLocker, redisOperator);
    }
}
```

###### service
```$xslt
@Service
public class Service {
    
    @Autowired
    private Countdowm countdown;
    
    public void start() {
        countdown.startCountdown(new CountdownTimer("bus1", 20, TimeUnit.SECONDS) {
            @Override
            public void onStart() {
                System.out.println("start");
            }

            @Override
            public void onCountdown() {
                System.out.println("countdown");
            }
        });
    }
    
    public void cancel() {
        countdown.cancelCountdown("bus1");
    }
}
```
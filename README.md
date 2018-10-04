Simple Java EE SMS sender with automatic failover
------------
This demonstration uses CDI to discover 
`@SmsProvider`s on the application classpath and then randomly fails over between them. 

This is a very simple implementation that doesn't use any sophisticated statistical analysis for failover.

If for any reason any reason an attempt to send SMS via a provider fails (e.g HTTP error), a failover is triggered. For example, if you run out of units on a provider, a failover happens.

Every 5 minutes, the system also tries to check if text messages actually got delivered to the phone they are destined for.
This is done by sampling 5 sent messages (evenly picked); if at least 3 of them didn't get delivered to their destination phone (e.g due to DND restriction on the route), a failover is also triggered.

This isn't a sophisticated strategy. For example, it could have been that those phones were actually switched off or that specific providers are not able to deliver to specific telcos; but it works for simple scenarios.

----

Sample usage:
---

*Kotlin*
```
@Inject 
private lateinit var handler: SmsHandler

// Don't care about which provider is actually used
handler.sendSms(phone, title, message)
```

*Java*
```
@Inject
private SmsHandler handler;

handler.sendSms(phone, title, message);
```

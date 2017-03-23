# disable-build-triggers-plugin
a jenkins plugin that disables build triggers but still lets you trigger manually and start downstream builds.

Configurable eighter via GUI: Manage Jenkins --> Configur system --> Disable Build Triggers Config  
or via REST API:
eg.:
(user needs admin permission)

`curl http://localhost:8080/jenkins/suppress-triggers/suppressTriggersOff -u 'user:pwd'`  
`curl http://localhost:8080/jenkins/suppress-triggers/suppressTriggersOn -u 'user:pwd'`

Triggers suppressed by default are:  
`TimerTrigger.TimerTriggerCause.class`  
`SCMTrigger.SCMTriggerCause.class`  

If you add further Triggers to suppress you have to use the Java Class name like above. If you want to keep the defaults make sure to add them to the list!
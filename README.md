# Embedded dotCMS
All you need to run dotCMS is this wrapper - it can be pointed to a dotCMS war file or to an exploded dotCMS webapp directory


## Building
To create an all in one jar to run dotCMS, call
```
./gradlew shadowJar
```
This will build the dotcms-all.jar in ./build/libs/

## Configuring

DB config can be set as environmental variables or passed in as System properties
```
export db_driver=com.mysql.jdbc.Driver
export db_removeAbandonedTimeout=60
export db_initialSize=10
export db_maxTotal=100
export db_url="jdbc:mysql://localhost/dotcms5?characterEncoding=UTF-8"
export db_username=dotcms
export db_password=dotcms
```

## Running
Embedded dotCMS can take a war or be pointed to an exploded war via the `-f` flag.


`java -jar ./dotcms-all.jar -f ROOT.war`

or

`java -jar ./dotcms-all.jar -f ./explodedwar`

The jar takes these args
```
  --fileOrFolder [-f] (a string; default: "ROOT.war")
    .war file or path to the exploded war folder.
  --home [-h] (a string; default: "catalina")
    Sets the CATALINA_HOME
  --host [-o] (a string; default: "localhost")
    The server host.
  --port [-p] (an integer; default: "8080")
    The server port to listen on.
```



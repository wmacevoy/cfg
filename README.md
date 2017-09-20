# Cfg - Manage configuration, including secrets, with open version control.

This is a configuration control tool.  It is designed to allow developers to
use version control for configuration settings in a way that

* Works well for large applications
* Works well for large teams
* Works well for managing secrets

To be specific it is written in Java, but the ideas could apply anywhere, and can be used as a command line tool to export configurations to other environments.

## Stream/XML Duality

The following structures are considered equivalent:

    /config/strings/en.cfg containing '<hi>hello</hi>'
    /config/strings/es.cfg containing '<hi>hola</hi>'

vs.

    /config.cfg containing '<strings><en><hi>hello</hi></en><es><hi>hola</hi></strings>'

vs.

    /config/strings.jar (or strings.zip) with en.cfg and es.cfg in the jar as above

vs.

    /config/strings.xml containing '<?xml version="1.0" encoding="utf-8"><strings><en><hi>hello</hi></en><es><hi>hola</hi></strings>'

It is possible to add other adapters for popular config formats (yaml, ini), but this is illustrative for now.

## Functional

Text-tranformation rules exist to facilitate the DRY principle

/config/domain.cfg with "example.com"

/config/database/host.cfg with "db.${../../domain}"

If the environment variable APP_DP_PORT is set,

/config/database/port.cfg with "$env{APP_DB_PORT}"

## Cryptographic

You can symmetric key encrypt any parts of the configuration you see fit:

     java -jar cfg.jar "$encrypt{$env{KEY},my-secret}" > secret.cfg

     java -jar cfg.jar "$decrypt{$env{KEY},${secret}"

As a convenience, you can generate strong keys

     java -jar cfg.jar "$key{}" >> key.cfg

Or, using your own-regex like pattern:

     java -jar cfg.jar "$random{[a-z0-9]{20}}" >> key.cfg

This way, you can share a few strong project keys to the dev team, and with them they can manage all the API and configuration keys as configuration within your normal version control.

## Union Mounted - the following are equivalent.

A configuration can have multiple mount points

config/tree.cfg - with '<more><stuff>some</stuff><stuff>other</stuff></more>'
config/tree/more/stuff.cfg  with 'things'

is the same as

config.cfg with '<tree><more><stuff>someotherthings</stuff></more><tree>'

or some permutation of the words 'some', 'other' and 'things'.


## Cached

Any resource will remain constant for the life of the configured application, unless some configuration path is explicity invalidated by the application.

cfg.invalidate("/"); // everthing

cfg.invalidate("/test-server/network") everything under /test-server/network
















  

## Step 1 - Require the cfg artifact, using a maven `pom.xml` this would be the following dependency:

```
    <dependencies>
      <dependency>
        <groupId>com.github.wmacevoy</groupId>
        <artifactId>cfg</artifactId>
        <version>1.1</version>
      </dependency>
    </dependencies>
```

## Step 2 - Decide on symmetric master dev key(s) to share.

For a small project this is probably one random key you could share via LastPass for example.  If you have access to the cfg jar file, you can generate cryptographically strong patterned keys with

```
java -cp /path/to/cfg.jar cfg.Cfg '$random{PATTERN}'
```
Here PATTERN is a regex-like pattern for the randomness

* `ABC...`  pattern A followed by pattern B, etc.
* `(A|B|...)` a uniformly randomly selected pattern A or B, etc.
* `P{min[,max]}` P repeated between min and optionally max times.
* `[set]` a random letter from the set (supports ranges)

Some useful examples

* `(rock|paper|scissors)` one random pick (ex: `paper`).
* `[A-Za-z0-9]{8-12}` from 8 to 12 alphanumeric characters (ex: `h75dQ0GypEsG`).
* `[acdefhjmnprtwxyz]{4}(.[acdefhjmnprtwxyz]{4}.){7}` 128 bit key using unambigous symbols and one mobile keypad (ex: `dyth.fxdm.ypzm.ftxr.xjff.trxa.znzc.frpx`).  This can equivalently be generated with $key{128}.
* `:([0-9a-f]{4}:){8}` mac-like 128 bit key (ex: `:8f38:a082:d902:fb19:3f91:9881:429e:096e:`).

## Step 3: Share and set the key.

Set an environment variable with the master key (the name and value does not matter, but you need to share this with the dev team NOT in the repository).

```
export CFG_KEY="8f38:a082:d902:fb19:3f91:9881:429e:096e"
```

## Step 4: Encrypt your application keys

Make note of the output (it will change every time, but all the outputs will decrypt to "db-password")
```
java -cp target/cfg-1.0.jar cfg.Cfg '$encrypt{$env{CFG_KEY},db-password}'
```
## Step 5: Store your encrypted configuration in the resources for your project (src/main/resources/cfg/db.cfg, etc)

```
<user>db-user</user>
<password>$decrypt{$env{CFG_KEY},${cipher}}</password>
<cipher>2f3da2aa34e978b768f8efb63f36600df5f78f71756f3feb5f7262ab99d008460409f0bef3ac473471964b3e</cipher>
```

## Step 6 -- load/use configuration.
Assuming the CFG_KEY environment variable is correctly set, and the resources are compiled into your project,

```
Cfg cfg = new Cfg("cfg");
String dbUser = cfg.getString("db/user");
String dbPassword = cfg.getString("db/password");
```

## Step 7 --- Rejoice!

Your configuration (with secret parts encrypted) is now safely managed in through source control and conveniently split into as many components as is useful.

# Functions

* `${path}` -- value of configuration item (supports absolute and relative addressing).
* `$env{VAR}` -- value of environment variable.
* `$raw{path}` -- untranslated value.
* `$encrypt{key,plain}` -- AES-128 GCM with 16-byte padding using the SHA256 hash of key as the encryption key and hex encoded result.
* `$decrypt{key,cipher}` -- reverse of encrypt.
* `$random{PATTERN}` -- cryptographically strong random pattern generation.  Supports `ABC...`, `(A|B|C..)`, `[abc]`, `[a-z]`, `A{min,max}` and `A{num}` patterns.
* `$key{bits}` 

